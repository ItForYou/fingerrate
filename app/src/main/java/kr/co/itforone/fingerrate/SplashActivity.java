package kr.co.itforone.fingerrate;


import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Intent push = getIntent();
        String pushurl = "";

        if (push.getStringExtra("goUrl") != null)
            pushurl = push.getStringExtra("goUrl");

        VersionCheck versionCheck=new VersionCheck(getPackageName().toString(),this, pushurl);
        versionCheck.execute();

    }

    @Override
    public void onBackPressed() {
    }

}