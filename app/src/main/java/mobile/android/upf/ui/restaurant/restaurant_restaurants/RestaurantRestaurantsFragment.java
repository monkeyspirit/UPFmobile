package mobile.android.upf.ui.restaurant.restaurant_restaurants;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.annotation.RequiresApi;
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
import java.util.Comparator;
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

    boolean approved_clicked = false, not_approved_clicked = false, waiting_clicked = false;
    private int counter_yes, counter_no, counter_wait;

    private Context context;
    private MaterialCardView cardview;
    private ViewGroup.LayoutParams layoutparams;
    private LinearLayout linearLayout;
    private TextView counter_approved_restaurants, counter_waiting_restaurants,counter_not_approved_restaurants;
    private TextView label_approved_restaurants, label_not_approved_restaurants, label_waiting_restaurants;
    private CardView card_approved, card_not_approved, card_waiting;

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
        setHasOptionsMenu(true);
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

        label_approved_restaurants = (TextView) root.findViewById(R.id.label_approved_restaurants);
        label_waiting_restaurants = (TextView) root.findViewById(R.id.label_waiting_restaurants);
        label_not_approved_restaurants = (TextView) root.findViewById(R.id.label_not_approved_restaurants);

        counter_approved_restaurants = (TextView) root.findViewById(R.id.counter_approved_restaurants);
        counter_waiting_restaurants = (TextView) root.findViewById(R.id.counter_waiting_restaurants);
        counter_not_approved_restaurants = (TextView) root.findViewById(R.id.counter_not_approved_restaurants);

        counter_yes = 0;
        counter_no = 0;
        counter_wait = 0;

        mDatabase.child("Restaurants").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
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


                    }

                    lstRest.sort(Comparator.comparing(Restaurant::getStatus));

                    counter_approved_restaurants.setText(String.valueOf(counter_yes));
                    counter_waiting_restaurants.setText(String.valueOf(counter_wait));
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

        card_approved = (CardView) root.findViewById(R.id.card_approved);
        card_approved.setBackgroundColor(getResources().getColor(R.color.white));
        card_not_approved = (CardView) root.findViewById(R.id.card_not_approved);
        card_not_approved.setBackgroundColor(getResources().getColor(R.color.white));
        card_waiting = (CardView) root.findViewById(R.id.card_waiting);
        card_waiting.setBackgroundColor(getResources().getColor(R.color.white));

        card_approved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!approved_clicked){
                    approved_clicked = true;
                    not_approved_clicked = false;
                    waiting_clicked = false;
                    statusFilter(1);

                    label_approved_restaurants.setTextColor(getResources().getColor(R.color.white));
                    counter_approved_restaurants.setTextColor(getResources().getColor(R.color.white));
                    counter_approved_restaurants.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_baseline_celebration_24_white);

                    label_waiting_restaurants.setTextColor(getResources().getColor(R.color.dark_grey));
                    counter_waiting_restaurants.setTextColor(getResources().getColor(R.color.dark_grey));
                    counter_waiting_restaurants.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_baseline_access_time_24);

                    label_not_approved_restaurants.setTextColor(getResources().getColor(R.color.ic_delete_background));
                    counter_not_approved_restaurants.setTextColor(getResources().getColor(R.color.ic_delete_background));
                    counter_not_approved_restaurants.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_baseline_sentiment_very_dissatisfied_24);

                    card_approved.setBackgroundColor(getResources().getColor(R.color.light_orange));
                    card_not_approved.setBackgroundColor(getResources().getColor(R.color.light_grey));
                    card_waiting.setBackgroundColor(getResources().getColor(R.color.light_grey));
                }
                else{
                    approved_clicked = false;
                    not_approved_clicked = false;
                    waiting_clicked = false;
                    statusFilterDisable();

                    label_approved_restaurants.setTextColor(getResources().getColor(R.color.green_done));
                    counter_approved_restaurants.setTextColor(getResources().getColor(R.color.green_done));
                    counter_approved_restaurants.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_baseline_celebration_24);

                    card_approved.setBackgroundColor(getResources().getColor(R.color.white));
                    card_waiting.setBackgroundColor(getResources().getColor(R.color.white));
                    card_not_approved.setBackgroundColor(getResources().getColor(R.color.white));
                }

            }
        });

        card_not_approved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!not_approved_clicked){
                    approved_clicked = false;
                    not_approved_clicked = true;
                    waiting_clicked = false;
                    statusFilter(2);

                    label_approved_restaurants.setTextColor(getResources().getColor(R.color.green_done));
                    counter_approved_restaurants.setTextColor(getResources().getColor(R.color.green_done));
                    counter_approved_restaurants.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_baseline_celebration_24);

                    label_waiting_restaurants.setTextColor(getResources().getColor(R.color.dark_grey));
                    counter_waiting_restaurants.setTextColor(getResources().getColor(R.color.dark_grey));
                    counter_waiting_restaurants.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_baseline_access_time_24);

                    label_not_approved_restaurants.setTextColor(getResources().getColor(R.color.white));
                    counter_not_approved_restaurants.setTextColor(getResources().getColor(R.color.white));
                    counter_not_approved_restaurants.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_baseline_sentiment_very_dissatisfied_24_white);

                    card_not_approved.setBackgroundColor(getResources().getColor(R.color.light_orange));
                    card_approved.setBackgroundColor(getResources().getColor(R.color.light_grey));
                    card_waiting.setBackgroundColor(getResources().getColor(R.color.light_grey));
                }
                else{
                    approved_clicked = false;
                    not_approved_clicked = false;
                    waiting_clicked = false;
                    statusFilterDisable();

                    label_not_approved_restaurants.setTextColor(getResources().getColor(R.color.ic_delete_background));
                    counter_not_approved_restaurants.setTextColor(getResources().getColor(R.color.ic_delete_background));
                    counter_not_approved_restaurants.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_baseline_sentiment_very_dissatisfied_24);


                    card_approved.setBackgroundColor(getResources().getColor(R.color.white));
                    card_waiting.setBackgroundColor(getResources().getColor(R.color.white));
                    card_not_approved.setBackgroundColor(getResources().getColor(R.color.white));
                }
            }
        });

        card_waiting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!waiting_clicked){
                    approved_clicked = false;
                    not_approved_clicked = false;
                    waiting_clicked = true;
                    statusFilter(0);

                    label_approved_restaurants.setTextColor(getResources().getColor(R.color.green_done));
                    counter_approved_restaurants.setTextColor(getResources().getColor(R.color.green_done));
                    counter_approved_restaurants.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_baseline_celebration_24);

                    label_waiting_restaurants.setTextColor(getResources().getColor(R.color.white));
                    counter_waiting_restaurants.setTextColor(getResources().getColor(R.color.white));
                    counter_waiting_restaurants.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_baseline_access_time_24_white);

                    label_not_approved_restaurants.setTextColor(getResources().getColor(R.color.ic_delete_background));
                    counter_not_approved_restaurants.setTextColor(getResources().getColor(R.color.ic_delete_background));
                    counter_not_approved_restaurants.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_baseline_sentiment_very_dissatisfied_24);

                    card_waiting.setBackgroundColor(getResources().getColor(R.color.light_orange));
                    card_approved.setBackgroundColor(getResources().getColor(R.color.light_grey));
                    card_not_approved.setBackgroundColor(getResources().getColor(R.color.light_grey));
                }
                else{
                    approved_clicked = false;
                    not_approved_clicked = false;
                    waiting_clicked = false;
                    statusFilterDisable();

                    label_waiting_restaurants.setTextColor(getResources().getColor(R.color.dark_grey));
                    counter_waiting_restaurants.setTextColor(getResources().getColor(R.color.dark_grey));
                    counter_waiting_restaurants.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.ic_baseline_access_time_24);

                    card_approved.setBackgroundColor(getResources().getColor(R.color.white));
                    card_waiting.setBackgroundColor(getResources().getColor(R.color.white));
                    card_not_approved.setBackgroundColor(getResources().getColor(R.color.white));
                }
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

                    }


                    counter_approved_restaurants.setText(String.valueOf(counter_yes));
                    counter_waiting_restaurants.setText(String.valueOf(counter_wait));
                    counter_not_approved_restaurants.setText(String.valueOf(counter_no));

                    myAdapter = new RecyclerViewAdapter_restaurant(getActivity(), lstRest, RestaurantRestaurantsFragment.this);

                    myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                    myrv.setAdapter(myAdapter);
                }
            }

        });

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);

        final SearchView searchView = (SearchView) menu.findItem(R.id.actionSearch).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (s.length() > 0) {
                    nameFilter(String.valueOf(s));
                } else {
                    myAdapter = new RecyclerViewAdapter_restaurant(getActivity(), lstRest, RestaurantRestaurantsFragment.this);

                    myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                    myrv.setAdapter(myAdapter);
                }
                return true;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                if (s.length() > 0) {
                    nameFilter(String.valueOf(s));
                } else {
                    myAdapter = new RecyclerViewAdapter_restaurant(getActivity(), lstRest, RestaurantRestaurantsFragment.this);

                    myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                    myrv.setAdapter(myAdapter);
                }
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);


    }

    private void nameFilter(String text) {
        ArrayList<Restaurant> filteredList = new ArrayList<>();

        for (Restaurant item : lstRest) {

            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(getActivity(), "No data found...", Toast.LENGTH_SHORT).show();
        }
        myAdapter = new RecyclerViewAdapter_restaurant(getActivity(), filteredList, RestaurantRestaurantsFragment.this);

        myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        myrv.setAdapter(myAdapter);
    }

    private void statusFilter(int status) {
        ArrayList<Restaurant> filteredList = new ArrayList<>();

        for (Restaurant item : lstRest) {

            if (item.getStatus() == status) {
                filteredList.add(item);
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(getActivity(), "No data found...", Toast.LENGTH_SHORT).show();
        }
        myAdapter = new RecyclerViewAdapter_restaurant(getActivity(), filteredList, RestaurantRestaurantsFragment.this);

        myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        myrv.setAdapter(myAdapter);
    }

    private void statusFilterDisable() {

        ArrayList<Restaurant> filteredList = new ArrayList<>(lstRest);
        if (filteredList.isEmpty()) {
            Toast.makeText(getActivity(), "No data found...", Toast.LENGTH_SHORT).show();
        }
        myAdapter = new RecyclerViewAdapter_restaurant(getActivity(), filteredList, RestaurantRestaurantsFragment.this);

        myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        myrv.setAdapter(myAdapter);
    }
}