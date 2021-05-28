package mobile.android.upf.ui.restaurant.restaurant_analytics;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import mobile.android.upf.R;
import mobile.android.upf.data.model.Dish;

public class RestaurantAnalyticsFragment extends Fragment {

    private RestaurantsAnalyticsViewModel restaurantAnalyticsViewModel;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;

    private ArrayList<Dish> lstDishes;
    private ArrayList<BarEntry> lstEntries;
    private BarDataSet barDataSet;
    private BarChart barChart;
    private String userId;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        restaurantAnalyticsViewModel =
                new ViewModelProvider(this).get(RestaurantsAnalyticsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_analytics_restaurant, container, false);

        mAuth = FirebaseAuth.getInstance();

        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();

        currentUser = mAuth.getCurrentUser();

        userId = currentUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        barChart = root.findViewById(R.id.barChart);
        lstDishes = new ArrayList<>();
        lstEntries = new ArrayList<>();

        mDatabase.child("Dishes").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    Iterable<DataSnapshot> dishes_database = task.getResult().getChildren();

                    for (DataSnapshot dish : dishes_database) {
                        Log.d("firebase ID rest", String.valueOf(dish.getKey()));
                        Log.d("firebase ID rest in iD", String.valueOf(dish.child("id").getValue()));
                        Log.d("firebase", String.valueOf(dish.child("name").getValue()));
                        if (String.valueOf(dish.child("restaurateur_id").getValue()).equals(userId)) {
                            lstDishes.add(new Dish(
                                    String.valueOf(dish.getKey()),
                                    String.valueOf(dish.child("name").getValue()),
                                    String.valueOf(dish.child("description").getValue()),
                                    String.valueOf(dish.child("restaurant_id").getValue()),
                                    Double.parseDouble(String.valueOf(dish.child("price").getValue())),
                                    Integer.parseInt(String.valueOf(dish.child("number").getValue()))));
                        }
                    }
                }
            }
        });

        for (Dish dish : lstDishes) {
            lstEntries.add(new BarEntry(Float.parseFloat(dish.getName()), dish.getNumber()));
        }


        return root;
    }
}
