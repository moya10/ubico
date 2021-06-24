package com.example.lbma.Services;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;

import android.util.Log;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.lbma.DatabaseHandler.DatabaseHandler;
import com.example.lbma.Variables.Constants;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;



public class ActivityRecognitionService extends IntentService {

    private static final String TAG = "ActivityDetection";
    private  String activity;
    float activity_conf;

    public ActivityRecognitionService(){
        super("ActivityRecognitionService");
    }

    public ActivityRecognitionService(String name){
        super(name);
    }

    //detect activities
    @SuppressLint("DefaultLocale")
    @Override
    protected void onHandleIntent(Intent intent) {

        if(ActivityRecognitionResult.hasResult(intent)){
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            assert result != null;
            DetectedActivity detectedActivity = result.getMostProbableActivity();
            long time = System.currentTimeMillis();
            Log.d("ACTIVITY: ", detectedActivity.getType() +" confidence: "+ detectedActivity.getConfidence() + " time: "+String.valueOf(time));
            activity_conf = (float) (detectedActivity.getConfidence() * 0.01);


            switch (detectedActivity.getType()) {
                case DetectedActivity.IN_VEHICLE: {
                    activity = "IN_VEHICLE";
                    break;
                }
                case DetectedActivity.ON_BICYCLE: {
                    activity = "ON_BICYCLE";
                    break;
                }
                case DetectedActivity.ON_FOOT: {
                    activity = "ON_FOOT";
                    break;
                }
                case DetectedActivity.RUNNING: {
                    activity = "RUNNING";
                    break;
                }
                case DetectedActivity.WALKING: {
                    activity = "WALKING";
                    break;
                }
                case DetectedActivity.STILL: {
                    activity = "STILL";
                    break;
                }
                case DetectedActivity.UNKNOWN: {
                    activity = "UNKNOWN";
                    break;
                }
            }
            broadcastActivity(activity,String.format("%.2f",activity_conf));
        }
        else{
            Log.d("ACTIVITY: ","ERROR!!!");
        }
    }

    private void broadcastActivity(String activity, String confidence) {
        Intent intent = new Intent(Constants.BROADCAST_DETECTED_ACTIVITY);
        intent.putExtra("type",  activity);
        intent.putExtra("confidence", confidence);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}