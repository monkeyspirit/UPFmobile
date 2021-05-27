package mobile.android.upf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import mobile.android.upf.data.model.Order;
import mobile.android.upf.data.model.RecyclerViewAdapter.RecyclerViewAdapter_client_view_order;
import mobile.android.upf.ui.client.client_homepage.ClientOrdersFragment;

public class ClientOrderHistory extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    RecyclerView myrv;
    RecyclerViewAdapter_client_view_order myAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    List<Order> lstOrder;
    ArrayList<String> lstOrdersId;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_order_history);

        // Back arrow
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

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

                                if(Integer.parseInt(String.valueOf(task.getResult().child("state").getValue())) == 6){
                                    lstOrder.add(new Order(
                                            String.valueOf(task.getResult().child("id").getValue()),
                                            String.valueOf(task.getResult().child("user_id").getValue()),
                                            String.valueOf(task.getResult().child("restaurant_id").getValue()),
                                            String.valueOf(task.getResult().child("restaurant_name").getValue()),
                                            String.valueOf(task.getResult().child("dishes_summary").getValue()),
                                            String.valueOf(task.getResult().child("total").getValue()),
                                            String.valueOf(task.getResult().child("payment_method").getValue()),
                                            String.valueOf(task.getResult().child("city").getValue()),
                                            String.valueOf(task.getResult().child("address").getValue()),
                                            String.valueOf(task.getResult().child("date").getValue()),
                                            String.valueOf(task.getResult().child("time").getValue()),
                                            Integer.parseInt(String.valueOf(task.getResult().child("state").getValue())))
                                    );
                                }


                                Collections.sort(lstOrder, new Comparator<Order>() {
                                    @SuppressLint("SimpleDateFormat")
                                    final
                                    DateFormat f = new SimpleDateFormat("hh:mm");
                                    @Override
                                    public int compare(Order lhs, Order rhs) {
                                        try {
                                            return Objects.requireNonNull(f.parse(rhs.getTime())).compareTo(f.parse(lhs.getTime()));
                                        } catch (ParseException e) {
                                            throw new IllegalArgumentException(e);
                                        }
                                    }
                                });

                                Collections.sort(lstOrder, new Comparator<Order>() {
                                    @SuppressLint("SimpleDateFormat")
                                    final
                                    DateFormat f = new SimpleDateFormat("dd/MM/yyyy");
                                    @Override
                                    public int compare(Order lhs, Order rhs) {
                                        try {
                                            return Objects.requireNonNull(f.parse(rhs.getDate())).compareTo(f.parse(lhs.getDate()));
                                        } catch (ParseException e) {
                                            throw new IllegalArgumentException(e);
                                        }
                                    }
                                });


                                myrv = (RecyclerView) findViewById(R.id.recyclerview_client_orders);
                                myAdapter = new RecyclerViewAdapter_client_view_order(ClientOrderHistory.this, lstOrder);
                                if(myrv != null) {
                                    myrv.setLayoutManager(new GridLayoutManager(ClientOrderHistory.this, 1));
                                    myrv.setAdapter(myAdapter);
                                }

                            }
                        });


                    }
                }

            }

        });

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_order_client);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateRecycler();
                swipeRefreshLayout.setRefreshing(false);
            }
        });




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
                                if(Integer.parseInt(String.valueOf(task.getResult().child("state").getValue())) == 6){
                                    lstOrder.add(new Order(
                                            String.valueOf(task.getResult().child("id").getValue()),
                                            String.valueOf(task.getResult().child("user_id").getValue()),
                                            String.valueOf(task.getResult().child("restaurant_id").getValue()),
                                            String.valueOf(task.getResult().child("restaurant_name").getValue()),
                                            String.valueOf(task.getResult().child("dishes_summary").getValue()),
                                            String.valueOf(task.getResult().child("total").getValue()),
                                            String.valueOf(task.getResult().child("payment_method").getValue()),
                                            String.valueOf(task.getResult().child("city").getValue()),
                                            String.valueOf(task.getResult().child("address").getValue()),
                                            String.valueOf(task.getResult().child("date").getValue()),
                                            String.valueOf(task.getResult().child("time").getValue()),
                                            Integer.parseInt(String.valueOf(task.getResult().child("state").getValue())))
                                    );
                                }

                                Collections.sort(lstOrder, new Comparator<Order>() {
                                    @SuppressLint("SimpleDateFormat")
                                    final
                                    DateFormat f = new SimpleDateFormat("hh:mm");
                                    @Override
                                    public int compare(Order lhs, Order rhs) {
                                        try {
                                            return Objects.requireNonNull(f.parse(rhs.getTime())).compareTo(f.parse(lhs.getTime()));
                                        } catch (ParseException e) {
                                            throw new IllegalArgumentException(e);
                                        }
                                    }
                                });

                                Collections.sort(lstOrder, new Comparator<Order>() {
                                    @SuppressLint("SimpleDateFormat")
                                    final
                                    DateFormat f = new SimpleDateFormat("dd/MM/yyyy");
                                    @Override
                                    public int compare(Order lhs, Order rhs) {
                                        try {
                                            return Objects.requireNonNull(f.parse(rhs.getDate())).compareTo(f.parse(lhs.getDate()));
                                        } catch (ParseException e) {
                                            throw new IllegalArgumentException(e);
                                        }
                                    }
                                });


                                myAdapter = new RecyclerViewAdapter_client_view_order(ClientOrderHistory.this, lstOrder);
                                if (myrv !=null) {
                                    myrv.setLayoutManager(new GridLayoutManager(ClientOrderHistory.this, 1));
                                    myrv.setAdapter(myAdapter);
                                }

                            }
                        });
                    }
                }

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
}