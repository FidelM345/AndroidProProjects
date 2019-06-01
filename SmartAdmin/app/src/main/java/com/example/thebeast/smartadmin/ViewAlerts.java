package com.example.thebeast.smartadmin;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewAlerts extends AppCompatActivity implements View.OnClickListener {
    RecyclerView blog_list_view;
    List<Alert_Adapter_model> bloglist; //list of type blog post model class.
    FirebaseFirestore firestore;
    AlertFragment_Adapter AlertFragment_adapter;
    FirebaseAuth mAuth;
    FloatingActionButton floatingActionButton;
    ImageView back_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_alerts);
        mAuth= FirebaseAuth.getInstance();
        firestore= FirebaseFirestore.getInstance();
        floatingActionButton=findViewById(R.id.add_registration);
        floatingActionButton.setOnClickListener(this);

        bloglist=new ArrayList<>();

        blog_list_view=findViewById(R.id.alert_recycler);
        back_btn=findViewById(R.id.id_backmain);
        back_btn.setOnClickListener(this);
        blog_list_view.setLayoutManager(new LinearLayoutManager(ViewAlerts.this));

        AlertFragment_adapter=new AlertFragment_Adapter(bloglist);//initializing adapter
        blog_list_view.setAdapter(AlertFragment_adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        bloglist.clear();
        dataLoader();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.add_registration:
                Intent intent=new Intent(getApplicationContext(),AdminAlertsPosts.class);
                startActivity(intent);
                break;

            case R.id.id_backmain:
                Intent intent1=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent1);
                break;
                default:
                    break;

        }




    }


    private void dataLoader() {

        Query firstQuery=firestore.collection("Alert_Posts");

        firstQuery.addSnapshotListener(ViewAlerts.this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {


                if (e != null) {
                    // Log.w("Beast", "Listen failed.", e);

                    return;
                }

                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {

                    if (documentChange.getType() == DocumentChange.Type.ADDED) {

                        showNotification();

                        String PostId=documentChange.getDocument().getId();

                        Alert_Adapter_model forum_adapter_model= documentChange.getDocument().toObject(Alert_Adapter_model.class).withId(PostId);

                        bloglist.add(forum_adapter_model);
                        AlertFragment_adapter.notifyDataSetChanged();//notify adapter when data set is changed


                    }


                }


            }
        });

    }


    public void showNotification() {


        Intent intent=new Intent(getApplicationContext(),Bar_graph.class);
        NotificationCompat.Builder builder=new NotificationCompat.Builder( this);
        builder.setSmallIcon(R.mipmap.delete_icon);
        builder.setContentTitle("My notification");
        builder.setContentText("This is my notification");
        builder.setColor(Color.RED);
        builder.setAutoCancel(true);


        TaskStackBuilder stackBuilder=TaskStackBuilder.create(this);
        stackBuilder.addParentStack(Bar_graph.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent=stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0,builder.build());


    }

}
