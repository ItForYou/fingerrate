package kr.co.itforone.fingerrate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;

import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.core.content.FileProvider;

import java.io.File;

class ChromeManager extends WebChromeClient {
    Activity activity;
    MainActivity mainActivity;
    static final int FILECHOOSER_LOLLIPOP_REQ_CODE=1300;

    public ChromeManager(Activity activity, MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.activity = activity;
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        return false;
    }

    //경고창 띄우기
    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        new AlertDialog.Builder(view.getContext())
                .setMessage("\n" + message + "\n")
                .setCancelable(false)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                result.confirm();
                            }
                        }).create().show();
        return true;
    }

    //컴펌 띄우기
    @Override
    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
        new AlertDialog.Builder(view.getContext())
                .setMessage("\n" + message + "\n")
                .setCancelable(false)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                result.confirm();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                result.cancel();
                            }
                        }).create().show();
        return true;
    }

    // For Android 5.0+
    public boolean onShowFileChooser(  WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
         mainActivity.filePathCallbackLollipop = filePathCallback;

            // 파일 선택
            // Create AndroidExampleFolder at sdcard

            //Toast.makeText(mainActivity.getApplicationContext(),file.getPath(),Toast.LENGTH_LONG).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {// API 24 이상 일경우..
                File imageStorageDir = new File(mainActivity.getFilesDir() + "/Pictures", "fingerrate");
                if (!imageStorageDir.exists()) {
                    // Create AndroidExampleFolder at sdcard
                    imageStorageDir.mkdirs();
                }
                // Create camera captured image file path and name

                //Toast.makeText(mainActivity.getApplicationContext(),imageStorageDir.toString(),Toast.LENGTH_LONG).show();
                File file = new File(imageStorageDir, "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                Uri providerURI = FileProvider.getUriForFile(mainActivity, mainActivity.getPackageName() + ".provider", file);
                mainActivity.mCapturedImageURI = providerURI;

            } else {// API 24 미만 일경우..

                File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "fingerrate");
                if (!imageStorageDir.exists()) {
                    // Create AndroidExampleFolder at sdcard
                    imageStorageDir.mkdirs();
                }
                // Create camera captured image file path and name
                File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                mainActivity.mCapturedImageURI = Uri.fromFile(file);

            }

            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mainActivity.mCapturedImageURI);

            // 기본 선택 (카메라, 앨범)
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            // Create file chooser intent
            Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
            // Set camera intent to file chooser
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});
            mainActivity.startActivityForResult(chooserIntent, FILECHOOSER_LOLLIPOP_REQ_CODE);

        // 갤러리 앨범 선택
           /*     Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                i.setType(MediaStore.Images.Media.CONTENT_TYPE);
                i.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Create file chooser intent
                Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
*/


        // 다중선택
      /*  Intent i = new Intent();
        i.setType("image/*");
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        i.setAction(Intent.ACTION_GET_CONTENT);
        // Create file chooser intent
        Intent chooserIntent = Intent.createChooser(i, "Image Chooser");*/

        // On select image call onActivityResult method of activity

        return true;

    }
    // 웹뷰 업로드 END


}
