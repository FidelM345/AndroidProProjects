package com.example.thebeast.service;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button start,stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start=findViewById(R.id.id_start);
        stop=findViewById(R.id.id_stop);

        start.setOnClickListener(this);
        stop.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.id_start:

                Intent intent=new Intent(this,MyService.class);
                startService(intent);


                break;

            case R.id.id_stop:
                Intent intent2=new Intent(this,MyService.class);
                stopService(intent2);

                break;

                default:
        }
    }
}
