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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    Context context;
    MaterialCardView cardview;
    LayoutParams layoutparams;
    LinearLayout linearLayout;

    RecyclerView myrv;
    RecyclerViewAdapter_client_view_order myAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;


    //    Solo di prova
    List<Order> lstOrder;
//


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        clientOrdersViewModel = new ViewModelProvider(this).get(ClientOrdersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home_client, container, false);

        lstOrder = new ArrayList<>();


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        String userId = currentUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("Users").child(userId).child("Orders").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
                                String.valueOf(order.child("time").getValue()))
                        );
                    }


                    myrv = (RecyclerView) root.findViewById(R.id.recyclerview_client_orders);
                    myAdapter = new RecyclerViewAdapter_client_view_order(getActivity(), lstOrder);

                    myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                    myrv.setAdapter(myAdapter);

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

    private void updateRecycler() {
        lstOrder = new ArrayList<>();
        mDatabase.child("Users").child(currentUser.getUid()).child("Orders").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
                                String.valueOf(order.child("time").getValue()))
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