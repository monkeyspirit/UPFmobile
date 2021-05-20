package mobile.android.upf.data.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Order {

    private String id;
    private String user_id;
    private String restaurant_id;
    private String restaurant_name;
    private String delivery_id;
    private ArrayList<Dish> dishes;
    private String total;
    private String paymemt_method;
    private String dishes_summary;
    private String address;
    private String date;
    private String time;
    private int state;

    public Order() {
    }

    public Order(String id, String user_id, String restaurant_id, String restaurant_name, String delivery_id, String dishes_summary, String total, String paymemt_method, String address, String date, String time, int state) {
        this.id = id;
        this.user_id = user_id;
        this.restaurant_id = restaurant_id;
        this.restaurant_name = restaurant_name;
        this.delivery_id = delivery_id;
        this.dishes_summary = dishes_summary;
        this.total = total;
        this.paymemt_method = paymemt_method;
        this.address = address;
        this.date = date;
        this.time = time;
        this.state = state;
    }

    public Order(String id, String user_id, String restaurant_id, String restaurant_name, String dishes_summary, String total, String paymemt_method, String address, String date, String time, int state) {
        this.id = id;
        this.user_id = user_id;
        this.restaurant_id = restaurant_id;
        this.restaurant_name = restaurant_name;
        this.dishes_summary = dishes_summary;
        this.total = total;
        this.paymemt_method = paymemt_method;
        this.address = address;
        this.date = date;
        this.time = time;
        this.state = state;
    }


    public Order(String user_id, String restaurant_id, String restaurant_name, String delivery_id, ArrayList<Dish> dishes, String dishes_summary, String total, String paymemt_method, String address, String date, String time, int state) {
        this.user_id = user_id;
        this.restaurant_id = restaurant_id;
        this.restaurant_name = restaurant_name;
        this.delivery_id = delivery_id;
        this.dishes = dishes;
        this.dishes_summary = dishes_summary;
        this.total = total;
        this.paymemt_method = paymemt_method;
        this.address = address;
        this.date = date;
        this.time = time;
        this.state = state;
        long tsLong = System.currentTimeMillis()/1000;
        String ts = Long.toString(tsLong);
        this.id = md5(user_id+restaurant_id+ts+dishes+dishes_summary+total+paymemt_method+address+date+time+state);
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public String getDelivery_id() {
        return delivery_id;
    }

    public ArrayList<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(ArrayList<Dish> dishes) {
        this.dishes = dishes;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getPaymemt_method() {
        return paymemt_method;
    }

    public void setPaymemt_method(String paymemt_method) {
        this.paymemt_method = paymemt_method;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public void setDelivery_id(String delivery_id) {
        this.delivery_id = delivery_id;
    }

    public String getDishes_summary() {
        return dishes_summary;
    }

    public void setDishes_summary(String dishes_summary) {
        this.dishes_summary = dishes_summary;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getRestaurant_name() {
        return restaurant_name;
    }

    public void setRestaurant_name(String restaurant_name) {
        this.restaurant_name = restaurant_name;
    }
}
