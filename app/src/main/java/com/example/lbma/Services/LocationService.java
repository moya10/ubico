package com.example.lbma.Services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.lbma.Variables.Constants;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;

public class LocationService extends IntentService {

    private static final String TAG = "DEVICE_INFO";

    public LocationService() {
        super("LocationService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ID);
        FindCurrentPlaceRequest req = FindCurrentPlaceRequest.newInstance(placeFields);
        if (Constants.LOCATION_PERMISSION) {
            LocationPermissionCheck(req);
        }

    }

    private void LocationPermissionCheck(FindCurrentPlaceRequest req) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (inintializePlaces()) {
                PlacesClient placesClient = Places.createClient(this);
                Task<FindCurrentPlaceResponse> res = placesClient.findCurrentPlace(req);
                getLocationAttributes(res);
            }
        }
    }

    private boolean inintializePlaces() {
        //if (!Places.isInitialized()) {
        if (Constants.LOCATION_INITIALIZED) {
            Places.initialize(getApplicationContext(), "YOUR_API_KEY");
            return true;
        } else {
            return false;
        }

    }

    @SuppressLint("DefaultLocale")
    private void getLocationAttributes(Task<FindCurrentPlaceResponse> res) {

        res.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FindCurrentPlaceResponse response = task.getResult();

                // COLLECTION SORT NOT WORKING... ;(

                    /*List<PlaceLikelihood> placeLikelihood = response.getPlaceLikelihoods();
                    //Log.d(TAG, String.format("Place has id '%s' ", Constants.PLACE_ID));

                    Collections.sort(placeLikelihood, new Comparator<PlaceLikelihood>() {
                        @Override
                        public int compare(PlaceLikelihood placeLikelihood, PlaceLikelihood t) {
                            double value = placeLikelihood.getLikelihood() - t.getLikelihood();
                            return  value == 0 ? 0 : value > 0 ? 1 : -1;
                        }
                    });

                    Collections.reverse(response.getPlaceLikelihoods());
                    double chara = response.getPlaceLikelihoods().get(0).getLikelihood();
                    Constants.PLACE_ID = response.getPlaceLikelihoods().get(0).getPlace().getId();
                    Log.d(TAG, String.format("Place has likely '%s' and id '%s'", String.format("%.2f",chara), Constants.PLACE_ID));*/


                for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                    Constants.PLACE_NAME = placeLikelihood.getPlace().getName();
                    Constants.PLACE_LIKELIHOOD = placeLikelihood.getLikelihood();
                    Constants.PLACE_ID = placeLikelihood.getPlace().getId();
                    Log.d(TAG, String.format("Places '%s' has likelihood: %s has id '%s' ", Constants.PLACE_NAME, String.format("%.2f", Constants.PLACE_LIKELIHOOD), Constants.PLACE_ID));
                    if (placeLikelihood.getPlace().getTypes() != null) {
                        Object[] TypeofPlace = placeLikelihood.getPlace().getTypes().toArray();
                        for (Object placeType : TypeofPlace) {
                            Constants.PLACE_TYPE = placeType.toString();

                            Log.d(TAG, String.format("Place type is: '%s' with id: '%s' ", Constants.PLACE_TYPE, Constants.PLACE_ID));
                        }
                    }

                }

                broadcastLocation(Constants.PLACE_NAME, Constants.PLACE_ID, Constants.PLACE_LIKELIHOOD);

            } else {
                Exception exc = task.getException();
                if (exc instanceof ApiException) {
                    ApiException apiException = (ApiException) exc;
                    Log.d(TAG, "Find Destination unknown: " + apiException.getStatusCode());
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getApplicationContext(), "Couldn't find Location", Toast.LENGTH_SHORT).show();
            broadcastLocation(Constants.PLACE_NAME, Constants.PLACE_ID, Constants.PLACE_LIKELIHOOD);
        });
    }

    private void broadcastLocation(String name, String id, double conf) {
        Intent intent = new Intent(Constants.BROADCAST_LOCATION_DETECTED);

        intent.putExtra("location_type", name);
        intent.putExtra("location_id", id);
        intent.putExtra("location_conf", conf);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}