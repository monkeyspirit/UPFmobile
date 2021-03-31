package mobile.android.upf.ui.delivery.delivery_profile;

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

public class DeliveryProfileFragment extends Fragment {

    private DeliveryProfileViewModel deliveryProfileViewModel;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    private TextView delivery_name, delivery_surname, delivery_phone, delivery_emailAddress;
    private EditText delivery_password_insert, delivery_passwordConfirm_insert;
    private Button delivery_change_password_button;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        deliveryProfileViewModel =
                new ViewModelProvider(this).get(DeliveryProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile_delivery, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        String userId = currentUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        delivery_name = (TextView) root.findViewById(R.id.delivery_name);
        delivery_surname = (TextView) root.findViewById(R.id.delivery_surname);
        delivery_phone = (TextView) root.findViewById(R.id.delivery_phone);
        delivery_emailAddress = (TextView) root.findViewById(R.id.delivery_emailAddress);

        delivery_password_insert = (EditText) root.findViewById(R.id.delivery_password_insert);
        delivery_passwordConfirm_insert = (EditText) root.findViewById(R.id.delivery_passwordConfirm_insert);

        mDatabase.child("Users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {

                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    delivery_name.setText(String.valueOf(task.getResult().child("name").getValue()));
                    delivery_surname.setText(String.valueOf(task.getResult().child("surname").getValue()));
                    delivery_phone.setText(String.valueOf(task.getResult().child("phone").getValue()));
                    delivery_emailAddress.setText(String.valueOf(task.getResult().child("email").getValue()));
                }
            }
        });

        delivery_password_insert.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                enableSubmitIfReady(delivery_password_insert,delivery_passwordConfirm_insert, delivery_change_password_button);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                enableSubmitIfReady(delivery_password_insert, delivery_passwordConfirm_insert,delivery_change_password_button);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableSubmitIfReady(delivery_password_insert,delivery_passwordConfirm_insert, delivery_change_password_button);
            }
        });

        delivery_change_password_button = (Button) root.findViewById(R.id.delivery_change_password_button);
        delivery_change_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference userRef = mDatabase.child("Users").child(userId);

                Map<String, Object> updates = new HashMap<>();
                updates.put("password", delivery_password_insert.getText().toString());

                userRef.updateChildren(updates);

                //Clean up the edittext
                delivery_password_insert.setText("");
                delivery_passwordConfirm_insert.setText("");
            }
        });



        return root;
    }

    public void enableSubmitIfReady(EditText password, EditText password_confirm, Button button) {
        boolean isReady = password.getText().toString().equals(password_confirm.getText().toString());
        button.setEnabled(isReady);
    }
}