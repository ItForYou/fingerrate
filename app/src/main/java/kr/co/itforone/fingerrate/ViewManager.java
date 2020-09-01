package kr.co.itforone.fingerrate;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


class ViewManager extends WebViewClient {

    Activity context;
    MainActivity mainActivity;

    public ViewManager(Activity activity, MainActivity mainActivity) {
        this.mainActivity  = mainActivity;
        this.context = activity;
    }


    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        //   Toast.makeText(mainActivity.getApplicationContext(),"test-"+url, Toast.LENGTH_LONG).show();

        //로그인, 글쓰기, 회원가입, 정보수정 뒤로가기 처리
            //Toast.makeText(mainActivity.getApplicationContext(),"view"+String.valueOf(mainActivity.flg_alert), Toast.LENGTH_LONG).show();
            if(url.contains("search_tes.php")){
                mainActivity.Norefresh();
                mainActivity.flg_refresh=0;
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
        // view.loadUrl("javascript:setToken('"+mainActivity.token+"')");
    }

}
