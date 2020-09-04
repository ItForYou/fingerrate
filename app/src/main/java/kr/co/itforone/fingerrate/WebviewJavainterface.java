package kr.co.itforone.fingerrate;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

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
    }

    @JavascriptInterface
    public void setLogininfo(String id,String password) {
        SharedPreferences pref = mainActivity.getSharedPreferences("logininfo", mainActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("id",id);
        editor.putString("pwd",password);
        editor.commit();
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
    public void Show_scan(){
        //Toast.makeText(mainActivity.getApplicationContext(),"show_scan",Toast.LENGTH_LONG).show();
        mainActivity.show_scaanner();
    }
//구글 로그인
    @JavascriptInterface
    public void login_google(int mb_no) {

        Intent signInIntent = mainActivity.mGoogleSignInClient.getSignInIntent();
        mainActivity.input_mbno = mb_no;
      //  Toast.makeText(mainActivity.getApplicationContext(),String.valueOf(mb_no),Toast.LENGTH_LONG).show();
        mainActivity.startActivityForResult(signInIntent, RC_SIGN_IN);

    }
    @JavascriptInterface
    public void sendlink(String mb_no){


        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        String text = mainActivity.getString(R.string.share) + mb_no;
        intent.putExtra(Intent.EXTRA_TEXT, text);
        Intent chooser = Intent.createChooser(intent, "공유하기");
        mainActivity.startActivity(chooser);

    }
    @JavascriptInterface
    public void pressback(){
        Toast.makeText(mainActivity.getApplicationContext(),"pressback",Toast.LENGTH_LONG).show();
        mainActivity.onBackPressed();
    }

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


}
