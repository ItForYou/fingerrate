package kr.co.itforone.fingerrate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

class VersionCheck extends AsyncTask<Void,Void,String> {
    String pakage;
    public String marketVersion;
    Activity mContext;
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    public VersionCheck(String pakage,Activity context){
        this.pakage=pakage;
        mContext=context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    //플레이스토어 파싱하기
    @Override
    protected String doInBackground(Void... params) {

        try {
            Document doc =
                    Jsoup.connect("https://play.google.com/store/apps/details?id=" + pakage).get();
            Elements Version = doc.select(".htlgb").eq(7);
            for (Element mElement : Version) {
                return mElement.text().trim();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    //버전이 맞지 않으면 AlertDialog창 띄우기
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        marketVersion=result;

        try {
            String versionName = mContext.getApplicationContext().getPackageManager()
                    .getPackageInfo(mContext.getApplicationContext().getPackageName(), 0)
                    .versionName;
            Log.d("versionName",versionName);
            Log.d("versionName",result);
            if(!marketVersion.equals(versionName)){
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("버전체크");
                builder.setMessage("버전업데이트가 되었습니다. 업데이트 후에 이용이 가능합니다.");
                builder.setCancelable(false);
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + mContext.getPackageName())));
                                mContext.finishAffinity();
                                System.runFinalization();
                                System.exit(0);
                            }
                        });
                builder.show();
            }
            else{
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //    finish();
                        Intent main = new Intent(mContext,MainActivity.class);
                        mContext.startActivity(main);
                        mContext.finish();
                        return;
                    }
                }, SPLASH_DISPLAY_LENGTH);
            }
            return;
        } catch (PackageManager.NameNotFoundException e) {
        }
    }
}