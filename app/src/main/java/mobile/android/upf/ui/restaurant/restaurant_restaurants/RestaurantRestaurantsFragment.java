package mobile.android.upf.ui.restaurant.restaurant_restaurants;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import mobile.android.upf.AddRestaurantActivity;
import mobile.android.upf.R;
import mobile.android.upf.data.model.RecyclerViewAdapter_restaurant;
import mobile.android.upf.data.model.Restaurant;
import mobile.android.upf.ui.client.client_homepage.ClientEditOrdersFragment;

import static android.app.Activity.RESULT_OK;

public class RestaurantRestaurantsFragment extends Fragment {

    private RestaurantRestourantsViewModel restaurantRestourantsViewModel;

    private Button edit_restaurant_card_btn, delete_restaurant_card_btn;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    private Context context;
    private MaterialCardView cardview;
    private ViewGroup.LayoutParams layoutparams;
    private LinearLayout linearLayout;

    private RecyclerView myrv;
    private RecyclerViewAdapter_restaurant myAdapter;

    private List<Restaurant> lstRest;
    private String userId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        restaurantRestourantsViewModel =
                new ViewModelProvider(this).get(RestaurantRestourantsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_restaurants_restaurant, container, false);

//        Floating button per l'aggiunta di nuovi ristoranti
        ExtendedFloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddRestaurantActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        userId = currentUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        lstRest = new ArrayList<>();

        mDatabase.child("Restaurants").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    Iterable<DataSnapshot> restaurants_database = task.getResult().getChildren();

                    for (DataSnapshot restaurant : restaurants_database) {
//                        ID del ristorante
                        Log.d("firebase ID rest", String.valueOf(restaurant.getKey()));
                        Log.d("firebase ID rest in iD", String.valueOf(restaurant.child("id").getValue()));
                        Log.d("firebase", String.valueOf(restaurant.child("name").getValue()));
                        if (String.valueOf(restaurant.child("restaurateur_id").getValue()).equals(userId)) {
                            lstRest.add(new Restaurant(
                                    String.valueOf(restaurant.getKey()),
                                    String.valueOf(restaurant.child("name").getValue()),
                                    String.valueOf(restaurant.child("description").getValue()),
                                    String.valueOf(restaurant.child("email").getValue()),
                                    String.valueOf(restaurant.child("address").getValue()),
                                    String.valueOf(restaurant.child("phone").getValue()),
                                    String.valueOf(restaurant.child("restaurateur_id").getValue()),
                                    String.valueOf(restaurant.child("imageUrl").getValue()),
                                    Integer.valueOf(String.valueOf(restaurant.child("status").getValue()))));
                        }

                    }

                    myrv = (RecyclerView) root.findViewById(R.id.recyclerview_restaurant_restaurants);
                    myAdapter = new RecyclerViewAdapter_restaurant(getActivity(), lstRest, RestaurantRestaurantsFragment.this);

                    myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                    myrv.setAdapter(myAdapter);

                }


            }

        });



        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {

            if (resultCode == RESULT_OK) {
                Log.d("Update", "YES");
                updateRecycler();
                myAdapter.notifyDataSetChanged();
            }
        }
    }

    public void updateRecycler(){
        lstRest = new ArrayList<>();

        mDatabase.child("Restaurants").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    Iterable<DataSnapshot> restaurants_database = task.getResult().getChildren();

                    for (DataSnapshot restaurant : restaurants_database) {
//                        ID del ristorante
                        Log.d("firebase ID rest", String.valueOf(restaurant.getKey()));
                        Log.d("firebase ID rest in iD", String.valueOf(restaurant.child("id").getValue()));
                        Log.d("firebase", String.valueOf(restaurant.child("name").getValue()));
                        if (String.valueOf(restaurant.child("restaurateur_id").getValue()).equals(userId)) {
                            lstRest.add(new Restaurant(
                                    String.valueOf(restaurant.getKey()),
                                    String.valueOf(restaurant.child("name").getValue()),
                                    String.valueOf(restaurant.child("description").getValue()),
                                    String.valueOf(restaurant.child("email").getValue()),
                                    String.valueOf(restaurant.child("address").getValue()),
                                    String.valueOf(restaurant.child("phone").getValue()),
                                    String.valueOf(restaurant.child("restaurateur_id").getValue()),
                                    String.valueOf(restaurant.child("imageUrl").getValue()),
                                    Integer.valueOf(String.valueOf(restaurant.child("status").getValue()))));
                        }

                    }


                    myAdapter = new RecyclerViewAdapter_restaurant(getActivity(), lstRest, RestaurantRestaurantsFragment.this);

                    myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                    myrv.setAdapter(myAdapter);
                }
            }

        });

    }

}