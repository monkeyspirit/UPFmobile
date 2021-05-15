package mobile.android.upf;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import mobile.android.upf.ui.login.LoginActivity;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ClientHomepageActivity extends AppCompatActivity {


    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;
    private static final String TAG_LOG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_homepage);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_client_orders, R.id.nav_client_profile, R.id.nav_client_restaurants, R.id.nav_client_logout, R.id.nav_client_notifications)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.getMenu().findItem(R.id.nav_client_logout).setOnMenuItemClickListener(menuItem -> {
            FirebaseAuth.getInstance().signOut();
            Log.d(TAG_LOG, "logout:success");
            Intent intent = new Intent(ClientHomepageActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        });

        mDatabase.child("Notifications").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getKey().equals(currentUser.getUid())){
                    mDatabase.child("Notifications").child(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            Iterable<DataSnapshot> notifications = task.getResult().getChildren();

                            for (DataSnapshot notification : notifications) {
                                if (String.valueOf(notification.child("state").getValue()).equals("1")){
                                    notification(String.valueOf(notification.child("content").getValue()));
                                    mDatabase.child("Notifications").child(currentUser.getUid()).child(notification.getKey()).child("state").setValue("2");
                                }


                            }

                        }
                    });


                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void notification(String data){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel =  new NotificationChannel("a_n","approvation_notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "a_n")
                .setContentTitle(getApplicationContext().getString(R.string.restaurant_update))
                .setSmallIcon(R.drawable.ic_baseline_local_pizza_24)
                .setAutoCancel(true)
                .setContentText(data);


        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getApplicationContext());
        managerCompat.notify(999, builder.build());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.client_homepage, menu);

        TextView nav_header_user = (TextView) findViewById(R.id.nav_header_user);
        CircularImageView nav_header_image = (CircularImageView) findViewById(R.id.nav_header_imageView);

        String userId = currentUser.getUid();

        mDatabase.child("Users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    nav_header_user.setText(String.valueOf(task.getResult().child("name").getValue())+" "+String.valueOf(task.getResult().child("surname").getValue()));

                    String path = String.valueOf(task.getResult().child("imageUrl").getValue());
                    if (path != "") {
                        Uri uri = Uri.parse(String.valueOf(task.getResult().child("imageUrl").getValue()));
                        Log.d("firebase", "Image Url: " + uri);
                        Glide.with(getApplicationContext()).load(uri).into(nav_header_image);
                    }

                }
            }
        });
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


}