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

            }
        });


        restauratour_r_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


//        RadioButton client = (RadioButton) findViewById(R.id.client_rdbtn);
//        RadioButton rest = (RadioButton) findViewById(R.id.rest_rdbtn);
//        RadioButton delivery = (RadioButton) findViewById(R.id.delivery_rdbtn);
//
//        RadioGroup registration_group = (RadioGroup) findViewById(R.id.registration_group);
//        registration_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                FragmentManager manager = getFragmentManager();
//                FragmentTransaction transaction = manager.beginTransaction();
//
//                ClientRegistrationFragment client_fragment = new ClientRegistrationFragment();
//                DeliveryRegistrationFragment delivery_fragment = new DeliveryRegistrationFragment();
//                RestRegistrationFragment rest_fragment = new RestRegistrationFragment();
//
//                switch (group.getCheckedRadioButtonId()){
//                    case R.id.client_rdbtn:
//                        transaction.replace(R.id.registration_fragment, client_fragment);
//                        transaction.commit();
//                        break;
//                    case R.id.delivery_rdbtn:
//                        transaction.replace(R.id.registration_fragment, delivery_fragment);
//                        transaction.commit();
//                        break;
//                    case R.id.rest_rdbtn:
//                        transaction.replace(R.id.registration_fragment, rest_fragment);
//                        transaction.commit();
//                        break;
//                    default:
//                        throw new IllegalStateException("Unexpected value: " + group.getCheckedRadioButtonId());
//                }
//
//            }
//        });


    }
}