/**
 * Created by vil on 02/05/16.
 */
public class ServerReceiver {
    public String address;
    public int port = 25;
    public String[] receivers;

    public ServerReceiver(String address, int port, String[] receivers){
        this.address = address;
        this.port = port;
        this.receivers = receivers;
    }
}
