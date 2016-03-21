import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class POPUI {

	public static void start() throws IOException {
		
		//134.214.118.131

		Scanner keyboard = new Scanner(System.in);
		int srvPort = 110;
		// TODO port parametre

		System.out.println("WELCOME TO ZGUYL POP3 MAIL SERVICE");
		POPServerInterface srv;
		String srvAddress;
		String usrName;
		String usrPass;
		
		boolean error = false;
		
		do
		{
			if (error) 
				System.out.println("ERROR, PLEASE TRY AGAIN");
			
			System.out.println("PLEASE TYPE SERVER ADDRESS");
			srvAddress = keyboard.nextLine();
	
			System.out.println("TRYING TO CONNECT TO : " + srvAddress);
			srv = new POPServerInterface(srvAddress);
			error = true;
		} while (srv.initialize() < 0);
		
		error = false;
		do
		{
			if (error) 
				System.out.println("ERROR, PLEASE TRY AGAIN");
			
			System.out.println("PLEASE TYPE USER NAME");
			usrName = keyboard.nextLine();
			
			System.out.println("PLEASE TYPE USER PASSWORD");
			usrPass = keyboard.nextLine();
			error = true;
		} while (srv.loginAPOP(usrName, usrPass) < 0);

		System.out.println("TRYING TO CONNECT USING :");
		System.out.println(usrName + '@' + srvAddress + ':' + srvPort + " ***" + usrPass + "***");
		

		do
		{

		} while (srv.retr() > 0);
		
		
		File users = new File("users.txt");
		
		BufferedReader br = new BufferedReader(new FileReader(users));
	    String line;
	    while ((line = br.readLine()) != null) {
	       String[] oneUser = line.split(" ");
	       if (oneUser[0].equals(usrName) && oneUser[1].equals(usrPass))
	       {
	    	   System.out.println("Connecté sur le compte utilisateur " + usrName + " localement.");
	    	   break;
	       }
	       else
	       {
	    	   System.out.println("Cet utilisateur n'existe pas localement.");
	    	   return ;
	       }
	    }
	    br.close();
		
		int question = 0;
		String nl = "";
		do
		{
			System.out.println("Consulter les messages lus(1) ou non-lus(2) ?");
			nl = keyboard.nextLine();
			System.out.println(nl);
			question = Integer.getInteger(nl);
		} while ( !(question == 1 || question == 2) );
		
		File lus = new File(usrName + "/lus");
		File[] mailsLus = lus.listFiles();
		System.out.println("Found ");
	    for (int i = 0; i < mailsLus.length; i++) {
	    	int index = 0;
	        if (mailsLus[i].isFile()) {
	        	System.out.println("Mail "+ index + " :  ---------------------------------------");
	        	FileInputStream fis = new FileInputStream(mailsLus[i]);
	        	byte[] data = new byte[(int) mailsLus[i].length()];
	        	fis.read(data);
	        	fis.close();
	        	String str = new String(data, "UTF-8");
	        	System.out.println(str);
	        	System.out.println("End of mail "+ index + " :  ---------------------------------------");
	        	index ++;
	        } else if (mailsLus[i].isDirectory()) {
	        	System.out.println("Directory " + mailsLus[i].getName());
	        }
	    }
	    
	    keyboard.close();
	
		
	}

}
