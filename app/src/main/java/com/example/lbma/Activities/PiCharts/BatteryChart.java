package com.example.lbma.Activities.PiCharts;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.lbma.Activities.DashboardMenu;
import com.example.lbma.Activities.PiRatings;
import com.example.lbma.BackgroundServices.BackgroundNotificationService;
import com.example.lbma.BackgroundServices.BackgroundScreenService;
import com.example.lbma.R;
import com.example.lbma.Sockets.TLSSocketFactory;
import com.example.lbma.Variables.Constants;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
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
import java.util.Locale;
import java.util.Map;


public class BatteryChart extends AppCompatActivity {
    private static final String TAG = "BATTERY_RES";
    DatePickerDialog datePickerDialog;
    TextInputLayout start_date, end_date;
    Button btn;
    LineChart mChart;
    int start_day, start_month, start_year, end_day, end_month, end_year;
    long start_ts, end_ts;
    public RequestQueue requestQueue;
    public JSONObject parameters;
    public String url = "http://[server]";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_chart);
        start_date= findViewById(R.id.start_date);
        end_date = findViewById(R.id.end_date);

        start_date.getEditText().setInputType(InputType.TYPE_NULL);
        start_date.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                start_day = calendar.get(Calendar.DAY_OF_MONTH);
                start_month = calendar.get(Calendar.MONTH);
                start_year = calendar.get(Calendar.YEAR);
                datePickerDialog = new DatePickerDialog(BatteryChart.this, new DatePickerDialog.OnDateSetListener() {
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
                datePickerDialog = new DatePickerDialog(BatteryChart.this, new DatePickerDialog.OnDateSetListener() {
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
        mChart = findViewById(R.id.batterychart);
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
                                    Log.d(TAG, Constants.DEVICE_TOKEN);
                                    if (start_ts > end_ts) {
                                        start_ts = end_ts;
                                    }
                                    if (end_ts > System.currentTimeMillis()) {
                                        end_ts = System.currentTimeMillis();
                                    }
                                    JsonObjectRequest jsonGetDeviceCredentials = new JsonObjectRequest(Request.Method.GET, url + "api/plugins/telemetry/DEVICE/" + Constants.DEVICE_ID + "/values/timeseries?limit=100&agg=NONE&useStrictDataTypes=false&keys=battery_level&startTs=" + start_ts + "&endTs=" + end_ts, null,
                                            response -> {
                                                try {
                                                    JSONArray values2 = response.getJSONArray("battery_level");

                                                    ArrayList<Entry> values = new ArrayList<>();
                                                    for (int i = 0; i < values2.length(); i++) {
                                                        double conf = Float.parseFloat(values2.getJSONObject(i).get("value").toString());
                                                        values.add(new Entry(Long.parseLong(values2.getJSONObject(i).get("ts").toString()), (int) conf));
                                                    }
                                                    Log.e(TAG,String.valueOf(values));
                                                    if (!values.isEmpty()) {
                                                        setAxis(values, "Battery Level");
                                                    }else{
                                                        Toast.makeText(BatteryChart.this, "Data missing", Toast.LENGTH_LONG).show();
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            },
                                            error -> { }
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
                                    BatteryChart.this.addToReqQueue(jsonGetDeviceCredentials, "getBatAttributes");
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
                            return params;
                        }
                    };
                    BatteryChart.this.addToReqQueue(req1, "getTOKEN");
                } catch (
                        Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }


    public void toggleMenu(View view) {
        Intent I = new Intent(BatteryChart.this, PiRatings.class);
        startActivity(I);
    }

    public void setAxis(ArrayList<Entry> val, String act) {
        mChart.setVisibility(View.VISIBLE);
        configureLineChart();
        LineDataSet set1;
        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(val);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(val, act);
            set1.setDrawIcons(false);
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.DKGRAY);
            set1.setCircleColor(Color.DKGRAY);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);
            if (Utils.getSDKInt() >= 18) {
                set1.setFillColor(Color.CYAN);
            } else {
                set1.setFillColor(Color.DKGRAY);
            }
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);
            mChart.setData(data);
        }
    }

    public void configureLineChart() {
        Description desc = new Description();
        desc.setText("level %");
        desc.setTextSize(14);
        mChart.setDescription(desc);
        XAxis xAxis = mChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("d MMM", Locale.GERMANY);

            @Override
            public String getFormattedValue(float value) {
                long millis = (long) value;
                return mFormat.format(new Date(millis));
            }
        });
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
                requestQueue = Volley.newRequestQueue(BatteryChart.this, stack);
            } else {
                requestQueue = Volley.newRequestQueue(BatteryChart.this);
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
        Intent backgroundService = new Intent(BatteryChart.this, BackgroundScreenService.class);
        startService(backgroundService);
        Intent notifs = new Intent(BatteryChart.this, BackgroundNotificationService.class);
        startService(notifs);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent backgrounScreenService = new Intent(BatteryChart.this, BackgroundScreenService.class);
        stopService(backgrounScreenService);
        Intent notifs = new Intent(BatteryChart.this, BackgroundNotificationService.class);
        stopService(notifs);
    }
}