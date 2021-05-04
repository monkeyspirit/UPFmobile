package mobile.android.upf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import mobile.android.upf.data.model.Dish;
import mobile.android.upf.data.model.RecyclerViewAdapter.RecyclerViewAdapter_cart;

public class OrderAddressCardInsertActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    private String userId;

    private EditText order_address;
    private LinearLayout card_parameter;
    private RadioGroup payment_method;
    private RadioButton card_radio_btn;
    private EditText card_1316digit,card_expmonth, card_expyear, card_namePossessor;
    private Button summary_btn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_address_card_insert);
        // Back arrow
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userId = mAuth.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        order_address = (EditText) findViewById(R.id.order_address);
        mDatabase.child("Users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    order_address.setText(String.valueOf(task.getResult().child("address").getValue()));

                }
            }}
        );

        card_parameter = (LinearLayout) findViewById(R.id.card_parameter);

        card_1316digit= (EditText) findViewById(R.id.card_1316digit);
        card_expmonth= (EditText) findViewById(R.id.card_expmonth);
        card_expyear= (EditText) findViewById(R.id.card_expyear);


        card_namePossessor= (EditText) findViewById(R.id.card_namePossessor);

        card_radio_btn = (RadioButton) findViewById(R.id.card_btn);

        summary_btn = (Button) findViewById(R.id.summary_btn);

        payment_method = (RadioGroup) findViewById(R.id.payment_method);
        payment_method.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d("Check id", String.valueOf(group.getCheckedRadioButtonId()));
                if (group.getCheckedRadioButtonId() == card_radio_btn.getId()){
                    card_parameter.setVisibility(View.VISIBLE);
// ADD YEAR AND MONTH CHECK!!!
                }
                else {
                    card_parameter.setVisibility(View.INVISIBLE);
                }
            }
        });

        summary_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderAddressCardInsertActivity.this, CartCheckout.class);
                String card_expiration = card_expmonth.getText().toString()+"/"+card_expyear.getText().toString();
                String card_possessor = card_namePossessor.getText().toString();
                String card_lastdigit = card_1316digit.getText().toString();
                intent.putExtra("expiration", card_expiration);
                intent.putExtra("possessor", card_possessor);
                intent.putExtra("last_digit", card_lastdigit);
                startActivityForResult(intent, 1);
            }
        });





    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }




}