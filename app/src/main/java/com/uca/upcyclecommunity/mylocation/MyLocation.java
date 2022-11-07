package com.uca.upcyclecommunity.mylocation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;

import java.util.List;

public class MyLocation {
    Context context;
    double latitude;
    double longitude;
    FusedLocationProviderClient mFusedLocationClient;
    LocationRequest locationRequest;
    Location location;
    Location mCurrentLocation;
    private static final int UPDATE_INTERVAL_MS = 1000;
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500;

    public MyLocation(Context context){
        this.context = context;
        location = null;
        mCurrentLocation = null;


        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        if(checkPermission()&&checkLocationServicesStatus()){
            startLocationUpdates();
        }
    }

    public double getLatitude(){
        if(location!=null)
            latitude = location.getLatitude();
        return latitude;
    }

    public double getLongitude(){
        if(location!=null)
            longitude = location.getLongitude();
        return longitude;
    }

    LocationCallback locationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            List<Location> locationList = locationResult.getLocations();
            if(locationList.size()>0){
                location = locationList.get(locationList.size() - 1);
                String markerSnippet = "위도:" + String.valueOf(location.getLatitude()) + "경도:" + String.valueOf(location.getLongitude());
                //Log.d("WeGlonD", "onLocationResult : " + markerSnippet);
                mCurrentLocation = location;
            }
        }
    };

    private void startLocationUpdates(){
        Log.d("WeGlonD", "startLocationUpdates called");
        if(!checkLocationServicesStatus()){
            Log.d("WeGlonD", "startLocationUpdates : call showDialogForLocationServiceSetting");
            //showDialogForLocationServiceSetting();
        }else{
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);

            if(hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED){
                Log.d("WeGlonD", "권한없음");
                return;
            }

            Log.d("WeGlonD", "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());

//            if(checkPermission()){
//                googleMap.setMyLocationEnabled(true);
//            }
        }
    }

    private boolean checkPermission(){
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION);

        if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    public boolean checkLocationServicesStatus(){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void stopUpdatingLocation(){
        mFusedLocationClient.removeLocationUpdates(locationCallback);
    }
}
