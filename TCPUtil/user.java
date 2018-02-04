package TCPUtil;

import java.io.IOException;
import java.io.Serializable;

public class user implements Serializable {
    public user(String username, String password){
        this.username = username;
        this.password = password;
    }

    public String get_password(){return this.password;}
    public String get_username(){ return this.username;}
    public void menu() throws IOException {}

    /*vars*/
    private String username;
    private String password;
}