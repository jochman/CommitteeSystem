package TCPServer;

import TCPUtil.provider;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Vector;

public class committee extends user_extension {

    committee(ObjectInputStream inObject, BufferedReader inFromClient, DataOutputStream outToClient, Socket incoming, String username) {
        super(inObject, inFromClient, outToClient, incoming, username);
    }

    @Override
    public void menu() throws IOException {
        while (true){
            try {
                String str = inFromClient.readLine();
                switch (str) {
                    case "paying status resident":
                        payment_status_resident();
                        break;
                    case "paying status all":
                        payment_status_all();
                        break;
                    case "delete payment":
                        delete_payment();
                        break;
                    case "update payment":
                        update_payment();
                        break;
                    case "display income":
                        display_income();
                        break;
                    case "new building":
                        new_building();
                        break;
                    case "display providers":
                        display_providers();
                        break;
                    case "insert provider":
                        insert_provider();
                        break;
                    case "check mail":
                        mailcheck();
                        break;
                    case "delete mails":
                        delete_mails();
                        break;
                    case "quit":
                        return;

                }
            }catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /*new building handling*/
    private void new_building() throws IOException, SQLException {
        Integer total = Integer.valueOf(inFromClient.readLine());
        delete_resident_data();
        try{
            PreparedStatement createBuildingStatement =
                    sql.connect().prepareStatement(
                            "INSERT INTO resident (name, surname, username, aptnum, monthlypayment, password) " +
                                    "VALUES (?,?,?,?,?,?)");

            createBuildingStatement.setString(1, "defname");
            createBuildingStatement.setString(2,"defsurname");
            createBuildingStatement.setString(5, "1500");
            createBuildingStatement.setString(6, "1234");

            for (int i = 1; i < total +1; ++i){
                createBuildingStatement.setString(3, "resident" + i);
                createBuildingStatement.setString(4, String.valueOf(i));
                createBuildingStatement.executeUpdate();

                /*will initialize paidmonth table*/
                PreparedStatement updatePaidMonth =
                        sql.connect().prepareStatement("INSERT INTO paidmonth (aptnum) VALUES (?)");
                updatePaidMonth.setString(1,String.valueOf(i));
                updatePaidMonth.executeUpdate();
            }


            outToClient.writeBytes("ok" + "\n");
        } catch (SQLException e) {
            e.printStackTrace();
            outToClient.writeBytes("error");
        }
    }

    private void delete_resident_data() throws SQLException {
        PreparedStatement statement =
                sql.connect().prepareStatement("DELETE FROM paidmonth");
        statement.executeUpdate();

        statement =
                sql.connect().prepareStatement("DELETE FROM resident");
        statement.executeUpdate();


    }

    /*payment handling*/
    private void payment_status_all() throws SQLException, IOException {
        PreparedStatement statement = sql.connect().prepareStatement("SELECT * FROM paidmonth");
        ResultSet result = statement.executeQuery();
        while (result.next()){
            StringBuilder str = new StringBuilder("Resident #" + result.getString("aptnum") + " paid for months: ");
            for (int i = 2; i<14; i++){
                if (Integer.parseInt(result.getString(i)) > 0)
                    str.append(i - 1).append(" ");
            }
            outToClient.writeBytes(str.toString() + "\n");
        }
        outToClient.writeBytes("end" + "\n");
    }

    private void payment_status_resident() throws IOException, SQLException {
        String aptnum = inFromClient.readLine();

        /* Checking if correct apartment number*/
        PreparedStatement check_if_exist = sql.connect().prepareStatement("SELECT aptnum FROM resident WHERE aptnum = ?");
        check_if_exist.setString(1, aptnum);
        ResultSet check_result = check_if_exist.executeQuery();

        if (!check_result.next()) {
            outToClient.writeBytes("no such apartment number!" + "\n");
            System.out.println("end!");
            outToClient.writeBytes("end" + "\n");
            return;
        }

        /*getting paid months*/
        PreparedStatement statement = sql.connect().prepareStatement("SELECT * FROM paidmonth WHERE aptnum = ?");
        statement.setString(1, aptnum);
        ResultSet result = statement.executeQuery();

        if (result.next()) {
            StringBuilder answers = new StringBuilder("Resident #" + aptnum + " paid for months "
                    + "\t");
            for (int i = 2; i <= 13; ++i) {
                if (Integer.parseInt(result.getString("m" + (i - 1))) > 0)
                    answers.append(i - 1);
            }
            outToClient.writeBytes(answers.toString() + "\n");
        } else outToClient.writeBytes("No payments detected" + "\n");

        outToClient.writeBytes("end" + "\n");
    }

    private void display_income() throws SQLException, IOException {
        for (Integer i = 2; i < 14; i++) {
            String str = "SELECT SUM(m" + String.valueOf(i-1) + ") FROM paidmonth";
            PreparedStatement statement = sql.connect().prepareStatement(str);
            ResultSet result = statement.executeQuery();
            result.next();
            String sum = result.getString(1);
            outToClient.writeBytes("Total income for month " +  String.valueOf(i-1) + ": " + sum + "\n");
        }
        outToClient.writeBytes("ok" + "\n");
    }

    private void update_payment() throws IOException, SQLException, ClassNotFoundException {
        String str[] = inFromClient.readLine().split(" "); //APTNUM, MONEY, MONTH

        System.out.println("aptnum " + str[0] + "Money " + str[1] + "month " + str[2]);
        PreparedStatement statement = sql.connect().prepareStatement(
                "SELECT * FROM resident WHERE aptnum = ?");
        statement.setString(1, str[0]);
        ResultSet result = statement.executeQuery();

        if (!result.next()) {
            outToClient.writeBytes("No such apartment number!" + "\n");
            outToClient.writeBytes("end" + "\n");
            return;
        }

        str[2] = 'm' + str[2];
        String Qstr = "UPDATE paidmonth SET " + str[2] + "= ";
        PreparedStatement updateStatement =
                sql.connect().prepareStatement(Qstr + "? WHERE aptnum = ?");
        updateStatement.setString(1, str[1]);
        updateStatement.setString(2, str[0]);
        updateStatement.executeUpdate();

        outToClient.writeBytes("Payment updated!" + "\n");
        outToClient.writeBytes("end" + "\n");
    }

    private void delete_payment() throws SQLException, IOException {
        String aptnum = inFromClient.readLine();
        PreparedStatement statement = sql.connect().prepareStatement("SELECT aptnum FROM resident WHERE aptnum = ?");
        statement.setString(1, aptnum);
        ResultSet result = statement.executeQuery();
        if (!result.next()) {
            outToClient.writeBytes("nosuch" + "\n");
            return;
        }
        String month = "m";
        Calendar cal = Calendar.getInstance();
        month += String.valueOf(cal.get(Calendar.MONTH) + 1);
        String str = "UPDATE paidmonth SET ";
        str += month;
        PreparedStatement updatestatement =
                sql.connect().prepareStatement(str + " = 0 WHERE aptnum = ?");
        updatestatement.setString(1, aptnum);
        updatestatement.executeUpdate();
        outToClient.writeBytes("ok" + "\n");
    }

    /* providers handling*/
    private void display_providers() throws IOException, SQLException {
        String kind_of_provider = inFromClient.readLine();
        PreparedStatement statement = sql.connect().prepareStatement("SELECT * FROM supplier WHERE type = ?");
        statement.setString(1, kind_of_provider);
        ResultSet result = statement.executeQuery();

        while (result.next()){
            String str = "";
            /*name, surname, phone*/
            for (int i = 3; i < 6; i++) {
                str += result.getString(i) + "\t";
            }
            outToClient.writeBytes(str + "\n");
        }
        outToClient.writeBytes("end" + "\n");

    }

    private void insert_provider() throws IOException, ClassNotFoundException, SQLException {
        provider prov = (provider) inObject.readObject();   //get provider object

        PreparedStatement statement = sql.connect().prepareStatement("SELECT 1 FROM supplier WHERE name = ? AND surname = ? AND type = ?");
        statement.setString(1, prov.getName());
        statement.setString(2, prov.getSurname());
        statement.setString(3, prov.getType());

        if (statement.executeQuery().next()){
            outToClient.writeBytes("already exist" + "\n");
            return;
        }

        PreparedStatement updateStatement =
                sql.connect().prepareStatement(
                        "INSERT INTO supplier (type, name, surname, phone) VALUES (?,?,?,?)");
        updateStatement.setString(1, prov.getType());
        updateStatement.setString(2, prov.getName());
        updateStatement.setString(3, prov.getSurname());
        updateStatement.setString(4, prov.getPhone());

        updateStatement.executeUpdate();

        outToClient.writeBytes("ok" + "\n");


    }

    /*mail handling*/
    private void mailcheck() throws SQLException, IOException {
        PreparedStatement statement = sql.connect().prepareStatement("SELECT * FROM mail");
        ResultSet result = statement.executeQuery();
        while (result.next()) {
            String str = "Mail from " + result.getString("sender") + ":\t" + result.getString("msg");
            outToClient.writeBytes(str + "\n");
        }
        outToClient.writeBytes("endnewmail" + "\n");

    }

    private void delete_mails() throws SQLException, IOException {
        PreparedStatement statement = sql.connect().prepareStatement("DELETE FROM mail");
        statement.executeUpdate();
        outToClient.writeBytes("ok" + "\n");
    }
}
