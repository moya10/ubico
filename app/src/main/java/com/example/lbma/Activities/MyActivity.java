package com.example.lbma.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.multidex.MultiDex;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.example.lbma.BackgroundServices.BackgroundNotificationService;
import com.example.lbma.BackgroundServices.BackgroundScreenService;
import com.example.lbma.Variables.Constants;
import com.example.lbma.R;
import com.example.lbma.BackgroundServices.BackgroundActivityService;


public class MyActivity extends AppCompatActivity {


    private ToggleButton toggleActivity;

    private final boolean runningQorLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    public TextView running, vehicle, walking, still, cycling, foot, activity_unknown;
    public BroadcastReceiver broadcastReceiver;
    public RelativeLayout activity_field;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_activity);
        toggleActivity = findViewById(R.id.toggleActivity);
        running = findViewById(R.id.running_conf);
        vehicle = findViewById(R.id.vehicle_conf);
        walking = findViewById(R.id.walking_conf);
        still = findViewById(R.id.still_conf);
        cycling = findViewById(R.id.bike_conf);
        foot = findViewById(R.id.foot_conf);
        activity_unknown = findViewById(R.id.unknown_conf);
        activity_field = findViewById(R.id.activity_field);



        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.BROADCAST_DETECTED_ACTIVITY)) {
                    String type = intent.getStringExtra("type");
                    String confidence = intent.getStringExtra("confidence");
                    handleUserActivity(type, confidence);
                }
            }
        };

        toggleActivity.setChecked(Constants.ACTIVITY_INITIALIZED);

        toggleActivity.setEnabled(Constants.ACTIVITY_PERMISSION);

        toggleActivity.setOnClickListener(v -> {
            if (ActivityRecognitionPermissionApproved()) {
                Constants.ACTIVITY_INITIALIZED = toggleActivity.isChecked();
                Log.d("toggle",String.valueOf(Constants.ACTIVITY_INITIALIZED));
                HandleBackgroundService();
            }
        });

        StartActivityUpdates();
    }

    @Override
    protected void onStart() {
        super.onStart();
        toggleActivity.setEnabled(Constants.ACTIVITY_PERMISSION);
        Intent backgrounScreenService = new Intent(MyActivity.this, BackgroundScreenService.class);
        startService(backgrounScreenService);
        Intent notifs = new Intent(MyActivity.this, BackgroundNotificationService.class);
        startService(notifs);
    }

    private void StartActivityUpdates() {
        if (ActivityRecognitionPermissionApproved()) {
            HandleBackgroundService();
        }
    }

    private void HandleBackgroundService() {
        Intent intent = new Intent(MyActivity.this, BackgroundActivityService.class);

        if (Constants.ACTIVITY_INITIALIZED) {
            startService(intent);


        } else {
            stopService(intent);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY));

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        Intent backgrounScreenService = new Intent(MyActivity.this, BackgroundScreenService.class);
        stopService(backgrounScreenService);
        Intent notifs = new Intent(MyActivity.this, BackgroundNotificationService.class);
        stopService(notifs);
    }

    public void toggleMenu(View view) {
        Intent intent = new Intent(MyActivity.this, DashboardMenu.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    private boolean ActivityRecognitionPermissionApproved() {
        if (runningQorLater) {
            return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION);
        } else {
            return true;
        }
    }


    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void handleUserActivity(String type, String confidence) {
        //String channel_ID = "My Activity";
        //NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channel_ID);


        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        //    if (notificationManager != null &&
        //            notificationManager.getNotificationChannel(channel_ID) == null) {
        //        NotificationChannel notificationChannel = new NotificationChannel(
        //                channel_ID,
        //                "Activity Service",
        //                NotificationManager.IMPORTANCE_HIGH
        //        );
        //        notificationChannel.setDescription("Activity service channel");
        //        notificationManager.createNotificationChannel(notificationChannel);
        //        Intent intent = new Intent("notification");
        //        intent.putExtra("notify","notify");
        //        sendBroadcast(intent);
        //    }
        //}
        switch (type) {
            case Constants.ACTIVITY_TYPE_IN_VEHICLE: {
                vehicle.setText(confidence);
                activity_field.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), Constants.ACTIVITY_TYPE_IN_VEHICLE +": " +confidence, Toast.LENGTH_SHORT).show();
                Log.d("ActivityRecogition", "In Vehicle: " + confidence);
                break;
            }
            case Constants.ACTIVITY_TYPE_ON_BICYCLE: {
                cycling.setText(confidence);
                activity_field.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), Constants.ACTIVITY_TYPE_ON_BICYCLE +": " + confidence, Toast.LENGTH_SHORT).show();
                Log.d("ActivityRecogition", "On Bicycle: " + confidence);
                break;
            }
            case Constants.ACTIVITY_TYPE_ON_FOOT: {
                foot.setText(confidence);
                activity_field.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), Constants.ACTIVITY_TYPE_ON_FOOT +": " + confidence, Toast.LENGTH_SHORT).show();
                Log.d("ActivityRecogition", "On Foot: " + confidence);
                //if (Float.parseFloat(confidence) >= 0.75) {

                //    builder.setContentText("Johnny?");
                //    builder.setSmallIcon(R.mipmap.ic_launcher);
                //    builder.setContentTitle(getString(R.string.app_name));
                //    NotificationManagerCompat.from(this).notify(0, builder.build());
                //    Intent intent = new Intent("notification");
                //    intent.putExtra("notify","notify");
                //    sendBroadcast(intent);
                //}
                break;
            }
            case Constants.ACTIVITY_TYPE_RUNNING: {
                running.setText(confidence);
                activity_field.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), Constants.ACTIVITY_TYPE_RUNNING +": " + confidence, Toast.LENGTH_SHORT).show();
                Log.d("ActivityRecogition", "Running: " + confidence);
                break;
            }
            case Constants.ACTIVITY_TYPE_WALKING: {
                walking.setText(confidence);
                activity_field.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), Constants.ACTIVITY_TYPE_WALKING +": " + confidence, Toast.LENGTH_SHORT).show();
                Log.d("ActivityRecogition", "Walking: " + confidence);
                //if (Float.parseFloat(confidence) >= 0.75) {

                //    builder.setContentText("Are you walking?");
                //    builder.setSmallIcon(R.mipmap.ic_launcher);
                //    builder.setContentTitle(getString(R.string.app_name));
                //    NotificationManagerCompat.from(this).notify(0, builder.build());
                //    Intent intent = new Intent("notification");
                //    intent.putExtra("notify","notify");
                //    sendBroadcast(intent);
                //}
                break;
            }
            case Constants.ACTIVITY_TYPE_STILL: {
                still.setText(confidence);
                activity_field.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), Constants.ACTIVITY_TYPE_STILL +": " + confidence, Toast.LENGTH_SHORT).show();
                Log.d("ActivityRecogition", "Still: " + confidence);
                //if (Float.parseFloat(confidence) >= 0.75) {

                //    builder.setContentText("Are you relaxed?");
                //    builder.setSmallIcon(R.mipmap.ic_launcher);
                //    builder.setContentTitle(getString(R.string.app_name));


                //    NotificationManagerCompat.from(this).notify(0, builder.build());
                 //   Intent intent = new Intent("notification");
                 //   intent.putExtra("notify","notify");
                 //   sendBroadcast(intent);
                //}
                break;
            }
            case Constants.ACTIVITY_TYPE_UNKNOWN: {
                activity_unknown.setText(confidence);
                activity_field.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), Constants.ACTIVITY_TYPE_UNKNOWN +": " + confidence, Toast.LENGTH_SHORT).show();
                Log.d("ActivityRecogition", "UNKNOWN: " + confidence);
                //if (Float.parseFloat(confidence) >= 0.75) {

                //    builder.setContentText("What are you doing?");
                //    builder.setSmallIcon(R.mipmap.ic_launcher);
                //    builder.setContentTitle(getString(R.string.app_name));
                 //   NotificationManagerCompat.from(this).notify(0, builder.build());
                //    Intent intent = new Intent("notification");
                //    intent.putExtra("notify","notify");
                //    sendBroadcast(intent);
                //}
                break;
            }
            default:
                activity_unknown.setText(confidence);
                activity_field.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), Constants.ACTIVITY_TYPE_UNKNOWN +": " + confidence, Toast.LENGTH_SHORT).show();
                Log.d("ActivityRecogition", "UNKNOWN: " + confidence);
                //if (Float.parseFloat(confidence) >= 0.75) {
//
                //    builder.setContentText("What are you doing?");
                //    builder.setSmallIcon(R.mipmap.ic_launcher);
                //    builder.setContentTitle(getString(R.string.app_name));
                //    NotificationManagerCompat.from(this).notify(0, builder.build());
                //    Intent intent = new Intent("notification");
                //    intent.putExtra("notify","notify");
                //    sendBroadcast(intent);
                //}
                break;
        }
    }
}