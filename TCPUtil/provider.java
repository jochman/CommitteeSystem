package TCPUtil;

import java.io.Serializable;

public class provider implements Serializable {

    public provider(String name, String surname, String type, String phone) {
        this.name = name;
        this.surname = surname;
        this.type = type;
        this.phone = phone;
    }
    /*vars*/
    private String name;
    private String surname;
    private String type;
    private String phone;

    /*getters*/
    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getType() {
        return type;
    }

    public String getPhone() {
        return phone;
    }
}
