import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.regex.*;


/**
 * Created by vil on 07/03/16.
 */
public class POPServerInterface {
    private int port = 110; //995 for secure connection
    private Socket sc;
    private POPState state;
    private String address;

    public POPServerInterface(String address){
        this.address = address;
    }

    private int getPort(){
        return this.port;
    }

    private POPState getState(){
        return this.state;
    }

    private void setState(POPState newState) {
        this.state = newState;
    }

    public boolean initialize(){
        try {
            sc = new Socket(address, this.getPort());
            this.setState(POPState.INITIALIZATION);
            byte[] message = new byte[256];
            sc.getInputStream().read(message);
            return (eventHandler(new String(message, StandardCharsets.UTF_8)));
        } catch (IOException e) {
            System.out.println("INITIALIZATION FAILED");
            return false;
        }
    }
    
    public boolean connect(String usrName, String usrPass){
        try {
            sc.getInputStream().read();
        } catch (IOException e) {
            System.out.println("CONNECTION FAILED");
            return false;
        }
        return true;
    }

    private boolean eventHandler(String event){
        System.out.println(event);

        if(event.equals("TEST"))
            System.out.println("TEST");
        else if(event.matches("(.*)OK(.*)ready(.*)"))
            System.out.println("Server ready !");
        else if(event.matches("(.*)"))
            System.out.println("ALL I WANT");
        else{
            System.out.println("ERROR");
            return false;
        }
        return true;
    }



}

enum POPState {
    INITIALIZATION,
    CONNECTED,
    USER_WAIT,
    WELCOME_WAIT,
    RETR_WAIT,
    DELE_WAIT
}