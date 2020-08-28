package kr.co.itforone.fingerrate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //    finish();
                Intent main = new Intent(SplashActivity.this,MainActivity.class);
                startActivity(main);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }

    @Override
    public void onBackPressed() {
    }

}