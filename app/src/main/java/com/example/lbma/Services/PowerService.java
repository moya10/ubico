package com.example.lbma.Services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.view.Display;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.example.lbma.Variables.Constants;

public class PowerService extends IntentService {
    private static final String TAG = "INFO";
    private PowerManager pm;
    private DisplayManager dm;

    public PowerService() {
        super("PowerService");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onHandleIntent(Intent intent) {

        dm = (DisplayManager) getApplicationContext().getSystemService(Context.DISPLAY_SERVICE);
        pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);

        if (pm == null) {
            Constants.DEVICE_INTERACTIVE = false;
            Log.d(TAG, "POWER_MANAGER_STATE: " + String.valueOf(Constants.DEVICE_INTERACTIVE));
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) { // API 20
            for (Display display : dm.getDisplays()) {
                if (display.getState() != Display.STATE_OFF) {
                    Constants.DISPLAY_STATE = display.getState();
                    Log.d(TAG, "DISPLAY_STATE: " + String.valueOf(display.getState()));
                }
            }
            Constants.DEVICE_INTERACTIVE = pm.isInteractive();
            Log.d(TAG, "POWER_MANAGER_STATE:" + String.valueOf(pm.isInteractive()));
        } else {
            Constants.DEVICE_INTERACTIVE = pm.isScreenOn();
            Constants.DISPLAY_STATE = 2; // default
            Log.d(TAG, "DISPLAY_STATE: " + String.valueOf(Constants.DISPLAY_STATE));
            Log.d(TAG, "POWER_MANAGER_STATE: " + String.valueOf(Constants.DEVICE_INTERACTIVE));
        }
        String milli = String.valueOf(System.currentTimeMillis());
        Log.d(TAG, "Unix_timestamp: " + milli);

        long et_milli = Long.parseLong(milli);
        Constants.SYSTEM_TIME = et_milli;
        broadcastPower(Constants.DISPLAY_STATE,Constants.DEVICE_INTERACTIVE,Constants.SYSTEM_TIME);
    }

    private void broadcastPower(int display_state, boolean device_interactive, long system_time){
        Intent intent = new Intent(Constants.BROADCAST_POWER_ACTIVITY);
        intent.putExtra("display_state",  display_state);
        intent.putExtra("device_interactive", device_interactive);
        intent.putExtra("system_time",system_time);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}