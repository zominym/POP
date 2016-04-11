import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class POPGUI extends JDialog {
    private JPanel contentPane;
    private JTextField userName;
    private JTextField servAddress;
    private JTextField userPass;
    private JLabel output;
    private JButton connectButton;

    public POPGUI() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(connectButton);

        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Button was cllicked !!!!");
                output.setText("Button was clicked");
                System.out.println("Button was cllicked 2 !!!!");
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
            }
        });
    }

    public static void main(String[] args) {
        POPGUI dialog = new POPGUI();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    public void start() throws IOException {

        //Gatean : 134.214.119.107
        //Bruno : 134.214.119.102
        //Sydney : 134.214.119.234
        //Leo : 134.214.119.113

        //Scanner keyboard = new Scanner(System.in);
        int srvPort = 110;
        // TODO port parametre

        output.setText(output.getText() + "\n" + "WELCOME TO ZGUYL POP3 MAIL SERVICE");
        POPServerInterface srv;
        String srvAddress;
        String usrName = "tata";
        String usrPass = "toto";

        boolean error = false;

        /*do
        {*/
            if (error)
                output.setText(output.getText() + "\n" + "ERROR, PLEASE TRY AGAIN");

            output.setText(output.getText() + "\n" + "PLEASE TYPE SERVER ADDRESS");
            //srvAddress = keyboard.nextLine();
            srvAddress = servAddress.getText();

            output.setText(output.getText() + "\n" + "TRYING TO CONNECT TO : " + srvAddress);
            //srv = new POPServerInterface(srvAddress);
            srv = new POPServerInterfaceSecure(srvAddress);
            error = true;
        //} while (srv.initialize() < 0);
        srv.initialize();

        error = false;
        /*do
        {*/
            if (error)
                System.out.println("ERROR, PLEASE TRY AGAIN");

            System.out.println("PLEASE TYPE USER NAME");
            //usrName = keyboard.nextLine();
            usrName = userName.getText();

            System.out.println("PLEASE TYPE USER PASSWORD");
            //usrPass = keyboard.nextLine();
            usrPass = userPass.getText();

            error = true;
        //} while ((srv.user(usrName) < 0) || (srv.pass(usrPass) < 0));
        srv.user(usrName);
        srv.pass(usrPass);


        output.setText(output.getText() + "\n" + "TRYING TO CONNECT USING :");
        output.setText(output.getText() + "\n" + usrName + '@' + srvAddress + ':' + srvPort + " ***" + usrPass + "***");

        if (new File(usrName + "/lus/../nonlus").mkdirs())
            output.setText(output.getText() + "\n" + "Successfully created local directories for new user.");

        /*while (srv.retr() > 0);

        while (srv.dele() > 0);*/

        srv.quit();

        String line;
        output.setText(output.getText() + "\n" + "Connecté sur le compte utilisateur " + usrName + " localement.");

//        int question = 0;
//        do
//        {
//            output.setText(output.getText() + "\n" + "Consulter les messages lus(1) ou non-lus(2) ?");
//            question = Integer.parseInt(keyboard.nextLine());
//        } while ( !(question == 1 || question == 2) );
//
//        File mails;
//        if (question == 2)
//            mails = new File(usrName + "/nonlus");
//        else
//            mails = new File(usrName + "/lus");
//
//
//        File[] mailsLus = mails.listFiles();
//        output.setText(output.getText() + "\n" + "Found ");
//        int index = 0;
//        for (int i = 0; i < mailsLus.length; i++) {
//            if (mailsLus[i].isFile()) {
//                output.setText(output.getText() + "\n" + "Mail "+ index + " :  ---------------------------------------");
//                FileInputStream fis = new FileInputStream(mailsLus[i]);
//                byte[] data = new byte[(int) mailsLus[i].length()];
//                fis.read(data);
//                fis.close();
//                String str = new String(data, "UTF-8");
//                output.setText(output.getText() + "\n" + str);
//                output.setText(output.getText() + "\n" + "End of mail "+ index + " :  ---------------------------------------");
//                index ++;
//            } else if (mailsLus[i].isDirectory()) {
//                output.setText(output.getText() + "\n" + "Directory " + mailsLus[i].getName());
//            }
//        }
//
//        if (question == 2)
//        {
//            output.setText(output.getText() + "\n" + "MOVING FILES FROM 'nonlus' TO 'lus'");
//
//            for (int i = 0; i < mailsLus.length; i++) {
//
//                if(!mailsLus[i].renameTo(new File(usrName + "/lus/" + mailsLus[i].getName()))){
//                    output.setText(output.getText() + "\n" + "Failed to move old mails!");
//                    error = true;
//                }
//            }
//            if (error == false)
//                output.setText(output.getText() + "\n" + "Successfully moved mails from 'nonlus' to 'lus'.");
//        }
//        keyboard.close();
    }
}
