package com.example.lbma.Permissions;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.multidex.MultiDex;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.lbma.R;
import com.example.lbma.Variables.Constants;

import java.util.Arrays;

public class PermissionRational extends AppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "PermissionRational";

    private static final int PERMISSION_REQUEST_ACTIVITY_RECOGNITION = 45;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If permissions granted, we start the main activity (shut this activity down).
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                == PackageManager.PERMISSION_GRANTED) {
            Constants.ACTIVITY_PERMISSION = true;
            finish();
        }

        setContentView(R.layout.permission_rational);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void onClickApprovePermissionRequest(View view) {
        Log.d(TAG, "onClickApprovePermissionRequest()");
        Constants.ACTIVITY_PERMISSION = true;
        // TODO: Review permission request for activity recognition.
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                PERMISSION_REQUEST_ACTIVITY_RECOGNITION);
    }

    public void onClickDenyPermissionRequest(View view) {
        Log.d(TAG, "onClickDenyPermissionRequest()");
        finish();
    }

    /*
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        String permissionResult = "Request code: " + requestCode + ", Permissions: " +
                Arrays.toString(permissions) + ", Results: " + Arrays.toString(grantResults);

        Log.d(TAG, "onRequestPermissionsResult(): " + permissionResult);

        if (requestCode == PERMISSION_REQUEST_ACTIVITY_RECOGNITION) {
            Constants.ACTIVITY_PERMISSION = true;
            // Close activity regardless of user's decision (decision picked up in main activity).
            finish();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }
}
