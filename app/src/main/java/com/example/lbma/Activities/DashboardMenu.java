package com.example.lbma.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.multidex.MultiDex;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.lbma.BackgroundServices.BackgroundNotificationService;
import com.example.lbma.BackgroundServices.BackgroundScreenService;
import com.example.lbma.Permissions.PermissionRational;
import com.example.lbma.R;
import com.example.lbma.Variables.Constants;

import java.util.Arrays;


public class DashboardMenu extends AppCompatActivity{
    private final boolean runningQorLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 48;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_dashboard);


        if(ActivityRecognitionPermissionApproved()){
            Constants.ACTIVITY_PERMISSION = true;
        }else {
            PermissionRequest();
        }

        if(LocationPermissionApproved()){
            Constants.LOCATION_PERMISSION = true;
        }else{
            String[] permisssions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(DashboardMenu.this, permisssions, LOCATION_PERMISSION_REQUEST_CODE);
        }

    }
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        String permissionResult = "Request code: " + requestCode + ", Permissions: " +
                Arrays.toString(permissions) + ", Results: " + Arrays.toString(grantResults);

        Log.d("TAG", "onRequestPermissionsResult(): " + permissionResult);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            Constants.LOCATION_PERMISSION= true;
        }

    }


    public void toggleStats(View view){
        Intent intent = new Intent(DashboardMenu.this, PiRatings.class);
        startActivity(intent);
        finish();
    }
    public void toggleDeviceInfo(View view){
        Intent intent = new Intent(DashboardMenu.this, LocationInfo.class);
        startActivity(intent);
        finish();
    }
    public void toggleProfile(View view){
        Intent intent = new Intent(DashboardMenu.this, UserProfile.class);
        startActivity(intent);
        finish();
    }
    public void toggleTrackerService(View view){
        Intent intent = new Intent(DashboardMenu.this, MyActivity.class);
        startActivity(intent);
        finish();
    }

    private void PermissionRequest() {
        Intent startIntent = new Intent(this, PermissionRational.class);
        startActivityForResult(startIntent, 0);
    }
    private boolean ActivityRecognitionPermissionApproved() {
        if (runningQorLater) {
            return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION);
        } else {
            return true;
        }
    }
    private boolean LocationPermissionApproved() {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) && PackageManager.PERMISSION_GRANTED ==  ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent backgroundService = new Intent(DashboardMenu.this, BackgroundScreenService.class);
        startService(backgroundService);
        Intent notifs = new Intent(DashboardMenu.this, BackgroundNotificationService.class);
        startService(notifs);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent backgrounScreenService = new Intent(DashboardMenu.this, BackgroundScreenService.class);
        stopService(backgrounScreenService);
        Intent notifs = new Intent(DashboardMenu.this, BackgroundNotificationService.class);
        stopService(notifs);
    }
}