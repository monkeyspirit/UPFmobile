package mobile.android.upf.ui.restaurant.restaurant_homepage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import mobile.android.upf.R;
import mobile.android.upf.data.model.Order;
import mobile.android.upf.data.model.RecyclerViewAdapter.RecyclerViewAdapter_client_view_order;
import mobile.android.upf.ui.client.client_homepage.ClientOrdersViewModel;

public class RestaurantOrdersFragment extends Fragment {

    private RestaurantOrdersViewModel restaurantOrdersViewModel;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    List<Order> lstOrder;
    RecyclerView myrv;

//    SOLO PER PROVARE
    RecyclerViewAdapter_client_view_order myAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        restaurantOrdersViewModel =
                new ViewModelProvider(this).get(RestaurantOrdersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home_restaurant, container, false);

        lstOrder = new ArrayList<>();


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        String userId = currentUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("Restaurants").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));

                    Iterable<DataSnapshot> restaurants = task.getResult().getChildren();

                    for(DataSnapshot restaurant: restaurants){
                        if (String.valueOf(restaurant.child("restaurateur_id").getValue()).equals(currentUser.getUid())){

                            Iterable<DataSnapshot> orders = restaurant.child("Orders").getChildren();
                            for (DataSnapshot order : orders) {

                                lstOrder.add(new Order(
                                        String.valueOf(order.child("id").getValue()),
                                        String.valueOf(order.child("user_id").getValue()),
                                        String.valueOf(order.child("restaurant_id").getValue()),
                                        String.valueOf(order.child("dishes_summary").getValue()),
                                        String.valueOf(order.child("total").getValue()),
                                        String.valueOf(order.child("paymemt_method").getValue()),
                                        String.valueOf(order.child("address").getValue()),
                                        String.valueOf(order.child("date").getValue()),
                                        String.valueOf(order.child("time").getValue()),
                                        Integer.parseInt(String.valueOf(order.child("state").getValue())))
                                );
                            }
                        }
                    }




                    myrv = (RecyclerView) root.findViewById(R.id.recyclerview_restaurant_orders);
                    myAdapter = new RecyclerViewAdapter_client_view_order(getActivity(), lstOrder);

                    myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                    myrv.setAdapter(myAdapter);

                }


            }



        });


        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_order_restaurant);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateRecycler();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        return root;
    }

    private void updateRecycler() {
        lstOrder = new ArrayList<>();
        mDatabase.child("Restaurants").child(currentUser.getUid()).child("Orders").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));

                    Iterable<DataSnapshot> orders = task.getResult().getChildren();

                    for (DataSnapshot order : orders) {

                        lstOrder.add(new Order(
                                String.valueOf(order.child("id").getValue()),
                                String.valueOf(order.child("user_id").getValue()),
                                String.valueOf(order.child("restaurant_id").getValue()),
                                String.valueOf(order.child("dishes_summary").getValue()),
                                String.valueOf(order.child("total").getValue()),
                                String.valueOf(order.child("paymemt_method").getValue()),
                                String.valueOf(order.child("address").getValue()),
                                String.valueOf(order.child("date").getValue()),
                                String.valueOf(order.child("time").getValue()),
                                Integer.parseInt(String.valueOf(order.child("state").getValue())))
                        );
                    }

                    myAdapter = new RecyclerViewAdapter_client_view_order(getActivity(), lstOrder);

                    myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                    myrv.setAdapter(myAdapter);

                }


            }



        });

    }


}