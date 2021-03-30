package mobile.android.upf.ui.admin.admin_homepage;

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
import mobile.android.upf.ui.client.client_homepage.ClientOrdersViewModel;

public class AdminHomepageFragment extends Fragment {

    private AdminHomepageViewModel adminHomepageViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        adminHomepageViewModel =
                new ViewModelProvider(this).get(AdminHomepageViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home_admin, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        adminHomepageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}