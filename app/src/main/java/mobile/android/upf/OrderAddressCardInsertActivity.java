package mobile.android.upf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OrderAddressCardInsertActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    private String userId;

    private EditText order_address;
    private LinearLayout card_parameter;
    private RadioGroup payment_method;
    private RadioButton card_radio_btn;
    private EditText card_14digit,card_58digit,card_912digit,card_1316digit, card_cvv,card_expmonth, card_expyear, card_namePossessor;
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

        card_cvv = (EditText) findViewById(R.id.card_cvv);

        card_14digit= (EditText) findViewById(R.id.card_14digit);
        card_58digit= (EditText) findViewById(R.id.card_58digit);
        card_912digit= (EditText) findViewById(R.id.card_912digit);
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
                }
                else {
                    card_parameter.setVisibility(View.INVISIBLE);
                }
            }
        });


        summary_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (payment_method.getCheckedRadioButtonId() != card_radio_btn.getId()){
                    Intent intent = new Intent(OrderAddressCardInsertActivity.this, CartCheckout.class);
                    intent.putExtra("card", false);
                    startActivityForResult(intent, 1);
                }
                else{
                    checkCardInput();
                }

            }
        });

    }

    public void checkCardInput(){

        if(order_address.getText().toString().isEmpty()){
            order_address.requestFocus();
            order_address.setError(getString(R.string.empty_address));
            return;
        }
        if(card_14digit.getText().toString().isEmpty()){
            card_14digit.requestFocus();
            card_14digit.setError(getString(R.string.empty_card));
            return;
        }
        if(card_58digit.getText().toString().isEmpty()){
            card_58digit.requestFocus();
            card_58digit.setError(getString(R.string.empty_card));
            return;
        }
        if(card_912digit.getText().toString().isEmpty()){
            card_912digit.requestFocus();
            card_912digit.setError(getString(R.string.empty_card));
            return;
        }
        if(card_1316digit.getText().toString().isEmpty()){
            card_1316digit.requestFocus();
            card_1316digit.setError(getString(R.string.empty_card));
            return;
        }
        if(card_namePossessor.getText().toString().isEmpty()){
            card_namePossessor.setError(getString(R.string.empty_name));
            card_namePossessor.requestFocus();
            return;
        }
        if(card_expmonth.getText().toString().isEmpty()){
            card_expmonth.requestFocus();
            card_expmonth.setError(getString(R.string.empty_card_exp));
            return;
        }
        if(Integer.parseInt(card_expmonth.getText().toString())>13){
            card_expmonth.requestFocus();
            card_expmonth.setError(getString(R.string.number_error));
            return;
        }
        if(card_expyear.getText().toString().isEmpty()){
            card_expyear.requestFocus();
            card_expyear.setError(getString(R.string.empty_card_exp));
            return;
        }
        if(Integer.parseInt(card_expyear.getText().toString())< 21){
            card_expyear.requestFocus();
            card_expyear.setError(getString(R.string.number_error));
            return;
        }
        if(card_cvv.getText().toString().isEmpty()){
            card_cvv.requestFocus();
            card_cvv.setError(getString(R.string.empty_card_cvv));
            return;
        }

        Intent intent = new Intent(OrderAddressCardInsertActivity.this, CartCheckout.class);
        String card_expiration = card_expmonth.getText().toString()+"/"+card_expyear.getText().toString();
        String card_possessor = card_namePossessor.getText().toString();
        String card_lastdigits = card_1316digit.getText().toString();
        intent.putExtra("card", true);
        intent.putExtra("expiration", card_expiration);
        intent.putExtra("possessor", card_possessor);
        intent.putExtra("last_digits", card_lastdigits);
        startActivityForResult(intent, 1);


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