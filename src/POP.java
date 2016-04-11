import java.io.IOException;



public class POP {
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		POPGUI dialog = new POPGUI();
		dialog.output.setText("<html>WELCOME TO ZGUYL POP3 MAIL SERVICE <br><br><br><br><br> PLEASE ENTER YOUR CREDENTIALS AND PRESS CONNECT");
		dialog.pack();
		dialog.setVisible(true);
		System.exit(0);
	}
}

// 134.214.117.89