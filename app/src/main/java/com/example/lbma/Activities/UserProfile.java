package com.example.lbma.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.lbma.BackgroundServices.BackgroundNotificationService;
import com.example.lbma.BackgroundServices.BackgroundScreenService;
import com.example.lbma.DatabaseHandler.DatabaseHandler;
import com.example.lbma.Models.ApiModel;
import com.example.lbma.Models.UserModel;
import com.example.lbma.R;
import com.example.lbma.Sockets.TLSSocketFactory;
import com.example.lbma.Variables.Constants;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserProfile extends AppCompatActivity {

    private static final String TAG = "PROFILE";
    public UserModel um;
    ImageButton toggle_menu;
    ImageView profile;
    Button profile_btn;
    TextInputLayout fullname, age, gender, email, telephone;
    TextView age_desc, gender_desc, full_name, name;
    DatabaseHandler databaseHandler;
    ApiModel apiModel;

    JSONObject parameters = null;
    JSONObject jsonBody = null;
    RequestQueue requestQueue;
    public String url = "http://[server]";

    private static final String profile_status = "PROFILE_INITIALIZED";

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_view);

        AutoCompleteTextView autoCompleteTextViewGender = findViewById(R.id.dropdown_text);
        AutoCompleteTextView autoCompleteTextViewAge = findViewById(R.id.dropdown_text2);

        String[] option = new String[]{"male", "female", "other", "not disclosed"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(this, R.layout.gender_item, option);
        String[] items = new String[]{"18-24", "25-30", "31-40", "41-50", "51-60", "60+", "not disclosed"};
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter(this, R.layout.age_item, items);

        autoCompleteTextViewGender.setAdapter(arrayAdapter);
        autoCompleteTextViewAge.setAdapter(arrayAdapter2);

        toggle_menu = findViewById(R.id.menu_button);
        age_desc = findViewById(R.id.age_desc);
        gender_desc = findViewById(R.id.gender_desc);
        full_name = findViewById(R.id.full_name);
        fullname = findViewById(R.id.fullname);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        telephone = findViewById(R.id.telephone);
        age = findViewById(R.id.select_age);
        gender = findViewById(R.id.select_gender);
        profile_btn = findViewById(R.id.profile_btn);
        profile = findViewById(R.id.profile_image);

        databaseHandler = new DatabaseHandler(UserProfile.this);
        getDeviceName();
        profile_btn.setOnClickListener(v -> {

            try {
                um = new UserModel(1, Objects.requireNonNull(fullname.getEditText()).getText().toString(),
                        Objects.requireNonNull(gender.getEditText()).getText().toString(),
                        Objects.requireNonNull(age.getEditText()).getText().toString(),
                        Objects.requireNonNull(email.getEditText()).getText().toString(),
                        Integer.parseInt(Objects.requireNonNull(telephone.getEditText()).getText().toString()));

                if (profile_status.equals(Constants.PROFILE_INITIALIZED)) {
                    databaseHandler.addProfile(um);
                    save_preferences();

                    try {
                        updateAndroidSecurityProvider();
                        parameters = new JSONObject();
                        parameters.put("username", "x@ceid.upatras.gr");
                        parameters.put("password", "x");
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "api/auth/login", parameters,
                                response -> {
                                    try {
                                        Constants.DEVICE_TOKEN = response.getString("token");

                                        try {
                                            updateAndroidSecurityProvider();
                                            jsonBody = new JSONObject();
                                            jsonBody.put("createdTime", 0);
                                            jsonBody.put("name", Constants.DEVICE);
                                            jsonBody.put("type", "Participant");

                                            JsonObjectRequest jsonAdddevice = new JsonObjectRequest(Request.Method.POST, url + "api/device", jsonBody,
                                                    response13 -> {
                                                        try {
                                                            updateAndroidSecurityProvider();
                                                            Constants.DEVICE_ID = response13.getJSONObject("id").getString("id");
                                                            Log.d(TAG, Constants.DEVICE_ID);

                                                            JsonObjectRequest jsonGetDeviceCredentials = new JsonObjectRequest(Request.Method.GET, url + "api/device/" + Constants.DEVICE_ID + "/credentials", null,
                                                                    response12 -> {

                                                                        try {
                                                                            Constants.DEVICE_CREDENTIALS = response12.getString("credentialsId");
                                                                            Log.d(TAG, Constants.DEVICE_CREDENTIALS);
                                                                            apiModel = new ApiModel(-1, Constants.DEVICE, Constants.DEVICE_ID, Constants.DEVICE_CREDENTIALS, Constants.DEVICE_TOKEN);
                                                                            databaseHandler.addApi(apiModel);
                                                                            JSONObject recruitObj = new JSONObject();
                                                                            recruitObj.put("user_age", String.valueOf(um.getAge()));
                                                                            recruitObj.put("user_sex", String.valueOf(um.getGender()));
                                                                            recruitObj.put("recruiting_team", "4");
                                                                            JsonObjectRequest jsonRecrue = new JsonObjectRequest(Request.Method.POST, url + "api/v1/" + Constants.DEVICE_CREDENTIALS + "/attributes", recruitObj,
                                                                                    response1 -> {},
                                                                                    error -> {}) {
                                                                                @Override
                                                                                public Map<String, String> getHeaders() throws AuthFailureError {
                                                                                    Map<String, String> params = new HashMap<>();
                                                                                    params.put("Content-Type", "application/json");
                                                                                    params.put("Access", "application/json");
                                                                                    params.put("x-authorization", "Bearer " + Constants.DEVICE_TOKEN);
                                                                                    return params;
                                                                                }
                                                                            };
                                                                            UserProfile.this.addToReqQueue(jsonRecrue, "postReqRecrue");
                                                                        } catch (JSONException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    },
                                                                    error -> {}) {
                                                                @Override
                                                                public Map<String, String> getHeaders() throws AuthFailureError {
                                                                    Map<String, String> params = new HashMap<>();
                                                                    params.put("Content-Type", "application/json");
                                                                    params.put("Access", "application/json");
                                                                    params.put("x-authorization", "Bearer " + Constants.DEVICE_TOKEN);
                                                                    return params;
                                                                }
                                                            };
                                                            UserProfile.this.addToReqQueue(jsonGetDeviceCredentials, "postReqGetDeviceCredentials");
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    },
                                                    error -> {}) {
                                                @Override
                                                public Map<String, String> getHeaders() throws AuthFailureError {
                                                    Map<String, String> params = new HashMap<>();
                                                    params.put("Content-Type", "application/json");
                                                    params.put("Access", "application/json");
                                                    params.put("x-authorization", "Bearer " + Constants.DEVICE_TOKEN);
                                                    return params;
                                                }
                                            };
                                            UserProfile.this.addToReqQueue(jsonAdddevice, "postReqAddNewDevice");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                },
                                error -> { error.printStackTrace(); }) {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                params.put("Content-Type", "application/json");
                                params.put("Access", "application/json");
                                return params;
                            }
                        };
                        UserProfile.this.addToReqQueue(jsonObjectRequest, "postReqDeviceInitialize");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    databaseHandler.updateProfile(um, 1);
                }
                ShowProfileView();

            } catch (Exception e) {
                Toast.makeText(UserProfile.this, "Please, try again", Toast.LENGTH_SHORT).show();
            }

        });
        ShowProfileView();
    }

    private void getDeviceName() {
        try {
            List<ApiModel> data = databaseHandler.getAllApi();
            if (data.isEmpty()) {
                try {
                    parameters = new JSONObject();
                    parameters.put("username", "x@ceid.upatras.gr");
                    parameters.put("password", "x");
                    JsonObjectRequest req1 = new JsonObjectRequest(Request.Method.POST, url + "api/auth/login", parameters,
                            response -> {
                                try {
                                    Constants.DEVICE_TOKEN = response.getString("token");
                                    Log.d(TAG, Constants.DEVICE_TOKEN);

                                    JsonObjectRequest req2 = new JsonObjectRequest(Request.Method.GET, url + "api/tenant/devices?type=Participant&textSearch=Participant_4&pageSize=100&page=0", null,
                                            new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    try {
                                                        Constants.DEVICE = "Participant_4_" + response.getJSONArray("data").length();
                                                        ApiModel apiModel = new ApiModel(-1, Constants.DEVICE, Constants.DEVICE_ID, Constants.DEVICE_CREDENTIALS, Constants.DEVICE_TOKEN);
                                                        databaseHandler.addApi(apiModel);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            error.printStackTrace();
                                        }
                                    }) {
                                        @Override
                                        public Map<String, String> getHeaders() throws AuthFailureError {
                                            Map<String, String> params = new HashMap<>();
                                            params.put("Content-Type", "application/json");
                                            params.put("Access", "application/json");
                                            params.put("x-authorization", "Bearer " + Constants.DEVICE_TOKEN);
                                            return params;
                                        }
                                    };
                                    UserProfile.this.addToReqQueue(req2, "postReq2");
                                } catch (Exception e) {

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("Content-Type", "application/json");
                            params.put("Access", "application/json");
                            return params;
                        }
                    };
                    UserProfile.this.addToReqQueue(req1, "postReq1");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Constants.DEVICE = data.get(data.size() - 1).getDEVICE();
                Constants.DEVICE_ID = data.get(data.size() - 1).getDEVICE_ID();
                Constants.DEVICE_CREDENTIALS = data.get(data.size() - 1).getDEVICE_CREDENTIALS();
                Constants.DEVICE_TOKEN = data.get(data.size() - 1).getDEVICE_TOKEN();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void save_preferences() {
        Constants.PROFILE_INITIALIZED = Constants.PROFILE_UPDATED;
    }

    @SuppressLint("ShowToast")
    private void ShowProfileView() {
        databaseHandler = new DatabaseHandler(UserProfile.this);
        try {
            List<UserModel> data = databaseHandler.getAllUser();

            Log.d("up", data.get(data.size() - 1).getGender());
            full_name.setText(data.get(data.size() - 1).getFullname());
            name.setText(data.get(data.size() - 1).getFullname());
            age_desc.setText(data.get(data.size() - 1).getAge());
            gender_desc.setText(data.get(data.size() - 1).getGender());

            if (data.get(data.size() - 1).getGender().equals("male")) {
                profile.setImageResource(R.mipmap.twod);
            } else if (data.get(data.size() - 1).getGender().equals("female")) {
                profile.setImageResource(R.mipmap.profile_image);
            } else profile.setImageResource(R.mipmap.multi);

        } catch (Exception e) {
            Toast.makeText(UserProfile.this, "Add profile", Toast.LENGTH_SHORT).show();
        }
    }


    public void toggleMenu(View view) {
        Intent intent = new Intent(UserProfile.this, DashboardMenu.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent backgroundService = new Intent(UserProfile.this, BackgroundScreenService.class);
        startService(backgroundService);
        Intent notifs = new Intent(UserProfile.this, BackgroundNotificationService.class);
        startService(notifs);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent backgrounScreenService = new Intent(UserProfile.this, BackgroundScreenService.class);
        stopService(backgrounScreenService);
        Intent notifs = new Intent(UserProfile.this, BackgroundNotificationService.class);
        stopService(notifs);
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                HttpStack stack = null;
                try {
                    stack = new HurlStack(null, new TLSSocketFactory());
                } catch (KeyManagementException e) {
                    e.printStackTrace();
                    stack = new HurlStack();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    stack = new HurlStack();
                }
                requestQueue = Volley.newRequestQueue(UserProfile.this, stack);
            //} else {
            //    requestQueue = Volley.newRequestQueue(UserProfile.this);
          //  }
        }
        return requestQueue;
    }

    private void updateAndroidSecurityProvider() {
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        }
    }

    public void addToReqQueue(Request request, String tag) {
        request.setTag(tag);
        getRequestQueue().add(request);
    }

    /*public void cancellAllRequests(String tag) {
        getRequestQueue().cancelAll(tag);
    }*/
}