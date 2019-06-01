package com.example.thebeast.smartadmin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth mAuth;
    FirebaseFirestore mfirestore;
    ArrayList<PieEntry> yValues=new ArrayList<>();
    TextView textView;
    int no_of_users;
    String user_id;

    TextView user_names,user_location,gallery,alert;
    CircleImageView user_profile_pic;

    Query firstQuery,firstQuery2;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth=FirebaseAuth.getInstance();


        textView=findViewById(R.id.text_no_donors);
        // textView1=findViewById(R.id.chart_donor);
        mfirestore=FirebaseFirestore.getInstance();

        firstQuery = mfirestore.collection("user_table").whereEqualTo("donor",true);
        firstQuery2 = mfirestore.collection("user_table").whereEqualTo("donor",false);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

    }

    public  void pieCharttwo(){
       /* Description description=new Description();
        description.setText("Percentage (%) Donor to Non Donor");
        description.setTextSize(2);
        description.setTextAlign(Paint.Align.LEFT);
*/



        PieChart pieChart=findViewById(R.id.chatmax);



        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        //pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.BLUE);
        pieChart.setTransparentCircleRadius(61f);

        PieDataSet dataSet=new PieDataSet(yValues,"Percentage(%) Donor to Non-Donor ");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData pieData=new PieData(dataSet);
        pieData.setValueTextSize(16f);
        pieData.setValueTextColor(Color.WHITE);
        pieChart.setUsePercentValues(true);
        pieChart.setData(pieData);
        pieChart.animateXY(1000,1000);
        pieChart.invalidate();

    }


    public void user_profile_details(){
        if(mAuth.getCurrentUser()!=null){
            user_id=mAuth.getCurrentUser().getUid();
            mfirestore.collection("Admin_table").document(user_id).get().addOnCompleteListener(this,new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()){

                        if (task.getResult().exists()){

                            String fname=task.getResult().getString("fname");
                            String lname=task.getResult().getString("lname");
                            String check_imageuri=task.getResult().getString("imageuri");
                            String check_thumburi=task.getResult().getString("thumburi");


                            //ou need to inflate the header view as it is not inflated automatically .
                            View header = navigationView.getHeaderView(0);
                            user_names = (TextView) header.findViewById(R.id.profile_name);

                            user_profile_pic=header.findViewById(R.id.profile_image);

                            user_names.setText(fname+" "+lname);

                            RequestOptions placeHolder=new RequestOptions();
                            placeHolder.placeholder(R.drawable.profile_placeholder);


                            Glide.with(MainActivity.this).applyDefaultRequestOptions(placeHolder).load(check_imageuri).thumbnail(
                                    //loads the thumbnail if the image has not loaded first
                                    Glide.with(MainActivity.this).load(check_thumburi)
                            ).into(user_profile_pic);




                        }
                        else{
                            Toast.makeText(MainActivity.this, "No data has been saved", Toast.LENGTH_LONG).show();
                            Intent in=new Intent(getApplicationContext(),Profile.class);
                            startActivity(in);
                        }
                    }

                    else{
                        String exception=task.getException().getMessage();

                        Toast.makeText(MainActivity.this, "Data Retreival Error is: "+exception, Toast.LENGTH_LONG).show();

                    }


                }
            });


        }




    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser()==null){
            Intent intent=new Intent(MainActivity.this,Login.class);
            startActivity(intent);
        }

        else{

            retrieve_and_loadChart_details();

            user_profile_details();


        }












    }

    private void retrieve_and_loadChart_details() {

        mfirestore.collection("user_table").addSnapshotListener(MainActivity.this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    // Log.w("Beast", "Listen failed.", e);

                    return;
                }
                if (!queryDocumentSnapshots.isEmpty()){

                    no_of_users=queryDocumentSnapshots.size();

                    textView.setText(Integer.toString(no_of_users));


                    firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {


                            if(queryDocumentSnapshots!=null){

                                final int donor=queryDocumentSnapshots.size();




                                firstQuery2.addSnapshotListener(MainActivity.this, new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                        if(queryDocumentSnapshots!=null){

                                            int non_donor=queryDocumentSnapshots.size();


                                            //textView1.setText(Integer.toString(donor));

                                            yValues.clear();
                                            yValues.add(new PieEntry(non_donor,"Non Donors"));
                                            yValues.add(new PieEntry(donor,"Donors"));

                                            pieCharttwo();


                                        }
                                    }
                                });

                            }
                        }
                    });





                }


            }
        });


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent intent=new Intent(MainActivity.this,Service_Locate.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_ambulance) {
            Intent intent=new Intent(MainActivity.this,ViewAmbulance.class);
            startActivity(intent);



        } else if (id == R.id.nav_website) {

            /*Intent intent=new Intent(MainActivity.this,Service_Locate.class);
            startActivity(intent);*/

            Intent intent=new Intent(MainActivity.this,Web_View.class);
            startActivity(intent);


        }  else if (id == R.id.nav_alerts) {

            Intent intent=new Intent(MainActivity.this,ViewAlerts.class);
            startActivity(intent);


        } else if (id == R.id.nav_send) {
            mAuth.signOut();
            Intent intent=new Intent(MainActivity.this,Login.class);
            startActivity(intent);
            finish();

        }
        else if (id == R.id.nav_bloodgroup) {
            Intent intent=new Intent(MainActivity.this,Blood_Group.class);
            startActivity(intent);

        }/*else if (id == R.id.nav_camera) {
            Intent intent=new Intent(MainActivity.this,Locationtest.class);
            startActivity(intent);

        }*/
        else if (id == R.id.nav_forum) {
            Intent intent=new Intent(MainActivity.this,Forum_page.class);
            startActivity(intent);

        }else if (id == R.id.nav_myprofile) {
            Intent intent=new Intent(MainActivity.this,Edit_Account.class);
            startActivity(intent);

        }else if (id == R.id.nav_home) {
            Intent intent=new Intent(MainActivity.this,Bar_graph.class);
            startActivity(intent);

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
