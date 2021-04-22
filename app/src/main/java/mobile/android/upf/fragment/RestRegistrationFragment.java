package mobile.android.upf.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import mobile.android.upf.R;
import mobile.android.upf.RestaurantHomepageActivity;
import mobile.android.upf.data.model.User;
import static android.widget.Toast.LENGTH_LONG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RestRegistrationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RestRegistrationFragment extends Fragment {

    private static final String TAG_LOG = "RegistrationActivity";

    private EditText editTextName, editTextSurname, editTextAddress, editTextEmail, editTextPhone,
            editTextPassword, editTextConfirmPassword;
    private final String imageUrl = "";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FirebaseAuth mAuth;

    private ProgressBar progressBar;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RestRegistrationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RestRegistratoinFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RestRegistrationFragment newInstance(String param1, String param2) {
        RestRegistrationFragment fragment = new RestRegistrationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_registration_restaurant, container, false);

        editTextName = (EditText) root.findViewById(R.id.restaurateur_name);
        editTextSurname = (EditText) root.findViewById(R.id.restaurateur_surname);
        editTextPassword = (EditText) root.findViewById(R.id.restaurateur_password);
        editTextConfirmPassword = (EditText) root.findViewById(R.id.restaurateur_passwordConfirm);
        editTextAddress = (EditText) root.findViewById(R.id.restaurateur_address);
        editTextPhone = (EditText) root.findViewById(R.id.restaurateur_phone);
        editTextEmail = (EditText) root.findViewById(R.id.restaurateur_emailAddress);

        progressBar = (ProgressBar) root.findViewById(R.id.progress_bar_restaurateur);

        Button delivery_registration_btn = (Button) root.findViewById(R.id.restaurateur_register_btn);
        delivery_registration_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.restaurateur_register_btn:
                        registerRestaurateurUser();
                        break;

                }
            }
        });
        return root;
    }

    public void registerRestaurateurUser() {

        String name = editTextName.getText().toString().trim();
        String surname = editTextSurname.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();


        if (name.isEmpty()) {
            editTextName.setError(getString(R.string.empty_name));
            editTextName.requestFocus();
            return;
        }
        if (surname.isEmpty()) {
            editTextSurname.setError(getString(R.string.empty_surname));
            editTextSurname.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            editTextEmail.setError(getString(R.string.empty_email));
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError(getString(R.string.invalid_email));
            editTextEmail.requestFocus();
            return;
        }
        if (address.isEmpty()) {
            editTextAddress.setError(getString(R.string.empty_address));
            editTextAddress.requestFocus();
            return;
        }
        if (phone.isEmpty()) {
            editTextPhone.setError(getString(R.string.empty_phone));
            editTextPhone.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editTextPassword.setError(getString(R.string.empty_password));
            editTextPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            editTextPassword.setError(getString(R.string.short_password));
            editTextPassword.requestFocus();
            return;
        }
        if (confirmPassword.isEmpty()) {
            editTextConfirmPassword.setError(getString(R.string.empty_confirm_password));
            editTextConfirmPassword.requestFocus();
            return;
        }

        if (!confirmPassword.equals(password)) {
            editTextConfirmPassword.setError(getString(R.string.password_mismatch));
            editTextConfirmPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        //creo l'utente
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            User user = new User(name, surname, password, address, phone, email, imageUrl, 3);
                            //aggiungo l'utente al db
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(getActivity(), getString(R.string.restaurateur_registration_success), LENGTH_LONG).show();

                                        final Intent restaurateur_homepage = new Intent(getActivity(), RestaurantHomepageActivity.class);
                                        startActivity(restaurateur_homepage);
                                        getActivity().finishAffinity();
                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(getActivity(), getString(R.string.restaurateur_registration_db_failed), LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), getString(R.string.restaurateur_registration_auth_failed), LENGTH_LONG).show();
                        }
                    }
                });
    }
}