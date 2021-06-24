package com.example.lbma.BackgroundServices;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.lbma.Services.LocationService;
import com.example.lbma.Variables.Constants;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class BackgroundLocationService extends Service {
    public BackgroundLocationService() {
    }
    Intent resultIntent;
    private static final String TAG = "DEVICE_INFO";
    private boolean start_activity = false;
    Intent mIntentService;
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null && locationResult.getLastLocation() != null) {
                double lat = locationResult.getLastLocation().getLatitude();
                double lon = locationResult.getLastLocation().getLongitude();
                //Log.d(TAG, "latitude " + lat + " Longitude " + lon);
               // broadcastUpdates(lat,lon);
            }else{
                Log.d(TAG,"failure");
            }
        }
    };
    /*private void broadcastUpdates(double lati, double longi) {
        Intent intent = new Intent(Constants.BROADCAST_LOCATION_UPDATES);
        intent.putExtra("latitude", lati);
        intent.putExtra("longitude", longi);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }*/
    private void startLocationService() {
        //String channel_ID = "location_channel";
        //NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        resultIntent = new Intent(this,LocationService.class);
        //PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        /*NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channel_ID);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Location Service");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null &&
                    notificationManager.getNotificationChannel(channel_ID) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        channel_ID,
                        "LocationService",
                        NotificationManager.IMPORTANCE_HIGH
                );
                notificationChannel.setDescription("Location service channel");
                notificationManager.createNotificationChannel(notificationChannel);

            }
        }*/
        LocationRequest request = new LocationRequest();
        request.setInterval(3 * 1000);
        request.setFastestInterval(2000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(request, locationCallback, Looper.getMainLooper());
            startService(resultIntent);
        }

    }
    private void StopLocationService(){
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
        stopService(resultIntent);
        stopSelf();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if(Constants.LOCATION_INITIALIZED) {
            CreateIntent();
            startLocationService();
        }
        return START_STICKY;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        if(Constants.LOCATION_INITIALIZED) {

            CreateIntent();
            startLocationService();
        }
    }

    private void CreateIntent() {
        start_activity = true;
        mIntentService = new Intent(this, LocationService.class);
        startService(mIntentService);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(start_activity){
            if(!Constants.LOCATION_INITIALIZED){
                StopLocationService();
            }
        }
    }
}