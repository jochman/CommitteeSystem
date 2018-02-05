package TCPClient;

import java.io.IOException;

public class user_extension{

    static void press_enter() throws IOException {
        /* function that will pop a message PRESS ENTER TO CONTINUE */
        System.out.println("Press enter to continue");
        System.in.read();
    }

    public void menu() throws IOException {}

    protected static server_connector s;
}
