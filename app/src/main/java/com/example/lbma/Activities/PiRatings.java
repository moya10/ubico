package com.example.lbma.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.state.State;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.lbma.Activities.PiCharts.ActivityChart;
import com.example.lbma.Activities.PiCharts.BatteryChart;
import com.example.lbma.Activities.PiCharts.LocationChart;
import com.example.lbma.BackgroundServices.BackgroundNotificationService;
import com.example.lbma.BackgroundServices.BackgroundScreenService;
import com.example.lbma.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;

public class PiRatings extends AppCompatActivity {


    CircleMenu circleMenu;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pi_ratings);

        circleMenu = findViewById(R.id.circle_menu);

        circleMenu.setMainMenu(Color.parseColor("#291b2b"), R.mipmap.icons8_squared_menu_64, R.drawable.icons8_cancel_64)
                .addSubMenu(Color.parseColor("#b1ecea"), R.drawable.icons8_compass_64)
                .addSubMenu(Color.parseColor("#caf0b3"), R.mipmap.icons8_unicycle_64)
                .addSubMenu(Color.parseColor("#f4f4ae"), R.drawable.icons8_battery_level_64)
                .setOnMenuSelectedListener(new OnMenuSelectedListener() {
                    @Override
                    public void onMenuSelected(int index) {
                        switch (index) {
                            case 0:
                                Toast.makeText(PiRatings.this, "Location Chart", Toast.LENGTH_LONG).show();
                                Intent lc = new Intent(PiRatings.this, LocationChart.class);
                                startActivity(lc);
                                break;
                            case 1:
                                Toast.makeText(PiRatings.this, "Activity Chart", Toast.LENGTH_LONG).show();
                                Intent ac = new Intent(PiRatings.this, ActivityChart.class);
                                startActivity(ac);
                                break;
                            case 2:
                                Toast.makeText(PiRatings.this, "Battery level's Chart", Toast.LENGTH_LONG).show();
                                Intent bl = new Intent(PiRatings.this, BatteryChart.class);
                                startActivity(bl);
                                break;
                        }
                    }
                });

    }

    public void toggleMenu(View view) {
        Intent I = new Intent(PiRatings.this, DashboardMenu.class);
        startActivity(I);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent backgroundService = new Intent(PiRatings.this, BackgroundScreenService.class);
        startService(backgroundService);
        Intent notifs = new Intent(PiRatings.this, BackgroundNotificationService.class);
        startService(notifs);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent backgrounScreenService = new Intent(PiRatings.this, BackgroundScreenService.class);
        stopService(backgrounScreenService);
        Intent notifs = new Intent(PiRatings.this, BackgroundNotificationService.class);
        stopService(notifs);
    }
}