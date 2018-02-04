package TCPServer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class user_extension {

    public user_extension(
            ObjectInputStream inObject, BufferedReader inFromClient,
            DataOutputStream outToClient, Socket incoming, String username) {
        this.inObject = inObject;
        this.inFromClient = inFromClient;
        this.outToClient = outToClient;
        this.incoming = incoming;
        this.username = username;

        PreparedStatement statement;
    }
    public abstract void menu() throws IOException;

    ObjectInputStream inObject;
    BufferedReader inFromClient;
    DataOutputStream outToClient;
    Socket incoming;
    String username;
}
