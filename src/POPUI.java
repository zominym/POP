import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class POPUI {

	public static void start() {
		
		//134.214.119.107

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
		
		
		File users = new File("users.txt");
		
		try (BufferedReader br = new BufferedReader(new FileReader(users))) {
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
		    	   System.out.println("Erreur, cet utilisateur n'existe pas localement.");
		    	   return ;
		       }
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		File folder = new File("your/path");
		File[] listOfFiles = folder.listFiles();

		    for (int i = 0; i < listOfFiles.length; i++) {
		      if (listOfFiles[i].isFile()) {
		        System.out.println("File " + listOfFiles[i].getName());
		      } else if (listOfFiles[i].isDirectory()) {
		        System.out.println("Directory " + listOfFiles[i].getName());
		      }
		    }
		
		
	}

}
