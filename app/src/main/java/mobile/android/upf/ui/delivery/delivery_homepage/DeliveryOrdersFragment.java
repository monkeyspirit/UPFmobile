package mobile.android.upf.ui.delivery.delivery_homepage;

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

public class DeliveryOrdersFragment extends Fragment {

    private DeliveryOrdersViewModel deliveryOrdersViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        deliveryOrdersViewModel =
                new ViewModelProvider(this).get(DeliveryOrdersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home_delivery, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        deliveryOrdersViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}