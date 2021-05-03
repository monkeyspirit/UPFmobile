package mobile.android.upf.data.model;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Dish {

    private String id;
    private String name, description, restaurant_id;
    private double price;
    private int number;

    public Dish() {
    }

    public Dish(String name, String description, String restaurant_id, double price) {
        this.name = name;
        this.description = description;
        this.restaurant_id = restaurant_id;
        this.price = price;
        long tsLong = System.currentTimeMillis()/1000;
        String ts = Long.toString(tsLong);
        this.id = md5(name+description+restaurant_id+price+ts);
    }

    public Dish(String id, String name, String description, String restaurant_id, double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.restaurant_id = restaurant_id;
        this.price = price;
    }

    public Dish(String id, String name, String description, String restaurant_id, double price, int number) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.restaurant_id = restaurant_id;
        this.price = price;
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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
