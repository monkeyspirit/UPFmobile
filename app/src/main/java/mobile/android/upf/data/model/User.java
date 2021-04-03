package mobile.android.upf.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class User {

    //private String userId;

    public String name, surname, password, email, address, phone, imageUrl;
    /**
     * Il type è 1 se cliente, 2 se fattorino, 3 se ristoratore, 4 admin
     */
    public int type;

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
}