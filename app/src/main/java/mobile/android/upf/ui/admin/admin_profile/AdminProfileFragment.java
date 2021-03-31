package mobile.android.upf.ui.admin.admin_profile;

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

public class AdminProfileFragment extends Fragment {

    private AdminProfileViewModel adminProfileViewModel;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        adminProfileViewModel =
                new ViewModelProvider(this).get(AdminProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile_admin, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        String userId = currentUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        TextView admin_name = root.findViewById(R.id.admin_name);
        TextView admin_surname = root.findViewById(R.id.admin_surname);
        TextView admin_phone = root.findViewById(R.id.admin_phone);
        TextView admin_address = root.findViewById(R.id.admin_address);
        TextView admin_emailAddress = root.findViewById(R.id.admin_emailAddress);

        EditText admin_password_insert = (EditText) root.findViewById(R.id.admin_password_insert);
        EditText admin_passwordConfirm_insert = (EditText) root.findViewById(R.id.admin_passwordConfirm_insert);
        EditText admin_address_insert = (EditText) root.findViewById(R.id.admin_address_insert);

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