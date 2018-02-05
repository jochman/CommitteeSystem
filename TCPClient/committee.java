package TCPClient;

import TCPUtil.provider;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;

public class committee extends user_extension {

    public void menu() throws IOException {
        Scanner scan = new Scanner(System.in);

        checkmail(); //making new mails pop at connection

        while (true) {
            System.out.println("-------------------" + "\n"
                    +"\tCommittee menu" + "\n"
                    +"-------------------" + "\n"
                    + "1. Show paying status of a resident" + "\n"
                    + "2. Show paying status of all residents" + "\n"
                    + "3. Create a new building" + "\n"
                    + "4. Update paying status" + "\n"
                    + "5. Delete this month payment" + "\n"
                    + "6. Display income by months" + "\n"
                    + "7. Display providers by specialty" + "\n"
                    + "8. Insert a new provider" + "\n"
                    + "9. Check mail" + "\n"
                    + "10. Delete mails" + "\n"
                    + "11. Quit");

            String str = scan.nextLine();
            switch (str){ //switch case for the menu
                case "1":
                    paying_status_resident();
                    break;
                case "2":
                    paying_status_all();
                    break;
                case "3":
                    new_building();
                    break;
                case "4":
                    update_paying_status();
                    break;
                case "5":
                    delete_paying_status();
                    break;
                case "6":
                    display_income();
                    break;
                case "7":
                    display_providers();
                    break;
                case "8":
                    insert_provider();
                    break;
                case  "9":
                    checkmail();
                    break;
                case "10":
                    delete_mails();
                    break;
                case "11":
                    server_connector.OutToServer().writeBytes("quit" + "\n");
                    return;
                default: break;
            }
        }
    }
    /*all functions start with sending a command string to the server.
    Than the server will proccess the request and return a full string answers
     */
    private void delete_mails() throws IOException {
        server_connector.OutToServer().writeBytes("delete mails" + "\n");
        String str = server_connector.InFromServer().readLine();
        System.out.println(str);
        if (str.equals("ok"))
            System.out.println("mails deleted!");
        else System.out.println("something went wrong");
        press_enter();
    }

    private void checkmail() throws IOException {
        server_connector.OutToServer().writeBytes("check mail" + "\n");
        String str = server_connector.InFromServer().readLine();
        if (str.equals("endnewmail")){
            System.out.println("No new mails");
            press_enter();
            return;}

        while(!str.equals("endnewmail")){
            System.out.println(str + "\n");
            str = server_connector.InFromServer().readLine();
        }
        press_enter();
    }

    private void paying_status_all() throws IOException {
        server_connector.OutToServer().writeBytes("paying status all" + "\n");
        System.out.print("All paying status: ");

        String server_line = server_connector.InFromServer().readLine();
        while(!server_line.equals("end")){
            System.out.println(server_line);
            server_line = server_connector.InFromServer().readLine();
        }
        press_enter();
    }

    private void paying_status_resident() throws IOException {
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter the apartment number");
        String aptNum = scan.nextLine();
        server_connector.OutToServer().writeBytes("paying status resident" + "\n");
        server_connector.OutToServer().writeBytes(aptNum + "\n");

        String server_line = server_connector.InFromServer().readLine();
        while (!server_line.equals("end")) {
            System.out.println(server_line);
            server_line = server_connector.InFromServer().readLine();
        }
        press_enter();
    }

    private void new_building() throws IOException {
        Scanner scan = new Scanner(System.in);
        System.out.println("Welcome to new building wizard!" + "\n");
        System.out.println("default username: resident[APTNUMBER] | default password \'1234\'");
        System.out.println("Please tell your residents to change their password!" + "\n");

        Boolean bool = true;
        while (bool) {
            System.out.println("This action will delete all of your database. +\n" +
                    "Are you sure you want to continue? Y\\N");
            switch (scan.nextLine().toLowerCase()){
                case "n":
                    System.out.println("ACTION TERMINATED");
                    bool = false;
                    break;
                case "y":
                    bool = false;
                    break;
                default:
                    break;
            }
        }
        server_connector.OutToServer().writeBytes("new building" + "\n");
        System.out.println("Enter apartment count: ");
        server_connector.OutToServer().writeBytes(scan.nextLine() + "\n");

        if (server_connector.InFromServer().readLine().equals("ok")){
            System.out.println("SUCCESS");
        }else System.out.println("something went wrong");
        press_enter();
    }

    private void update_paying_status() throws IOException {
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter apartment number");
        String aptnum = scan.nextLine();
        System.out.println("How much money have paid?");
        String money = scan.nextLine();
        Boolean bool = true;
        String month = null;
        while (bool) {
            System.out.println("for which month? 1-12");
            month = String.valueOf(Integer.parseInt(scan.nextLine()));
            if (Integer.parseInt(month) > 0 && Integer.parseInt(month) < 13)
                bool = false;
            else System.out.println("wrong input");
        }
        String str = aptnum + " " + money + " " + month;

        server_connector.OutToServer().writeBytes("update payment" + "\n");
        server_connector.OutToServer().writeBytes(str + "\n");

        String serverMsg = server_connector.InFromServer().readLine();
        while (!serverMsg.equals("end")) {
            System.out.println(serverMsg);
            serverMsg = server_connector.InFromServer().readLine();
        }
        press_enter();
    }

    private void delete_paying_status() throws IOException {
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter apartment number to delete payment:");
        String apt_num = scan.nextLine();
        server_connector.OutToServer().writeBytes("delete payment" + "\n");
        server_connector.OutToServer().writeBytes(apt_num + "\n");
        if (server_connector.InFromServer().readLine().toLowerCase().equals("ok"))
            System.out.println("Payment for resident #" + apt_num + " has been deleted");
        else System.out.println("No such resident or no payment , going to menu");
        press_enter();
    }

    private void display_income() throws IOException {
        String str;
        System.out.println("All incoming:");
        server_connector.OutToServer().writeBytes("display income" + "\n");
        str = server_connector.InFromServer().readLine();
        while (!str.equals("ok")){
            System.out.println(str);
            str = server_connector.InFromServer().readLine();
        }
        press_enter();
}

    private void display_providers() throws IOException {
            server_connector.OutToServer().writeBytes("display providers" + "\n");   //server command
            String choice = choose_provider();                          //getting the provider type
            server_connector.OutToServer().writeBytes(choice + "\n");                //sending provider type to server

            System.out.println("List of all " + choice + " providers:");
            System.out.println("Name\tSurname\tPhone");

            String str = server_connector.InFromServer().readLine();
            while (!str.toLowerCase().equals("end")) {
                System.out.println(str);
                str = server_connector.InFromServer().readLine();
            }
            press_enter();
        }

    private void insert_provider() throws IOException {
        Scanner scan = new Scanner(System.in);
        server_connector.OutToServer().writeBytes("insert provider" + "\n");
        String type = choose_provider() + " ";

        System.out.println("Please enter provider first name:");
        String name = scan.nextLine() + " ";

        System.out.println("Please enter provider surname:");
        String surname = scan.nextLine() + " ";

        System.out.println("Please insert provider\'s phone number");
        String phone = scan.nextLine();

        provider prov = new provider(name, surname, type, phone);
        server_connector.OutToServerObject().writeObject(prov);
        if (server_connector.InFromServer().readLine().equals("ok"))
            System.out.println("Provider has been added successfully");
        else System.out.println("provider already exists");
        press_enter();
    }

    private String choose_provider() {
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.out.println(
                    "Please insert provider's type to display" + "\n" +
                            "1. General" + "\n" +
                            "2. Plumbers" + "\n" +
                            "3. Electricity");
            String choice = scan.nextLine();
            switch (choice) {
                case "1":
                    return "general";
                case "2":
                    return "plumbers";
                case "3":
                    return "electricity";
                default:
                    break;
            }
        }
    }

}
