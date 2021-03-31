package mobile.android.upf.ui.admin.admin_profile;

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

public class AdminProfileFragment extends Fragment {

    private AdminProfileViewModel adminProfileViewModel;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    private TextView admin_name, admin_surname, admin_phone, admin_address, admin_emailAddress;
    private EditText admin_password_insert, admin_passwordConfirm_insert, admin_address_insert;
    private Button admin_change_address_button, admin_change_password_button;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        adminProfileViewModel =
                new ViewModelProvider(this).get(AdminProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile_admin, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        String userId = currentUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        admin_name = root.findViewById(R.id.admin_name_textview);
        admin_surname = root.findViewById(R.id.admin_surname_textview);
        admin_phone = root.findViewById(R.id.admin_phone_textview);
        admin_address = root.findViewById(R.id.admin_address_textview);
        admin_emailAddress = root.findViewById(R.id.admin_emailAddress_textview);

        admin_password_insert = (EditText) root.findViewById(R.id.admin_password_insert);
        admin_passwordConfirm_insert = (EditText) root.findViewById(R.id.admin_passwordConfirm_insert);
        admin_address_insert = (EditText) root.findViewById(R.id.admin_address_insert);

        mDatabase.child("Users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {

                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    admin_name.setText(String.valueOf(task.getResult().child("name").getValue()));
                    admin_surname.setText(String.valueOf(task.getResult().child("surname").getValue()));
                    admin_phone.setText(String.valueOf(task.getResult().child("phone").getValue()));
                    admin_address.setText(String.valueOf(task.getResult().child("address").getValue()));
                    admin_emailAddress.setText(String.valueOf(task.getResult().child("email").getValue()));
                }
            }
        });

        admin_address_insert.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                enableSubmitIfReady(admin_address_insert, admin_change_address_button);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                enableSubmitIfReady(admin_address_insert, admin_change_address_button);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableSubmitIfReady(admin_address_insert, admin_change_address_button);
            }
        });

        admin_change_address_button = (Button) root.findViewById(R.id.admin_change_address_button);
        admin_change_address_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference userRef = mDatabase.child("Users").child(userId);

                Map<String, Object> updates = new HashMap<>();
                updates.put("address", admin_address_insert.getText().toString());

                userRef.updateChildren(updates);

                // Reload information
                updateTextView(userId);

                //Clean up the edittext
                admin_address_insert.setText("");
            }
        });

        admin_password_insert.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                enablePasswordSubmitIfReady(admin_password_insert,admin_passwordConfirm_insert, admin_change_password_button);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                enablePasswordSubmitIfReady(admin_password_insert, admin_passwordConfirm_insert,admin_change_password_button);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enablePasswordSubmitIfReady(admin_password_insert,admin_passwordConfirm_insert, admin_change_password_button);
            }
        });

        admin_passwordConfirm_insert.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                enablePasswordSubmitIfReady(admin_password_insert,admin_passwordConfirm_insert, admin_change_password_button);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                enablePasswordSubmitIfReady(admin_password_insert, admin_passwordConfirm_insert,admin_change_password_button);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enablePasswordSubmitIfReady(admin_password_insert,admin_passwordConfirm_insert, admin_change_password_button);
            }
        });

        admin_change_password_button = (Button) root.findViewById(R.id.admin_change_password_button);
        admin_change_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference userRef = mDatabase.child("Users").child(userId);

                Map<String, Object> updates = new HashMap<>();
                updates.put("password", admin_password_insert.getText().toString());

                userRef.updateChildren(updates);


                String newPassword = admin_password_insert.getText().toString();

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
                admin_password_insert.setText("");
                admin_passwordConfirm_insert.setText("");
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
                    admin_name.setText(String.valueOf(task.getResult().child("name").getValue()));
                    admin_surname.setText(String.valueOf(task.getResult().child("surname").getValue()));
                    admin_phone.setText(String.valueOf(task.getResult().child("phone").getValue()));
                    admin_address.setText(String.valueOf(task.getResult().child("address").getValue()));
                    admin_emailAddress.setText(String.valueOf(task.getResult().child("email").getValue()));
                }
            }
        });
    }
}