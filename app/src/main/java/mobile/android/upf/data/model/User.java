package mobile.android.upf.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class User {

    public String name, surname, password, email, city, address, phone, imageUrl;
    /**
     * Il type è 1 se cliente, 2 se fattorino, 3 se ristoratore, 4 admin
     */
    public int type, work;

    public String busy;

    public User() {

    }

    public User(String name, String surname, String password, String city, String address,
                String phone, String email, String imageUrl, int type, int work) {
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.city = city;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.type = type;
        this.work = work;
    }

//    Fattorino
    public User(String name, String surname, String password, String city, String address,
                String phone, String email, String imageUrl, int type, int work, String busy) {
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.city = city;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.type = type;
        this.work = work;
        this.busy = busy;
    }

    public int getType() {
        return type;
    }

    public int getWork() {
        return work;
    }

    public void setWork() {
        this.work = work;
    }

    public String isBusy() {
        return busy;
    }

    public void setBusy(String busy) {
        this.busy = busy;
    }
}