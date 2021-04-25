package mobile.android.upf;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

        //Get restaurant ID
        Intent intent = getIntent();
        restaurant_id = intent.getExtras().getString("restaurant_id");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userId = mAuth.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        lstCartItem = new ArrayList<>();

        mDatabase.child("Users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    Iterable<DataSnapshot> dishes_cart = task.getResult().getChildren();



                }
            }
        });

    }

}
