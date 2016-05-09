import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.regex.*;

/**
 * Created by vil on 02/05/16.
 */
public class SMTPServerInterface {

    private Socket sc;
    private List<ServerReceiver> servers;
    private String user;
    private List<String> content;
    private int indexServer;
    private SMTPState state;
    private boolean needToCommunicate = true;
    private boolean isConnected;
    private Matcher m;
    private Pattern serverReady = Pattern.compile("220 (.*) ready"),
            greeting = Pattern.compile("250 (.*) greet (.*)"),
            ok = Pattern.compile("250 OK"),
            unknowUserName = Pattern.compile("550 (.*)"),
            startMailInput = Pattern.compile("354 (.*)"),
            closeMessage = Pattern.compile("221 (.*)");
    private boolean noUserFound = true;

    public SMTPServerInterface(List<ServerReceiver> servers, String user, List<String> content){
        this.state = SMTPState.INIT;
        this.servers = servers;
        this.user = user;
        this.content = content;
        this.indexServer = servers.size() - 1;
        try {
            while(needToCommunicate){
                System.err.println("Try to communicate");
                this.send(servers.get(indexServer));
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    protected void writeStream(String toSend) throws IOException {
        System.err.println("SENDING :"+toSend);
        toSend += "\r\n";
        byte[] bytesToSend = toSend.getBytes();
        sc.getOutputStream().write(bytesToSend);
        sc.getOutputStream().flush();
    }

    protected String readStream() throws IOException {
        byte[] receipt = new byte[1024];
        sc.getInputStream().read(receipt);
        String result = new String(receipt, "UTF-8");
        System.err.println("READING :"+result);
        return result;
    }


    public void send(ServerReceiver srv) throws IOException {
        sc = new Socket(srv.address, srv.port);
        isConnected = true;
        noUserFound = true;
        this.state = SMTPState.WAIT_CONNECTION;
        while(isConnected){
            messageHandler(readStream());
        }

    }

    public void messageHandler(String msg){

        m = serverReady.matcher(msg);
        if(m.matches() && state == SMTPState.WAIT_CONNECTION){
            try {
                writeStream("HELO "+user.split("@")[1]);
                state = SMTPState.WAIT_GREETINGS;
            }
            catch (IOException e) { e.printStackTrace(); }
            return;
        }

        m = greeting.matcher(msg);
        if(m.matches() && state == SMTPState.WAIT_GREETINGS){
            try {
                writeStream("MAIL FROM:<"+user+">");
                state = SMTPState.WAIT_SENDER_CONFIRMATION;
            } catch (IOException e) { e.printStackTrace(); }
            return;
        }

        m = ok.matcher(msg);
        if(m.matches()){
            if(state == SMTPState.WAIT_SENDER_CONFIRMATION){
                noUserFound = false;
                try{
                    writeStream("RCPT TO:<"+servers.get(indexServer).receivers.remove(servers.get(indexServer).receivers.size()-1)+"@"+servers.get(indexServer).address+">");
                    state = SMTPState.WAIT_RECIPIENT_CONFIRMATION;
                } catch (IOException e) { e.printStackTrace();}
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
                } catch (IOException e) { e.printStackTrace(); }
                return;
            }
            if(state == SMTPState.WAIT_END_CONFIRMAITON){
                try {
                    writeStream("QUIT");
                } catch (IOException e) { e.printStackTrace(); }
                return;
            }
        }

        m = unknowUserName.matcher(msg);
        if(m.matches() && state == SMTPState.WAIT_RECIPIENT_CONFIRMATION){
            System.err.println("UTILISATEUR INCONNU");
            try {
                if(servers.get(indexServer).receivers.isEmpty()){
                    if(noUserFound){
                        try {
                            sc.close();
                        } catch (IOException e) { e.printStackTrace(); }
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
            } catch (IOException e) {
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
            } catch (IOException e) { e.printStackTrace(); }
        }

        m = closeMessage.matcher(msg);
        if(m.matches() && state == SMTPState.END){
            try {
                sc.close();
            } catch (IOException e) { e.printStackTrace(); }
            indexServer --;
            isConnected = false;
            if(indexServer < 0){
                needToCommunicate = false;
            }
            return;
        }

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