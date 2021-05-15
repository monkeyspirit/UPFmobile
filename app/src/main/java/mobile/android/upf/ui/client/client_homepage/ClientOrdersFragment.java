package mobile.android.upf.ui.client.client_homepage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import mobile.android.upf.R;
import mobile.android.upf.data.model.Dish;
import mobile.android.upf.data.model.Order;
import mobile.android.upf.data.model.RecyclerViewAdapter.RecyclerViewAdapter_client_view_order;

public class ClientOrdersFragment extends Fragment {

    private ClientOrdersViewModel clientOrdersViewModel;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    RecyclerView myrv;
    RecyclerViewAdapter_client_view_order myAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    List<Order> lstOrder;
    ArrayList<String> lstOrdersId;
    String userId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        clientOrdersViewModel = new ViewModelProvider(this).get(ClientOrdersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home_client, container, false);

        lstOrder = new ArrayList<>();
        lstOrdersId = new ArrayList<>();


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        userId = currentUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("Orders").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                updateRecycler();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mDatabase.child("Users").child(userId).child("Orders").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {

                    Iterable<DataSnapshot> orders_id = task.getResult().getChildren();

                    for (DataSnapshot order_id : orders_id) {
                        lstOrdersId.add(order_id.getKey());
                    }

                    for (String order_id : lstOrdersId){
                        Log.d("firebase id", String.valueOf(order_id));
                        mDatabase.child("Orders").child(order_id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {

                                Log.d("Order", String.valueOf(task.getResult()));
                                lstOrder.add(new Order(
                                        String.valueOf(task.getResult().child("id").getValue()),
                                        String.valueOf(task.getResult().child("user_id").getValue()),
                                        String.valueOf(task.getResult().child("restaurant_id").getValue()),
                                        String.valueOf(task.getResult().child("restaurant_name").getValue()),
                                        String.valueOf(task.getResult().child("dishes_summary").getValue()),
                                        String.valueOf(task.getResult().child("total").getValue()),
                                        String.valueOf(task.getResult().child("payment_method").getValue()),
                                        String.valueOf(task.getResult().child("address").getValue()),
                                        String.valueOf(task.getResult().child("date").getValue()),
                                        String.valueOf(task.getResult().child("time").getValue()),
                                        Integer.parseInt(String.valueOf(task.getResult().child("state").getValue())))
                                );

                                myrv = (RecyclerView) root.findViewById(R.id.recyclerview_client_orders);
                                myAdapter = new RecyclerViewAdapter_client_view_order(getActivity(), lstOrder, ClientOrdersFragment.this);

                                myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                                myrv.setAdapter(myAdapter);

                            }
                        });


                    }


                }

            }

        });



        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_order_client);

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

        mDatabase.child("Users").child(userId).child("Orders").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {

                    Iterable<DataSnapshot> orders_id = task.getResult().getChildren();

                    for (DataSnapshot order_id : orders_id) {
                        lstOrdersId.add(order_id.getKey());
                    }

                    Log.d("length id", String.valueOf(lstOrdersId.size()));
                    for (String order_id : lstOrdersId){
                        Log.d("firebase id", String.valueOf(order_id));
                        mDatabase.child("Orders").child(order_id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {

                                Log.d("Order", String.valueOf(task.getResult()));
                                lstOrder.add(new Order(
                                                String.valueOf(task.getResult().child("id").getValue()),
                                                String.valueOf(task.getResult().child("user_id").getValue()),
                                                String.valueOf(task.getResult().child("restaurant_id").getValue()),
                                        String.valueOf(task.getResult().child("restaurant_name").getValue()),
                                                String.valueOf(task.getResult().child("dishes_summary").getValue()),
                                                String.valueOf(task.getResult().child("total").getValue()),
                                                String.valueOf(task.getResult().child("paymemt_method").getValue()),
                                                String.valueOf(task.getResult().child("address").getValue()),
                                                String.valueOf(task.getResult().child("date").getValue()),
                                                String.valueOf(task.getResult().child("time").getValue()),
                                                Integer.parseInt(String.valueOf(task.getResult().child("state").getValue())))
                                );

                                myAdapter = new RecyclerViewAdapter_client_view_order(getActivity(), lstOrder, ClientOrdersFragment.this);

                                myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                                myrv.setAdapter(myAdapter);

                            }
                        });
                    }
                }

            }

        });


    }

}