package com.example.lbma.BackgroundServices;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.lbma.Services.ActivityRecognitionService;
import com.example.lbma.Variables.Constants;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.tasks.Task;

public class BackgroundActivityService extends Service {
    //private static final String TAG = BackgroundActivityService.class.getSimpleName();
    private boolean start_activity = false;
    private PendingIntent mPendingIntent;
    private ActivityRecognitionClient mActivityRecognitionClient;


    public BackgroundActivityService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(Constants.ACTIVITY_INITIALIZED) {
            CreateIntent();
        }
    }

    private void CreateIntent() {
        mActivityRecognitionClient = new ActivityRecognitionClient(this);
        Intent mIntentService = new Intent(this, ActivityRecognitionService.class);
        mPendingIntent = PendingIntent.getService(this, 1, mIntentService, PendingIntent.FLAG_UPDATE_CURRENT);
        start_activity = true;
        requestActivityUpdatesButtonHandler();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if(Constants.ACTIVITY_INITIALIZED) {
            CreateIntent();
        }
        return START_STICKY;
    }

    public void requestActivityUpdatesButtonHandler() {
        Task<Void> task = mActivityRecognitionClient.requestActivityUpdates(
                6*1000,
                mPendingIntent);

        task.addOnSuccessListener(result -> Toast.makeText(getApplicationContext(),
                "Successfully requested activity updates",
                Toast.LENGTH_SHORT)
                .show());

       task.addOnFailureListener(e -> Toast.makeText(getApplicationContext(),
               "Requesting activity updates failed to start",
               Toast.LENGTH_SHORT)
               .show());
    }

    public void removeActivityUpdatesButtonHandler() {
        Task<Void> task = mActivityRecognitionClient.removeActivityUpdates(
                mPendingIntent);
        task.addOnSuccessListener(result -> Toast.makeText(getApplicationContext(),
                "Removed activity updates successfully!",
                Toast.LENGTH_SHORT)
                .show());

        task.addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to remove activity updates!",
                Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(start_activity) {
            if(!Constants.ACTIVITY_INITIALIZED){
                removeActivityUpdatesButtonHandler();
            }

        }
    }
}