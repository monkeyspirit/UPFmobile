package mobile.android.upf.ui.restaurant.restaurant_restaurants;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import mobile.android.upf.AddRestaurantActivity;
import mobile.android.upf.R;
import mobile.android.upf.ui.client.client_restaurants.ClientRestourantsViewModel;

public class RestaurantRestaurantsFragment extends Fragment {

    private RestaurantRestourantsViewModel restaurantRestourantsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        restaurantRestourantsViewModel =
                new ViewModelProvider(this).get(RestaurantRestourantsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_restaurants_restaurant, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);
        restaurantRestourantsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddRestaurantActivity.class);
                startActivity(intent);
            }
        });


        return root;
    }
}