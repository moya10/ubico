package com.example.lbma;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.multidex.MultiDex;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.lbma.Activities.DashboardMenu;
import com.example.lbma.Activities.UserProfile;
import com.example.lbma.BackgroundServices.BackgroundScreenService;
import com.example.lbma.DatabaseHandler.DatabaseHandler;
import com.example.lbma.Models.ActivityModel;
import com.example.lbma.Models.ApiModel;
import com.example.lbma.Models.BatteryModel;
import com.example.lbma.Models.ConnectionModel;
import com.example.lbma.Models.InfoModel;
import com.example.lbma.Models.PlaceModel;
import com.example.lbma.Sockets.TLSSocketFactory;
import com.example.lbma.Variables.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends AppCompatActivity {

    private static final String TAG = "MAIN ACTIVITY";
    private static double[] battery_level_x;
    Animation topAnim, bottomAnim;
    ImageView image;
    TextView logo, slogan;
    BroadcastReceiver mReceiver;
    double[] list = new double[3];
    DatabaseHandler databaseHandler;
    PlaceModel placeModel;
    BatteryModel batteryModel;
    InfoModel infoModel;
    ConnectionModel connectionModel;
    ActivityModel activityModel;
    NotificationCompat.Builder builder;
    JSONArray jsonBatteryVals;


    public String activity, activity_conf, network_type, location_type, location_id;
    public double location_conf;
    public int battery_level, display_state, notifs_active;
    public long system_time;
    public boolean device_interactive, battery_status;
    public RequestQueue requestQueue;
    public JSONObject parameters;
    public String url = "http://[server]";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String channel_ID = "BATTERY SOS";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(getApplicationContext(), channel_ID);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null &&
                    notificationManager.getNotificationChannel(channel_ID) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        channel_ID,
                        "SOS",
                        NotificationManager.IMPORTANCE_HIGH
                );
                notificationChannel.setDescription("Power Management");
                notificationManager.createNotificationChannel(notificationChannel);
                //Intent intent = new Intent("sos");
                //intent.putExtra("notify","notify");
                //sendBroadcast(intent);
            }
        }
        try {
            parameters = new JSONObject();
            parameters.put("username", "x@ceid.upatras.gr");
            parameters.put("password", "x");
            JsonObjectRequest req1 = new JsonObjectRequest(Request.Method.POST, url + "api/auth/login", parameters,
                    response1 -> {
                        try {
                            Constants.DEVICE_TOKEN = response1.getString("token");
                            Log.d(TAG, Constants.DEVICE_TOKEN);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        Log.e(TAG, String.valueOf(error));
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");
                    params.put("Access", "application/json");
                    return params;
                }
            };
            Main.this.addToReqQueue(req1, "getTOKEN");
        } catch (Exception e) {
            e.printStackTrace();
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        image = findViewById(R.id.imageView2);
        logo = findViewById(R.id.textView3);
        slogan = findViewById(R.id.textView);

        image.setAnimation(topAnim);
        logo.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);


        databaseHandler = new DatabaseHandler(Main.this);

        try {
            List<ApiModel> data2 = databaseHandler.getAllApi();
            if (!data2.isEmpty()) {
                Constants.DEVICE = data2.get(1).getDEVICE();
                Constants.DEVICE_ID = data2.get(1).getDEVICE_ID();
                Constants.DEVICE_CREDENTIALS = data2.get(1).getDEVICE_CREDENTIALS();
                Constants.DEVICE_TOKEN = data2.get(1).getDEVICE_TOKEN();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.BROADCAST_NOTIFY)) {
                    notifs_active = intent.getIntExtra("notifs_active", 1);
                    handleResult6(notifs_active);
                }

                if (intent.getAction().equals(Constants.BROADCAST_DETECTED_ACTIVITY)) {
                    activity = intent.getStringExtra("type");
                    activity_conf = intent.getStringExtra("confidence");
                    activityModel = new ActivityModel(-1, activity, activity_conf);
                    handleResult4(activityModel);
                }

                if (intent.getAction().equals(Constants.BROADCAST_LOCATION_DETECTED)) {
                    location_type = intent.getStringExtra("location_type");
                    location_id = intent.getStringExtra("location_id");
                    location_conf = intent.getDoubleExtra("location_conf", Constants.PLACE_LIKELIHOOD);
                    placeModel = new PlaceModel(-1, location_type, location_id, location_conf);
                    handleResult5(placeModel);
                }

                if (intent.getAction().equals(Constants.BROADCAST_BATTERY_ACTIVITY)) {
                    battery_level = intent.getIntExtra("battery_level", 1);
                    battery_status = intent.getBooleanExtra("battery_status", false);
                    batteryModel = new BatteryModel(-1, battery_level, battery_status);
                    handleResult1(batteryModel);
                }

                if (intent.getAction().equals(Constants.BROADCAST_CONNECTION_ACTIVITY)) {
                    network_type = intent.getStringExtra("network_type");
                    connectionModel = new ConnectionModel(-1, network_type);
                    handleResult3(connectionModel);

                }

                if (intent.getAction().equals(Constants.BROADCAST_POWER_ACTIVITY)) {
                    device_interactive = intent.getBooleanExtra("device_interactive", false);
                    display_state = intent.getIntExtra("display_state", 2);
                    system_time = intent.getLongExtra("system_time", 123456789);
                    infoModel = new InfoModel(-1, device_interactive, display_state, system_time);
                    handleResult2(infoModel);
                }
            }
        };

        Intent backgroundService = new Intent(Main.this, BackgroundScreenService.class);
        startService(backgroundService);

        int SPLASH_SCREEN = 5000;
        new Handler().postDelayed(() -> {
            if (databaseHandler.getAllUser().isEmpty()) {
                Intent intent = new Intent(Main.this, UserProfile.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(Main.this, DashboardMenu.class);
                startActivity(intent);
                finish();
            }

        }, SPLASH_SCREEN);

    }

    private void getData() {
        ArrayList<Double> output = new ArrayList<>();
        JsonObjectRequest getEntities = new JsonObjectRequest(Request.Method.GET, url + "api/dashboard/57ec5c90-c6cf-11eb-b9f4-cd7797153d94", null,
                response -> {
                    try {
                        JSONObject jsonObject = response.getJSONObject("configuration").getJSONObject("entityAliases");
                        JSONObject jsonObject1 = jsonObject.getJSONObject("888fe18a-ad76-9d56-f60b-ded8f8808069").getJSONObject("filter");
                        JSONArray jsonArray = jsonObject1.getJSONArray("entityList");
                        Log.e(TAG, String.valueOf(jsonArray.get(2)));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JsonObjectRequest getdata = new JsonObjectRequest(Request.Method.GET, url + "api/plugins/telemetry/DEVICE/" + jsonArray.get(i) + "/values/timeseries?limit=100&agg=NONE&useStrictDataTypes=false&keys=battery_level&startTs=1619820000000&endTs=1622843999000", null,
                                    response1 -> {

                                        try {
                                            jsonBatteryVals = response1.getJSONArray("battery_level");
                                            if (jsonBatteryVals != null) {
                                                ArrayList<Double> bat = new ArrayList<>();
                                                ArrayList<Double> ts = new ArrayList<>();

                                                for (int j = 0; j < jsonBatteryVals.length(); j++) {
                                                    bat.add(Double.parseDouble(jsonBatteryVals.getJSONObject(j).get("value").toString()));
                                                    ts.add(Double.parseDouble(jsonBatteryVals.getJSONObject(j).get("ts").toString()));
                                                }
                                                if(!(bat.isEmpty() && ts.isEmpty())) getTimestamp(bat, ts, battery_level, System.currentTimeMillis());
                                            }

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
                                    params.put("x-authorization", "Bearer " + Constants.DEVICE_TOKEN);
                                    return params;
                                }

                            };
                            Main.this.addToReqQueue(getdata, "getBattery");
                        }


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
                params.put("x-authorization", "Bearer " + Constants.DEVICE_TOKEN);
                return params;
            }
        };
        Main.this.addToReqQueue(getEntities, "getIDs");
    }

    private double[] getTimestamp(ArrayList<Double> data, ArrayList<Double> time, int battery_level, double ts) {
        ArrayList<Double> output = new ArrayList<>();
        ArrayList<Double> time_trans = new ArrayList<>();
        ArrayList<Double> time_period = new ArrayList<>();

        List<Double> leftArray;
        List<Double> rightArray;

        if (data.size() % 2 == 0) {
            leftArray = data.subList(0, data.size() / 2);
            rightArray = data.subList(data.size() / 2, data.size());
        } else {
            leftArray = data.subList(0, data.size() / 2);
            rightArray = data.subList(data.size() / 2 + 1, data.size());
        }

        double Q1 = getMedian(leftArray);
        double Q3 = getMedian(rightArray);
        double IQR = Q3 - Q1;
        double lF = Q1 - 1.5 * IQR;
        double Uf = Q3 + 1.5 * IQR;

        for (int i = 0; i < data.size(); i++) {
            if ((data.get(i) > lF || data.get(i) < Uf) && data.get(i) != null) {
                output.add(data.get(i)); // outliers removed
                time_trans.add(time.get(i));
            }
        }


        for (int i = 0; i < output.size() - 2; i++) {
            if (!((output.get(i) == output.get(i + 1)) && (output.get(i) == output.get(i + 2)) && (output.get(i + 1) == output.get(i + 2)))) {
                time_period.add(time_trans.get(i + 2) - time_trans.get(i));
            }
        }

        double dt = Math.abs(getMean(time_period));
        double divider = max(output) - min(output);

        battery_level_x = new double[80];
        for (int i = 1; i < 80; i++) {
            battery_level_x[i] = (battery_level * Math.exp(-i / (divider / 3)));
        }

        ArrayList<Double> new_battery_level = new ArrayList<>();

        for (int i = 0; i < battery_level_x.length; i++) {
            new_battery_level.add(battery_level_x[i] - battery_level);
        }

        double min_val = max(new_battery_level);
        int count = 0;
        int count2 = 0;

        for (int i = 0; i < new_battery_level.size(); i++) {
            if (new_battery_level.get(i) >= min_val && new_battery_level.get(i) <= (25 - battery_level))
                count += 1;
            else if (new_battery_level.get(i) >= (24 - battery_level) && new_battery_level.get(i) >= (15 - battery_level))
                count2 += 1;
        }

        if (count2 == 0) count2 = 3;

        double battery_life_time = 0;
        double battery_dead_time = 0;

        if ((25 - battery_level) < 0) {
            for (int i = -9; i <= 0; i++) {
                if (!((25 - battery_level) == i)) {
                    battery_life_time = ts + count * dt;
                    battery_dead_time = ts + (count + count2) * dt;
                }else{
                    battery_life_time = ts + count * dt;
                    battery_dead_time = ts + (count2) * dt;
                }
            }
        } else if ((25 - battery_level) > 0) {
            for (int i = -9; i <= 15; i++) {
                if (!((25 - battery_level) == i)) {
                    battery_life_time = ts + count * dt;
                    battery_dead_time = battery_life_time;
                }else{
                    battery_life_time = ts + count * dt;
                    battery_dead_time = ts + (count2) * dt;
                }
            }
        }


        list[0] = ts;
        list[1] = battery_life_time;
        list[2] = battery_dead_time;

        return list;
    }

    private static double max(ArrayList<Double> data) {
        double maximum = 0;
        for (int i = 1; i < data.size(); i++) {
            if (data.get(i) > maximum) {
                maximum = data.get(i);
            }
        }
        return maximum;
    }

    private static double min(ArrayList<Double> data) {
        double minimum = max(data);
        for (int i = 1; i < data.size(); i++) {
            if (data.get(i) < minimum) {
                minimum = data.get(i);
            }
        }
        return minimum;
    }

    private static double getMean(ArrayList<Double> data) {
        double mean = 0;
        for (int i = 0; i < data.size(); i++) {
            mean += data.get(i);
        }
        return mean / data.size();
    }

    private static double getMedian(List<Double> data) {
        double median;
        if (data.size() % 2 == 0) { // 1 5 6 7 8 9 Median = (6+7)/2
            median = (data.get(data.size() / 2) + data.get(data.size() / 2 + 1)) / 2;
        } else { // 1 5 6 7 8 Median = 6
            median = data.get(data.size() / 2);
        }
        return median;
    }

    private void handleResult1(BatteryModel batteryModel) {

        try {
            parameters = new JSONObject();
            parameters.put("battery_level", battery_level);
            parameters.put("battery_status", battery_status);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "api/plugins/telemetry/DEVICE/" + Constants.DEVICE_ID + "/timeseries/CLIENT_SCOPE", parameters,
                    response -> {
                    }, error -> {
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
            Main.this.addToReqQueue(jsonObjectRequest, "postBattery");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getData();
        battery_lifetime_expectancy(list);

        Log.d(TAG, batteryModel.toString());

    }

    private void battery_lifetime_expectancy(double[] data) {
        long threshold_time = (long) data[1];
        long dead_time = (long) data[2];
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        String init_time = formatter.format(new Date((long) data[0]));
        String threshold = formatter.format(new Date(threshold_time));
        String end_time = formatter.format(new Date(dead_time));
        Intent intent = new Intent(this, Main.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.icons8_battery_level_64);

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Battery life expectancy")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Current date time\r\n" + init_time + "\r\n" +
                        "It is recommended to charge your phone on\n" + threshold + "\r\n" +
                        "Immediately charge your phone on\n" + end_time))
                .setLargeIcon(bitmap)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentTitle(getString(R.string.app_name));
        NotificationManagerCompat.from(this).notify(0, builder.build());

        intent.putExtra("notify", "notify");
        sendBroadcast(intent);
    }

    private void handleResult2(InfoModel infoModel) {

        try {
            parameters = new JSONObject();
            parameters.put("device_interactive", device_interactive);
            parameters.put("display_state", display_state);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "api/plugins/telemetry/DEVICE/" + Constants.DEVICE_ID + "/timeseries/CLIENT_SCOPE", parameters,
                    response -> {
                    }, error -> {
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
            Main.this.addToReqQueue(jsonObjectRequest, "postInfo");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, infoModel.toString());

    }

    private void handleResult3(ConnectionModel connectionModel) {

        try {
            parameters = new JSONObject();
            parameters.put("network_type", network_type);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "api/plugins/telemetry/DEVICE/" + Constants.DEVICE_ID + "/timeseries/CLIENT_SCOPE", parameters,
                    response -> {
                    }, error -> {
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
            Main.this.addToReqQueue(jsonObjectRequest, "postConn");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, connectionModel.toString());
    }

    private void handleResult4(ActivityModel activityModel) {

        try {
            parameters = new JSONObject();
            parameters.put("activity", activity);
            parameters.put("activity_conf", activity_conf);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "api/plugins/telemetry/DEVICE/" + Constants.DEVICE_ID + "/timeseries/CLIENT_SCOPE", parameters,
                    response -> {
                    }, error -> {
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
            Main.this.addToReqQueue(jsonObjectRequest, "postActivity");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, activityModel.toString());
    }

    private void handleResult5(PlaceModel placeModel) {

        try {
            parameters = new JSONObject();
            parameters.put("location_type", location_type);
            parameters.put("location_id", location_id);
            parameters.put("location_conf", location_conf);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "api/plugins/telemetry/DEVICE/" + Constants.DEVICE_ID + "/timeseries/CLIENT_SCOPE", parameters,
                    response -> {
                    }, error -> {
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
            Main.this.addToReqQueue(jsonObjectRequest, "postPlace");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, placeModel.toString());
    }

    private void handleResult6(int notify) {

        try {
            parameters = new JSONObject();
            parameters.put("notifs", notifs_active);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "api/plugins/telemetry/DEVICE/" + Constants.DEVICE_ID + "/timeseries/CLIENT_SCOPE", parameters,
                    response -> {
                    }, error -> {
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
            Main.this.addToReqQueue(jsonObjectRequest, "postNotifs");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, String.valueOf(notify));
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.BROADCAST_CONNECTION_ACTIVITY);
        intentFilter.addAction(Constants.BROADCAST_BATTERY_ACTIVITY);
        intentFilter.addAction(Constants.BROADCAST_POWER_ACTIVITY);
        intentFilter.addAction(Constants.BROADCAST_DETECTED_ACTIVITY);
        intentFilter.addAction(Constants.BROADCAST_LOCATION_DETECTED);
        intentFilter.addAction(Constants.BROADCAST_NOTIFY);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
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
                requestQueue = Volley.newRequestQueue(Main.this, stack);
            } else {
                requestQueue = Volley.newRequestQueue(Main.this);
            }
        }
        return requestQueue;
    }

    /*private void updateAndroidSecurityProvider() {
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        }
    }*/

    public void addToReqQueue(Request request, String tag) {
        request.setTag(tag);
        getRequestQueue().add(request);
    }

    /*public void cancellAllRequests(String tag) {
        getRequestQueue().cancelAll(tag);
    }*/

}