package mobile.android.upf.data.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Timestamp;

public class Restaurant {

    private String id;
    private String name, description, email, address, phone, restaurateur_id, imageUrl;
    private int status;
    private String decline_msg;

    public Restaurant() {
    }

//    Costruttore per quando si CREA un ristorante quando si aggiunge
    public Restaurant(String name, String description, String email, String address, String phone,
                      String restaurateur_id, String imageUrl, int status) {
        this.name = name;
        this.description = description;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.restaurateur_id = restaurateur_id;
        this.imageUrl = imageUrl;
        this.status = status;
        long tsLong = System.currentTimeMillis()/1000;
        String ts = Long.toString(tsLong);
        this.id = md5(name+description+email+address+phone+restaurateur_id+ts);
    }

//    Costruttore per quando si LEGGE un ristorante
    public Restaurant(String id, String name, String description, String email, String address, String phone,
                      String restaurateur_id, String imageUrl, int status) {
        this.name = name;
        this.description = description;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.restaurateur_id = restaurateur_id;
        this.imageUrl = imageUrl;
        this.status = status;
        this.id = id;
    }

    public Restaurant(String id, String name, String description, String email, String address, String phone,
                      String restaurateur_id, String imageUrl, int status, String decline_msg) {
        this.name = name;
        this.description = description;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.restaurateur_id = restaurateur_id;
        this.imageUrl = imageUrl;
        this.status = status;
        this.id = id;
        this.decline_msg = decline_msg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRestaurateur_id() {
        return restaurateur_id;
    }

    public void setRestaurateur_id(String restaurateur_id) {
        this.restaurateur_id = restaurateur_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDecline_msg() {
        return decline_msg;
    }

    public void setDecline_msg(String decline_msg) {
        this.decline_msg = decline_msg;
    }

    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
