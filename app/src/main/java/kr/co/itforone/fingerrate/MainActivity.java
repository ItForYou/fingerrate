package kr.co.itforone.fingerrate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.webView)    WebView webView;
    @BindView(R.id.refreshlayout)    SwipeRefreshLayout refreshlayout;
    private long backPrssedTime = 0;
    static final int PERMISSION_REQUEST_CODE = 1;
    final int FILECHOOSER_NORMAL_REQ_CODE = 1200, FILECHOOSER_LOLLIPOP_REQ_CODE = 1300;
    String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,

    };
    String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    String SERVER_KEY = "AAAArkeYuGw:APA91bFUiPeytevc24oskyOiPiFqLT1zVoBHCVmFayiiB1Xr1Jzy11Kn13ATCpGIA4zHmyXMwHHWM6ciJBnOHz3QbzFmHq5AtK-a8R4-pWpy0LbDcRd3sgUx45i__PVh_4kFkOXeRV4q";
    double init_lat=0,init_lng=0;
    ValueCallback<Uri> filePathCallbackNormal;
    ValueCallback<Uri[]> filePathCallbackLollipop;
    Uri mCapturedImageURI;
    private LocationManager locationManager;
    private Location location;
    String token = "";
    int flg_refresh =1;
    private final int MY_PERMISSIONS_REQUEST_CAMERA=1001;
    IntentIntegrator integrator;
    private boolean hasPermissions(String[] permissions) {
        // 퍼미션 확인
        int result = -1;
        for (int i = 0; i < permissions.length; i++) {
            result = ContextCompat.checkSelfPermission(getApplicationContext(), permissions[i]);
        }
        Log.d("per_result",String.valueOf(result));
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        }else {
            return false;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (!hasPermissions(PERMISSIONS)){

                }else{
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                   /* LocationPosition.act=MainActivity.this;
                    LocationPosition.setPosition(this);
                    if(LocationPosition.lng==0.0){
                        LocationPosition.setPosition(this);
                    }
                    String place= LocationPosition.getAddress(LocationPosition.lat,LocationPosition.lng);
                    webView.loadUrl("javascript:getAddress('"+place+"')");*/
                }
                return;
            }
        }
    }


    public void set_filePathCallbackLollipop(ValueCallback<Uri[]> filePathCallbackLollipop){
        this.filePathCallbackLollipop = filePathCallbackLollipop;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == FILECHOOSER_NORMAL_REQ_CODE) {
                if (filePathCallbackNormal == null) return;
                Uri result = (data == null || resultCode != RESULT_OK) ? null : data.getData();
                Toast.makeText(getApplicationContext(), data.getData().toString(), Toast.LENGTH_LONG).show();
                filePathCallbackNormal.onReceiveValue(result);
                filePathCallbackNormal = null;

            } else if (requestCode == FILECHOOSER_LOLLIPOP_REQ_CODE) {

                Uri[] result;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    // 이미지 다중선택 추가
                    if (data!=null && data.getClipData() != null) {
                        int cnt = data.getClipData().getItemCount();

                        result = new Uri[cnt];
                        for (int i = 0; i < cnt; i++) {
                            result[i] = data.getClipData().getItemAt(i).getUri();

                        }
                    } else {

                        if(data==null) {
                            Toast.makeText(getApplicationContext(), mCapturedImageURI.toString(), Toast.LENGTH_LONG).show();
                            result = new Uri[]{mCapturedImageURI};
                        }
                        else{
                            //Toast.makeText(getApplicationContext(), "data is not null", Toast.LENGTH_LONG).show();
                            result = WebChromeClient.FileChooserParams.parseResult(resultCode, data);
                        }
                    }

                    filePathCallbackLollipop.onReceiveValue(result);
                    filePathCallbackLollipop = null;
                }
            }
            else if(requestCode  == IntentIntegrator.REQUEST_CODE){
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

                if (result == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                        webView.loadUrl(result.getContents().toString());
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        } else {
            try {
                if (filePathCallbackLollipop != null) {
                    filePathCallbackLollipop.onReceiveValue(null);
                    filePathCallbackLollipop = null;
                 //   webView.loadUrl("javascript:removeInputFile()");
                }
            } catch (Exception e) {

            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);

        if(hasPermissions(PERMISSIONS)) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        init_lat = getlat();
        init_lng = getlng();

       Data data = new Data.Builder()
                .putString("lat", String.valueOf(init_lat))
                .putString("lng", String.valueOf(init_lng))
                .build();


        OneTimeWorkRequest uploadWorkRequest = new OneTimeWorkRequest.Builder(UploadWorker.class)
                .setInitialDelay(1, TimeUnit.MINUTES)
               .setInputData(data)
                .build();
        WorkManager.getInstance().enqueue(uploadWorkRequest);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("D", "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        token = task.getResult().getToken();
                    }
                });



        webView.setWebChromeClient(new ChromeManager(this,this));
        webView.setWebViewClient(new ViewManager(this, this));
        webView.addJavascriptInterface(new WebviewJavainterface(this, this),"Android");
        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);//웹에서 파일 접근 여부
        settings.setAppCacheEnabled(true);//캐쉬 사용여부
        settings.setDatabaseEnabled(true);//HTML5에서 db 사용여부 -> indexDB
        settings.setDomStorageEnabled(true);//HTML5에서 DOM 사용여부
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);//캐시 사용모드 LOAD_NO_CACHE는 캐시를 사용않는다는 뜻
        settings.setTextZoom(100);       // 폰트크기 고정
        webView.setWebContentsDebuggingEnabled(true);
        webView.setLongClickable(true);
        webView.loadUrl(getString(R.string.home));

        refreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                webView.clearCache(true);
                webView.reload();
                refreshlayout.setRefreshing(false);
            }

        });

        refreshlayout.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {

                if(webView.getScrollY() == 0 && flg_refresh ==1){
                    refreshlayout.setEnabled(true);
                }
                else{
                    refreshlayout.setEnabled(false);
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPrssedTime;
        if(webView.getUrl().contains("search_tes.php")){
            Yesrefresh();
        }
        if(webView.canGoBack()){
            webView.goBack();
            return;
        }
        else {
            if (0 <= intervalTime && 2000 >= intervalTime) {
                finish();
            } else {
                backPrssedTime = tempTime;
                Toast.makeText(getApplicationContext(), "한번 더 뒤로가기 누를시 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public double getlat(){
        //Toast.makeText(getApplicationContext(),""+location.getLatitude() + "//" +location.getLongitude(),Toast.LENGTH_LONG).show();
        if(location!=null) {
            return location.getLatitude();
        }
        else return 0;
    }

    public double getlng(){
        //Toast.makeText(getApplicationContext(),""+location.getLatitude() + "//" +location.getLongitude(),Toast.LENGTH_LONG).show();
        if(location!=null) {
            return location.getLongitude();
        }
        else return 0;

    }

    public void Norefresh(){
        refreshlayout.setEnabled(false);
    }
    public void Yesrefresh(){
        refreshlayout.setEnabled(true);
    }
    public void show_scaanner(){
       new IntentIntegrator(this).initiateScan();
    }
}
