package mobile.android.upf.ui.client_profile;

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

public class ClientProfileFragment extends Fragment {

    private ClientProfileViewModel clientProfileViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        clientProfileViewModel =
                new ViewModelProvider(this).get(ClientProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile_client, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        clientProfileViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}