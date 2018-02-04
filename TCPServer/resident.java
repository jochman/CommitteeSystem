package TCPServer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class resident extends user_extension {

    resident(ObjectInputStream inObject, BufferedReader inFromClient, DataOutputStream outToClient, Socket incoming, String username) {
        super(inObject, inFromClient, outToClient, incoming, username);

        /*extracting aptnum*/
        PreparedStatement statement;
        try {
            statement = sql.connect().prepareStatement("SELECT aptnum FROM resident WHERE username = ?");
            statement.setString(1, username);

            ResultSet result = statement.executeQuery();

            result.next();
            this.aptnum = result.getString("aptnum");


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void menu(){
        while(true) try {
            String str = inFromClient.readLine();
            switch (str) {
                case "get_Paid_Months":
                    get_Paid_Months();
                    break;
                case "sendMail":
                    sendMail();
                    break;
                case "quit":
                    return;
                default:
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void sendMail() throws IOException, SQLException {
        String str = inFromClient.readLine();
        PreparedStatement statement = sql.connect().prepareStatement(
                "INSERT INTO mail(sender, msg) VALUES(?, ?)");
        statement.setString(1, username);
        statement.setString(2, str);
        statement.executeUpdate();
        outToClient.writeBytes("ok" + "\n");
    }

    private void get_Paid_Months() throws SQLException, IOException {
        PreparedStatement statement;
        statement = sql.connect().prepareStatement("SELECT * from paidmonth WHERE aptnum = ?");
        statement.setString(1, aptnum);
        ResultSet result = statement.executeQuery();

        String str = "";
        if(result.next()){
            for (int i = 2; i <= 13; ++i)
                if(result.getString(i).equals("1"))
                    str += i + " ";
        }
        if (str.equals("")) str = "none";
        outToClient.writeBytes(str + "\n");
        outToClient.writeBytes("end" + "\n");
    }

    /*vars*/
    String aptnum;
}
