package mobile.android.upf.ui.client_restaurants;

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

import mobile.android.upf.R;

public class ClientRestaurantsFragment extends Fragment {

    private ClientRestourantsViewModel clientRestourantsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        clientRestourantsViewModel =
                new ViewModelProvider(this).get(ClientRestourantsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_restaurants_client, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);
        clientRestourantsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}