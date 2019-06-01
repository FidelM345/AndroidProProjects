package com.example.thebeast.smartadmin;

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

public class GPS_Service extends Service {


    LocationListener listener;
    LocationManager locationManager;

    public GPS_Service() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {


        locationManager=(LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);



      listener=new LocationListener() {
          @Override
          public void onLocationChanged(Location location) {
              Intent intent=new Intent("location_update");
              intent.putExtra("coordinates",location.getLatitude()+" "+location.getLongitude());
              sendBroadcast(intent);
          }

          @Override
          public void onStatusChanged(String provider, int status, Bundle extras) {

          }

          @Override
          public void onProviderEnabled(String provider) {
            /*  Location location = locationManager
                      .getLastKnownLocation(provider);


              Intent intent=new Intent("location_update");
              intent.putExtra("coordinates",location.getLatitude()+" "+location.getLongitude());
              sendBroadcast(intent);
*/
          }

          @Override
          public void onProviderDisabled(String provider) {

              Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
              intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
              startActivity(intent);
          }
      };


      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000,0,listener);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (locationManager!=null){

            locationManager.removeUpdates(listener);
        }
    }
}
