package mobile.android.upf.ui.admin.admin_homepage;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
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

import mobile.android.upf.R;
import mobile.android.upf.data.model.RecyclerViewAdapter_admin;
import mobile.android.upf.data.model.RecyclerViewAdapter_restaurant;
import mobile.android.upf.data.model.Restaurant;
import mobile.android.upf.ui.client.client_homepage.ClientOrdersViewModel;
import mobile.android.upf.ui.restaurant.restaurant_restaurants.RestaurantRestaurantsFragment;

public class AdminHomepageFragment extends Fragment {

    private AdminHomepageViewModel adminHomepageViewModel;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    private List<Restaurant> lstRest;
    private RecyclerView myrv;
    private RecyclerViewAdapter_admin myAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        adminHomepageViewModel =
                new ViewModelProvider(this).get(AdminHomepageViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home_admin, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

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

                        if (String.valueOf(restaurant.child("status").getValue()).equals("0")) {
                            lstRest.add(new Restaurant(
                                    String.valueOf(restaurant.getKey()),
                                    String.valueOf(restaurant.child("name").getValue()),
                                    String.valueOf(restaurant.child("description").getValue()),
                                    String.valueOf(restaurant.child("email").getValue()),
                                    String.valueOf(restaurant.child("address").getValue()),
                                    String.valueOf(restaurant.child("phone").getValue()),
                                    String.valueOf(restaurant.child("restaurateur_id").getValue()),
                                    String.valueOf(restaurant.child("imageUrl").getValue()),
                                    Integer.parseInt(String.valueOf(restaurant.child("status").getValue()))));
                        }

                    }

                    myrv = (RecyclerView) root.findViewById(R.id.recyclerview_admin_restaurants);
                    myAdapter = new RecyclerViewAdapter_admin(getActivity(), lstRest, AdminHomepageFragment.this);

                    myrv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                    myrv.setAdapter(myAdapter);

                }


            }

        });

        return root;
    }
}