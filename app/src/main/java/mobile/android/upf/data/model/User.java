package mobile.android.upf.data.model;

import java.util.ArrayList;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class User {

    public String name, surname, password, email, address, phone, imageUrl;
    /**
     * Il type è 1 se cliente, 2 se fattorino, 3 se ristoratore, 4 admin
     */
    public int type;

    public String busy;

    public User() {

    }

    public User(String name, String surname, String password, String address, String phone, String email, String imageUrl, int type) {
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.type = type;
    }

//    Fattorino
    public User(String name, String surname, String password, String address, String phone, String email, String imageUrl, int type, String busy) {
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.type = type;
        this.busy = busy;
    }

    public String isBusy() {
        return busy;
    }

    public void setBusy(String busy) {
        this.busy = busy;
    }
}