package mobile.android.upf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
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

import java.util.ArrayList;
import java.util.List;

import mobile.android.upf.data.model.Dish;
import mobile.android.upf.data.model.RecyclerViewAdapter.RecyclerViewAdapter_client_order_dish;

import static android.widget.Toast.LENGTH_LONG;

public class AddNewOrderClientActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    RecyclerView myrv;
    RecyclerViewAdapter_client_order_dish myAdapter;

    private static final String TAG_LOG = "AddDishOrderActivity";

    private Dish dish;
    private String restaurant_id;

    List<Dish> lstDish;
    private String userId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_order);

        //        Back arrow
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

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
                    myAdapter = new RecyclerViewAdapter_client_order_dish(AddNewOrderClientActivity.this, lstDish);

                    myrv.setLayoutManager(new GridLayoutManager(AddNewOrderClientActivity.this, 1));
                    myrv.setAdapter(myAdapter);

                }

            }
        });

        // Floating button per la visione del carrello
        ExtendedFloatingActionButton fab = findViewById(R.id.fab_dish);
        final boolean[] enabled = {false};

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
                Intent intent = new Intent(AddNewOrderClientActivity.this, CartViewActivity.class);
                intent.putExtra("id", userId);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            AlertDialog myQuittingDialogBox = new AlertDialog.Builder(AddNewOrderClientActivity.this)
                    // set message, title, and icon
                    .setTitle(getString(R.string.cart_exit))
                    .setMessage(getString(R.string.cart_exit_delete))
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                        }
                    })
                    .setPositiveButton(getString(R.string.exit), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mDatabase.child("Cart").child(currentUser.getUid()).removeValue();
                            AddNewOrderClientActivity.this.finish();

                        }
                    })
                    .create();
            myQuittingDialogBox.show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
