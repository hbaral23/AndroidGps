package com.example.gpsService;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;


import java.util.ArrayList;
import java.util.List;

public class GpsService extends Service {

    private LocationListener listener;
    private LocationManager locationManager;
    private List<Location> locationList = new ArrayList<>();
    private float distancetracking = 0;
    private float distancebetweenlastcoords = 0;
    private int j = 0;

    public IBinder onBind(Intent intent){
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                locationList.add(location);
                Intent i = new Intent("location_update");
                while (j < locationList.size() - 1) {
                    Location loc1 = locationList.get(j);
                    Location loc2 = locationList.get(j + 1);
                    distancetracking += loc1.distanceTo(loc2);
                    distancebetweenlastcoords = loc1.distanceTo(loc2);
                    j++;
                }
                i.putExtra("coordinates", distancetracking);
                i.putExtra("location", location);
                i.putExtra("distance", distancebetweenlastcoords);
                sendBroadcast(i);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0,listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null){
            locationManager.removeUpdates(listener);
        }

        locationList.clear();
    }
}

