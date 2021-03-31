package mobile.android.upf.ui.client.client_profile;

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

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import mobile.android.upf.R;

public class ClientProfileFragment extends Fragment {

    private ClientProfileViewModel clientProfileViewModel;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    private EditText client_address_insert, client_passwordConfirm_insert, client_password_insert;
    private TextView client_name, client_surname, client_phone, client_address, client_email;
    private Button client_change_address_button, client_change_password_button;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        clientProfileViewModel =
                new ViewModelProvider(this).get(ClientProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile_client, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        String userId = currentUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        client_name = (TextView) root.findViewById(R.id.client_name);
        client_surname = (TextView) root.findViewById(R.id.client_surname);
        client_phone = (TextView) root.findViewById(R.id.client_phone);
        client_address = (TextView) root.findViewById(R.id.client_address);
        client_email = (TextView) root.findViewById(R.id.client_emailAddress);

        client_password_insert = (EditText) root.findViewById(R.id.client_password_insert);
        client_passwordConfirm_insert = (EditText) root.findViewById(R.id.client_passwordConfirm_insert);
        client_address_insert = (EditText) root.findViewById(R.id.client_address_insert);

        mDatabase.child("Users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {

                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    client_name.setText(String.valueOf(task.getResult().child("name").getValue()));
                    client_surname.setText(String.valueOf(task.getResult().child("surname").getValue()));
                    client_phone.setText(String.valueOf(task.getResult().child("phone").getValue()));
                    client_address.setText(String.valueOf(task.getResult().child("address").getValue()));
                    client_email.setText(String.valueOf(task.getResult().child("email").getValue()));
                }
            }
        });

        client_address_insert.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                enableSubmitIfReady(client_address_insert, client_change_address_button);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                enableSubmitIfReady(client_address_insert, client_change_address_button);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableSubmitIfReady(client_address_insert, client_change_address_button);
            }
        });

        client_change_address_button = (Button) root.findViewById(R.id.client_change_address_button);
        client_change_address_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference userRef = mDatabase.child("Users").child(userId);

                Map<String, Object> updates = new HashMap<>();
                updates.put("address", client_address_insert.getText().toString());

                userRef.updateChildren(updates);

                // Reload information
                updateTextView(userId);

                //Clean up the edittext
                client_address_insert.setText("");
            }
        });

        client_password_insert.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                enablePasswordSubmitIfReady(client_password_insert,client_passwordConfirm_insert, client_change_password_button);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                enablePasswordSubmitIfReady(client_password_insert, client_passwordConfirm_insert,client_change_password_button);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enablePasswordSubmitIfReady(client_password_insert,client_passwordConfirm_insert, client_change_password_button);
            }
        });

        client_passwordConfirm_insert.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                enablePasswordSubmitIfReady(client_password_insert,client_passwordConfirm_insert, client_change_password_button);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                enablePasswordSubmitIfReady(client_password_insert, client_passwordConfirm_insert,client_change_password_button);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enablePasswordSubmitIfReady(client_password_insert,client_passwordConfirm_insert, client_change_password_button);
            }
        });

        client_change_password_button = (Button) root.findViewById(R.id.client_change_password_button);
        client_change_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference userRef = mDatabase.child("Users").child(userId);

                Map<String, Object> updates = new HashMap<>();
                updates.put("password", client_password_insert.getText().toString());

                userRef.updateChildren(updates);


                String newPassword = client_password_insert.getText().toString();

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
                client_password_insert.setText("");
                client_passwordConfirm_insert.setText("");
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
                    client_name.setText(String.valueOf(task.getResult().child("name").getValue()));
                    client_surname.setText(String.valueOf(task.getResult().child("surname").getValue()));
                    client_phone.setText(String.valueOf(task.getResult().child("phone").getValue()));
                    client_address.setText(String.valueOf(task.getResult().child("address").getValue()));
                    client_email.setText(String.valueOf(task.getResult().child("email").getValue()));
                }
            }
        });
    }
}