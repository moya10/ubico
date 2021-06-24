package com.example.lbma.BackgroundServices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.lbma.Variables.Constants;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

public class BackgroundNotificationService extends NotificationListenerService {
    private static final String TAG = BackgroundNotificationService.class.getSimpleName();
    NotificationReceiver notificationReceiver = null;
    private boolean isbound = false;
    private final IBinder binder = new ServiceBinder();

    public BackgroundNotificationService() {
    }

    public class ServiceBinder extends Binder {
        BackgroundNotificationService getService() {
            return BackgroundNotificationService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        isbound = true;
        String action = intent.getAction();
        Log.d("notify_action", action);
        if (SERVICE_INTERFACE.equals(action)) {
            Log.d("notify", "bound by system");
            return super.onBind(intent);
        } else {
            Log.d("notify", "Bound by app");
            return binder;
        }
    }

    public boolean isbound() {
        return isbound;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (notificationAccess()) {
            createIntent();
        } else {
            requestPermission();
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (notificationAccess()) {
            createIntent();
        } else {
            requestPermission();
        }
    }

    private boolean notificationAccess() {
        String notificationListener = Settings.Secure.getString(this.getContentResolver(), "enabled_notification_listeners");
        if (notificationListener != null && notificationListener.contains(getPackageName())) {
            Log.d("notify", "has access");
            return true;
        } else {
            Log.d("notify", "no access");
            return false;
        }
    }

    private void createIntent() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("notification");
        notificationReceiver = new NotificationReceiver();
        registerReceiver(notificationReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        if(notificationReceiver != null){
            unregisterReceiver(notificationReceiver);
        }
        super.onDestroy();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        Intent intent1 = new Intent(Constants.BROADCAST_NOTIFY);
        intent1.putExtra("notifs_active", getActiveNotifications().length);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent1);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        Intent intent1 = new Intent(Constants.BROADCAST_NOTIFY);
        intent1.putExtra("notifs_active", getActiveNotifications().length);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent1);
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.d("connected!!!!!!!!!!!!!", "success");

    }

    public void requestPermission() {
        Intent requestIntent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        requestIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(requestIntent);
    }

    public class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("notify").equals("notify")) {
                Log.d("NOTIFY", "received");
                int i = 1;
                for (StatusBarNotification sbn : BackgroundNotificationService.this.getActiveNotifications()) {
                    Log.d(TAG, sbn.getPackageName());
                    Intent intent1 = new Intent(Constants.BROADCAST_NOTIFY);
                    intent1.putExtra("notifs_active", i);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);
                    i++;
                }
            } else {
                Log.d("NOTIFY", "abort");
            }
        }
    }
}
