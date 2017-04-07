package com.project.group2.phms.fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.group2.phms.R;
import com.project.group2.phms.model.Breakfast;
import com.project.group2.phms.model.Dinner;
import com.project.group2.phms.model.Lunch;
import com.project.group2.phms.model.Snacks;
import com.project.group2.phms.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by vishwath on 4/6/17.
 */

public class DietChartFragment extends Fragment {

    String userId;

    DatabaseReference databaseReferenceBreakfast;
    DatabaseReference databaseReferenceLunch;
    DatabaseReference databaseReferenceDinner;
    DatabaseReference databaseReferenceSnacks;

    ArrayList<Breakfast> breakfastArrayList;
    ArrayList<Lunch> lunchArrayList;
    ArrayList<Dinner> dinnerArrayList;
    ArrayList<Snacks> snacksArrayList;

    ArrayList<Float> breakfastCalories;
    ArrayList<Float> lunchCalories;
    ArrayList<Float> dinnerCalories;
    ArrayList<Float> snacksCalories;

    LineChart breakfastChart, lunchChart, dinnerChart, snacksChart;
    Set<String> breakfastSet, lunchSet, dinnerSet, snacksSet;

    public DietChartFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diet_chart, container, false);

        breakfastChart = (LineChart) view.findViewById(R.id.breakfastChart);
        lunchChart = (LineChart) view.findViewById(R.id.lunchChart);
        dinnerChart = (LineChart) view.findViewById(R.id.dinnerChart);
        snacksChart = (LineChart) view.findViewById(R.id.snacksChart);


        breakfastArrayList = new ArrayList<>();
        lunchArrayList = new ArrayList<>();
        dinnerArrayList = new ArrayList<>();
        snacksArrayList = new ArrayList<>();

        breakfastCalories = new ArrayList<>();
        lunchCalories = new ArrayList<>();
        dinnerCalories = new ArrayList<>();
        snacksCalories = new ArrayList<>();


        breakfastSet = new HashSet<>();
        lunchSet = new HashSet<>();
        dinnerSet = new HashSet<>();
        snacksSet = new HashSet<>();


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userId = sharedPreferences.getString(Preferences.USERID, null);
        if (userId != null) {
            databaseReferenceBreakfast = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("diet").child("breakfast");
            databaseReferenceLunch = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("diet").child("lunch");
            databaseReferenceDinner = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("diet").child("dinner");
            databaseReferenceSnacks = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("diet").child("snacks");
        }

        databaseReferenceBreakfast.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Breakfast breakfast = snapshot.getValue(Breakfast.class);
                    String breakfastKey = snapshot.getKey();
                    breakfast.setKey(breakfastKey);
                    breakfastSet.add(breakfast.getDate());
                    breakfastArrayList.add(breakfast);

                }
                Log.d("breakfast", breakfastArrayList.toString());
                float calculatedCalories;
                float totalCalories = 0;
                if (!breakfastSet.isEmpty()) {
                    for (String date : breakfastSet) {
                        for (int i = 0; i < breakfastArrayList.size(); i++) {
                            if (date.equalsIgnoreCase(breakfastArrayList.get(i).getDate())) {
                                calculatedCalories = Integer.parseInt(breakfastArrayList.get(i).getCalories());
                                totalCalories += calculatedCalories;
                            }
                        }
                        breakfastCalories.add(totalCalories);
                        totalCalories = 0;
                    }
                    Log.d("breakfastCalories", breakfastCalories.toString());
                    LineData data = getData(breakfastCalories);
                    setupChart(breakfastChart, data, R.color.primary_dark);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReferenceLunch.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Lunch lunch = snapshot.getValue(Lunch.class);
                    String lunchKey = snapshot.getKey();
                    lunch.setKey(lunchKey);
                    lunchSet.add(lunch.getDate());
                    lunchArrayList.add(lunch);

                }
                Log.d("lunch", lunchArrayList.toString());
                float calculatedCalories;
                float totalCalories = 0;
                if (!lunchSet.isEmpty()) {
                    for (String date : lunchSet) {
                        for (int i = 0; i < lunchArrayList.size(); i++) {
                            if (date.equalsIgnoreCase(lunchArrayList.get(i).getDate())) {
                                calculatedCalories = Integer.parseInt(lunchArrayList.get(i).getCalories());
                                totalCalories += calculatedCalories;
                            }
                        }
                        lunchCalories.add(totalCalories);
                        totalCalories = 0;
                    }
                    Log.d("lunchCalories", lunchCalories.toString());
                    LineData data = getData(lunchCalories);
                    setupChart(lunchChart, data, R.color.primary_dark);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReferenceDinner.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Dinner dinner = snapshot.getValue(Dinner.class);
                    String dinnerKey = snapshot.getKey();
                    dinner.setKey(dinnerKey);
                    dinnerSet.add(dinner.getDate());
                    dinnerArrayList.add(dinner);

                }
                Log.d("dinner", dinnerArrayList.toString());
                float calculatedCalories;
                float totalCalories = 0;
                if (!dinnerSet.isEmpty()) {
                    for (String date : dinnerSet) {
                        for (int i = 0; i < dinnerArrayList.size(); i++) {
                            if (date.equalsIgnoreCase(dinnerArrayList.get(i).getDate())) {
                                calculatedCalories = Integer.parseInt(dinnerArrayList.get(i).getCalories());
                                totalCalories += calculatedCalories;
                            }
                        }
                        dinnerCalories.add(totalCalories);
                        totalCalories = 0;
                    }
                    Log.d("dinnerCalories", dinnerCalories.toString());
                    LineData data = getData(dinnerCalories);
                    setupChart(dinnerChart, data, R.color.primary_dark);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReferenceSnacks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Snacks snacks = snapshot.getValue(Snacks.class);
                    String snacksKey = snapshot.getKey();
                    snacks.setKey(snacksKey);
                    snacksSet.add(snacks.getDate());
                    snacksArrayList.add(snacks);

                }
                Log.d("snacks", snacksArrayList.toString());
                float calculatedCalories;
                float totalCalories = 0;
                if (!snacksSet.isEmpty()) {
                    for (String date : snacksSet) {
                        for (int i = 0; i < snacksArrayList.size(); i++) {
                            if (date.equalsIgnoreCase(snacksArrayList.get(i).getDate())) {
                                calculatedCalories = Integer.parseInt(snacksArrayList.get(i).getCalories());
                                totalCalories += calculatedCalories;
                            }
                        }
                        snacksCalories.add(totalCalories);
                        totalCalories = 0;
                    }
                    Log.d("snacksCalories", snacksCalories.toString());
                    LineData data = getData(snacksCalories);
                    setupChart(snacksChart, data, R.color.primary_dark);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
    }

    private void setupChart(final LineChart chart, LineData data, int color) {

        ((LineDataSet) data.getDataSetByIndex(0)).setCircleColorHole(color);

        // no description text
        chart.getDescription().setEnabled(false);

        // mChart.setDrawHorizontalGrid(false);
        //
        // enable / disable grid background
        chart.setDrawGridBackground(false);
//        chart.getRenderer().getGridPaint().setGridColor(Color.WHITE & 0x70FFFFFF);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(true);

        chart.setBackgroundColor(color);

        // set custom chart offsets (automatic offset calculation is hereby disabled)
        chart.setViewPortOffsets(10, 0, 10, 0);

        // add data
        chart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();
        l.setEnabled(false);

        chart.getAxisLeft().setEnabled(false);
        chart.getAxisLeft().setSpaceTop(40);
        chart.getAxisLeft().setSpaceBottom(40);
        chart.getAxisRight().setEnabled(false);

        chart.getXAxis().setEnabled(false);

        // animate calls invalidate()...
        chart.animateX(2500);
        chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chart.animateX(1000);
            }
        });
    }

    private LineData getData(ArrayList<Float> CaloriesList) {

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < CaloriesList.size(); i++) {
            float val = CaloriesList.get(i);
            yVals.add(new Entry(i, val));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");
        // set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

        set1.setLineWidth(1.75f);
        set1.setCircleRadius(5f);
        set1.setCircleHoleRadius(2.5f);
        set1.setColor(Color.WHITE);
        set1.setCircleColor(Color.WHITE);
        set1.setHighLightColor(Color.WHITE);
        set1.setDrawValues(false);

        // create a data object with the datasets
        LineData data = new LineData(set1);

        return data;
    }


}
