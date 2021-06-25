package com.example.lbma.BackgroundServices;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.lbma.Services.BatteryService;
import com.example.lbma.Services.ConnectivityService;
import com.example.lbma.Services.PowerService;
import com.example.lbma.Variables.Constants;

public class BackgroundScreenService extends Service {
    private static final String TAG = BackgroundScreenService.class.getSimpleName();
    public static boolean isScreenOn = true;
    ScreenReceiver screenReceiver = new ScreenReceiver();
    public BackgroundScreenService() {
    }

    IBinder mBinder = new BackgroundScreenService.LocalBinder();

    public class LocalBinder extends Binder {
        public BackgroundScreenService getServerInstance() {
            return BackgroundScreenService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        CreateIntent();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CreateIntent();

    }

    private void CreateIntent() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenReceiver, intentFilter);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(screenReceiver);

    }

    public static class ScreenReceiver extends  BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                isScreenOn = false;
                Log.d("TAG", "Screen off");
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                isScreenOn = true;
                Log.d("TAG", "Screen on");
            }
            handleIntentActivities(context);
        }

        private void handleIntentActivities(Context context) {
            Intent notify = new Intent(context, BackgroundNotificationService.class);
            notify.setAction(Constants.BROADCAST_NOTIFY);
            context.startService(notify);

            Intent intent = new Intent(context, BackgroundActivityService.class);
            context.startService(intent);

            Intent loc = new Intent(context, BackgroundLocationService.class);
            context.startService(loc);

            Intent battery = new Intent(context, BatteryService.class);
            battery.setAction(Intent.ACTION_BATTERY_CHANGED);
            context.startService(battery);

            Intent conn = new Intent(context, ConnectivityService.class);
            context.startService(conn);

            Intent power = new Intent(context, PowerService.class);
            context.startService(power);







        }
    }
}