package mobile.android.upf.ui.client.client_homepage;

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

public class ClientOrdersFragment extends Fragment {

    private ClientOrdersViewModel clientOrdersViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        clientOrdersViewModel =
                new ViewModelProvider(this).get(ClientOrdersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home_client, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        clientOrdersViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}