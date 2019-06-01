package com.example.thebeast.smartadmin;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Service_Locate extends AppCompatActivity implements View.OnClickListener {
    TextView locate;
    Button start, stop;
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onResume() {
        super.onResume();

        if(broadcastReceiver==null){
            broadcastReceiver=new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    locate.append("\n"+intent.getExtras().get("coordinates"));
                }
            };

        }

        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver!=null){
           unregisterReceiver(broadcastReceiver);
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service__locate);

        locate = findViewById(R.id.id_locate);
        start = findViewById(R.id.id_start);
        stop = findViewById(R.id.id_stop);

        start.setOnClickListener(this);
        stop.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.id_start:
                startLocationUpdates();

                break;

            case R.id.id_stop:
                stopLocationUpdates();

                break;


        }
    }

    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED) {

            Intent intent=new Intent(getApplicationContext(),GPS_Service.class);
            startService(intent);

        }
        else{
            ActivityCompat.requestPermissions(Service_Locate.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }

    }


    private void stopLocationUpdates() {
        Intent intent=new Intent(getApplicationContext(),GPS_Service.class);
        stopService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){


            case 1:

                if (grantResults.length>0){

                    if (grantResults[0]==PackageManager.PERMISSION_GRANTED){

                       startLocationUpdates();


                    }else if(grantResults[0]==PackageManager.PERMISSION_DENIED){

                        Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_LONG).show();

                    }
                }
        }

    }



}
