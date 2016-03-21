import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by vil on 07/03/16.
 */
class POPServerInterface {
    private Socket sc;
    private POPState state;
    private String address;
    private int nbMails;
    private String user;
    
    private static final int CREDENTIALS = 10;

    POPServerInterface(String address){
        this.address = address;
    }

    private int getPort(){
        return 2048;//110; //995 for secure connection

    }

    private POPState getState(){
        return this.state;
    }

    private void setState(POPState newState) {
        this.state = newState;
    }

    private void writeStream(String toSend) throws IOException {
        toSend += "\n\r";
        byte[] bytesToSend = toSend.getBytes();
        sc.getOutputStream().write(bytesToSend);
        sc.getOutputStream().flush();
    }

    private String readStream() throws IOException {
        byte[] receipt = new byte[1024];
        String result = "";
        sc.getInputStream().read(receipt);
        result += new String(receipt, "UTF-8");
        return result;
    }

    int initialize(){
        try {
            sc = new Socket(address, this.getPort());
            this.setState(POPState.INITIALIZATION);
            return (eventHandler(readStream()));
        } catch (IOException e) { return -1; }
    }

    int apop(String userName, String userPassword){
        this.user = userName;
    	try {
            writeStream("APOP "+userName+" "+userPassword);
			this.setState(POPState.WELCOME_WAIT);
			return (eventHandler(readStream()));
		} catch (IOException e) { return -1; }
    }

    public int login(String userName, String userPassword){
        user(userName);
        return pass(userPassword);
    }

    private int user(String userName){
        this.user = userName;
        try {
            writeStream("USER "+userName);
            return (eventHandler(readStream()));
        } catch (IOException e) { return -1; }
    }

    private int pass(String userPass){
        try {
            writeStream("PASS "+userPass);
            return (eventHandler(readStream()));
        } catch (IOException e) { return -1; }
    }

    int retr(){
        int resultRetr, resultDele;
        try {
            writeStream("RETR "+(nbMails));
            this.setState(POPState.RETR_WAIT);
            resultRetr = eventHandler(readStream());
            resultDele = dele();
            nbMails --;
            return resultDele*resultRetr;
        } catch (IOException e) { return -1; }
    }

    private int dele(){
        try {
            writeStream("DELE "+nbMails);
            this.setState(POPState.DELE_WAIT);
            return eventHandler(readStream());
        } catch (IOException e){ return -1; }
    }

    int quit(){
        try { writeStream("QUIT"); return 0; }
        catch (IOException e) { return -1; }
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
                    nbMails = Integer.parseInt(m.group(2));
    			System.out.println("MAILDROP HAS " + nbMails + " MESSAGES.");
    			return 0;
            case RETR_WAIT:
                try { writeMail(event); } catch (IOException e) { return -1; }
                return nbMails;
            default:
			    break;
    	}
    	return -1;
    }
    
    private int errHandler(String event){
        System.out.printf(event);
        return -1;
    }

    private void writeMail(String mail) throws IOException{
        String date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        File directory = new File(user);
        File mailfile = new File(user+"/nonlus/"+date+".mail");

        if (!directory.exists())
            directory.mkdir();

        if (!mailfile.exists())
            mailfile.createNewFile();

        FileWriter fw = new FileWriter(mailfile.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(mail);
        bw.close();
    }

}

enum POPState {
    INITIALIZATION,
    CONNECTED,
    WELCOME_WAIT,
    RETR_WAIT,
    DELE_WAIT
}