package com.example.thebeast.smartadmin;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Notification extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
    }

    public void showNotification(View view) {
        NotificationCompat.Builder builder=new NotificationCompat.Builder( this);
         builder.setSmallIcon(R.mipmap.send_arrow);
         builder.setContentTitle("My notification");
         builder.setContentText("This is my notification");

        Intent intent=new Intent(getApplicationContext(),Bar_graph.class);

        TaskStackBuilder stackBuilder=TaskStackBuilder.create(this);
        stackBuilder.addParentStack(Bar_graph.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent=stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0,builder.build());


    }
}
