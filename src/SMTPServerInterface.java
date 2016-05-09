import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vil on 02/05/16.
 * 10.42.0.56:2049
 * 77.134.169.38:2049
 */
class SMTPServerInterface {

    private Socket sc;
    private List<ServerReceiver> servers;
    private String user;
    private List<String> content;
    private int indexServer;
    private SMTPState state;
    private boolean needToCommunicate = true;
    private boolean isConnected;
    private Matcher m;
    private Pattern serverReady = Pattern.compile("220 (.*)"),
            greeting = Pattern.compile("250 (.*)"),
            ok = Pattern.compile("250 OK(.*)"),
            unknowUserName = Pattern.compile("550 (.*)"),
            startMailInput = Pattern.compile("354 (.*)"),
            closeMessage = Pattern.compile("221 (.*)");
    private boolean noUserFound = true;

    SMTPServerInterface(List<ServerReceiver> servers, String user, List<String> content){
        this.state = SMTPState.INIT;
        this.servers = servers;
        this.user = user;
        this.content = content;
        this.indexServer = servers.size() - 1;
        while(needToCommunicate){
            try{ this.send(servers.get(indexServer)); }
            catch (UnknownHostException e) {
                System.err.println("Serveur injoignable");
                indexServer--;
                if(indexServer < 0)
                    needToCommunicate = false;
            }
            catch (SocketTimeoutException e) {
                System.err.println("Le serveur n'a pas répondu à temps");
                indexServer--;
                if(indexServer < 0)
                    needToCommunicate = false;
            }
            catch (IOException e) { e.printStackTrace(); }
        }

    }

    private void writeStream(String toSend) throws IOException {
        System.err.println("SENDING :"+toSend);
        toSend += "\r\n";
        byte[] bytesToSend = toSend.getBytes();
        sc.getOutputStream().write(bytesToSend);
        sc.getOutputStream().flush();
    }

    private String readStream() throws IOException {
        byte[] receipt = new byte[1024];
        sc.getInputStream().read(receipt);
        String result = new String(receipt, "UTF-8");
        System.err.println("READING :"+result);
        return result;
    }


    private void send(ServerReceiver srv) throws IOException {
        sc = new Socket();
        sc.connect(new InetSocketAddress(srv.address, srv.port), 1000);
        sc.setSoTimeout(1000);
        isConnected = true;
        noUserFound = true;
        this.state = SMTPState.WAIT_CONNECTION;
        while(isConnected){
            messageHandler(readStream());
        }

    }

    private void messageHandler(String msg){
        msg = msg.replaceAll("(\\r|\\n)", "");

        m = serverReady.matcher(msg);
        if(m.matches() && state == SMTPState.WAIT_CONNECTION){
            try {
                writeStream("HELO "+user.split("@")[1]);
                state = SMTPState.WAIT_GREETINGS;
            }
            catch (SocketTimeoutException e) {
                System.err.println("Le serveur n'a pas répondu à temps");
                indexServer--;
                if(indexServer < 0)
                    needToCommunicate = false;
            }
            catch (IOException e) { e.printStackTrace(); }
            return;
        }

        m = greeting.matcher(msg);
        if(m.matches() && state == SMTPState.WAIT_GREETINGS){
            try {
                writeStream("MAIL FROM:<"+user+">");
                state = SMTPState.WAIT_SENDER_CONFIRMATION;
            }
            catch (SocketTimeoutException e) {
                System.err.println("Le serveur n'a pas répondu à temps");
                indexServer--;
                if(indexServer < 0)
                    needToCommunicate = false;
            }
            catch (IOException e) { e.printStackTrace(); }
            return;
        }

        m = ok.matcher(msg);
        if(m.matches()){
            if(state == SMTPState.WAIT_SENDER_CONFIRMATION){
                try{
                    ServerReceiver srv = servers.get(indexServer);
                    String adrs = "@"+srv.address+">";
                    int i = srv.receivers.size()-1;
                    String receiver = srv.receivers.remove(i);
                    writeStream("RCPT TO:<"+receiver+adrs);
                    state = SMTPState.WAIT_RECIPIENT_CONFIRMATION;
                }
                catch (SocketTimeoutException e) {
                    System.err.println("Le serveur n'a pas répondu à temps");
                    indexServer--;
                    if(indexServer < 0)
                        needToCommunicate = false;
                }
                catch (IOException e) { e.printStackTrace();}
                return;
            }
            if(state == SMTPState.WAIT_RECIPIENT_CONFIRMATION){
                noUserFound = false;
                try {
                    if(servers.get(indexServer).receivers.isEmpty()){
                        writeStream("DATA");
                    }
                    else {
                        writeStream("RCPT TO:<"+servers.get(indexServer).receivers.remove(servers.get(indexServer).receivers.size()-1)+"@"+servers.get(indexServer).address+">");
                    }
                }
                catch (SocketTimeoutException e) {
                    System.err.println("Le serveur n'a pas répondu à temps");
                    indexServer--;
                    if(indexServer < 0)
                        needToCommunicate = false;
                }
                catch (IOException e) { e.printStackTrace(); }
                return;
            }
            if(state == SMTPState.WAIT_END_CONFIRMAITON){
                try {
                    writeStream("QUIT");
                    state = SMTPState.END;
                }
                catch (SocketTimeoutException e) {
                    System.err.println("Le serveur n'a pas répondu à temps");
                    indexServer--;
                    if(indexServer < 0)
                        needToCommunicate = false;
                }
                catch (IOException e) { e.printStackTrace(); }
                return;
            }
        }

        m = unknowUserName.matcher(msg);
        if(m.matches() && state == SMTPState.WAIT_RECIPIENT_CONFIRMATION){
            System.err.println("Utilisateur inconnu");
            try {
                if(servers.get(indexServer).receivers.isEmpty()){
                    if(noUserFound){
                        try {
                            writeStream("QUIT");
                            state = SMTPState.END;
                        }
                        catch (SocketTimeoutException e) {
                            System.err.println("Le serveur n'a pas répondu à temps");
                            indexServer--;
                            if(indexServer < 0)
                                needToCommunicate = false;
                        }
                        catch (IOException e) { e.printStackTrace(); }
                        indexServer --;
                        isConnected = false;
                        if(indexServer < 0){
                            needToCommunicate = false;
                        }
                        return;
                    }
                    writeStream("DATA");
                }
                else {
                    writeStream("RCPT TO:<"+servers.get(indexServer).receivers.remove(servers.get(indexServer).receivers.size()-1)+"@"+servers.get(indexServer).address+">");
                }
            }
            catch (SocketTimeoutException e) {
                System.err.println("Le serveur n'a pas répondu à temps");
                indexServer--;
                if(indexServer < 0)
                    needToCommunicate = false;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        m = startMailInput.matcher(msg);
        if(m.matches() && state == SMTPState.WAIT_STARTDATA_SIGNAL){
            try {
                while ( !content.isEmpty() ){
                        writeStream(content.remove(0));
                }
            }
            catch (SocketTimeoutException e) {
                System.err.println("Le serveur n'a pas répondu à temps");
                indexServer--;
                if(indexServer < 0)
                    needToCommunicate = false;
            }
            catch (IOException e) { e.printStackTrace(); }
        }

        m = closeMessage.matcher(msg);
        if(m.matches() && state == SMTPState.END){
            try {
                System.err.println("La connexion au serveur s'est terminée correctement.");
                sc.close();
                isConnected = false;
            }
            catch (SocketTimeoutException e) {
                System.err.println("Le serveur n'a pas répondu à temps");
                indexServer--;
                if(indexServer < 0)
                    needToCommunicate = false;
            }
            catch (IOException e) { e.printStackTrace(); }
            indexServer --;
            isConnected = false;
            if(indexServer < 0){
                needToCommunicate = false;
            }
            return;
        }

        if(state == SMTPState.END){
            System.err.println("La connexion au serveur ne s'est pas terminée correctement. Le serveur n'a pas répondu.");
            try {
                sc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            isConnected = false;
            return;
        }

        try {
            writeStream("QUIT");
            state = SMTPState.END;
        }
        catch (SocketTimeoutException e) {
            System.err.println("Le serveur n'a pas répondu à temps");
            indexServer--;
            if(indexServer < 0)
                needToCommunicate = false;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        needToCommunicate = false;

    }


}

enum SMTPState {
    INIT,
    WAIT_CONNECTION,
    WAIT_GREETINGS,
    WAIT_SENDER_CONFIRMATION,
    WAIT_RECIPIENT_CONFIRMATION,
    WAIT_STARTDATA_SIGNAL,
    WAIT_END_CONFIRMAITON,
    END
}