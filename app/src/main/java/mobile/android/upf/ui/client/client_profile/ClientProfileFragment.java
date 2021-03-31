package mobile.android.upf.ui.client.client_profile;

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

import org.w3c.dom.Text;

import mobile.android.upf.R;

public class ClientProfileFragment extends Fragment {

    private ClientProfileViewModel clientProfileViewModel;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        clientProfileViewModel =
                new ViewModelProvider(this).get(ClientProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile_client, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        String userId = currentUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        TextView client_name = (TextView) root.findViewById(R.id.client_name);
        TextView client_surname = (TextView) root.findViewById(R.id.client_surname);
        TextView client_phone = (TextView) root.findViewById(R.id.client_phone);
        TextView client_address = (TextView) root.findViewById(R.id.client_address);
        TextView client_email = (TextView) root.findViewById(R.id.client_emailAddress);

        EditText client_password_insert = (EditText) root.findViewById(R.id.client_password_insert);
        EditText client_passwordConfirm_insert = (EditText) root.findViewById(R.id.client_passwordConfirm_insert);
        EditText client_address_insert = (EditText) root.findViewById(R.id.client_address_insert);

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