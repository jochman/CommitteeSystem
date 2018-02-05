package TCPClient;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class resident extends user_extension {

    public void menu() throws IOException {
        Scanner scan = new Scanner(System.in);

        while (true){
            System.out.println("-------------------" + "\n"
                    +"\tResident menu" + "\n"
                    +"-------------------" + "\n"
                    + "1. Check what you've paid" + "\n"
                    + "2. send eMail to committee" + "\n"
                    + "3. Quit");
            String choice;
            try { choice = scan.nextLine();
                switch (choice){
                    case "1":
                        check_what_paid();
                        break;
                    case "2":
                        sendMail();
                        break;
                    case "3":
                        server_connector.OutToServer().writeBytes("quit" + "\n");
                        return;
                    default: break;
                }
            } catch(NoSuchElementException e){e.getStackTrace();}
        }
    }

    private void check_what_paid() throws IOException {
        String str;
        server_connector.OutToServer().writeBytes("get_Paid_Months" + "\n");
        str = server_connector.InFromServer().readLine();
        System.out.println("You've paid for months: " + str);
        press_enter();
    }

    private void sendMail() throws IOException {
        Scanner scan = new Scanner(System.in);

        System.out.println("Please enter your message: ");
        String str = scan.nextLine();

        server_connector.OutToServer().writeBytes("sendMail" + "\n");
        server_connector.OutToServer().writeBytes(str + "\n");

        System.out.println("Message sent!");
        press_enter();
    }
}
