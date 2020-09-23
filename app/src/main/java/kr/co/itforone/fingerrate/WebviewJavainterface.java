package kr.co.itforone.fingerrate;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.webkit.JavascriptInterface;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

class WebviewJavainterface {
    Activity activity;
    MainActivity mainActivity;
    private static final int RC_SIGN_IN = 9001;
    public WebviewJavainterface(Activity activity, MainActivity mainActivity) {
        this.activity = activity;
        this.mainActivity = mainActivity;
    }

    @JavascriptInterface
    public void setlogout() {
        //   Toast.makeText(mainActivity.getApplicationContext(),"logout",Toast.LENGTH_LONG).show();
        SharedPreferences pref = mainActivity.getSharedPreferences("logininfo", mainActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
        mainActivity.mGoogleSignInClient.signOut().addOnCompleteListener(mainActivity,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }

    @JavascriptInterface
    public void setLogininfo(String id,String password) {
       // Toast.makeText(mainActivity.getApplicationContext(),"setlogin",Toast.LENGTH_LONG).show();
        SharedPreferences pref = mainActivity.getSharedPreferences("logininfo", mainActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("id",id);
        editor.putString("pwd",password);
        editor.commit();
    }

   /* @JavascriptInterface
    public void suc_reg(String id, String pwd) {

        SharedPreferences pref = mainActivity.getSharedPreferences("logininfo", mainActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("id",id);
        editor.putString("pwd",pwd);
        editor.commit();

        mainActivity.webView.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mainActivity.getApplicationContext(),id+','+pwd,Toast.LENGTH_LONG).show();
                mainActivity.webView.loadUrl(mainActivity.getString(R.string.login)+"mb_email="+id+"&mb_password="+pwd);

            }
        });

    }*/


    @JavascriptInterface
    public void setpwd(String password) {

        SharedPreferences pref = mainActivity.getSharedPreferences("logininfo", mainActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("pwd",password);
        editor.apply();

    }

    @JavascriptInterface
    public void getlocation() {

        double lat = mainActivity.getlat() * 1000000;
        double lng = mainActivity.getlng() * 1000000;
        lat = Math.ceil(lat) / 1000000;
        lng = Math.ceil(lng) / 1000000;
        double finalLat = lat;
        double finalLng = lng;

        mainActivity.webView.post(new Runnable() {
            @Override
            public void run() {

                    mainActivity.webView.loadUrl("javascript:move_now('" + finalLat + "','" + finalLng + "');");

            }
        });
        //Toast.makeText(mainActivity.getApplicationContext(),""+lat+" , "+lng, Toast.LENGTH_LONG).show();


    }

    @JavascriptInterface
    public void getnow() {

        double lat = mainActivity.getlat() * 1000000;
        double lng = mainActivity.getlng() * 1000000;
        lat = Math.ceil(lat) / 1000000;
        lng = Math.ceil(lng) / 1000000;
        double finalLat = lat;
        double finalLng = lng;

        mainActivity.webView.post(new Runnable() {
            @Override
            public void run() {

                mainActivity.webView.loadUrl("javascript:set_initlocate(" + finalLat + "," + finalLng + ");");

            }
        });
        //Toast.makeText(mainActivity.getApplicationContext(),""+lat+" , "+lng, Toast.LENGTH_LONG).show();


    }

    @JavascriptInterface
    public void Show_scan(){

        //Toast.makeText(mainActivity.getApplicationContext(),"show_scan",Toast.LENGTH_LONG).show();
        mainActivity.show_scaanner();

    }

//구글 로그인
    @JavascriptInterface
    public void login_google(int mb_no, String mb_3) {

        Intent signInIntent = mainActivity.mGoogleSignInClient.getSignInIntent();
        if(mb_no!=0)
            mainActivity.input_mbno = mb_no;
        else
            mainActivity.input_mbno = 0;
        if(!mb_3.isEmpty()){
            mainActivity.input_mb3 = mb_3;
        }
        else{
            mainActivity.input_mb3 = "";
        }

      //  Toast.makeText(mainActivity.getApplicationContext(),String.valueOf(mb_no),Toast.LENGTH_LONG).show();
        mainActivity.startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @JavascriptInterface
    public void sendlink(String mb_no, String mb_2){

        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        String text = mainActivity.getString(R.string.share) +"&mb_1="+mb_no + "&mb_3=" + mb_2;
        intent.putExtra(Intent.EXTRA_TEXT, text);
        Intent chooser = Intent.createChooser(intent, "공유하기");
        mainActivity.startActivity(chooser);

    }

    @JavascriptInterface
    public void pressback(){

       /// Toast.makeText(mainActivity.getApplicationContext(),"pressback",Toast.LENGTH_LONG).show();
        mainActivity.onBackPressed();

    }
    @JavascriptInterface
    public void qrurl(String qrurl){
        /// Toast.makeText(mainActivity.getApplicationContext(),"pressback",Toast.LENGTH_LONG).show();
        mainActivity.qr_url = qrurl;

    }
/*

    @JavascriptInterface
    public void NoRefresh(){

        //Toast.makeText(mainActivity.getApplicationContext(),"Norefresh",Toast.LENGTH_LONG).show();
        mainActivity.Norefresh();
        mainActivity.flg_refresh=0;

    }

    @JavascriptInterface
    public void YesRefresh(){

        mainActivity.Yesrefresh();
        mainActivity.flg_refresh=1;

    }
*/


}
