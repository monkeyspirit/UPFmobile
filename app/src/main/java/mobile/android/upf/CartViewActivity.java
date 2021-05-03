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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import mobile.android.upf.data.model.Dish;
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

        //Get restaurant ID
        Intent intent = getIntent();
        restaurant_id = intent.getExtras().getString("restaurant_id");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userId = mAuth.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        lstCartItem = new ArrayList<>();

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

        //        Floating button per l'aggiunta di nuovo ordine
        ExtendedFloatingActionButton fab = findViewById(R.id.fab_dish);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(CartViewActivity.this, CartViewActivity.class);
//                intent.putExtra("id", userId);
//                startActivityForResult(intent, 1);
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
