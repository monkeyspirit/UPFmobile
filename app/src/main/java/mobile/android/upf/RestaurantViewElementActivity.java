package mobile.android.upf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;

public class RestaurantViewElementActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private ImageView restaurant_pic;
    private TextView restaurant_name_element_tv, restaurant_phone_element_tv, restaurant_address_element_tv,
            restaurant_emailAddress_element_tv, restaurant_description_element_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_view_element);

//        Back arrow
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

//        Get restaurant ID
        Intent intent = getIntent();
        String restaurant_id = intent.getExtras().getString("id");

        restaurant_name_element_tv = findViewById(R.id.restaurant_name_element_tv);
        restaurant_phone_element_tv = findViewById(R.id.restaurant_phone_element_tv);
        restaurant_address_element_tv = findViewById(R.id.restaurant_address_element_tv);
        restaurant_emailAddress_element_tv = findViewById(R.id.restaurant_emailAddress_element_tv);
        restaurant_description_element_tv = findViewById(R.id.restaurant_description_element_tv);

        restaurant_pic =  findViewById(R.id.restaurant_pic);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Restaurants").child(restaurant_id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {

                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    restaurant_name_element_tv.setText(String.valueOf(task.getResult().child("name").getValue()));
                    setTitle(String.valueOf(task.getResult().child("name").getValue()));
                    restaurant_phone_element_tv.setText(String.valueOf(task.getResult().child("phone").getValue()));
                    restaurant_address_element_tv.setText(String.valueOf(task.getResult().child("address").getValue()));
                    restaurant_emailAddress_element_tv.setText(String.valueOf(task.getResult().child("email").getValue()));
                    restaurant_description_element_tv.setText(String.valueOf(task.getResult().child("description").getValue()));

                    String uriS = String.valueOf(task.getResult().child("imageUrl").getValue());

                    if (uriS != "") {
                        Uri uri = Uri.parse(String.valueOf(task.getResult().child("imageUrl").getValue()));
                        Log.d("firebase", "Image Url: " + uri);
                        Glide.with(getApplicationContext()).load(uri).into(restaurant_pic);
                    }
                }
            }
        });

        //        Floating button per l'aggiunta di nuovi piatti
        ExtendedFloatingActionButton fab = findViewById(R.id.fab_dish);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RestaurantViewElementActivity.this, AddDishRestaurantActivity.class);
                intent.putExtra("id", restaurant_id);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}