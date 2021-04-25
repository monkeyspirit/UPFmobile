package mobile.android.upf.ui.delivery.delivery_restaurants;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import mobile.android.upf.R;
import mobile.android.upf.data.model.RecyclerViewAdapter.RecyclerViewAdapter_restaurant_for_delivery;
import mobile.android.upf.data.model.Restaurant;

public class DeliveryRestaurantsFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    private Context context;
    private MaterialCardView cardview;
    private ViewGroup.LayoutParams layoutparams;
    private LinearLayout linearLayout;

    private RecyclerView myrv;
    private RecyclerViewAdapter_restaurant_for_delivery myAdapter;

    private List<Restaurant> lstRest;
    private String userId;

    private SwipeRefreshLayout swipeRefreshLayout;

    private DeliveryRestourantsViewModel deliveryRestourantsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        deliveryRestourantsViewModel =
                new ViewModelProvider(this).get(DeliveryRestourantsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_restaurants_delivery, container, false);

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
//                        ID del ristorante


                            lstRest.add(new Restaurant(
                                    String.valueOf(restaurant.getKey()),
                                    String.valueOf(restaurant.child("name").getValue()),
                                    String.valueOf(restaurant.child("description").getValue()),
                                    String.valueOf(restaurant.child("email").getValue()),
                                    String.valueOf(restaurant.child("address").getValue()),
                                    String.valueOf(restaurant.child("phone").getValue()),
                                    String.valueOf(restaurant.child("restaurateur_id").getValue()),
                                    String.valueOf(restaurant.child("imageUrl").getValue()),
                                    Integer.parseInt(String.valueOf(restaurant.child("status").getValue()))));


                    }

                    myrv = (RecyclerView) root.findViewById(R.id.recyclerview_delivery_restaurants);
                    myAdapter = new RecyclerViewAdapter_restaurant_for_delivery(getActivity(), DeliveryRestaurantsFragment.this, lstRest);

                    myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                    myrv.setAdapter(myAdapter);

                }


            }

        });


        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_restaurant_delivery);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateRecycler();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return root;
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
//                        ID del ristorante


                        lstRest.add(new Restaurant(
                                String.valueOf(restaurant.getKey()),
                                String.valueOf(restaurant.child("name").getValue()),
                                String.valueOf(restaurant.child("description").getValue()),
                                String.valueOf(restaurant.child("email").getValue()),
                                String.valueOf(restaurant.child("address").getValue()),
                                String.valueOf(restaurant.child("phone").getValue()),
                                String.valueOf(restaurant.child("restaurateur_id").getValue()),
                                String.valueOf(restaurant.child("imageUrl").getValue()),
                                Integer.parseInt(String.valueOf(restaurant.child("status").getValue()))));


                    }

                    myAdapter = new RecyclerViewAdapter_restaurant_for_delivery(getActivity(), DeliveryRestaurantsFragment.this, lstRest);

                    myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                    myrv.setAdapter(myAdapter);

                }


            }

        });
    }
}