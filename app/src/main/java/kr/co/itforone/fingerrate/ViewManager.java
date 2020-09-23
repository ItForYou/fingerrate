package kr.co.itforone.fingerrate;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
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

        //로그인, 글쓰기, 회원가입, 정보수정 뒤로가기 처리
        //Toast.makeText(mainActivity.getApplicationContext(),url, Toast.LENGTH_LONG).show();

                if(url.contains("search_tes_view.php") || url.contains("place_view.php") || url.contains("search_tes.php")){

                    mainActivity.Norefresh();
                    mainActivity.flg_refresh=0;

                }

                else{
                    mainActivity.Yesrefresh();
                    mainActivity.flg_refresh=1;
                }

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
           else if(url.contains(mainActivity.getString(R.string.qrcode))){
              // Toast.makeText(mainActivity.getApplicationContext(), mainActivity.qr_url.toString(), Toast.LENGTH_SHORT).show();
               Glide.with(mainActivity)
                       .asBitmap()
                       .load(mainActivity.qr_url)
                       .into(new CustomTarget<Bitmap>() {

                           @Override
                           public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                               String dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                                       + File.separator+mainActivity.getString(R.string.app_name)+ File.separator;
                            //   Toast.makeText(mainActivity.getApplicationContext(), dirPath, Toast.LENGTH_SHORT).show();
                               String file_name = System.currentTimeMillis() + ".jpg";
                                   Bitmap bitmap = resource;
                                   //Toast.makeText(mainActivity.getApplicationContext(), "Saving Image...", Toast.LENGTH_SHORT).show();
                                   saveImage(bitmap, dirPath, file_name);

                           }

                           @Override
                                 public void onLoadCleared(@Nullable Drawable placeholder) {

                                 }
                             });



               /*view.loadUrl(url);
               view.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
               view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
               view.setDrawingCacheEnabled(true);
               view.buildDrawingCache();
               Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
               Canvas canvas = new Canvas(bitmap);
               Paint paint = new Paint();
               int iHeight = bitmap.getHeight();
               canvas.drawBitmap(bitmap, 0, iHeight, paint);
               view.draw(canvas);
               */

               return true;
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
               url = url+"?now_lat="+mainActivity.getlat()+"&now_lng="+mainActivity.getlng();
               view.loadUrl(url);

               return true;

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

    private void saveImage(Bitmap image, String storageDir, String imageFileName) {


            File imageFile = new File(storageDir, imageFileName);
            String savedImagePath = imageFile.getAbsolutePath();
             //   Toast.makeText(mainActivity.getApplicationContext(), imageFile.toString(), Toast.LENGTH_SHORT).show();

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            final File file_path;

            try {

                file_path = new File(storageDir);
                if (!file_path.isDirectory()) {
                    file_path.mkdirs();
                }
                File ImageFile = new File(file_path, imageFileName);
                OutputStream fOut = new FileOutputStream(ImageFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.flush();
                fOut.close();
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {// A
                    scanFile(storageDir);
                }
                else{
                    mainActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(ImageFile)));
                }

                Toast.makeText(mainActivity.getApplicationContext(), "이미지가 저장되었습니다. 갤러리를 확인해주세요.", Toast.LENGTH_SHORT).show();
                NotificationSomethings(imageFileName);


            } catch (Exception e) {
                Toast.makeText(mainActivity.getApplicationContext(), "Error while saving image!" + e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }


        }
    }

    private void scanFile(String path) {

        MediaScannerConnection.scanFile(mainActivity,
                new String[] { path }, null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("TAG", "Finished scanning " + path);
                    }
                });
    }

    public void NotificationSomethings(String filename) {

        NotificationManager notificationManager = (NotificationManager)mainActivity.getSystemService(mainActivity.NOTIFICATION_SERVICE);
        Intent notificationIntent;

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.N) {
            File sdDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            File file = new File(sdDir, "FingeRate");
            notificationIntent = new Intent();
            notificationIntent.setAction(Intent.ACTION_VIEW);
            notificationIntent.setDataAndType(Uri.withAppendedPath(Uri.fromFile(file), File.separator + filename), "image/*");
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //notificationIntent.putExtra("notificationId", "tset"); //전달할 값
        }
        else{
          /*  File sdDir = new File(mainActivity.getFilesDir()+"/DCIM", "FingeRate");
            File file_sdk24 = new File(sdDir, filename);
            Uri providerURI = FileProvider.getUriForFile( mainActivity , mainActivity.getPackageName()+".provider" , file_sdk24);
            notificationIntent = new Intent();
            notificationIntent.setAction(Intent.ACTION_VIEW);
            notificationIntent.setDataAndType(providerURI,"image/*");
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);*/

            Intent intent= new Intent("android.intent.action.MAIN", null);
            intent.addCategory("android.intent.category.APP_GALLERY");
             notificationIntent = Intent.createChooser(intent, "Gallery");
        /*    notificationIntent = new Intent();
            notificationIntent.setAction(Intent.ACTION_VIEW);
            notificationIntent.setType("image/*");
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);*/
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(mainActivity, 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mainActivity, "qr_down")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("QR코드 다운로드가 완료되었습니다.")
                // 더 많은 내용이라서 일부만 보여줘야 하는 경우 아래 주석을 제거하면 setContentText에 있는 문자열 대신 아래 문자열을 보여줌
                //.setStyle(new NotificationCompat.BigTextStyle().bigText("더 많은 내용을 보여줘야 하는 경우..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent) // 사용자가 노티피케이션을 탭시 ResultActivity로 이동하도록 설정
                .setAutoCancel(true);

        //OREO API 26 이상에서는 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            builder.setSmallIcon(R.drawable.logo); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
            CharSequence channelName  = "Fingerate";
            String description = "";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel("qr_down", channelName , importance);
            channel.setDescription(description);

            // 노티피케이션 채널을 시스템에 등록
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

        }else builder.setSmallIcon(R.drawable.logo); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

        assert notificationManager != null;
        notificationManager.notify(1234, builder.build()); // 고유숫자로 노티피케이션 동작시킴

    }



}
