package kr.co.itforone.fingerrate;

import static com.android.installreferrer.api.InstallReferrerClient.InstallReferrerResponse;

import androidx.ads.identifier.AdvertisingIdInfo;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import kr.co.itforone.fingerrate.volley.RequestOfferwall;
import kr.co.itforone.fingerrate.volley.Requestpush;


public class SplashActivity extends AppCompatActivity {
    private final Executor backgroundExecutor = Executors.newSingleThreadExecutor();
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
       // mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);



        SharedPreferences pref = getSharedPreferences("first_flg", MODE_PRIVATE);
        String first_flg = pref.getString("value", "");

        if(first_flg.endsWith("") || first_flg.isEmpty()) {

            InstallReferrerClient referrerClient;
            referrerClient = InstallReferrerClient.newBuilder(this).build();

            referrerClient.startConnection(new InstallReferrerStateListener() {
                @Override
                public void onInstallReferrerSetupFinished(int responseCode) {
                    switch (responseCode) {
                        case InstallReferrerResponse.OK:
                            ReferrerDetails response = null;
                            try {
                                response = referrerClient.getInstallReferrer();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                            if (response != null) {

                                String referrerUrl = response.getInstallReferrer();
                                String google_ad_id = "";

                                Uri temp_uri = Uri.parse("?"+referrerUrl);
                                if(temp_uri.getQueryParameter("utm_medium")!=null && !temp_uri.getQueryParameter("utm_medium").isEmpty())
                                    google_ad_id = temp_uri.getQueryParameter("utm_medium");

                                long referrerClickTime = response.getReferrerClickTimestampSeconds();
                                long appInstallTime = response.getInstallBeginTimestampSeconds();
                                boolean instantExperienceLaunched = response.getGooglePlayInstantParam();
                                //Toast.makeText(SplashActivity.this, referrerUrl, Toast.LENGTH_SHORT).show();

                                Bundle params = new Bundle();
                                params.putString("installer", referrerUrl);
                                //           mFirebaseAnalytics.logEvent("installer", params);
                                if(!google_ad_id.isEmpty() && google_ad_id!=null) {
                                    Log.d("installed_referrer", referrerUrl);
                                    SharedPreferences pref = getSharedPreferences("first_flg", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putString("value", google_ad_id);
                                    editor.commit();


                                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    };
                                    RequestOfferwall requestOfferwall = new RequestOfferwall(google_ad_id, responseListener);
                                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                                    queue.add(requestOfferwall);
                                }
                            }
                            // Connection established.
                            break;
                        case InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                            // API not available on the current Play Store app.
                            break;
                        case InstallReferrerResponse.SERVICE_UNAVAILABLE:
                            // Connection couldn't be established.
                            break;
                    }
                }


                @Override
                public void onInstallReferrerServiceDisconnected() {
                    // Try to restart the connection on the next request to
                    // Google Play by calling the startConnection() method.
                }
            });
        }

        Intent push = getIntent();
        String pushurl = "";

        if (push.getStringExtra("goUrl") != null)
            pushurl = push.getStringExtra("goUrl");

        VersionCheck versionCheck=new VersionCheck(getPackageName().toString(),this, pushurl);
        versionCheck.execute();

       // determineAdvertisingInfo();
    }

    private void determineAdvertisingInfo() {

        AdvertisingIdClient.Info adinfo;

        try {
            adinfo = AdvertisingIdClient.getAdvertisingIdInfo(this);
            Toast.makeText(this,adinfo.getId(),Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        }

    }




    @Override
    public void onBackPressed() {
    }

}