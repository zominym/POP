import java.awt.*;
import java.io.IOException;



public class POP {
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		POPGUI dialog = new POPGUI();
		dialog.output.setText("Bienvenue sur le serveur mail ZGUYL \n\n Veuillez entrer vos identifiants de connexion");
		dialog.pack();
		dialog.setSize(1200,600);
		dialog.setMinimumSize(new Dimension(600, 500));
		dialog.setResizable(true);
		dialog.setVisible(true);
		System.exit(0);
	}
}

// 134.214.117.89