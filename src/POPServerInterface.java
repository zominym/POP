import java.net.Socket;

/**
 * Created by vil on 07/03/16.
 */
public class POPServerInterface {
    private String address, userName, userPassword;
    private int port = 110; //995 for secure connection
    private Socket sc;

    public POPServerInterface(String address, String userName, String userPassword){
        this.address = address;
        this.userName = userName;
        this.userPassword = userPassword;

    }
}
