import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class POPGUI extends JDialog {
    private JPanel contentPane;
    private JTextField userName;
    private JTextField servAddress;
    private JTextField userPass;
    public JLabel output;
    private JButton connectButton;
    private JButton readOldsButton;
    private JButton readNewsButton;
    private JButton sendButton;
    private JTextArea mailSender;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;

    private String usrName = "";
    private String usrPass = "";
    private File mails;

    public POPGUI() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(connectButton);
        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    start();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                // TODO what to do when cross is clicked ???
                System.exit(0);
            }
        });

        readOldsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                readOldMails();
            }
        });

        readNewsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                readNewMails();
            }
        });
    }

    /*public static void main(String[] args) {
        POPGUI dialog = new POPGUI();
        dialog.output.setText("<html>Bienvenue sur le serveur mail ZGUYL <br><br> Veuillez entrer vos identifiants de connexion");
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }*/

    public void readOldMails() {
        usrName = userName.getText();
        mails = new File(usrName + "/lus");

        try {
            if (readMails() != 0)
                return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readNewMails() {
        usrName = userName.getText();
        mails = new File(usrName + "/nonlus");

        try {
            if (readMails() != 0)
                return;
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean error = false;

        //output.setText(output.getText() + "\n" + "MOVING FILES FROM 'nonlus' TO 'lus'");

        File[] mailsLus = mails.listFiles();

        for (int i = 0; i < mailsLus.length; i++) {

            if(!mailsLus[i].renameTo(new File(usrName + "/lus/" + mailsLus[i].getName()))){
                //output.setText(output.getText() + "\n" + "Failed to move old mails!");
                error = true;
            }
        }
        //if (error == false)
            //output.setText(output.getText() + "\n" + "Successfully moved mails from 'nonlus' to 'lus'.");
    }

    public int readMails() throws IOException {
        File[] mailsLus = mails.listFiles();
        output.setText("<html>");
        int index = 0;
        if (mailsLus == null)
        {
            output.setText(output.getText() + "<br>" + "Erreur, veuillez entrer un nom d'utilisateur avant de consulter vos mails.");
            return -1;
        }
        if (mailsLus.length <= 0)
        {
            output.setText(output.getText() + "<br>" + "Aucun mails à afficher.");
            return -1;
        }
        for (int i = 0; i < mailsLus.length; i++) {
            if (mailsLus[i].isFile()) {
                output.setText(output.getText() + "<br>" + "Mail "+ index + " :  ---------------------------------------");
                FileInputStream fis = new FileInputStream(mailsLus[i]);
                byte[] data = new byte[(int) mailsLus[i].length()];
                fis.read(data);
                fis.close();
                String str = (new String(data, "UTF-8")).replace("\n","<br>");
                output.setText(output.getText() + "<br>" + str);
                output.setText(output.getText() + "<br>" + "FIN DU MAIL "+ index + " :  ---------------------------------------");
                index ++;
            } else if (mailsLus[i].isDirectory()) {
                output.setText(output.getText() + "<br>" + "REPERTOIRE " + mailsLus[i].getName());
            }
        }
        return 0;
    }

    public void start() throws IOException {

        //Gatean : 134.214.119.107
        //Bruno : 134.214.119.102
        //Sydney : 134.214.119.234
        //Leo : 134.214.119.113

        //Scanner keyboard = new Scanner(System.in);
        int srvPort = 110;
        // TODO port parametre

        //output.setText(output.getText() + "\n" + "WELCOME TO ZGUYL POP3 MAIL SERVICE");
        POPServerInterface srv;
        String srvAddress = "";

        boolean error = false;

        do
        {
            if (error) {
                output.setText("Impossible de se connecter au serveur "+srvAddress);
                return;
            }

            //output.setText(output.getText() + "<br>" + "PLEASE TYPE SERVER ADDRESS");
            //srvAddress = keyboard.nextLine();
            srvAddress = servAddress.getText();

            //output.setText(output.getText() + "<br>" + "TRYING TO CONNECT TO : " + srvAddress);
            //srv = new POPServerInterface(srvAddress);
            srv = new POPServerInterfaceSecure(srvAddress);
            error = true;
        } while (srv.initialize() < 0);

        error = false;
        do
        {
            if (error) {
                output.setText("Nom d'utilisateur ou mot de passe incorrect");
                return;
            }

            //output.setText(output.getText() + "PLEASE TYPE USER NAME");
            //usrName = keyboard.nextLine();
            usrName = userName.getText();

            //output.setText(output.getText() + "PLEASE TYPE USER PASSWORD");
            //usrPass = keyboard.nextLine();
            usrPass = userPass.getText();

            error = true;
        } while ((srv.user(usrName) < 0) || (srv.pass(usrPass) < 0));


        //output.setText(output.getText() + "\n" + "TRYING TO CONNECT USING :");
        //output.setText(output.getText() + "\n" + usrName + '@' + srvAddress + ':' + srvPort + " ***" + usrPass + "***");

        new File(usrName + "/lus/../nonlus").mkdirs();
            //output.setText(output.getText() + "\n" + "Successfully created local directories for new user.");

        while (srv.retr() > 0);

        while (srv.dele() > 0);

        srv.quit();

        String line;
        //output.setText(output.getText() + "\n" + "Connecté sur le compte utilisateur " + usrName + " localement.");
    }
}
