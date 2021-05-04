package mobile.android.upf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CartCheckout extends AppCompatActivity {

    private TextView order_summary, order_total_summary, order_payment_summary;
    private Button payment_btn;

    String expiration, possessor, last_digit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_checkout);
        // Back arrow
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        boolean card = intent.getExtras().getBoolean("card");

        if (card){
            expiration = intent.getExtras().getString("expiration");
            possessor = intent.getExtras().getString("possessor");
            last_digit = intent.getExtras().getString("last_digits");
        }


        order_summary = (TextView) findViewById(R.id.order_summary);
        order_total_summary = (TextView) findViewById(R.id.order_total_summary);
        order_payment_summary = (TextView) findViewById(R.id.order_payment_summary);

        payment_btn = (Button) findViewById(R.id.payment_btn);
        payment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartCheckout.this.finishAffinity();
                Intent intent = new Intent(CartCheckout.this, ClientHomepageActivity.class);
                startActivity(intent);

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