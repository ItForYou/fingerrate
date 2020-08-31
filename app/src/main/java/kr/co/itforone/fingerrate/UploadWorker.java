package kr.co.itforone.fingerrate;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

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
        String temp="",temp2="";


        if(input!=null){

            temp = input.getString("lat");
            temp2 = input.getString("lng");

        }

        Log.d("location:" ,temp+','+temp2);
        return Result.success();

    }
}

