package mobile.android.upf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import mobile.android.upf.data.model.Dish;
import mobile.android.upf.data.model.RecyclerViewAdapter_dish;
import mobile.android.upf.data.model.RecyclerViewAdapter_restaurant;
import mobile.android.upf.data.model.Restaurant;

public class RestaurantViewElementActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private ImageView restaurant_pic;
    private TextView restaurant_name_element_tv, restaurant_phone_element_tv, restaurant_address_element_tv,
            restaurant_emailAddress_element_tv, restaurant_description_element_tv;

    private RecyclerView myrv;
    private RecyclerViewAdapter_dish myAdapter;

    private List<Dish> lstDish;
    private String restaurant_id;

//    private Button add_restaurant_dish_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_view_element);

//        Back arrow
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

//        Get restaurant ID
        Intent intent = getIntent();
        restaurant_id = intent.getExtras().getString("id");

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
                startActivityForResult(intent, 1);
            }
        });

//        add_restaurant_dish_btn = findViewById(R.id.add_restaurant_dish_btn);
//        add_restaurant_dish_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(RestaurantViewElementActivity.this, AddDishRestaurantActivity.class);
//                intent.putExtra("id", restaurant_id);
//                startActivity(intent);
//            }
//        });



        lstDish = new ArrayList<>();

        mDatabase.child("Dishes").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    Iterable<DataSnapshot> dishes_database = task.getResult().getChildren();

                    for (DataSnapshot dish : dishes_database) {
                        Log.d("firebase", String.valueOf(dish.child("name").getValue()));
                        if (String.valueOf(dish.child("restaurant_id").getValue()).equals(restaurant_id)) {
                            lstDish.add(new Dish(
                                    String.valueOf(dish.getKey()),
                                    String.valueOf(dish.child("name").getValue()),
                                    String.valueOf(dish.child("description").getValue()),
                                    String.valueOf(dish.child("restaurant_id").getValue()),
                                    Double.parseDouble(String.valueOf(dish.child("price").getValue()))
                                    )
                            );
                        }

                    }

                    myrv = (RecyclerView) findViewById(R.id.recyclerview_restaurant_dishes);
                    myAdapter = new RecyclerViewAdapter_dish(RestaurantViewElementActivity.this, lstDish);

                    myrv.setLayoutManager(new GridLayoutManager(RestaurantViewElementActivity.this, 1));
                    myrv.setAdapter(myAdapter);

                }


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {

            if (resultCode == RESULT_OK) {
                Log.d("Update", "YES");
                lstDish = new ArrayList<>();

                mDatabase.child("Dishes").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {
                            Log.d("firebase", String.valueOf(task.getResult().getValue()));
                            Iterable<DataSnapshot> dishes_database = task.getResult().getChildren();

                            for (DataSnapshot dish : dishes_database) {
                                Log.d("firebase", String.valueOf(dish.child("name").getValue()));
                                if (String.valueOf(dish.child("restaurant_id").getValue()).equals(restaurant_id)) {
                                    lstDish.add(new Dish(
                                                    String.valueOf(dish.getKey()),
                                                    String.valueOf(dish.child("name").getValue()),
                                                    String.valueOf(dish.child("description").getValue()),
                                                    String.valueOf(dish.child("restaurant_id").getValue()),
                                                    Double.parseDouble(String.valueOf(dish.child("price").getValue()))
                                            )
                                    );
                                }

                            }

                            myAdapter = new RecyclerViewAdapter_dish(RestaurantViewElementActivity.this, lstDish);

                            myrv.setLayoutManager(new GridLayoutManager(RestaurantViewElementActivity.this, 1));
                            myrv.setAdapter(myAdapter);

                        }
                    }

                });

            }
        }
    }
}