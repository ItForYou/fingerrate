package kr.co.itforone.fingerrate;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.net.URISyntaxException;

class ViewManager extends WebViewClient {

    Activity context;
    MainActivity mainActivity;

    public ViewManager(Activity activity, MainActivity mainActivity) {
        this.mainActivity  = mainActivity;
        this.context = activity;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
             //  Toast.makeText(mainActivity.getApplicationContext(),"test-"+url, Toast.LENGTH_LONG).show();


        //로그인, 글쓰기, 회원가입, 정보수정 뒤로가기 처리
            //Toast.makeText(mainActivity.getApplicationContext(),"view"+String.valueOf(mainActivity.flg_alert), Toast.LENGTH_LONG).show();

           if(url.contains("intent")){
               try {
                   Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);

                   if (intent.resolveActivity(mainActivity.getPackageManager()) != null) {
                     //  Toast.makeText(mainActivity.getApplicationContext(),"startactivity",Toast.LENGTH_LONG).show();
                       mainActivity.startActivity(intent);
                       //Log.d(TAG, "ACTIVITY: ${intent.`package`}");
                       return true;
                   }

                   // Fallback URL이 있으면 현재 웹뷰에 로딩
                   String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                   if (fallbackUrl != null) {
                  //     Toast.makeText(mainActivity.getApplicationContext(),"fallback",Toast.LENGTH_LONG).show();
                       view.loadUrl(fallbackUrl);
                       //Log.d(TAG, "FALLBACK: $fallbackUrl");
                       return true;
                   }
               }catch (URISyntaxException e) {
                   e.printStackTrace();
               }

           }
           else if(url.contains("play.google.com/store")){
               Intent intent = new Intent(Intent.ACTION_VIEW);
               intent.setData(Uri.parse(
                       "https://play.google.com/store/apps/details?id=kr.co.itforone.fingerrate"));
               intent.setPackage("com.android.vending");
               mainActivity.startActivity(intent);
               return true;
           }
           else if(url.equals(mainActivity.getString(R.string.home))|| url.equals(mainActivity.getString(R.string.home2))){

               double lat = mainActivity.getlat(), lng = mainActivity.getlng();
               url = url+"?lat="+lat+"&lng="+lng;
               view.loadUrl(url);
               return true;
           }
           else if(url.contains("search_tes.php")){
                mainActivity.Norefresh();
                mainActivity.flg_refresh=0;

               url = url+"?now_lat="+mainActivity.getlat()+"&now_lng="+mainActivity.getlng();
               view.loadUrl(url);
               return true;

            }
            else{
                mainActivity.Yesrefresh();
                mainActivity.flg_refresh=1;
            }
            view.loadUrl(url);
            return true;

    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        // mainActivity.dialogloading.show();
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
         view.loadUrl("javascript:setToken('"+mainActivity.token+"')");
    }

}
