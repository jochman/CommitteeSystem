package TCPServer;

import TCPUtil.user;

import java.io.*;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class socketHandler extends Thread {
    socketHandler(Socket incoming) {
        this.incoming = incoming;
    }

    public void run() {
        try {
            inFromClient =
                    new BufferedReader(new
                            InputStreamReader(incoming.getInputStream()));
            outToClient =
                    new DataOutputStream (incoming.getOutputStream() );
            inObject =
                    new ObjectInputStream(incoming.getInputStream());
        }catch (IOException e){
            e.printStackTrace();
        }

        try {
            resident_committee();
            /*changing or connecting*/
            String str = inFromClient.readLine();
            user User = (user) inObject.readObject();
            if (str.toLowerCase().equals("connect")){ //connecting execution
                if (checkPassword(User)){
                    outToClient.writeBytes("ok" + "\n");}
                else outToClient.writeBytes("wrong" + "\n");
            }
            else if (str.toLowerCase().equals("change")) //changing password execution
                if (changePassword(User))
                    outToClient.writeBytes("ok" + "\n");
                else {
                    outToClient.writeBytes("wrong" + "\n");
                 return;
                }

                if (type.equals("committee"))
                    usere = new committee(inObject, inFromClient, outToClient, incoming, User.get_username());
                else if (type.equals("resident"))
                    usere = new resident(inObject, inFromClient, outToClient, incoming, User.get_username());

                usere.menu(); /*while true inside*/
        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }

    private boolean changePassword(user User) throws SQLException, IOException {
        PreparedStatement statement;

                if (type.equals("committee"))
                    statement = sql.connect().prepareStatement("SELECT username FROM committee WHERE username = ?");
                else
                    statement = sql.connect().prepareStatement("SELECT username FROM resident WHERE username = ?");
        statement.setString(1,User.get_username());

        ResultSet result = statement.executeQuery();
        if (result.next()) {
            if (type.equals("committee"))
                statement = sql.connect().prepareStatement("UPDATE committee SET password = ? WHERE username = ?");
            else statement = sql.connect().prepareStatement("UPDATE resident SET password = ? WHERE username = ?");

            statement.setString(1,User.get_password());
            statement.setString(2,User.get_username());
            statement.executeUpdate();
            return true;
        }
        outToClient.writeBytes("wrong" + "\n");
        return false;
    }

    private boolean checkPassword(user User) throws SQLException {
        PreparedStatement statement;
        if (type.equals("committee"))
        statement = sql.connect().prepareStatement(
                "SELECT username FROM committee WHERE username = ? AND password = ?");
        else statement = sql.connect().prepareStatement(
                "SELECT username FROM resident WHERE username = ? AND password = ?");

        statement.setString(1, User.get_username());
        statement.setString(2, User.get_password());
        ResultSet result = statement.executeQuery();

        if (result.next())
            return true;
        return false;

    }

    private void resident_committee() throws IOException {
        /*resident or committee*/
        type = inFromClient.readLine();
    }

    String type;
    ObjectInputStream inObject;
    BufferedReader inFromClient;
    DataOutputStream outToClient;
    Socket incoming;
    user_extension usere;
}
