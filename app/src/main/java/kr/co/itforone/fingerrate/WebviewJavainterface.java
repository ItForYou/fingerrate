package kr.co.itforone.fingerrate;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

class WebviewJavainterface {
    Activity activity;
    MainActivity mainActivity;

    public WebviewJavainterface(Activity activity, MainActivity mainActivity) {
        this.activity = activity;
        this.mainActivity = mainActivity;
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
