import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Created by vil on 07/03/16.
 */
public class POPServerInterface {
    private int port = 110; //995 for secure connection
    private Socket sc;
    private POPState state;

    public POPServerInterface(){
        
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

    public boolean initialize(String address){
        try {
            sc = new Socket(address, this.getPort());
            this.setState(POPState.INITIALIZATION);
            byte[] message = new byte[256];
            sc.getInputStream().read(message);
            eventHandler(new String(message, StandardCharsets.UTF_8));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean connect(String usrName, String usrPass){
    	//TODO
    	return true;
    }

    private void eventHandler(String event){
        if(event == "TEST")
            System.out.println("TEXT");
        else
            System.out.println("ERROR");
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