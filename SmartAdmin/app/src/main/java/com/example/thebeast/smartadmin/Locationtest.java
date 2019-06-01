package com.example.thebeast.smartadmin;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;

import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class Locationtest extends AppCompatActivity {
    private FusedLocationProviderClient mFusedLocationClient;

    TextView locate;


    LocationCallback mLocationCallback;
    LocationRequest mLocationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locationtest);


        locate = findViewById(R.id.id_locate);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Locationtest.this);


        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {

                    locate.setText(""+location.getLatitude()+" "+location.getLongitude());

                }
            };
        };


        createLocationRequest();

        //gets the last known location
        googleLocation();



    }


    @Override
    protected void onStart() {
        super.onStart();
        //calls for location updates
        startLocationUpdates();

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

    }

    private void googleLocation() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location objec
                                locate.setText(""+location.getLatitude()+" "+location.getLongitude());
                            }else{
                                LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                                boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

                                if(!isGPSEnabled){
                                    Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(intent);

                                }

                            }
                        }
                    });
        }
        else{
            ActivityCompat.requestPermissions(Locationtest.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
      /*  if (mRequestingLocationUpdates) {

        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void startLocationUpdates() {


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);

                }
        else{
            ActivityCompat.requestPermissions(Locationtest.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){


            case 1:

                if (grantResults.length>0){

                    if (grantResults[0]==PackageManager.PERMISSION_GRANTED){

                        googleLocation();


                    }else if(grantResults[0]==PackageManager.PERMISSION_DENIED){

                        Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_LONG).show();

                    }
                }
        }

    }

}
