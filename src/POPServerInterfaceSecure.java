import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by vil on 04/04/16.
 */
public class POPServerInterfaceSecure extends POPServerInterface {

    /***
     * TODO:Write a description
     *
     * @param address
     */

    private SocketFactory sscf;
    private SSLSocket sc;

    POPServerInterfaceSecure(String address) {
        super(address);
    }

    @Override
    int initialize(){
        sscf = SSLSocketFactory.getDefault();
        try {
            sc = (SSLSocket)sscf.createSocket(address, this.getPort());
            this.setState(POPState.INITIALIZATION);

            String[] supportedCipherSuites = sc.getSupportedCipherSuites();

            ArrayList<String> myCipherSuites = new ArrayList<String>();
            for (String line : supportedCipherSuites) {
                if(line.contains("anon"))
                    myCipherSuites.add(line);
            }

            sc.setEnabledCipherSuites(myCipherSuites.toArray(new String[myCipherSuites.size()]));

            return (eventHandler(readStream()));
        } catch (IOException e) {
            return -1;
        }
    }

    protected int getPort() {
        return 2048;
    }

    protected void writeStream(String toSend) throws IOException {
        System.err.println("ENVOI :"+toSend);
        toSend += "\r\n";
        byte[] bytesToSend = toSend.getBytes();
        sc.getOutputStream().write(bytesToSend);
        sc.getOutputStream().flush();
    }

    /***
     * TODO:Write a description
     * @return
     * @throws IOException
     */
    protected String readStream() throws IOException {
        byte[] receipt = new byte[1024];
        sc.getInputStream().read(receipt);
        String result = new String(receipt, "UTF-8");
        System.err.println("RECU :"+result);
        return result;
    }
}
