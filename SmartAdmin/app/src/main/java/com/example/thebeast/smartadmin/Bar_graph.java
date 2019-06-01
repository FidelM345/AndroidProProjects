package com.example.thebeast.smartadmin;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class Bar_graph extends AppCompatActivity {
    BarChart barChart;
    ImageView back_btn;
    Query user_Rft,user_Cent,user_Nai,user_Nest,user_Est,user_Nya,user_Cst,user_Wst;
    int rift,nai,est,cst,cent,nya,wst,n_est;
    FirebaseAuth mAuth;
    FirebaseFirestore mfirestore;
    ArrayList<PieEntry> pieEntries=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_graph);
        mAuth=FirebaseAuth.getInstance();
        mfirestore=FirebaseFirestore.getInstance();

       user_Nai= mfirestore.collection("user_table").whereEqualTo("location","Nairobi");
       user_Cent = mfirestore.collection("user_table").whereEqualTo("location","Central");
        user_Rft = mfirestore.collection("user_table").whereEqualTo("location","Rift valley");
        user_Nya = mfirestore.collection("user_table").whereEqualTo("location","Nyanza");
        user_Wst = mfirestore.collection("user_table").whereEqualTo("location","Western");
        user_Est = mfirestore.collection("user_table").whereEqualTo("location","Eastern");
        user_Cst = mfirestore.collection("user_table").whereEqualTo("location","Coast");
        user_Nest = mfirestore.collection("user_table").whereEqualTo("location","North Eastern");

        back_btn=findViewById(R.id.id_backmain);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(in);
                finish();

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        user_Location();
    }

    private void bloodBargraph() {

        barChart=findViewById(R.id.barchart);

        ArrayList<BarEntry>barEntries_Y=new ArrayList<>();
        barEntries_Y.add(new BarEntry(0,rift));
        barEntries_Y.add(new BarEntry(1,nya));
        barEntries_Y.add(new BarEntry(2,nai));
        barEntries_Y.add(new BarEntry(3,est));
        barEntries_Y.add(new BarEntry(4,wst));
        barEntries_Y.add(new BarEntry(5,cst));
        barEntries_Y.add(new BarEntry(6,cent));
        barEntries_Y.add(new BarEntry(7,n_est));

        BarDataSet barDataSet=new BarDataSet(barEntries_Y,"User distribution per Region");

        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData barData=new BarData(barDataSet);


        barChart.setData(barData);
        barChart.animateXY(1000,1000);
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);



        final String[] quarters = new String[] { "Rft", "Nya", "Nai", "Est" ,"Wst","Cst","Cent","N-Est"};

        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return quarters[(int) value];
            }

            // we don't draw numbers, so no decimal digits needed

            public int getDecimalDigits() {  return 0; }
        };

        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);




    }


    public  void pieCharttwo(){

        PieChart pieChart=findViewById(R.id.id_piechart);

       // pieEntries.clear();
        pieEntries.add(new PieEntry(rift,"Rft"));
        pieEntries.add(new PieEntry(nya,"Nya"));
        pieEntries.add(new PieEntry(nai,"Nai"));
        pieEntries.add(new PieEntry(est,"Est"));
        pieEntries.add(new PieEntry(wst,"Wst"));
        pieEntries.add(new PieEntry(cst,"Cst"));
        pieEntries.add(new PieEntry(cent,"Cent"));
        pieEntries.add(new PieEntry(n_est,"N-Est"));

        pieChart.getDescription().setEnabled(false);
        //pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        PieDataSet dataSet=new PieDataSet(pieEntries,"Blood Group Percentages(%)");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData pieData=new PieData(dataSet);
        //pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.WHITE);
        pieChart.setUsePercentValues(true);

        pieChart.setData(pieData);
        pieChart.animateXY(1000,1000);
        pieChart.invalidate();


    }



    private void user_Location() {

        user_Nai.addSnapshotListener(Bar_graph.this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Log.w("Beast", "Listen failed.", e);

                    return;
                }


                nai=queryDocumentSnapshots.size();

                user_Cent.addSnapshotListener(Bar_graph.this,new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                if (e != null) {
                                    // Log.w("Beast", "Listen failed.", e);

                                    return;
                                }
                                cent=queryDocumentSnapshots.size();
                                //textView1.setText(Integer.toString(donor));

                                user_Cst.addSnapshotListener(Bar_graph.this,new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                        if (e != null) {
                                            // Log.w("Beast", "Listen failed.", e);

                                            return;
                                        }

                                        cst=queryDocumentSnapshots.size();

                                        user_Rft.addSnapshotListener(Bar_graph.this,new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                                if (e != null) {
                                                    // Log.w("Beast", "Listen failed.", e);

                                                    return;
                                                }
                                                rift=queryDocumentSnapshots.size();


                                                user_Nya.addSnapshotListener(Bar_graph.this,new EventListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                                        if (e != null) {
                                                            // Log.w("Beast", "Listen failed.", e);

                                                            return;
                                                        }
                                                        nya=queryDocumentSnapshots.size();


                                                        user_Wst.addSnapshotListener(Bar_graph.this,new EventListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                                                if (e != null) {
                                                                    // Log.w("Beast", "Listen failed.", e);

                                                                    return;
                                                                }
                                                                wst=queryDocumentSnapshots.size();


                                                                user_Est.addSnapshotListener(Bar_graph.this,new EventListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                                                        if (e != null) {
                                                                            // Log.w("Beast", "Listen failed.", e);

                                                                            return;
                                                                        }
                                                                        est=queryDocumentSnapshots.size();


                                                                        user_Nest.addSnapshotListener(Bar_graph.this,new EventListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                                                                if (e != null) {
                                                                                    // Log.w("Beast", "Listen failed.", e);

                                                                                    return;
                                                                                }
                                                                                n_est=queryDocumentSnapshots.size();


                                                                                bloodBargraph();

                                                                                pieCharttwo();



                                                                            }
                                                                        });






                                                                    }
                                                                });






                                                            }
                                                        });






                                                    }
                                                });






                                            }
                                        });





                                    }
                                });





                            }
                        }


                );






            }
        });

    }




}
