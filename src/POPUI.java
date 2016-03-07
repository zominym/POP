import java.util.Scanner;

public class POPUI {

	public static void start() {

		Scanner keyboard = new Scanner(System.in);
		int srvPort = 110;
		
		System.out.println("WELCOME TO ZGUYL POP3 MAIL SERVICE");
		System.out.println("PLEASE TYPE SERVER ADDRESS");
		String srvAddress = keyboard.nextLine();
		
		System.out.println("PLEASE TYPE USER NAME");
		String usrName = keyboard.nextLine();
		
		System.out.println("PLEASE TYPE USER PASSWORD");
		String usrPass = keyboard.nextLine();
		
		
		
		
		System.out.println("TRYING TO CONNECT USING :");
		System.out.println(usrName + '@' + srvAddress + ':' + srvPort + " ***" + usrPass + "***");
		
	}

}
