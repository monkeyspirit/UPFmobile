package mobile.android.upf.ui.restaurant.restaurant_restaurants;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import mobile.android.upf.R;

public class RestaurantDeleteFragment extends DialogFragment {

    private Button confirm_delete_btn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View dialog = inflater.inflate(R.layout.dialog_fragment_delete_restaurant_restaurateur, container, false);

        confirm_delete_btn = dialog.findViewById(R.id.confirm_delete_restaurant_restaurateur_btn);
        confirm_delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}
