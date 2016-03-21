import java.io.*;
import java.util.Scanner;

public class POPUI {

	public static void start() throws IOException {
		
		//Gatean : 134.214.118.131
        //Bruno : 10.42.129.211
        //134.214.119.206

		Scanner keyboard = new Scanner(System.in);
		int srvPort = 110;
		// TODO port parametre

		System.out.println("WELCOME TO ZGUYL POP3 MAIL SERVICE");
		POPServerInterface srv;
		String srvAddress;
		String usrName = "tata";
		String usrPass = "toto";
		
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
		} while (srv.apop(usrName, usrPass) < 0);

		System.out.println("TRYING TO CONNECT USING :");
		System.out.println(usrName + '@' + srvAddress + ':' + srvPort + " ***" + usrPass + "***");
		

        while (srv.retr() > 0){}

		srv.quit();
		
		
		File users = new File("users.txt");
		
		BufferedReader br = new BufferedReader(new FileReader(users));
	    String line;
	    while ((line = br.readLine()) != null) {
	       String[] oneUser = line.split(" ");
	       if (oneUser[0].equals(usrName) && oneUser[1].equals(usrPass))
	       {
	    	   System.out.println("Connect� sur le compte utilisateur " + usrName + " localement.");
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
		do
		{
			System.out.println("Consulter les messages lus(1) ou non-lus(2) ?");
			question = Integer.parseInt(keyboard.nextLine());
		} while ( !(question == 1 || question == 2) );
		
		File mails;
		if (question == 2)
			mails = new File(usrName + "/nonlus");
		else
			mails = new File(usrName + "/lus");
		
		
		File[] mailsLus = mails.listFiles();
		System.out.println("Found ");
    	int index = 0;
	    for (int i = 0; i < mailsLus.length; i++) {
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
	    
	    if (question == 2)
	    { // A CODER -----------------------------------------------------------------------------
	    	System.out.println("MOVING FILES FROM 'nonlus' TO 'lus'");

	    	for (int i = 0; i < mailsLus.length; i++) {
	    	   File afile =new File("C:\\folderA\\Afile.txt");
	    		
	    	   if(afile.renameTo(new File("C:\\folderB\\" + afile.getName()))){
	    		System.out.println("File is moved successful!");
	    	   }else{
	    		System.out.println("File is failed to move!");
	    	   }
	    	    
	    	}
            /*catch(Exception e){
	    		e.printStackTrace();
	    	}*/
	    }

	    keyboard.close();

		
	}

}
