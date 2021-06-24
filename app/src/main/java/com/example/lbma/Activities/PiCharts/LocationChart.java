package com.example.lbma.Activities.PiCharts;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.lbma.Activities.PiRatings;
import com.example.lbma.BackgroundServices.BackgroundNotificationService;
import com.example.lbma.BackgroundServices.BackgroundScreenService;
import com.example.lbma.R;
import com.example.lbma.Sockets.TLSSocketFactory;
import com.example.lbma.Variables.Constants;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationChart extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "LOCATION_RES";

    DatePickerDialog datePickerDialog;
    TextInputLayout start_date, end_date;
    Button btn;

    int start_day, start_month, start_year, end_day, end_month, end_year;
    long start_ts, end_ts;
    public RequestQueue requestQueue;
    public JSONObject parameters;
    public String url = "http://[server]";

    GoogleMap mMap;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_chart);
        start_date = findViewById(R.id.start_date);
        end_date = findViewById(R.id.end_date);

        start_date.getEditText().setInputType(InputType.TYPE_NULL);
        start_date.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                start_day = calendar.get(Calendar.DAY_OF_MONTH);
                start_month = calendar.get(Calendar.MONTH);
                start_year = calendar.get(Calendar.YEAR);
                datePickerDialog = new DatePickerDialog(LocationChart.this, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        start_date.getEditText().setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                        String startDate = year + "/" + (month + 1) + "/" + dayOfMonth + " " + "00:00:00";
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        try {
                            Date date = simpleDateFormat.parse(startDate);
                            start_ts = date.getTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, start_year, start_month, start_day);
                datePickerDialog.show();
            }
        });

        end_date.getEditText().setInputType(InputType.TYPE_NULL);
        end_date.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                end_day = calendar.get(Calendar.DAY_OF_MONTH);
                end_month = calendar.get(Calendar.MONTH);
                end_year = calendar.get(Calendar.YEAR);
                datePickerDialog = new DatePickerDialog(LocationChart.this, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        end_date.getEditText().setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                        String endDate = year + "/" + (month + 1) + "/" + dayOfMonth + " " + "23:59:59";
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        try {
                            Date date = simpleDateFormat.parse(endDate);
                            end_ts = date.getTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, end_year, end_month, end_day);
                datePickerDialog.show();
            }
        });

        btn = (Button) findViewById(R.id.get_date);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    parameters = new JSONObject();
                    parameters.put("username", "x@ceid.upatras.gr");
                    parameters.put("password", "x");
                    JsonObjectRequest req1 = new JsonObjectRequest(Request.Method.POST, url + "api/auth/login", parameters,
                            response1 -> {
                                try {
                                    Constants.DEVICE_TOKEN = response1.getString("token");
                                    JsonObjectRequest jsonGetDeviceCredentials = new JsonObjectRequest(Request.Method.GET, url + "api/plugins/telemetry/DEVICE/" + Constants.DEVICE_ID + "/values/timeseries?limit=100&agg=NONE&useStrictDataTypes=false&keys=location_id&startTs=" + start_ts + "&endTs=" + end_ts, null,
                                            response -> {
                                                try {
                                                    JSONArray values2 = response.getJSONArray("location_id");
                                                    try {

                                                        ArrayList<Double> lats = new ArrayList<>();
                                                        ArrayList<Double> lngs = new ArrayList<>();
                                                        for (int i = 1; i < values2.length(); i++) {
                                                            JsonObjectRequest req2 = new JsonObjectRequest(Request.Method.GET, "https://maps.googleapis.com/maps/api/place/details/json?place_id=" + values2.getJSONObject(i).get("value").toString() + "&key=YOUR_API_KEY", null,
                                                                    response2 -> {
                                                                        try {
                                                                            JSONObject coordinates = response2.getJSONObject("result").getJSONObject("geometry").getJSONObject("location");

                                                                            if (coordinates != null) {
                                                                                double latitude = coordinates.optDouble("lat");
                                                                                double longitude = coordinates.optDouble("lng");
                                                                                lats.add(latitude);
                                                                                lngs.add(longitude);
                                                                                Log.e(TAG, String.valueOf(lats) + " " + String.valueOf(lngs));
                                                                                multiMark(mMap, lats, lngs);
                                                                            }

                                                                        } catch (JSONException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    },
                                                                    error -> {
                                                                    });

                                                            LocationChart.this.addToReqQueue(req2, "getLatLng");
                                                        }

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            },
                                            error -> {
                                            }
                                    ) {
                                        @Override
                                        public Map<String, String> getHeaders() throws AuthFailureError {
                                            Map<String, String> params = new HashMap<>();
                                            params.put("Content-Type", "application/json");
                                            params.put("Access", "application/json");
                                            params.put("x-authorization", "Bearer " + Constants.DEVICE_TOKEN);
                                            return params;
                                        }
                                    };
                                    LocationChart.this.addToReqQueue(jsonGetDeviceCredentials, "getLocAttributes");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            },
                            error -> {
                            }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("Content-Type", "application/json");
                            params.put("Access", "application/json");
                            return params;
                        }
                    };
                    LocationChart.this.addToReqQueue(req1, "getTOKEN");
                } catch (
                        Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney)
        );
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public void multiMark(GoogleMap googleMap, ArrayList<Double> lats, ArrayList<Double> lngs) {
        if (!lats.isEmpty()) {
            mapFragment.getView().setVisibility(View.VISIBLE);
            for (int i = 0; i < lats.size(); i++) {
                createMarker(googleMap, lats.get(i), lngs.get(i));
            }
        }
    }

    protected Marker createMarker(GoogleMap googleMap, double latitude, double longitude) {

        return googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude)));
    }

    public void toggleMenu(View view) {
        Intent I = new Intent(LocationChart.this, PiRatings.class);
        startActivity(I);
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
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
                requestQueue = Volley.newRequestQueue(LocationChart.this, stack);
            } else {
                requestQueue = Volley.newRequestQueue(LocationChart.this);
            }
        }
        return requestQueue;
    }

    public void addToReqQueue(Request request, String tag) {
        request.setTag(tag);
        getRequestQueue().add(request);
    }

    @Override
    protected void onStart() {
        super.onStart();
        super.onStart();
        Intent backgroundService = new Intent(LocationChart.this, BackgroundScreenService.class);
        startService(backgroundService);
        Intent notifs = new Intent(LocationChart.this, BackgroundNotificationService.class);
        startService(notifs);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent backgrounScreenService = new Intent(LocationChart.this, BackgroundScreenService.class);
        stopService(backgrounScreenService);
        Intent notifs = new Intent(LocationChart.this, BackgroundNotificationService.class);
        stopService(notifs);
    }
}