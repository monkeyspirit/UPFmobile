package mobile.android.upf.ui.delivery.delivery_homepage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import mobile.android.upf.R;
import mobile.android.upf.data.model.Order;
import mobile.android.upf.data.model.RecyclerViewAdapter.RecyclerViewAdapter_delivery_view_order;

public class DeliveryOrdersFragment extends Fragment {

    private DeliveryOrdersViewModel deliveryOrdersViewModel;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    RecyclerView myrv;
    RecyclerViewAdapter_delivery_view_order myAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    List<Order> lstOrder;
    ArrayList<String> lstOrdersId;
    String userId;

    String busy = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        deliveryOrdersViewModel = new ViewModelProvider(this).get(DeliveryOrdersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home_delivery, container, false);

        lstOrder = new ArrayList<>();
        lstOrdersId = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        userId = currentUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("Users").child(currentUser.getUid()).child("busy").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null){
                    busy = String.valueOf(snapshot.getValue());
                }
                else{
                    busy = null;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mDatabase.child("Orders").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                updateRecycler();
            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        mDatabase.child("Orders").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {

                    Iterable<DataSnapshot> orders = task.getResult().getChildren();


                    for (DataSnapshot order : orders) {
                        if(busy == null) {
                            if(Integer.parseInt(String.valueOf(order.child("state").getValue()))==2 || Integer.parseInt(String.valueOf(order.child("state").getValue()))==3){
                                lstOrder.add(new Order(
                                        String.valueOf(order.child("id").getValue()),
                                        String.valueOf(order.child("user_id").getValue()),
                                        String.valueOf(order.child("restaurant_id").getValue()),
                                        String.valueOf(order.child("delivery_id").getValue()),
                                        String.valueOf(order.child("dishes_summary").getValue()),
                                        String.valueOf(order.child("total").getValue()),
                                        String.valueOf(order.child("payment_method").getValue()),
                                        String.valueOf(order.child("address").getValue()),
                                        String.valueOf(order.child("date").getValue()),
                                        String.valueOf(order.child("time").getValue()),
                                        Integer.parseInt(String.valueOf(order.child("state").getValue())))
                                );
                            }
                            myrv = (RecyclerView) root.findViewById(R.id.recyclerview_delivery_orders);
                            myAdapter = new RecyclerViewAdapter_delivery_view_order(getActivity(), lstOrder, DeliveryOrdersFragment.this);

                            myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                            myrv.setAdapter(myAdapter);

                        }
                        else {
                            if (String.valueOf(order.child("id").getValue()).equals(busy)) {

                                lstOrder.add(new Order(
                                        String.valueOf(order.child("id").getValue()),
                                        String.valueOf(order.child("user_id").getValue()),
                                        String.valueOf(order.child("restaurant_id").getValue()),
                                        String.valueOf(order.child("delivery_id").getValue()),
                                        String.valueOf(order.child("dishes_summary").getValue()),
                                        String.valueOf(order.child("total").getValue()),
                                        String.valueOf(order.child("payment_method").getValue()),
                                        String.valueOf(order.child("address").getValue()),
                                        String.valueOf(order.child("date").getValue()),
                                        String.valueOf(order.child("time").getValue()),
                                        Integer.parseInt(String.valueOf(order.child("state").getValue())))
                                );
                            }
                            myrv = (RecyclerView) root.findViewById(R.id.recyclerview_delivery_orders);
                            myAdapter = new RecyclerViewAdapter_delivery_view_order(getActivity(), lstOrder, DeliveryOrdersFragment.this);

                            myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                            myrv.setAdapter(myAdapter);
                        }


                    }

                }
            }
        });


        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_order_delivery);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateRecycler();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return root;
    }

    public void updateRecycler() {
        lstOrder = new ArrayList<>();
        lstOrdersId = new ArrayList<>();

        mDatabase.child("Orders").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {



                    Iterable<DataSnapshot> orders = task.getResult().getChildren();


                    for (DataSnapshot order : orders) {
                        if(busy == null) {
                            if(Integer.parseInt(String.valueOf(order.child("state").getValue()))==2 || Integer.parseInt(String.valueOf(order.child("state").getValue()))==3){
                                lstOrder.add(new Order(
                                        String.valueOf(order.child("id").getValue()),
                                        String.valueOf(order.child("user_id").getValue()),
                                        String.valueOf(order.child("restaurant_id").getValue()),
                                        String.valueOf(order.child("delivery_id").getValue()),
                                        String.valueOf(order.child("dishes_summary").getValue()),
                                        String.valueOf(order.child("total").getValue()),
                                        String.valueOf(order.child("payment_method").getValue()),
                                        String.valueOf(order.child("address").getValue()),
                                        String.valueOf(order.child("date").getValue()),
                                        String.valueOf(order.child("time").getValue()),
                                        Integer.parseInt(String.valueOf(order.child("state").getValue())))
                                );
                            }

                            myAdapter = new RecyclerViewAdapter_delivery_view_order(getActivity(), lstOrder, DeliveryOrdersFragment.this);

                            myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                            myrv.setAdapter(myAdapter);

                        }
                        else {
                            if (String.valueOf(order.child("id").getValue()).equals(busy)) {

                                lstOrder.add(new Order(
                                        String.valueOf(order.child("id").getValue()),
                                        String.valueOf(order.child("user_id").getValue()),
                                        String.valueOf(order.child("restaurant_id").getValue()),
                                        String.valueOf(order.child("delivery_id").getValue()),
                                        String.valueOf(order.child("dishes_summary").getValue()),
                                        String.valueOf(order.child("total").getValue()),
                                        String.valueOf(order.child("payment_method").getValue()),
                                        String.valueOf(order.child("address").getValue()),
                                        String.valueOf(order.child("date").getValue()),
                                        String.valueOf(order.child("time").getValue()),
                                        Integer.parseInt(String.valueOf(order.child("state").getValue())))
                                );
                            }

                            myAdapter = new RecyclerViewAdapter_delivery_view_order(getActivity(), lstOrder, DeliveryOrdersFragment.this);

                            myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                            myrv.setAdapter(myAdapter);
                        }


                    }

                }
            }
        });
    }
}