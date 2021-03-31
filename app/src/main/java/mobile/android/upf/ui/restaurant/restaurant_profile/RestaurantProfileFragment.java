package mobile.android.upf.ui.restaurant.restaurant_profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import mobile.android.upf.R;
import mobile.android.upf.ui.client.client_profile.ClientProfileViewModel;

public class RestaurantProfileFragment extends Fragment {

    private RestaurantProfileViewModel restaurantProfileViewModel;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        restaurantProfileViewModel =
                new ViewModelProvider(this).get(RestaurantProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile_restaurant, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        String userId = currentUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        TextView restaurateur_name = root.findViewById(R.id.restaurateur_name);
        TextView restaurateur_surname = root.findViewById(R.id.restaurateur_surname);
        TextView restaurateur_phone = root.findViewById(R.id.restaurateur_phone);
        TextView restaurateur_address = root.findViewById(R.id.restaurateur_address);
        TextView restaurateur_emailAddress = root.findViewById(R.id.restaurateur_emailAddress);

        EditText restaurateur_password_insert = (EditText) root.findViewById(R.id.restaurateur_password_insert);
        EditText restaurateur_passwordConfirm_insert = (EditText) root.findViewById(R.id.restaurateur_passwordConfirm_insert);
        EditText restaurateur_address_insert = (EditText) root.findViewById(R.id.restaurateur_address_insert);

        mDatabase.child("Users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {

                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    restaurateur_name.setText(String.valueOf(task.getResult().child("name").getValue()));
                    restaurateur_surname.setText(String.valueOf(task.getResult().child("surname").getValue()));
                    restaurateur_phone.setText(String.valueOf(task.getResult().child("phone").getValue()));
                    restaurateur_address.setText(String.valueOf(task.getResult().child("address").getValue()));
                    restaurateur_emailAddress.setText(String.valueOf(task.getResult().child("email").getValue()));
                }
            }
        });

//        final TextView textView = root.findViewById(R.id.text_gallery);
//        clientProfileViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });



        return root;
    }
}