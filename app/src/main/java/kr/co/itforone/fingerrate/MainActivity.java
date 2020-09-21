package kr.co.itforone.fingerrate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.webView)    WebView webView;
    @BindView(R.id.refreshlayout)    SwipeRefreshLayout refreshlayout;
    private long backPrssedTime = 0;
    static final int PERMISSION_REQUEST_CODE = 1;
    private static final int RC_SIGN_IN = 9001;
    final int FILECHOOSER_NORMAL_REQ_CODE = 1200, FILECHOOSER_LOLLIPOP_REQ_CODE = 1300;

    String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,

    };

    double init_lat=0,init_lng=0;
    ValueCallback<Uri> filePathCallbackNormal;
    ValueCallback<Uri[]> filePathCallbackLollipop;
    Uri mCapturedImageURI,mCapturedImageURI2;
    private LocationManager locationManager;
    private Location location;
    String token = "";
    int flg_refresh =1;
    public GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    String mb_no, mb_3;
    int input_mbno=0;
    String input_mb3 = "", pushurl = "";;
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

        if (resultCode == RESULT_OK) {

            switch (requestCode){
                 case FILECHOOSER_NORMAL_REQ_CODE: {
                     if (filePathCallbackNormal == null) return;
                     Uri result = (data == null || resultCode != RESULT_OK) ? null : data.getData();
                     Toast.makeText(getApplicationContext(), data.getData().toString(), Toast.LENGTH_LONG).show();
                     filePathCallbackNormal.onReceiveValue(result);
                     filePathCallbackNormal = null;
                     break;
                 }
                case FILECHOOSER_LOLLIPOP_REQ_CODE: {
                    Uri[] result = new Uri[0];
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        // 이미지 다중선택 추가
                        if (data!=null && data.getClipData() != null) {
                            int cnt = data.getClipData().getItemCount();

                            result = new Uri[cnt];
                            for (int i = 0; i < cnt; i++) {
                                result[i] = data.getClipData().getItemAt(i).getUri();

                            }
                        } else {

                            if (data == null)
                                data = new Intent();
                            if (data.getData() == null)
                                data.setData(mCapturedImageURI);

                            filePathCallbackLollipop.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                            filePathCallbackLollipop = null;
                            return;
                        }
                        filePathCallbackLollipop.onReceiveValue(result);
                        filePathCallbackLollipop = null;
                    }
                    break;
                }
                case IntentIntegrator.REQUEST_CODE:{
                    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

                    if (result == null) {
                        Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                    } else {
                        webView.loadUrl(result.getContents().toString());
                    }
                    break;
                }
                case RC_SIGN_IN:{
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        // Google Sign In was successful, authenticate with Firebase
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        Log.d("google_login", "firebaseAuthWithGoogle:" + account.getEmail());

                        if(input_mbno>0) {
                            webView.loadUrl(getString(R.string.register) + account.getEmail() + "&mb_1=" + String.valueOf(input_mbno)+"&mb_3=" +input_mb3+"&google=1");
                           // Toast.makeText(this,getString(R.string.register) + account.getEmail() + "&mb_1=" + String.valueOf(input_mbno) , Toast.LENGTH_LONG).show();
                        }

                        else{
                            webView.loadUrl(getString(R.string.register) + account.getEmail()+"&google=1");
                           // Toast.makeText(this, getString(R.string.register) + account.getEmail(), Toast.LENGTH_LONG).show();
                        }

                        firebaseAuthWithGoogle(account.getIdToken());
                    }catch (ApiException e) {
                        // Google Sign In failed, update UI appropriately
                        Log.w("google_login", "Google sign in failed", e);
                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                    }
                    break;
                }
                default:  break;
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
        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);

        if (hasPermissions(PERMISSIONS)) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        Intent push = getIntent();

        if (push.getStringExtra("goUrl") != null)
            pushurl = push.getStringExtra("goUrl");
        else{
            Uri data = push.getData();
            if(data!=null) {

               mb_no = data.getQueryParameter("mb_1");
               mb_3= data.getQueryParameter("mb_3");

           /*     Log.d("scheme", String.valueOf(mb_no));
                Log.d("scheme2", String.valueOf(mb_3));*/
            }
        }

        SharedPreferences pref = getSharedPreferences("logininfo", MODE_PRIVATE);
        String id = pref.getString("id", "");
        String pwd = pref.getString("pwd", "");
        //Toast.makeText(getApplicationContext(),id+","+pwd,Toast.LENGTH_LONG).show();
       // Toast.makeText(getApplicationContext(),pushurl,Toast.LENGTH_LONG).show();

        init_lat = getlat();
        init_lng = getlng();

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

                        if(pushurl=="" || pushurl.isEmpty()) {
                            Data data = new Data.Builder()
                                    .putString("lat", String.valueOf(init_lat))
                                    .putString("lng", String.valueOf(init_lng))
                                    .putString("token", token)
                                    .putString("mb_id", id)
                                    .build();

                            OneTimeWorkRequest uploadWorkRequest = new OneTimeWorkRequest.Builder(UploadWorker.class)
                                    .setInitialDelay(15, TimeUnit.MINUTES)
                                    .setInputData(data)
                                    .build();
                            WorkManager.getInstance().enqueue(uploadWorkRequest);
                        }
                    }
                });

        webView.setWebChromeClient(new ChromeManager(this, this));
        webView.setWebViewClient(new ViewManager(this, this));
        webView.addJavascriptInterface(new WebviewJavainterface(this, this), "Android");

        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(true);
        settings.setAllowFileAccess(true);//웹에서 파일 접근 여부
        settings.setAppCacheEnabled(true);//캐쉬 사용여부
        settings.setDatabaseEnabled(true);//HTML5에서 db 사용여부 -> indexDB
        settings.setDomStorageEnabled(true);//HTML5에서 DOM 사용여부
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);//캐시 사용모드 LOAD_NO_CACHE는 캐시를 사용않는다는 뜻
        settings.setTextZoom(100);       // 폰트크기 고정
        webView.setWebContentsDebuggingEnabled(true);
        webView.setLongClickable(true);

        if (!pushurl.isEmpty() && !id.isEmpty() && id!="") {
            webView.loadUrl(pushurl);
        }else if(mb_no!=null && !mb_no.isEmpty()){
            webView.loadUrl(getString(R.string.register_rcmm)+mb_no+"&mb_3="+mb_3);
        }
        else if(!id.isEmpty() && !pwd.isEmpty()){
           // Toast.makeText(getApplicationContext(),getString(R.string.login)+"mb_email="+id.toString()+"&mb_password="+pwd, Toast.LENGTH_LONG).show();
            webView.loadUrl(getString(R.string.login)+"mb_email="+id.toString()+"&mb_password="+pwd);
        }
        else{
           webView.loadUrl(getString(R.string.intro));
        }

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                try {

                    //String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
                    //fileName = URLEncoder.encode(fileName, "EUC-KR").replace("+", "%20");
                    //fileName = URLDecoder.decode(fileName, "UTF-8");
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    request.setMimeType(mimetype);
                    Log.d("file_error", "step1");
                    //------------------------COOKIE!!------------------------
                    String cookies = CookieManager.getInstance().getCookie(url);
                    Log.d("file_error", "step2");
                    request.addRequestHeader("cookie", cookies);
                    //------------------------COOKIE!!------------------------
                    request.addRequestHeader("User-Agent", userAgent);
                    request.setDescription("Downloading file...");
                    request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype));
                    request.allowScanningByMediaScanner();
                    Log.d("file_error", "step3");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimetype));
                    Log.d("file_error", "step4");

                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    Log.d("file_error", "step5");
                    dm.enqueue(request);
                    Log.d("file_error", "step6");
                    Toast.makeText(getApplicationContext(), "다운로드 시작..", Toast.LENGTH_LONG).show();
                    Log.d("file_error", "step7");
                    //Toast.makeText(getApplicationContext(), fileName, Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            Toast.makeText(getBaseContext(), "첨부파일 다운로드를 위해\n동의가 필요합니다.", Toast.LENGTH_LONG).show();
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    110);
                        } else {
                            Toast.makeText(getBaseContext(), "첨부파일 다운로드를 위해\n동의가 필요합니다.", Toast.LENGTH_LONG).show();
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    110);
                        }
                    }
                }
            }
        });

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
//구글로그인
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
//파이어베이스 동기화
    private void firebaseAuthWithGoogle(String idToken) {
        // [START_EXCLUDE silent]

        // [END_EXCLUDE]
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("fireabaseAuth : ", "signInWithCredential:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("fireabaseAuth : ", "signInWithCredential:failure", task.getException());
                            //Snackbar.make(mBinding.mainLayout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }
                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                    }
                });
    }

    @Override
    public void onBackPressed() {
        //Toast.makeText(getApplicationContext(),webView.getUrl(),Toast.LENGTH_LONG).show();
        WebBackForwardList list = null;
        String backurl ="";

        try{
            list = webView.copyBackForwardList();
            if(list.getSize() >1 ){
                backurl = list.getItemAtIndex(list.getCurrentIndex() - 1).getUrl();
                // Toast.makeText(getApplicationContext(),backurl,Toast.LENGTH_LONG).show();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPrssedTime;

        if(webView.getUrl().contains("search_tes.php")){
            Yesrefresh();
        }

        if((webView.getUrl().contains(getString(R.string.home)+"?lat=") || webView.getUrl().contains(getString(R.string.home2)+"?lat=") || webView.getUrl().equals(getString(R.string.intro))) && !webView.getUrl().contains("#hash-menu")){
            if (0 <= intervalTime && 2000 >= intervalTime) {
                finish();
            } else {
                backPrssedTime = tempTime;
                Toast.makeText(getApplicationContext(), "한번 더 뒤로가기 누를시 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
            }
        }
        else if(webView.canGoBack()){
           // Toast.makeText(getApplicationContext(), "goback", Toast.LENGTH_SHORT).show();
            webView.goBack();
            return;
        }
        else {
            if (0 <= intervalTime && 2000 >= intervalTime){
                finish();
            } else {
                backPrssedTime = tempTime;
                Toast.makeText(getApplicationContext(), "한번 더 뒤로가기 누를시 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
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
       new IntentIntegrator(this)
               .setBeepEnabled(false)
               .initiateScan();
    }
}
