package com.appttude.h_mal.exchangemap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by h_mal on 05/05/2018.
 */

public class getLatLong {
    static Location location;
    public static Double longitude;
    public static Double latitude;

    private static String TAG = getLatLong.class.getSimpleName();

    public getLatLong(){
        super();
    }

    public static void configLatLong(Context context) {

    LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                !=PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show();
        } else {
            try {
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }catch (Exception e){
                Log.e("latlong error", "configLatLong: ", e);
            }finally {
                if (location == null){
                    Log.i(TAG, "configLatLong: location initially was null");
                    try{
                        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }catch (Exception e){
                        Log.e(TAG, "configLatLong: ", e);
                    }finally {
                        if (location != null){
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            Log.i(TAG, "onSuccess: Latitude:" + location.getLatitude()
                                    + "\n longitude: " + location.getLongitude());
                        }
                    }

                }else{
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
        }
    }

}
