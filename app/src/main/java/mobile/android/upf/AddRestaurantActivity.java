package mobile.android.upf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import mobile.android.upf.data.model.Restaurant;

import static android.widget.Toast.LENGTH_LONG;

public class AddRestaurantActivity extends AppCompatActivity {

    private Button add_restaurant_btn;
    private EditText restaurant_name, restaurant_phone, restaurant_address, restaurant_emailAddress, restaurant_description;

    private ProgressBar progressBar;

    private static final String TAG_LOG = "RestaurantRegistrationActivity";

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar_add_restaurant);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        String restaurateur_id = mAuth.getCurrentUser().getUid();

        add_restaurant_btn = (Button) findViewById(R.id.add_restaurant_btn);

        add_restaurant_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restaurant_name = (EditText) findViewById(R.id.restaurant_name);
                restaurant_phone = (EditText) findViewById(R.id.restaurant_phone);
                restaurant_address = (EditText) findViewById(R.id.restaurant_address);
                restaurant_emailAddress = (EditText) findViewById(R.id.restaurant_emailAddress);
                restaurant_description = (EditText) findViewById(R.id.restaurant_description);

                String name = restaurant_name.getText().toString().trim();
                String address = restaurant_address.getText().toString().trim();
                String phone = restaurant_phone.getText().toString().trim();
                String email = restaurant_emailAddress.getText().toString().trim();
                String description = restaurant_description.getText().toString().trim();

                if (name.isEmpty()) {
                    restaurant_name.setError(getString(R.string.empty_name));
                    restaurant_name.requestFocus();
                    return;
                }
                if (description.isEmpty()) {
                    restaurant_description.setError(getString(R.string.empty_name));
                    restaurant_description.requestFocus();
                    return;
                }
                if (email.isEmpty()) {
                    restaurant_emailAddress.setError(getString(R.string.empty_email));
                    restaurant_emailAddress.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    restaurant_emailAddress.setError(getString(R.string.invalid_email));
                    restaurant_emailAddress.requestFocus();
                    return;
                }
                if (phone.isEmpty()) {
                    restaurant_phone.setError(getString(R.string.empty_phone));
                    restaurant_phone.requestFocus();
                    return;
                }
                if (address.isEmpty()) {
                    restaurant_address.setError(getString(R.string.empty_address));
                    restaurant_address.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                Restaurant restaurant = new Restaurant(name, description, email, address, phone, restaurateur_id);

                mDatabase.child("Restaurants").child(restaurant.getId()).setValue(restaurant).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            finish();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(AddRestaurantActivity.this, getString(R.string.client_registration_db_failed), LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }


}