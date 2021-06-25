package com.example.lbma.Services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.lbma.Variables.Constants;


public class ConnectivityService extends IntentService {
    private static final String TAG = "DEVICE_INFO";

    public ConnectivityService() {
        super("ConnectivityService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        android.content.Context context = getApplicationContext();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNets = connectivityManager.getActiveNetworkInfo();
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileData = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (activeNets == null) {
            Constants.NETWORK_TYPE = "DISCONNECTED";
            Log.d(TAG, "CONNECTION: " + Constants.NETWORK_TYPE);
        } else {
            //Check NetworkType
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // API 21
                Network[] activeNetworks = connectivityManager.getAllNetworks();
                for (Network network : activeNetworks) {

                    if (wifi.isConnected()) {
                        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                        if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {

                            Constants.NETWORK_TYPE = "TRANSPORT_WIFI";
                            Log.d(TAG, "CONNECTION: " + Constants.NETWORK_TYPE);
                        }else{
                            Constants.NETWORK_TYPE = "UNKNOWN";
                            Log.d(TAG, "CONNECTION: " + Constants.NETWORK_TYPE);
                        }

                    } else if (mobileData.isConnected()) {
                        Constants.NETWORK_TYPE = "Roaming Data";
                        Log.d(TAG, "CONNECTION: " + Constants.NETWORK_TYPE);
                    } else {
                        Constants.NETWORK_TYPE = "NONE";
                        Log.d(TAG, "CONNECTION: " + Constants.NETWORK_TYPE);
                    }
                }
            } else {
                if (wifi.isConnected()) {
                    Constants.NETWORK_TYPE = "TRANSPORT_WIFI";
                    Log.d(TAG, "CONNECTION: " + Constants.NETWORK_TYPE);
                } else if (mobileData.isConnected()) {
                    Constants.NETWORK_TYPE = "Roaming Data";
                    Log.d(TAG, "CONNECTION: " + Constants.NETWORK_TYPE);
                } else {
                    Constants.NETWORK_TYPE = "NONE";
                    Log.d(TAG, "CONNECTION: " + Constants.NETWORK_TYPE);
                }
            }
        }
        broadcastConnection(Constants.NETWORK_TYPE);

    }

    private void broadcastConnection(String network_type) {
        Intent intent = new Intent(Constants.BROADCAST_CONNECTION_ACTIVITY);
        intent.putExtra("network_type", network_type);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}