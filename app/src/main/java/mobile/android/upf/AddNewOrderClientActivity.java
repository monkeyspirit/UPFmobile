package mobile.android.upf;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
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
import mobile.android.upf.data.model.RecyclerViewAdapter_order_dish_client;

public class AddNewOrderClientActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    RecyclerView myrv;
    RecyclerViewAdapter_order_dish_client myAdapter;

    private static final String TAG_LOG = "AddDishOrderActivity";

    private Dish dish;
    private String restaurant_id;

    List<Dish> lstDish;
    private String userId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_order);

        // Get restaurant ID
        Intent intent = getIntent();
        restaurant_id = intent.getExtras().getString("id");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        userId = currentUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        lstDish = new ArrayList<>();

        mDatabase.child("Dishes").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    Iterable<DataSnapshot> dishes_database = task.getResult().getChildren();

                    for (DataSnapshot dish : dishes_database) {
                        Log.d("firebase", String.valueOf(dish.child("name").getValue()));
                        if (String.valueOf(dish.child("restaurant_id").getValue()).equals(restaurant_id)) {
                            lstDish.add(new Dish(
                                            String.valueOf(dish.getKey()),
                                            String.valueOf(dish.child("name").getValue()),
                                            String.valueOf(dish.child("description").getValue()),
                                            String.valueOf(dish.child("restaurant_id").getValue()),
                                            Double.parseDouble(String.valueOf(dish.child("price").getValue()))
                                    )
                            );
                        }

                    }

                    myrv = (RecyclerView) findViewById(R.id.recyclerview_restaurant_dishes_client_order);
                    myAdapter = new RecyclerViewAdapter_order_dish_client(AddNewOrderClientActivity.this, lstDish);

                    myrv.setLayoutManager(new GridLayoutManager(AddNewOrderClientActivity.this, 1));
                    myrv.setAdapter(myAdapter);

                }

            }
        });

        //        Floating button per l'aggiunta di nuovo ordine
        ExtendedFloatingActionButton fab = findViewById(R.id.fab_dish);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddNewOrderClientActivity.this, CartViewActivity.class);
                intent.putExtra("id", userId);
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
