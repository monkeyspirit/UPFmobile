package mobile.android.upf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        RadioButton client = (RadioButton) findViewById(R.id.client_rdbtn);
        RadioButton rest = (RadioButton) findViewById(R.id.rest_rdbtn);
        RadioButton delivery = (RadioButton) findViewById(R.id.delivery_rdbtn);

        RadioGroup registration_group = (RadioGroup) findViewById(R.id.registration_group);
        registration_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();

                ClientRegistrationFragment client_fragment = new ClientRegistrationFragment();
                DeliveryRegistrationFragment delivery_fragment = new DeliveryRegistrationFragment();
                RestRegistratoinFragment rest_fragment = new RestRegistratoinFragment();

                switch (group.getCheckedRadioButtonId()){
                    case R.id.client_rdbtn:
                        transaction.replace(R.id.registration_fragment, client_fragment);
                        transaction.commit();
                        break;
                    case R.id.delivery_rdbtn:
                        transaction.replace(R.id.registration_fragment, delivery_fragment);
                        transaction.commit();
                        break;
                    case R.id.rest_rdbtn:
                        transaction.replace(R.id.registration_fragment, rest_fragment);
                        transaction.commit();
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + group.getCheckedRadioButtonId());
                }

            }
        });
    }
}