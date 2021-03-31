package mobile.android.upf.ui.restaurant.restaurant_profile;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Map;

import mobile.android.upf.R;
import mobile.android.upf.ui.client.client_profile.ClientProfileViewModel;

public class RestaurantProfileFragment extends Fragment {

    private RestaurantProfileViewModel restaurantProfileViewModel;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    private TextView restaurateur_name, restaurateur_surname, restaurateur_phone, restaurateur_address, restaurateur_emailAddress;
    private EditText restaurateur_password_insert, restaurateur_passwordConfirm_insert, restaurateur_address_insert_change;
    private Button restaurateur_change_password_button, restaurateur_change_address_button;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        restaurantProfileViewModel =
                new ViewModelProvider(this).get(RestaurantProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile_restaurant, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        String userId = currentUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        restaurateur_name = root.findViewById(R.id.restaurateur_name_textview);
        restaurateur_surname = root.findViewById(R.id.restaurateur_surname_textview);
        restaurateur_phone = root.findViewById(R.id.restaurateur_phone_textview);
        restaurateur_address = root.findViewById(R.id.restaurateur_address_textview);
        restaurateur_emailAddress = root.findViewById(R.id.restaurateur_emailAddress_textview);

        restaurateur_password_insert = (EditText) root.findViewById(R.id.restaurateur_password_insert);
        restaurateur_passwordConfirm_insert = (EditText) root.findViewById(R.id.restaurateur_passwordConfirm_insert);
        restaurateur_address_insert_change = (EditText) root.findViewById(R.id.restaurateur_address_insert);

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



        restaurateur_address_insert_change.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                enableSubmitIfReady(restaurateur_address_insert_change, restaurateur_change_address_button);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                enableSubmitIfReady(restaurateur_address_insert_change, restaurateur_change_address_button);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableSubmitIfReady(restaurateur_address_insert_change, restaurateur_change_address_button);
            }
        });

        restaurateur_change_address_button = (Button) root.findViewById(R.id.restaurateur_change_address_button);
        restaurateur_change_address_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference userRef = mDatabase.child("Users").child(userId);

                Map<String, Object> updates = new HashMap<>();
                updates.put("address", restaurateur_address_insert_change.getText().toString());

                userRef.updateChildren(updates);

                // Reload information
                updateTextView(userId);

                //Clean up the edittext
                restaurateur_address_insert_change.setText("");
            }
        });

        restaurateur_password_insert.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                enablePasswordSubmitIfReady(restaurateur_password_insert,restaurateur_passwordConfirm_insert, restaurateur_change_password_button);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                enablePasswordSubmitIfReady(restaurateur_password_insert, restaurateur_passwordConfirm_insert,restaurateur_change_password_button);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enablePasswordSubmitIfReady(restaurateur_password_insert,restaurateur_passwordConfirm_insert, restaurateur_change_password_button);
            }
        });

        restaurateur_passwordConfirm_insert.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                enablePasswordSubmitIfReady(restaurateur_password_insert,restaurateur_passwordConfirm_insert, restaurateur_change_password_button);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                enablePasswordSubmitIfReady(restaurateur_password_insert, restaurateur_passwordConfirm_insert,restaurateur_change_password_button);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enablePasswordSubmitIfReady(restaurateur_password_insert,restaurateur_passwordConfirm_insert, restaurateur_change_password_button);
            }
        });

        restaurateur_change_password_button = (Button) root.findViewById(R.id.restaurateur_change_password_button);
        restaurateur_change_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference userRef = mDatabase.child("Users").child(userId);

                Map<String, Object> updates = new HashMap<>();
                updates.put("password", restaurateur_password_insert.getText().toString());

                userRef.updateChildren(updates);


                String newPassword = restaurateur_password_insert.getText().toString();

                currentUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("firebase", "User password updated.");
                        }
                        else {
                            Log.d("firebase", "User password NOT updated.");
                        }
                    }
                });
                //Clean up the edittext
                restaurateur_password_insert.setText("");
                restaurateur_passwordConfirm_insert.setText("");
            }
        });

        return root;
    }

    public void enableSubmitIfReady(EditText editText, Button button) {
        boolean isReady = editText.getText().toString().length() > 1;
        button.setEnabled(isReady);
    }

    public void enablePasswordSubmitIfReady(EditText password, EditText password_confirm, Button button) {
        boolean isReady = password.getText().toString().equals(password_confirm.getText().toString()) && password.getText().toString().length()>5;
        button.setEnabled(isReady);
    }

    public void updateTextView(String userId){
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
    }
}