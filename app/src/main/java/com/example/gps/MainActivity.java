package com.example.gps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gpsService.GpsService;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver broadcastReceiver;
    private Button btn;
    private int step;
    private int resetstep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.step = 0;
        this.resetstep = 0;

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

    }

    private SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            Sensor sensor = event.sensor;
            float[] values = event.values;

            int value = -1;

            if (values.length > 0) {
                value = (int) values[0];
            }

            if (resetstep == 0) {
                resetstep = value;
            }

            if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                step = value-resetstep;
                TextView tvstep = ((TextView) findViewById(R.id.step));
                tvstep.setText("Pas2 : "+step+" \n soit :" + step * 0.6 +" m");
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MainActivity.this, GpsService.class);
                    startService(intent);
                    Toast.makeText(this, "Service GPS lancé", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener( listener, stepDetectorSensor,SensorManager.SENSOR_DELAY_FASTEST);

        if (broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Location locatrack = (Location) intent.getExtras().get("location");
                    double latitude = locatrack.getLatitude();
                    double longitude = locatrack.getLongitude();
                    TextView coords = ((TextView) findViewById(R.id.Location));
                    TextView distance = ((TextView) findViewById(R.id.distance));
                    Object dist = intent.getExtras().get("distance");
                    distance.setText("Distance : "+dist.toString()+" m");
                    coords.setText("Latitude :"+latitude+" Longitude :"+longitude);
                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }
}
