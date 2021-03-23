package mobile.android.upf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

import mobile.android.upf.ui.login.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    private static final long MIN_WAIT_INTERVAL = 5000L; //5 seconds
    private long mStartTime = -1L; //for current time, in android SystemClock.uptimeMillis()
    private static final String TAG_LOG = SplashActivity.class.getName();
    private static final String START_TIME_KEY = "mobile.android.upf.START_TIME_KEY";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong(START_TIME_KEY, mStartTime);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.mStartTime = savedInstanceState.getLong(START_TIME_KEY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final ImageView logoImageView = (ImageView) findViewById(R.id.splash_imageview);

//        logoImageView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                long elapsedTime = SystemClock.uptimeMillis() -mStartTime;
//                if (elapsedTime >= MIN_WAIT_INTERVAL) {
//                    Log.d(TAG_LOG, "Go ahead ...");
//                    goAhead();
//                }
//                else {
//                    Log.d(TAG_LOG, "To early!");
//                }
//                return false;
//            }
//        });

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG_LOG, "Go ahead ...");
                goAhead();
            }
        }, 5000);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mStartTime = SystemClock.uptimeMillis();
        Log.d(TAG_LOG, "Activity started");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG_LOG, "Activity destroyed");
    }

    private void goAhead(){
        final Intent intent =  new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}