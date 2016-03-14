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
		
		
		
		
	}

}
