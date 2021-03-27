package mobile.android.upf.fragment;

import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import mobile.android.upf.R;
import mobile.android.upf.data.model.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClientRegistrationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClientRegistrationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private EditText name, surname, address, email, phone, password;
    private DatabaseReference mDatabase;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ClientRegistrationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClientRegistrationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClientRegistrationFragment newInstance(String param1, String param2) {
        ClientRegistrationFragment fragment = new ClientRegistrationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_client_registration, container, false);
        Button client_registration_btn = (Button) root.findViewById(R.id.client_register_btn);
        client_registration_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Write a message to the database
                mDatabase = FirebaseDatabase.getInstance().getReference();

                EditText name = (EditText) root.findViewById(R.id.client_name);
                EditText email = (EditText) root.findViewById(R.id.client_emailAddress);

                User user = new User(name.getText().toString(), email.getText().toString());

                mDatabase.child("users").child("2").setValue(user);

            }
        });
        return root;
    }
}