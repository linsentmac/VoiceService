package com.lenovo.smartcastvoice;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by linsen on 17-12-26.
 */

public class LocationService extends Service {

    private static final String TAG = "SC-Location";

    @Override
    public void onCreate() {
        super.onCreate();
        LocationManager locationManager;
        String serviceName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(serviceName);
        //String provider = LocationManager.GPS_PROVIDER;
        String provider = LocationManager.NETWORK_PROVIDER;

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        //String provider = locationManager.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(TAG, "checkSelfPermission false");
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        updateWithNewLocation(location);
        locationManager.requestLocationUpdates(provider, 2000, 10, locationListener);
    }


    private final LocationListener locationListener = new LocationListener() {

        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged");
            updateWithNewLocation(location);

        }

        public void onProviderDisabled(String provider){
            Log.d(TAG, "onProviderDisabled");
            updateWithNewLocation(null);

        }

        public void onProviderEnabled(String provider){
            Log.d(TAG, "onProviderEnabled");
        }

        public void onStatusChanged(String provider, int status, Bundle extras){
            Log.d(TAG, "onStatusChanged");
        }
    };



    private void updateWithNewLocation(Location location) {
        double lat = 0;
        double lng = 0;
        String latLongString;

        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            DecimalFormat df = new  DecimalFormat("#.###");
            /*lat = df.format(lat);
            lng = df.format(lng);
            latLongString = "纬度:" + df.format(lat) + "n经度:" + df.format(lng);*/
            latLongString = "纬度:" + lat + "n经度:" + lng;
            Log.d(TAG, "latLongString = " + latLongString);
        } else {
            latLongString = "无法获取地理信息";
        }

        List<Address> addList = null;
        Geocoder ge = new Geocoder(this);
        try {
            //addList = ge.getFromLocation(24.463, 118.1, 1);
            addList = ge.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if(addList!=null && addList.size()>0){
            for(int i=0; i<addList.size(); i++){
                Address ad = addList.get(i);
                latLongString +="n";
                latLongString += ad.getCountryName() +";"+ ad.getLocality();
            }
        }
        Log.d(TAG, "latLongString = " + latLongString);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
