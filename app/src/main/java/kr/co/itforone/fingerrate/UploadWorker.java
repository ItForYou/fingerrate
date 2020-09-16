package kr.co.itforone.fingerrate;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

public class UploadWorker extends Worker {
    //public static final String SLEEP_DURATION = "SLEEP_DURATION";
    MainActivity mainActivity;
    public UploadWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParameters) {
        super(context, workerParameters);
    }

    @NonNull
    @Override
    public Result doWork() {

        Data input = getInputData();
        String temp="",temp2="", token="", id="";
        if(input!=null){

            temp = input.getString("lat");
            temp2 = input.getString("lng");
            token = input.getString("token");
            id = input.getString("mb_id");

        }

        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response){
                try {

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        Requestpush requestpush = new Requestpush(token,temp, temp2,id,responseListener);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(requestpush);

      //  Log.d("location:" ,token);
        return Result.success();

    }
}

