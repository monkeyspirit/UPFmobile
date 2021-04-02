package mobile.android.upf.data.model;

public class Order {

    private String id;
    private String user_id;
    private String restaurant_id;

    public Order() {
    }

    public Order(String id, String user_id, String restaurant_id) {
        this.id = id;
        this.user_id = user_id;
        this.restaurant_id = restaurant_id;
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

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }
}
