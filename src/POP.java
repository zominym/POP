import java.io.IOException;



public class POP {
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		POPGUI dialog = new POPGUI();
		dialog.output.setText("<html>Bienvenue sur le serveur mail ZGUYL <br><br> Veuillez entrer vos identifiants de connexion");
		dialog.pack();
		dialog.setSize(800,600);
		dialog.setVisible(true);
		System.exit(0);
	}
}

// 134.214.117.89