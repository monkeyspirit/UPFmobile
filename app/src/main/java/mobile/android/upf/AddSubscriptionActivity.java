package mobile.android.upf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import mobile.android.upf.data.model.RecyclerViewAdapter.RecyclerViewAdapter_delivery_restaurant;
import mobile.android.upf.data.model.Restaurant;
import mobile.android.upf.ui.delivery.delivery_restaurants.DeliveryRestaurantsFragment;

public class AddSubscriptionActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    private RecyclerView myrv;
    private RecyclerViewAdapter_delivery_restaurant myAdapter;

    private List<Restaurant> lstRest;
    private String userId;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subscription);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        userId = currentUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();


        lstRest = new ArrayList<>();

        mDatabase.child("Restaurants").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    Iterable<DataSnapshot> restaurants_database = task.getResult().getChildren();

                    for (DataSnapshot restaurant : restaurants_database) {

                        if (!restaurant.child("Subscribers").exists()){
                            lstRest.add(new Restaurant(
                                    String.valueOf(restaurant.getKey()),
                                    String.valueOf(restaurant.child("name").getValue()),
                                    String.valueOf(restaurant.child("description").getValue()),
                                    String.valueOf(restaurant.child("email").getValue()),
                                    String.valueOf(restaurant.child("city").getValue()),
                                    String.valueOf(restaurant.child("address").getValue()),
                                    String.valueOf(restaurant.child("phone").getValue()),
                                    String.valueOf(restaurant.child("restaurateur_id").getValue()),
                                    String.valueOf(restaurant.child("admin_id").getValue()),
                                    String.valueOf(restaurant.child("imageUrl").getValue()),
                                    Integer.parseInt(String.valueOf(restaurant.child("status").getValue()))));
                        }

                        for (DataSnapshot subscriber: restaurant.child("Subscribers").getChildren()) {

                            if (!subscriber.getKey().equals(userId)) {
                                lstRest.add(new Restaurant(
                                        String.valueOf(restaurant.getKey()),
                                        String.valueOf(restaurant.child("name").getValue()),
                                        String.valueOf(restaurant.child("description").getValue()),
                                        String.valueOf(restaurant.child("email").getValue()),
                                        String.valueOf(restaurant.child("city").getValue()),
                                        String.valueOf(restaurant.child("address").getValue()),
                                        String.valueOf(restaurant.child("phone").getValue()),
                                        String.valueOf(restaurant.child("restaurateur_id").getValue()),
                                        String.valueOf(restaurant.child("admin_id").getValue()),
                                        String.valueOf(restaurant.child("imageUrl").getValue()),
                                        Integer.parseInt(String.valueOf(restaurant.child("status").getValue()))));

                            }
                        }


                    }

                    myrv = (RecyclerView) findViewById(R.id.recyclerview_delivery_restaurants);
                    myAdapter = new RecyclerViewAdapter_delivery_restaurant(AddSubscriptionActivity.this, lstRest);

                    myrv.setLayoutManager(new GridLayoutManager(AddSubscriptionActivity.this, 1));
                    myrv.setAdapter(myAdapter);

                }


            }

        });

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_restaurant_delivery);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateRecycler();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateRecycler(){
        lstRest = new ArrayList<>();

        mDatabase.child("Restaurants").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    Iterable<DataSnapshot> restaurants_database = task.getResult().getChildren();

                    for (DataSnapshot restaurant : restaurants_database) {

                        if (!restaurant.child("Subscribers").exists()){
                            lstRest.add(new Restaurant(
                                    String.valueOf(restaurant.getKey()),
                                    String.valueOf(restaurant.child("name").getValue()),
                                    String.valueOf(restaurant.child("description").getValue()),
                                    String.valueOf(restaurant.child("email").getValue()),
                                    String.valueOf(restaurant.child("city").getValue()),
                                    String.valueOf(restaurant.child("address").getValue()),
                                    String.valueOf(restaurant.child("phone").getValue()),
                                    String.valueOf(restaurant.child("restaurateur_id").getValue()),
                                    String.valueOf(restaurant.child("admin_id").getValue()),
                                    String.valueOf(restaurant.child("imageUrl").getValue()),
                                    Integer.parseInt(String.valueOf(restaurant.child("status").getValue()))));
                        }

                        for (DataSnapshot subscriber: restaurant.child("Subscribers").getChildren()) {

                            if (!subscriber.getKey().equals(userId)) {
                                lstRest.add(new Restaurant(
                                        String.valueOf(restaurant.getKey()),
                                        String.valueOf(restaurant.child("name").getValue()),
                                        String.valueOf(restaurant.child("description").getValue()),
                                        String.valueOf(restaurant.child("email").getValue()),
                                        String.valueOf(restaurant.child("city").getValue()),
                                        String.valueOf(restaurant.child("address").getValue()),
                                        String.valueOf(restaurant.child("phone").getValue()),
                                        String.valueOf(restaurant.child("restaurateur_id").getValue()),
                                        String.valueOf(restaurant.child("admin_id").getValue()),
                                        String.valueOf(restaurant.child("imageUrl").getValue()),
                                        Integer.parseInt(String.valueOf(restaurant.child("status").getValue()))));

                            }
                        }


                    }


                    myAdapter = new RecyclerViewAdapter_delivery_restaurant(AddSubscriptionActivity.this, lstRest);

                    myrv.setLayoutManager(new GridLayoutManager(AddSubscriptionActivity.this, 1));
                    myrv.setAdapter(myAdapter);

                }


            }

        });
    }
}