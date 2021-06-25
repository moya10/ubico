package com.example.lbma.Services;

import android.app.IntentService;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Build;
import android.util.Log;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.lbma.Variables.Constants;


public class BatteryService extends IntentService {
    private static final String TAG = "DEVICE_INFO";

    public BatteryService() {
        super("BatteryService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        android.content.Context context = getApplicationContext();
        BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
        if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // API 23
                Constants.BATTERY_LEVEL = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                Constants.BATTERY_STATUS = bm.isCharging();
                Log.d(TAG, "BATTERY_STATUS_CHARGING: " + String.valueOf(Constants.BATTERY_STATUS));
                Log.d(TAG, "BATTERY_LEVEL: " + String.valueOf(Constants.BATTERY_LEVEL));
            } else {
                Constants.BATTERY_LEVEL = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                Log.d(TAG, "BATTERY_LEVEL: " + String.valueOf(100 * Constants.BATTERY_LEVEL / scale));

                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 2);
                if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                    Constants.BATTERY_STATUS = true;
                    Log.d(TAG, "BATTERY_STATUS_CHARGING: " + String.valueOf(Constants.BATTERY_STATUS));
                } else {
                    Constants.BATTERY_STATUS = false;
                    Log.d(TAG, "BATTERY_STATUS_CHARGING: " + String.valueOf(Constants.BATTERY_STATUS));
                }
            }
            broadcastBattery(Constants.BATTERY_STATUS, Constants.BATTERY_LEVEL);
        }

    }

    private void broadcastBattery(boolean battery_status, int battery_level) {
        Intent intent = new Intent(Constants.BROADCAST_BATTERY_ACTIVITY);
        intent.putExtra("battery_level", battery_level);
        intent.putExtra("battery_status", battery_status);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}