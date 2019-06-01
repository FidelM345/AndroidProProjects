package com.example.thebeast.smartadmin;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
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

public class ViewAmbulance extends AppCompatActivity implements View.OnClickListener {
    RecyclerView blog_list_view;
    List<EmergencyLines_model> bloglist; //list of type blog post model class.
    FirebaseFirestore firestore;
    Emergency_Adapter emergency_adapter;
    FirebaseAuth mAuth;
    FloatingActionButton floatingActionButton;
    ImageView back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_ambulance);

        mAuth= FirebaseAuth.getInstance();
        firestore= FirebaseFirestore.getInstance();
        floatingActionButton=findViewById(R.id.add_registration);
        floatingActionButton.setOnClickListener(this);

        bloglist=new ArrayList<>();

        blog_list_view=findViewById(R.id.alert_recycler);
        back_btn=findViewById(R.id.id_backmain);
        back_btn.setOnClickListener(this);

        blog_list_view.setLayoutManager(new LinearLayoutManager(ViewAmbulance.this));

        emergency_adapter=new Emergency_Adapter(bloglist);//initializing adapter
        blog_list_view.setAdapter(emergency_adapter);


    }


    @Override
    protected void onStart() {
        super.onStart();
        bloglist.clear();
        dataLoader();

    }
    private void dataLoader() {

        Query firstQuery=firestore.collection("EmergencyLines_Posts");

        firstQuery.addSnapshotListener(ViewAmbulance.this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {


                if (e != null) {
                    // Log.w("Beast", "Listen failed.", e);

                    return;
                }

                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {

                    if (documentChange.getType() == DocumentChange.Type.ADDED ) {

                        String PostId=documentChange.getDocument().getId();

                        EmergencyLines_model forum_adapter_model= documentChange.getDocument().toObject(EmergencyLines_model.class).withId(PostId);


                        bloglist.add(forum_adapter_model);
                        emergency_adapter.notifyDataSetChanged();//notify adapter when data set is changed

                    }


                }


            }
        });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.add_registration:
                Intent intent=new Intent(getApplicationContext(),Emergency_Post.class);
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
}
