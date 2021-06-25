package com.example.lbma.Activities;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.multidex.MultiDex;

import com.example.lbma.BackgroundServices.BackgroundLocationService;
import com.example.lbma.BackgroundServices.BackgroundNotificationService;
import com.example.lbma.BackgroundServices.BackgroundScreenService;
import com.example.lbma.R;
import com.example.lbma.Variables.Constants;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationInfo extends AppCompatActivity {
    public BroadcastReceiver broadcastReceiver;
    private ToggleButton toggleLocation;
    private TextView place_val, place_id_val, place_conf_val;
    public RelativeLayout loc_field;
    SupportMapFragment mapFragment;
    LatLng myLoc ;
    //GoogleMap mMap;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_info);
        toggleLocation = findViewById(R.id.toggleLocation);
        place_val =findViewById(R.id.place_val);
        place_id_val =findViewById(R.id.place_id_val);
        place_conf_val =findViewById(R.id.place_conf_val);
        loc_field = findViewById(R.id.location_field);
        //mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);


        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.BROADCAST_LOCATION_DETECTED)) {
                    String location_type = intent.getStringExtra("location_type");
                    String location_id = intent.getStringExtra("location_id");
                    double location_conf = intent.getDoubleExtra("location_conf", 0.2);
                    handleUserLocation(location_type,location_id,location_conf);
                }
                if(intent.getAction().equals(Constants.BROADCAST_LOCATION_UPDATES)){
                    double lati = intent.getDoubleExtra("latitude", 34.25856);
                    double longi = intent.getDoubleExtra("longitude", -122.2222);
                    myLoc = new LatLng(lati,longi);
                }
            }
        };
        toggleLocation.setChecked(Constants.LOCATION_INITIALIZED);
        toggleLocation.setEnabled(Constants.LOCATION_PERMISSION);
        toggleLocation.setOnClickListener(v -> {
            Constants.LOCATION_INITIALIZED = toggleLocation.isChecked();
            Intent LocationService = new Intent(LocationInfo.this, BackgroundLocationService.class);
            if(Constants.LOCATION_INITIALIZED){
                startService(LocationService);
            }else{
                stopService(LocationService);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        toggleLocation.setEnabled(Constants.LOCATION_PERMISSION);
        Intent backgroundService = new Intent(LocationInfo.this, BackgroundScreenService.class);
        startService(backgroundService);
        Intent notifs = new Intent(LocationInfo.this, BackgroundNotificationService.class);
        startService(notifs);
    }



    public void toggleMenu(View view) {
        Intent intent = new Intent(LocationInfo.this, DashboardMenu.class);
        startActivity(intent);
        finish();

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_LOCATION_DETECTED));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @SuppressLint("DefaultLocale")
    private void  handleUserLocation(String location_type, String location_id, double location_conf){
        Log.d("TAG", location_id+ " " + location_type + " "+ String.format("%.5f", location_conf));
        place_val.setText(location_type);
        place_id_val.setText(location_id);
        place_conf_val.setText(String.format("%.5f", location_conf));
        loc_field.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        Intent backgrounScreenService = new Intent(LocationInfo.this, BackgroundScreenService.class);
        stopService(backgrounScreenService);
        Intent notifs = new Intent(LocationInfo.this, BackgroundNotificationService.class);
        stopService(notifs);
    }

   /* @Override
    public void onMapReady(GoogleMap googleMap) {
        if(myLoc == null){
            myLoc =new LatLng(37.4219983,-122.084);
        }
        googleMap.addMarker(new MarkerOptions()
        .position(myLoc)
        .title("Me"));
    }*/

}