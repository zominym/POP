import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vil on 02/05/16.
 */
public class SMTPServerInterface {

    private Socket sc;
    private ServerReceiver servers[];
    private String user;
    private String content;
    private int indexServer;
    private SMTPState state;
    private Matcher m;
    private Pattern serverReady = Pattern.compile("220 (.*) ready"),
            greeting = Pattern.compile("250 (.*) greet (.*)"),
            ok = Pattern.compile("250 OK"),
            unknowUserName = Pattern.compile("550 (.*)"),
            startMailInput = Pattern.compile("354 (.*)"),
            closeMessage = Pattern.compile("221 (.*)");


    public SMTPServerInterface(ServerReceiver servers[], String user, String content){
        this.state = SMTPState.INIT;
        this.servers = servers;
        this.user = user;
        this.content = content;
        this.indexServer = servers.length;
        try {
            this.send(servers[indexServer-1]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void writeStream(String toSend) throws IOException {
        toSend += "\r\n";
        byte[] bytesToSend = toSend.getBytes();
        sc.getOutputStream().write(bytesToSend);
        sc.getOutputStream().flush();
    }

    protected String readStream() throws IOException {
        byte[] receipt = new byte[1024];
        sc.getInputStream().read(receipt);
        String result = new String(receipt, "UTF-8");
        return result;
    }


    public void send(ServerReceiver srv) throws IOException {
        sc = new Socket(srv.address, srv.port);
        this.state = SMTPState.WAIT_CONNECTION;
        messageHandler(readStream());
    }

    public void messageHandler(String msg){

        m = serverReady.matcher(msg);
        if(m.matches() && state == SMTPState.WAIT_CONNECTION){
            try {
                writeStream("HELO "+ InetAddress.getLocalHost());
                state = SMTPState.WAIT_GREETINGS;
            }
            catch (IOException e) { e.printStackTrace(); }
            return;
        }

        m = greeting.matcher(msg);
        if(m.matches() && state == SMTPState.WAIT_GREETINGS){

        }

        m = ok.matcher(msg);
        if(m.matches()){
            if(state == SMTPState.WAIT_SENDER_CONFIRMATION){}
            if(state == SMTPState.WAIT_RECIPIENT_CONFIRMATION){}
            if(state == SMTPState.WAIT_END_CONFIRMAITON){}
        }

        m = unknowUserName.matcher(msg);
        if(m.matches() && state == SMTPState.WAIT_RECIPIENT_CONFIRMATION){
        }

        m = startMailInput.matcher(msg);
        if(m.matches() && state == SMTPState.WAIT_STARTDATA_SIGNAL){

        }

        m = closeMessage.matcher(msg);
        if(m.matches() && state == SMTPState.END){

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