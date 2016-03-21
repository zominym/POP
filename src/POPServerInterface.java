import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by vil on 07/03/16.
 */
public class POPServerInterface {
    private int port = 110; //995 for secure connection
    private Socket sc;
    private POPState state;
    private String address;
    private int nbMails;
    
    public static final int CREDENTIALS = 10;

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

    public int initialize(){
        try {
            sc = new Socket(address, this.getPort());
            this.setState(POPState.INITIALIZATION);
            byte[] message = new byte[8000];
            sc.getInputStream().read(message, 0, 8000);
            return (eventHandler(new String(message, StandardCharsets.UTF_8)));
        } catch (IOException e) {
            return -1;
        }
    }

    public int loginAPOP(String userName, String userPassword){
    	String strToSend = "APOP "+userName+" "+userPassword+"\n\r";
    	byte[] byteToSend = strToSend.getBytes();
    	try {
			sc.getOutputStream().write(byteToSend);
			sc.getOutputStream().flush();
			
			this.setState(POPState.WELCOME_WAIT);
            byte[] message = new byte[8000];
            sc.getInputStream().read(message);
			return (eventHandler(new String(message, StandardCharsets.UTF_8)));
		} catch (IOException e) {
			return -1;	
		}
    }

    public int retr(){
        String strToSend = "RETR "+(nbMails - 1)+"\n\r";
        byte[] byteToSend = strToSend.getBytes();
        try {
            sc.getOutputStream().write(byteToSend);
            sc.getOutputStream().flush();

            this.setState(POPState.RETR_WAIT);
            byte[] message = new byte[8000];
            sc.getInputStream().read(message);
            nbMails --;
            return (eventHandler(new String(message, StandardCharsets.UTF_8)));
        } catch (IOException e) {
            return -1;
        }
    }
    
    private int eventHandler(String event){
    	System.out.println(event);
        if(event.split(" ")[0].equals("+OK"))
        	return okHandler(event);
        else if(event.split(" ")[0].equals("-ERR"))
        	return errHandler(event);
        else{
            return -1;
        }
    }
    
    private int okHandler(String event){
        Pattern p = Pattern.compile("(.*)has (.*) m(.*)");
        Matcher m = p.matcher(event);
    	switch(this.getState())
    	{
    		case INITIALIZATION :
    			this.setState(POPState.CONNECTED);
    			return CREDENTIALS;
    		case WELCOME_WAIT :
                if(m.find())
                    nbMails = Integer.parseInt(m.group(3));
    			System.out.println("MAILDROP HAS " + nbMails + "MESSAGES.");
    			return 0;
            case RETR_WAIT:
                System.out.println(event); return nbMails;
            default:
			    break;
    	}
    	return -1;
    }
    
    private int errHandler(String event){
    	
    	return -1;
    }



}

enum POPState {
    INITIALIZATION,
    CONNECTED,
    WELCOME_WAIT,
    RETR_WAIT,
    DELE_WAIT
}