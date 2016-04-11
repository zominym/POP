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
    private int nbMails = 0, nbToDele = 0;
    private String user;
    
    private static final int CREDENTIALS = 10;

    /***
     * TODO:Write a description
     * @param address
     */
    POPServerInterface(String address){
        this.address = address;
    }

    /***
     * TODO:Write a description
     * @return
     */
    private int getPort(){
        return 2048; //995 for secure connection

    }

    /***
     * TODO:Write a description
     * @return
     */
    private POPState getState(){
        return this.state;
    }

    /***
     * TODO:Write a description
     * @param newState
     */
    private void setState(POPState newState) {
        this.state = newState;
    }

    /***
     * TODO:Write a description
     * @param toSend
     * @throws IOException
     */
    private void writeStream(String toSend) throws IOException {
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
    private String readStream() throws IOException {
        byte[] receipt = new byte[1024];
        sc.getInputStream().read(receipt);
        String result = new String(receipt, "UTF-8");
    	System.err.println("RECU :"+result);
        return result;
    }

    /***
     * TODO:Write a description
     * @return
     */
    int initialize(){
        try {
            sc = new Socket(address, this.getPort());
            this.setState(POPState.INITIALIZATION);
            return (eventHandler(readStream()));
        } catch (IOException e) { return -1; }
    }

    /***
     * TODO:Write a description
     * @param userName
     * @param userPassword
     * @return
     */
    int apop(String userName, String userPassword){
        this.user = userName;
    	try {
            writeStream("APOP "+userName+" "+userPassword);
			this.setState(POPState.WELCOME_WAIT);
			return (eventHandler(readStream()));
		} catch (IOException e) { return -1; }
    }

    /***
     * TODO:Write a description
     * @param userName
     * @param userPassword
     * @return
     */
    public int login(String userName, String userPassword){
        user(userName);
        return pass(userPassword);
    }

    /***
     * TODO:Write a description
     * @param userName
     * @return
     */
    private int user(String userName){
        this.user = userName;
        try {
            writeStream("USER "+userName);
            return (eventHandler(readStream()));
        } catch (IOException e) { return -1; }
    }

    /***
     * TODO:Write a description
     * @param userPass
     * @return
     */
    private int pass(String userPass){
        try {
            writeStream("PASS "+userPass);
            return (eventHandler(readStream()));
        } catch (IOException e) { return -1; }
    }

    /***
     * TODO:Write a description
     * @return
     */
    int retr(){
        if(nbMails < 1)
            return 0;
        try {
            writeStream("RETR "+nbMails);
            this.setState(POPState.RETR_WAIT);
            nbMails --; nbToDele ++;
            return eventHandler(readStream());
        } catch (IOException e) { return -1; }
    }

    /***
     * TODO:Write a description
     * @return
     */
    int dele(){
        if(nbToDele < 1)
            return 0;
        try {
            writeStream("DELE "+nbToDele);
            this.setState(POPState.DELE_WAIT);
            nbToDele --;
            return eventHandler(readStream());
        } catch (IOException e){ return -1; }
    }

    /***
     * TODO:Write a description
     * @return
     */
    int quit(){
        try { writeStream("QUIT"); return 0; }
        catch (IOException e) { return -1; }
    }


    /***
     * TODO:Write a description
     * @param event
     * @return
     */
    private int eventHandler(String event){
        if(event.split(" ")[0].equals("+OK"))
        	return okHandler(event);
        else if(event.split(" ")[0].equals("-ERR"))
        	return errHandler(event);
        else{
            return -1;
        }
    }


    /***
     * TODO:Write a description
     * @param event
     * @return
     */
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
                return 1;
            case DELE_WAIT:
                return 1;
            default:
			    break;
    	}
    	return -1;
    }
    
    private int errHandler(String event){
        System.err.printf(event);
        return -1;
    }


    /***
     * TODO:Write a description
     * @param mail
     * @throws IOException
     */
    private void writeMail(String mail) throws IOException{
        Pattern p = Pattern.compile("(\\+OK.*\\r\\n)(.*)((\\r\\n).(\\r\\n))", Pattern.DOTALL);
        Matcher m = p.matcher(mail);
        
        int index = mail.indexOf("\r\n");
        
        //System.err.println("INDEX");
        //System.err.println(index);
        mail = mail.substring(index);
        //System.err.println("MAIL");
        //System.err.println(mail);

        String date = new SimpleDateFormat("yyyyMMdd_HHmmssSS").format(Calendar.getInstance().getTime());
        File directory = new File(user+"/nonlus/");
        File directory2 = new File(user+"/lus/");
        File mailfile = new File(user+"/nonlus/"+date+".mail.txt");

        if (!directory.exists())
            directory.mkdir();
        if (!directory2.exists())
            directory2.mkdir();

        if (!mailfile.exists())
            mailfile.createNewFile();

        FileWriter fw = new FileWriter(mailfile.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        
//        if (m.find()){
//        	//for (int i = 0; i < m.groupCount(); i++)
//        		//System.err.println("GR" + i + " "+m.group(i));
//        	bw.write(m.group(2));
//        }
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