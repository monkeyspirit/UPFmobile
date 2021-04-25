package mobile.android.upf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getSupportActionBar().setTitle(R.string.register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button client_r_btn = (Button) findViewById(R.id.client_r_btn);
        Button delivery_r_btn = (Button) findViewById(R.id.delivery_r_btn);
        Button restauratour_r_btn = (Button) findViewById(R.id.restauratour_r_btn);

        client_r_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, ClientRegistrationActivity.class);
                startActivity(intent);
            }
        });

        delivery_r_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, DeliveryRegistrationActivity.class);
                startActivity(intent);
            }
        });


        restauratour_r_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, RestaurateurRegistrationActivity.class);
                startActivity(intent);
            }
        });


    }
}