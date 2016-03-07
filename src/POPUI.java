import java.util.Scanner;

public class POPUI {

	public static void start() {

		Scanner keyboard = new Scanner(System.in);
		int srvPort = 110;
		// TODO port parametre

		System.out.println("WELCOME TO ZGUYL POP3 MAIL SERVICE");
		POPServerInterface srv;
		String srvAddress;
		String usrName;
		String usrPass;
		
		
		do
		{
			System.out.println("PLEASE TYPE SERVER ADDRESS");
			srvAddress = keyboard.nextLine();
	
			System.out.println("TRYING TO : " + srvAddress);
			srv = new POPServerInterface();
		} while (!srv.initialize(srvAddress));
		
		do
		{
		System.out.println("PLEASE TYPE USER NAME");
		usrName = keyboard.nextLine();
		
		System.out.println("PLEASE TYPE USER PASSWORD");
		usrPass = keyboard.nextLine();
		} while (!srv.connect(usrName, usrPass));
		
		
		
		
		System.out.println("TRYING TO CONNECT USING :");
		System.out.println(usrName + '@' + srvAddress + ':' + srvPort + " ***" + usrPass + "***");
		
		
		
		
	}

}
