package mobile.android.upf.data.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Notification {
    private String id;
    private String user_id;
    private String date;
    private String time;
    private String state;
    private String content;

    public Notification() {
    }

    public Notification(String user_id, String date, String time, String state, String content) {
        this.user_id = user_id;
        this.date = date;
        this.time = time;
        this.state = state;
        this.content = content;
        long tsLong = System.currentTimeMillis()/1000;
        String ts = Long.toString(tsLong);
        this.id = md5(user_id+date+time+state+ts+content);
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

    public Notification(String id, String user_id, String date, String time, String state, String content) {
        this.id = id;
        this.user_id = user_id;
        this.content = content;
        this.date = date;
        this.time = time;
        this.state = state;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
