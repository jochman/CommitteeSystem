package TCPServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class main {

    public static class mainServer {
        public static void main(String[] args) {
            ServerSocket s = null;
            sql data;
            try {
                s = new ServerSocket(10001);
                data = new sql();

            } catch (IOException e) {
                System.out.println(e);
                System.exit(1);
            }

            while (true) {
                Socket incoming = null;
                try {
                    incoming = s.accept();

                } catch (IOException e) {
                    System.out.println(e);
                    continue;
                }

                System.out.println(incoming);
                new socketHandler(incoming).start();

            }
        }
    }
}
