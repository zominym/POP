import java.util.*;

/**
 * Created by vil on 02/05/16.
 */
public class ServerReceiver {
    public String address;
    public int port = 25;
    public List<String> receivers;

    public ServerReceiver(String address, int port, List<String> receivers){
        this.address = address;
        this.port = port;
        this.receivers = receivers;
    }
}
