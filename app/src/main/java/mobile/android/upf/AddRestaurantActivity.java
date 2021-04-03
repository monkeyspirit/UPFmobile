package mobile.android.upf;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class AddRestaurantActivity extends AppCompatActivity {

    private Button add_restaurant_btn;
    private EditText restaurant_name, restaurant_phone, restaurant_address, restaurant_emailAddress, restaurant_description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);

    }
}