import java.util.*;

/**
 * Created by vil on 02/05/16.
 */
public class ServerReceiver {
    public String address;
    public int port;
    public LinkedList<String> receivers;

    public ServerReceiver(String address, int port, LinkedList<String> receivers){
        this.address = address;
        this.port = port;
        this.receivers = receivers;
    }

    public ServerReceiver (String address, LinkedList<String> receivers){
        this(address, 25, receivers);
    }
}
