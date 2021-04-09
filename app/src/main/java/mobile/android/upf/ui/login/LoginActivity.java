package mobile.android.upf.ui.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import mobile.android.upf.AdminHomepageActivity;
import mobile.android.upf.ClientHomepageActivity;
import mobile.android.upf.DeliveryHomepageActivity;
import mobile.android.upf.R;
import mobile.android.upf.RegistrationActivity;
import mobile.android.upf.RestaurantHomepageActivity;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private DatabaseReference mDatabase;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private static final String TAG_LOG = "LoginActivity";

    private Button login_admin_pre_btn,login_client_pre_btn, login_rest_pre_btn,login_del_pre_btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("Login UPF");
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final Button loginButton = findViewById(R.id.login);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlloginUser();
            }
        });




        Button registrationButton =  (Button) findViewById(R.id.registration);
        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent registration = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(registration);
            }
        });

        // LOGIN RAPIDO

        login_admin_pre_btn = (Button) findViewById(R.id.login_admin_pre_btn);
        login_client_pre_btn = (Button) findViewById(R.id.login_client_pre_btn);
        login_rest_pre_btn = (Button) findViewById(R.id.login_rest_pre_btn);
        login_del_pre_btn = (Button) findViewById(R.id.login_del_pre_btn);

        login_admin_pre_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser("admin@gmail.com", "admin1");
            }
        });
        login_client_pre_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser("mario.rossi@gmail.com", "123456");
            }
        });
        login_rest_pre_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser("salvo.calogero@gmail.com", "123456");
            }
        });
        login_del_pre_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser("corrado.collodi@gmail.com", "123456");
            }
        });

    }

    private void updateUI(FirebaseUser user) {

    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
    }

    private void controlloginUser(){
        final EditText email_edittext = findViewById(R.id.email_edittext);
        final EditText password_edittext = findViewById(R.id.password_edittext);

        String email = email_edittext.getText().toString();
        String password = password_edittext.getText().toString();

        if (email.isEmpty()) {
            email_edittext.setError(getString(R.string.empty_email));
            email_edittext.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            password_edittext.setError(getString(R.string.empty_password));
            password_edittext.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        loginUser(email, password);
    }

    private void loginUser(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG_LOG, "signInWithEmail:success");
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            String userId = currentUser.getUid();


                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            mDatabase.child("Users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (!task.isSuccessful()) {
                                        progressBar.setVisibility(View.GONE);
                                        Log.e("firebase", "Error getting data", task.getException());
                                    }
                                    else {
                                        progressBar.setVisibility(View.GONE);
                                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
                                        Toast.makeText(LoginActivity.this, getString(R.string.authentication_success), Toast.LENGTH_LONG).show();
                                        int type = Integer.parseInt(String.valueOf(task.getResult().child("type").getValue()));

                                        switch (type){
                                            case 1: {
//                                                client
                                                final Intent client_homepage = new Intent(LoginActivity.this, ClientHomepageActivity.class);
                                                startActivity(client_homepage);
                                                finish();
                                                break;
                                            }
                                            case 2:{
//                                                delivery
                                                final Intent delivery_homepage = new Intent(LoginActivity.this, DeliveryHomepageActivity.class);
                                                startActivity(delivery_homepage);
                                                finish();
                                                break;
                                            }
                                            case 3:{
//                                                restaurant
                                                final Intent restaurant_homepage = new Intent(LoginActivity.this, RestaurantHomepageActivity.class);
                                                startActivity(restaurant_homepage);
                                                finish();
                                                break;
                                            }
                                            case 4:{
//                                                admin
                                                final Intent admin_homepage = new Intent(LoginActivity.this, AdminHomepageActivity.class);
                                                startActivity(admin_homepage);
                                                finish();
                                                break;
                                            }
                                        }
                                    }
                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            progressBar.setVisibility(View.GONE);
                            Log.w(TAG_LOG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, getString(R.string.authentication_failed),
                                    Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }
                    }
                });
    }


}