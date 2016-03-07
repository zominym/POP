import java.io.IOException;
import java.net.Socket;

/**
 * Created by vil on 07/03/16.
 */
public class POPServerInterface {
    private String address, userName, userPassword;
    private int port = 110; //995 for secure connection
    private Socket sc;
    private POPState state;

    public POPServerInterface(String address, String userName, String userPassword){
        this.address = address;
        this.userName = userName;
        this.userPassword = userPassword;

    }

    private String getAddress(){
        return this.address;
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

    private void initialize(){
        try {
            sc = new Socket(this.getAddress(), this.getPort());
            this.setState(POPState.INITIALIZATION);
        } catch (IOException e) {
            e.printStackTrace();
        }
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