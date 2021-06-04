package mobile.android.upf.ui.restaurant.restaurant_restaurants;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import mobile.android.upf.AddRestaurantActivity;
import mobile.android.upf.R;
import mobile.android.upf.data.model.RecyclerViewAdapter.RecyclerViewAdapter_restaurant;
import mobile.android.upf.data.model.Restaurant;

import static android.app.Activity.RESULT_OK;

public class RestaurantRestaurantsFragment extends Fragment {

    private RestaurantRestourantsViewModel restaurantRestourantsViewModel;

    private Button edit_restaurant_card_btn, delete_restaurant_card_btn;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;


    private int counter_yes, counter_no, counter_wait;

    private Context context;
    private MaterialCardView cardview;
    private ViewGroup.LayoutParams layoutparams;
    private LinearLayout linearLayout;
    private TextView counter_approved_restaurants, counter_waiting_approved_restaurants,counter_not_approved_restaurants;

    private RecyclerView myrv;
    private RecyclerViewAdapter_restaurant myAdapter;

    private List<Restaurant> lstRest;
    private String userId;

    private SwipeRefreshLayout swipeRefreshLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        restaurantRestourantsViewModel =
                new ViewModelProvider(this).get(RestaurantRestourantsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_restaurants_restaurant, container, false);

//        Floating button per l'aggiunta di nuovi ristoranti
        ExtendedFloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddRestaurantActivity.class);
                startActivityForResult(intent, 1);
            }
        });



        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        userId = currentUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        lstRest = new ArrayList<>();

        counter_approved_restaurants = (TextView) root.findViewById(R.id.counter_approved_restaurants);
        counter_waiting_approved_restaurants = (TextView) root.findViewById(R.id.counter_waiting_approved_restaurants);
        counter_not_approved_restaurants = (TextView) root.findViewById(R.id.counter_not_approved_restaurants);

        counter_yes = 0;
        counter_no = 0;
        counter_wait = 0;

        mDatabase.child("Restaurants").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    Iterable<DataSnapshot> restaurants_database = task.getResult().getChildren();

                    for (DataSnapshot restaurant : restaurants_database) {
//                        ID del ristorante
                        Log.d("firebase ID rest", String.valueOf(restaurant.getKey()));
                        Log.d("firebase ID rest in iD", String.valueOf(restaurant.child("id").getValue()));
                        Log.d("firebase", String.valueOf(restaurant.child("name").getValue()));
                        if (String.valueOf(restaurant.child("restaurateur_id").getValue()).equals(userId)) {
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
                                    String.valueOf(restaurant.child("decline_msg").getValue()),
                                    Integer.parseInt(String.valueOf(restaurant.child("status").getValue()))));
                        }

                        switch (Integer.parseInt(String.valueOf(restaurant.child("status").getValue()))){
                            case 0:{
                                counter_wait += 1;
                                break;
                            }
                            case 1:{
                                counter_yes += 1;
                                break;
                            }
                            case 2: {
                                counter_no += 1;
                                break;
                            }
                        }

                    }


                    counter_approved_restaurants.setText(String.valueOf(counter_yes));
                    counter_waiting_approved_restaurants.setText(String.valueOf(counter_wait));
                    counter_not_approved_restaurants.setText(String.valueOf(counter_no));


                    myrv = (RecyclerView) root.findViewById(R.id.recyclerview_restaurant_restaurants);
                    myAdapter = new RecyclerViewAdapter_restaurant(getActivity(), lstRest, RestaurantRestaurantsFragment.this);

                    myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                    myrv.setAdapter(myAdapter);

                }


            }

        });

        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_restaurant_restaurant);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateRecycler();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {

            if (resultCode == RESULT_OK) {
                Log.d("Update", "YES");
                updateRecycler();
                myAdapter.notifyDataSetChanged();
            }
        }
    }

    public void updateRecycler(){
        lstRest = new ArrayList<>();

        counter_yes = 0;
        counter_no = 0;
        counter_wait = 0;

        mDatabase.child("Restaurants").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    Iterable<DataSnapshot> restaurants_database = task.getResult().getChildren();

                    for (DataSnapshot restaurant : restaurants_database) {
//                        ID del ristorante
                        Log.d("firebase ID rest", String.valueOf(restaurant.getKey()));
                        Log.d("firebase ID rest in iD", String.valueOf(restaurant.child("id").getValue()));
                        Log.d("firebase", String.valueOf(restaurant.child("name").getValue()));
                        if (String.valueOf(restaurant.child("restaurateur_id").getValue()).equals(userId)) {
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
                                    String.valueOf(restaurant.child("decline_msg").getValue()),
                                    Integer.parseInt(String.valueOf(restaurant.child("status").getValue()))));
                        }
                        switch (Integer.parseInt(String.valueOf(restaurant.child("status").getValue()))){
                            case 0:{
                                counter_wait += 1;
                                break;
                            }
                            case 1:{
                                counter_yes += 1;
                                break;
                            }
                            case 2: {
                                counter_no += 1;
                                break;
                            }
                        }

                    }


                    counter_approved_restaurants.setText(String.valueOf(counter_yes));
                    counter_waiting_approved_restaurants.setText(String.valueOf(counter_wait));
                    counter_not_approved_restaurants.setText(String.valueOf(counter_no));

                    myAdapter = new RecyclerViewAdapter_restaurant(getActivity(), lstRest, RestaurantRestaurantsFragment.this);

                    myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                    myrv.setAdapter(myAdapter);
                }
            }

        });

    }

}