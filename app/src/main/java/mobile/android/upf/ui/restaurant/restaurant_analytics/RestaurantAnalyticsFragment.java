package mobile.android.upf.ui.restaurant.restaurant_analytics;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
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
import mobile.android.upf.data.model.Restaurant;

public class RestaurantAnalyticsFragment extends Fragment {

    private RestaurantsAnalyticsViewModel restaurantAnalyticsViewModel;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;

    private ArrayList<Restaurant> lstRest;
    private ArrayList<Dish> lstDishes;
    private ArrayList<BarEntry> lstEntries;
    private ArrayList<String> lstLabels;
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
        lstRest = new ArrayList<>();
        lstDishes = new ArrayList<>();
        lstEntries = new ArrayList<>();
        lstLabels = new ArrayList<>();

        mDatabase.child("Restaurants").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    Iterable<DataSnapshot> restaurants_database = task.getResult().getChildren();

                    for (DataSnapshot restaurant : restaurants_database) {
                        if (String.valueOf(restaurant.child("restaurateur_id").getValue()).equals(userId)) {
                            lstRest.add(new Restaurant(
                                    String.valueOf(restaurant.getKey()),
                                    String.valueOf(restaurant.child("name").getValue()),
                                    String.valueOf(restaurant.child("description").getValue()),
                                    String.valueOf(restaurant.child("email").getValue()),
                                    String.valueOf(restaurant.child("city").getValue()),
                                    String.valueOf(restaurant.child("address").getValue()),
                                    String.valueOf(restaurant.child("phone").getValue()),
                                    String.valueOf(restaurant.child("restaurateur_id").getValue()),
                                    String.valueOf(restaurant.child("imageUrl").getValue()),
                                    Integer.parseInt(String.valueOf(restaurant.child("status").getValue()))
                            ));
                        }
                    }
                }
            }
        });

        mDatabase.child("Dishes").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Iterable<DataSnapshot> dishes_database = task.getResult().getChildren();

                    for (DataSnapshot dish : dishes_database) {
                        for (Restaurant rest : lstRest) {

                            if (String.valueOf(dish.child("restaurant_id").getValue()).equals(rest.getId())) {
                                Log.d("firebase", String.valueOf(dish.child("name").getValue()));
                                lstDishes.add(new Dish(
                                        String.valueOf(dish.getKey()),
                                        String.valueOf(dish.child("name").getValue()),
                                        String.valueOf(dish.child("description").getValue()),
                                        String.valueOf(dish.child("restaurant_id").getValue()),
                                        Double.parseDouble(String.valueOf(dish.child("price").getValue())),
                                        Integer.parseInt(String.valueOf(dish.child("number").getValue()))));
                                Log.d("LISTA", String.valueOf(lstDishes.size()));
                                break;
                            }
                        }
                    }

                    int counter = 0;
                    for (Dish dish : lstDishes) {
                        Log.d("Dish", dish.getName());
                        for (Restaurant rest : lstRest) {
                            if (rest.getId().equals(dish.getRestaurant_id())) {
                                lstEntries.add(new BarEntry(counter, dish.getNumber()));
                                lstLabels.add(dish.getName());
                                counter++;
                                break;
                            }
                        }
                    }

                    barDataSet = new BarDataSet(lstEntries, "Piatti");
                    barDataSet.setColor(Color.BLUE);
                    barDataSet.setValueTextColor(Color.DKGRAY);
                    barDataSet.setValueTextSize(16f);

                    BarData barData = new BarData(barDataSet);

                    barChart.setFitBars(true);
                    barChart.setData(barData);
                    barChart.getDescription().setText("Istogramma");
                    XAxis xAxis = barChart.getXAxis();
                    //xAxis.setLabelCount(lstEntries.size(), true);
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(lstLabels));
                    xAxis.setLabelRotationAngle(-45);
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    barChart.animateY(2000);
                }
            }
        });

        return root;
    }
}
