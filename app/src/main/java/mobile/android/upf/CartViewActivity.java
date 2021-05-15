package mobile.android.upf;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;

import mobile.android.upf.data.model.Dish;
import mobile.android.upf.data.model.Order;
import mobile.android.upf.data.model.RecyclerViewAdapter.RecyclerViewAdapter_cart;

public class CartViewActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;
    private RecyclerView myrv;
    private RecyclerViewAdapter_cart myAdapter;

    private List<Dish> lstCartItem;
    private String restaurant_id;
    private String userId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Back arrow
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userId = mAuth.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        lstCartItem = new ArrayList<>();

        StepView stepView = findViewById(R.id.step_view);
        stepView.getState()
                // You should specify only stepsNumber or steps array of strings.
                // In case you specify both steps array is chosen.
                .steps(new ArrayList<String>() {{
                    add(getApplicationContext().getString(R.string.address_payment));
                    add(getApplicationContext().getString(R.string.summary));
                    add(getApplicationContext().getString(R.string.payment));
                }})

                .animationDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                .stepLineWidth(1)
                .textSize(getResources().getDimensionPixelSize(R.dimen.text_size_small))
                .stepNumberTextSize(getResources().getDimensionPixelSize(R.dimen.text_size_small))
                .commit();


        mDatabase.child("Cart").child(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    Iterable<DataSnapshot> dishes_database = task.getResult().getChildren();

                    for (DataSnapshot dish : dishes_database) {

                        lstCartItem.add(new Dish(
                                        String.valueOf(dish.getKey()),
                                        String.valueOf(dish.child("name").getValue()),
                                        String.valueOf(dish.child("description").getValue()),
                                        String.valueOf(dish.child("restaurant_id").getValue()),
                                        Double.parseDouble(String.valueOf(dish.child("price").getValue())),
                                        Integer.parseInt(String.valueOf(dish.child("number").getValue()))
                                )
                        );

                    }

                    myrv = (RecyclerView) findViewById(R.id.recyclerview_cart_dishes_client);
                    myAdapter = new RecyclerViewAdapter_cart(CartViewActivity.this, lstCartItem);

                    myrv.setLayoutManager(new GridLayoutManager(CartViewActivity.this, 1));
                    myrv.setAdapter(myAdapter);

                }

            }
        });


        ExtendedFloatingActionButton fab = findViewById(R.id.fab_dish);
        mDatabase.child("Cart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(mAuth.getCurrentUser().getUid())) {
                    fab.setEnabled(true);
                    fab.setVisibility(View.VISIBLE);
                } else {
                    fab.setEnabled(false);
                    fab.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartViewActivity.this, OrderAddressCardInsertActivity.class);
                startActivityForResult(intent, 1);
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

}
