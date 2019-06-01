package com.example.thebeast.smartadmin;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Blood_Group extends AppCompatActivity {
    Query blood_A,blood_B,blood_O,blood_AB,rhesus_Negative,rhesus_Positive,blood_Male,blood_Female;
    FirebaseAuth mAuth;
    FirebaseFirestore mfirestore;
    TextView text_donors;
    int no_of_donors;

    ArrayList<PieEntry> pieEntries=new ArrayList<>();
    ArrayList<PieEntry> pieEntries_rhesus=new ArrayList<>();
    ArrayList<PieEntry> pieEntries_gender=new ArrayList<>();
    BarChart barChart;

    int donor_O,donor_B,donor_AB,donor_A,rhesus_p,rhesus_n,male,female;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood__group);
        mAuth=FirebaseAuth.getInstance();
        mfirestore=FirebaseFirestore.getInstance();

        blood_A =mfirestore.collection("user_table").whereEqualTo("blood_group","A").whereEqualTo("donor",true);
        blood_B =mfirestore.collection("user_table").whereEqualTo("blood_group","B").whereEqualTo("donor",true);
        blood_O=mfirestore.collection("user_table").whereEqualTo("blood_group","O").whereEqualTo("donor",true);
        blood_AB=mfirestore.collection("user_table").whereEqualTo("blood_group","AB").whereEqualTo("donor",true);

        rhesus_Positive=mfirestore.collection("user_table").whereEqualTo("rhesus","Rhesus +ve").whereEqualTo("donor",true);
        rhesus_Negative=mfirestore.collection("user_table").whereEqualTo("rhesus","Rhesus -ve").whereEqualTo("donor",true);

        blood_Male=mfirestore.collection("user_table").whereEqualTo("gender","Male").whereEqualTo("donor",true);
        blood_Female=mfirestore.collection("user_table").whereEqualTo("gender","Female").whereEqualTo("donor",true);

        text_donors=findViewById(R.id.text_no_donors);




    }

    @Override
    protected void onStart() {
        super.onStart();
        bloodGroups();
        bloodRhesus();
        bloodGender();


    }


    private void bloodBargraph() {
        barChart=findViewById(R.id.barchart);

        ArrayList<BarEntry>barEntries_Y=new ArrayList<>();
        barEntries_Y.add(new BarEntry(0,donor_A));
        barEntries_Y.add(new BarEntry(1,donor_B));
        barEntries_Y.add(new BarEntry(2,donor_AB));
        barEntries_Y.add(new BarEntry(3,donor_O));


        BarDataSet barDataSet=new BarDataSet(barEntries_Y,"No. of donors in each blood group.");

        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData barData=new BarData(barDataSet);


        barChart.setData(barData);
        barChart.animateXY(1000,1000);
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);



        final String[] quarters = new String[] { "Blood A", " Blood B", "Blood AB", "Blood O"};

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

    private void bloodGender() {
        blood_Male.addSnapshotListener(Blood_Group.this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Log.w("Beast", "Listen failed.", e);
                    return;
                }
                male=queryDocumentSnapshots.size();

                blood_Female.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            // Log.w("Beast", "Listen failed.", e);
                            return;
                        }

                        female=queryDocumentSnapshots.size();

                        pieEntries_gender.clear();
                        pieEntries_gender.add(new PieEntry(male,"Male Donors"));
                        pieEntries_gender.add(new PieEntry(female,"Female Donors"));
                        pieChartGender();



                    }
                });

            }
        });
    }

    private void pieChartGender() {
        PieChart pieChart=findViewById(R.id.id_Gender);


        pieChart.getDescription().setEnabled(false);
        //pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        PieDataSet dataSet=new PieDataSet(pieEntries_gender,"Donor Gender Percentages(%)");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        PieData pieData=new PieData(dataSet);
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.WHITE);
        pieChart.setUsePercentValues(true);

        pieChart.setData(pieData);
        pieChart.animateXY(1000,1000);
        pieChart.invalidate();

    }

    private void bloodRhesus() {

        rhesus_Positive.addSnapshotListener(this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Log.w("Beast", "Listen failed.", e);

                    return;
                }
                rhesus_p=queryDocumentSnapshots.size();

                rhesus_Negative.addSnapshotListener(Blood_Group.this,new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            // Log.w("Beast", "Listen failed.", e);

                            return;
                        }
                        rhesus_n=queryDocumentSnapshots.size();

                        pieEntries_rhesus.clear();
                        pieEntries_rhesus.add(new PieEntry(rhesus_p,"Rhesus +Ve"));
                        pieEntries_rhesus.add(new PieEntry(rhesus_n,"Rhesus -Ve"));
                        pieChartRhesus();

                    }
                });



            }
        });


    }

    private void pieChartRhesus() {
        PieChart pieChart=findViewById(R.id.id_Rhesus);


        pieChart.getDescription().setEnabled(false);
        //pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        PieDataSet dataSet=new PieDataSet(pieEntries_rhesus,"Blood Rhesus Percentages(%)");
        dataSet.setColors(ColorTemplate.PASTEL_COLORS);
        PieData pieData=new PieData(dataSet);
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.WHITE);
        pieChart.setUsePercentValues(true);

        pieChart.setData(pieData);
        pieChart.animateXY(1000,1000);
        pieChart.invalidate();

    }


    public void bloodGroups(){

        mfirestore.collection("user_table").whereEqualTo("donor",true).addSnapshotListener(Blood_Group.this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    // Log.w("Beast", "Listen failed.", e);

                    return;
                }

                    no_of_donors=queryDocumentSnapshots.size();
                    text_donors.setText(Integer.toString(no_of_donors));

                    blood_A.addSnapshotListener(Blood_Group.this,new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                // Log.w("Beast", "Listen failed.", e);

                                return;
                            }


                                donor_A=queryDocumentSnapshots.size();

                                blood_B.addSnapshotListener(Blood_Group.this,new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                        if (e != null) {
                                            // Log.w("Beast", "Listen failed.", e);

                                            return;
                                        }
                                            donor_B=queryDocumentSnapshots.size();
                                            //textView1.setText(Integer.toString(donor));

                                            blood_AB.addSnapshotListener(Blood_Group.this,new EventListener<QuerySnapshot>() {
                                                @Override
                                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                                    if (e != null) {
                                                        // Log.w("Beast", "Listen failed.", e);

                                                        return;
                                                    }

                                                        donor_AB=queryDocumentSnapshots.size();

                                                        blood_O.addSnapshotListener(Blood_Group.this,new EventListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                                                donor_O=queryDocumentSnapshots.size();
                                                                pieEntries.clear();
                                                                pieEntries.add(new PieEntry(donor_A,"Group A"));
                                                                pieEntries.add(new PieEntry(donor_B,"Group B"));
                                                                pieEntries.add(new PieEntry(donor_AB,"Group AB"));
                                                                pieEntries.add(new PieEntry(donor_O,"Group O"));
                                                                pieCharttwo();

                                                                bloodBargraph();

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
        });



    }

    public  void pieCharttwo(){

        PieChart pieChart=findViewById(R.id.id_piechart);


        pieChart.getDescription().setEnabled(false);
        //pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        PieDataSet dataSet=new PieDataSet(pieEntries,"Blood Group Percentages(%)");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData pieData=new PieData(dataSet);
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.WHITE);
        pieChart.setUsePercentValues(true);

        pieChart.setData(pieData);
        pieChart.animateXY(1000,1000);
        pieChart.invalidate();


    }






}
