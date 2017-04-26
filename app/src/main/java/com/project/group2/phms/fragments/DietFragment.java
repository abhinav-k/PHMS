package com.project.group2.phms.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.DecoDrawEffect;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.project.group2.phms.R;
import com.project.group2.phms.activities.PhmsActivity;
import com.project.group2.phms.adapter.BreakfastAdapter;
import com.project.group2.phms.adapter.DinnerAdapter;
import com.project.group2.phms.adapter.FoodAutoCompleteAdapter;
import com.project.group2.phms.adapter.LunchAdapter;
import com.project.group2.phms.adapter.SnacksAdapter;
import com.project.group2.phms.model.Breakfast;
import com.project.group2.phms.model.Dinner;
import com.project.group2.phms.model.Food;
import com.project.group2.phms.model.Lunch;
import com.project.group2.phms.model.Snacks;
import com.project.group2.phms.other.DelayAutoCompleteTextView;
import com.project.group2.phms.preferences.Preferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by vishwath on 2/13/17.
 */

public class DietFragment extends Fragment implements View.OnClickListener {
    Toolbar toolbar;
    TextView addBreakfast;
    TextView addLunch;
    TextView addDinner;
    TextView addSnacks;

    TextView breakfastCalorieTextView, lunchCalorieTextView, dinnerCalorieTextView, snacksCalorieTextView,
            caloriesPercentage, caloriesRemaining;

    String userId;

    DatabaseReference databaseReferenceBreakfast;
    DatabaseReference databaseReferenceLunch;
    DatabaseReference databaseReferenceDinner;
    DatabaseReference databaseReferenceSnacks;

    TextInputEditText brandNameEditText;
    TextInputEditText foodDescriptionEditText;
    TextInputEditText servingSizeEditText;
    TextInputEditText caloriesEditText;

    DelayAutoCompleteTextView foodAutoComplete;

    FloatingActionsMenu fam;
    FloatingActionButton fab_calendar;
    FloatingActionButton fab_clear;

    Button cancelButton;
    Button addFoodButton;

    TextView totalCals;
    int calculatedCalories = 0;
    int totalCalories = 0;
    int breakfastCalories = 0, lunchCalories = 0, dinnerCalories = 0, snacksCalories = 0;
    int targetCalories = 1000;

    RecyclerView recyclerViewBreakfast;
    RecyclerView recyclerViewLunch;
    RecyclerView recyclerViewDinner;
    RecyclerView recyclerViewSnacks;

    RecyclerView.Adapter mBreakFastAdapter;
    RecyclerView.Adapter mLunchAdapter;
    RecyclerView.Adapter mDinnerAdapter;
    RecyclerView.Adapter mSnacksAdapter;

    ArrayList<Breakfast> breakfastArrayList;
    ArrayList<Lunch> lunchArrayList;
    ArrayList<Dinner> dinnerArrayList;
    ArrayList<Snacks> snacksArrayList;

    LinearLayout totalCalorieLayout;
    DecoView arcView;

    private int mBackIndex;
    private int mSeries1Index;
    private int mSeries2Index;
    private int mSeries3Index;
    private int mSeries4Index;

    private int mSeriesMax = 1000;


    public DietFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diet, container, false);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.diet));
        toolbar.setVisibility(View.VISIBLE);

        Log.d("enter", "enter");

        //ArrayList for storing each diet objects
        breakfastArrayList = new ArrayList<>();
        lunchArrayList = new ArrayList<>();
        dinnerArrayList = new ArrayList<>();
        snacksArrayList = new ArrayList<>();

        /* Floating Action Menu and button declaration
         *  This is used for diet filter and clearing the filter
         */
        fam = (FloatingActionsMenu) view.findViewById(R.id.fab_menu);
        fab_calendar = (FloatingActionButton) view.findViewById(R.id.fab_calendar);
        fab_clear = (FloatingActionButton) view.findViewById(R.id.fab_clear);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userId = sharedPreferences.getString(Preferences.USERID, null);

        //Database reference for each of the diet
        if (userId != null) {
            databaseReferenceBreakfast = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("diet").child("breakfast");
            databaseReferenceLunch = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("diet").child("lunch");
            databaseReferenceDinner = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("diet").child("dinner");
            databaseReferenceSnacks = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("diet").child("snacks");
        }

        //Recycler view initialization for breakfast
        recyclerViewBreakfast = (RecyclerView) view.findViewById(R.id.breakfast_recycler);
        recyclerViewBreakfast.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mBreakFastAdapter = new BreakfastAdapter(getContext(), breakfastArrayList);
        recyclerViewBreakfast.setLayoutManager(mLayoutManager);

        totalCalorieLayout = (LinearLayout) view.findViewById(R.id.totalCalsLayout);
        totalCals = (TextView) view.findViewById(R.id.totalCals);

        arcView = (DecoView) view.findViewById(R.id.dynamicArcView);

        breakfastCalorieTextView = (TextView) view.findViewById(R.id.breakfastCalories);
        lunchCalorieTextView = (TextView) view.findViewById(R.id.lunchCalories);
        dinnerCalorieTextView = (TextView) view.findViewById(R.id.dinnerCalories);
        snacksCalorieTextView = (TextView) view.findViewById(R.id.snacksCalories);
        caloriesPercentage = (TextView) view.findViewById(R.id.textPercentage);
        caloriesRemaining = (TextView) view.findViewById(R.id.textRemaining);

        /* Database call for finding out the total calories in the breakfast*/
        databaseReferenceBreakfast.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Breakfast breakfast = snapshot.getValue(Breakfast.class);
                    String breakfastKey = snapshot.getKey();
                    breakfast.setKey(breakfastKey);
                    breakfastArrayList.add(breakfast);
                }

                recyclerViewBreakfast.setAdapter(mBreakFastAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        registerForContextMenu(recyclerViewBreakfast);

        //Recycler view initialization for lunch
        recyclerViewLunch = (RecyclerView) view.findViewById(R.id.lunch_recycler);
        recyclerViewLunch.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager mLayoutManagerLunch = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mLunchAdapter = new LunchAdapter(getContext(), lunchArrayList);
        recyclerViewLunch.setLayoutManager(mLayoutManagerLunch);

         /* Database call for finding out the total calories in the lunch*/
        databaseReferenceLunch.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Lunch lunch = snapshot.getValue(Lunch.class);
                    String lunchKey = snapshot.getKey();
                    lunch.setKey(lunchKey);
                    lunchArrayList.add(lunch);
                }

                recyclerViewLunch.setAdapter(mLunchAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        registerForContextMenu(recyclerViewLunch);

        //Recycler view initialization for dinner
        recyclerViewDinner = (RecyclerView) view.findViewById(R.id.dinner_recycler);
        recyclerViewDinner.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager mLayoutManagerDinner = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mDinnerAdapter = new DinnerAdapter(getContext(), dinnerArrayList);
        recyclerViewDinner.setLayoutManager(mLayoutManagerDinner);

        /* Database call for finding out the total calories in the dinner*/
        databaseReferenceDinner.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Dinner dinner = snapshot.getValue(Dinner.class);
                    String dinnerKey = snapshot.getKey();
                    dinner.setKey(dinnerKey);
                    dinnerArrayList.add(dinner);
                }


                recyclerViewDinner.setAdapter(mDinnerAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        registerForContextMenu(recyclerViewDinner);

        //Recycler view initialization for dinner
        recyclerViewSnacks = (RecyclerView) view.findViewById(R.id.snacks_recycler);
        recyclerViewSnacks.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager mLayoutManagerSnacks = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mSnacksAdapter = new SnacksAdapter(getContext(), snacksArrayList);
        recyclerViewSnacks.setLayoutManager(mLayoutManagerSnacks);

        /* Database call for finding out the total calories in the snacks*/
        databaseReferenceSnacks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Snacks snacks = snapshot.getValue(Snacks.class);
                    String snacksKey = snapshot.getKey();
                    snacks.setKey(snacksKey);
                    snacksArrayList.add(snacks);
                }
                recyclerViewSnacks.setAdapter(mSnacksAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        registerForContextMenu(recyclerViewSnacks);

        addBreakfast = (TextView) view.findViewById(R.id.addBreakfastIcon);
        addLunch = (TextView) view.findViewById(R.id.addLunchIcon);
        addDinner = (TextView) view.findViewById(R.id.addDinnerIcon);
        addSnacks = (TextView) view.findViewById(R.id.addSnacksIcon);

        addBreakfast.setOnClickListener(this);
        addLunch.setOnClickListener(this);
        addDinner.setOnClickListener(this);
        addSnacks.setOnClickListener(this);


        //Filter Operation
        fab_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fam.collapse();
                int mYear;
                int mMonth;
                int mDay;
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                totalCalorieLayout.setVisibility(View.VISIBLE);
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(year, monthOfYear, dayOfMonth);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                                final String selectedDate = dateFormat.format(calendar.getTime());
                                totalCalories = 0;
                                breakfastCalories = lunchCalories = dinnerCalories = snacksCalories = 0;
                                breakfastArrayList.clear();
                                databaseReferenceBreakfast.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            Breakfast breakfast = snapshot.getValue(Breakfast.class);
                                            String breakfastKey = snapshot.getKey();
                                            breakfast.setKey(breakfastKey);
                                            if (selectedDate.equalsIgnoreCase(breakfast.getDate())) {
                                                breakfastArrayList.add(breakfast);
                                            }
                                        }
                                        for (int i = 0; i < breakfastArrayList.size(); i++) {
                                            calculatedCalories = Integer.parseInt(breakfastArrayList.get(i).getCalories());
                                            totalCalories += calculatedCalories;
                                            breakfastCalories += calculatedCalories;
                                        }
                                        totalCals.setText(String.valueOf(totalCalories));
                                        breakfastCalorieTextView.setText(String.valueOf(breakfastCalories) + " kCal");
                                        mBreakFastAdapter.notifyDataSetChanged();

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                lunchArrayList.clear();
                                databaseReferenceLunch.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            Lunch lunch = snapshot.getValue(Lunch.class);
                                            String lunchKey = snapshot.getKey();
                                            lunch.setKey(lunchKey);
                                            if (selectedDate.equalsIgnoreCase(lunch.getDate())) {
                                                lunchArrayList.add(lunch);
                                            }
                                        }
                                        for (int i = 0; i < lunchArrayList.size(); i++) {
                                            calculatedCalories = Integer.parseInt(lunchArrayList.get(i).getCalories());
                                            totalCalories += calculatedCalories;
                                            lunchCalories += calculatedCalories;
                                        }
                                        totalCals.setText(String.valueOf(totalCalories));
                                        lunchCalorieTextView.setText(String.valueOf(lunchCalories) + " kCal");
                                        mLunchAdapter.notifyDataSetChanged();

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                dinnerArrayList.clear();
                                databaseReferenceDinner.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            Dinner dinner = snapshot.getValue(Dinner.class);
                                            String dinnerKey = snapshot.getKey();
                                            dinner.setKey(dinnerKey);
                                            if (selectedDate.equalsIgnoreCase(dinner.getDate())) {
                                                dinnerArrayList.add(dinner);
                                            }
                                        }
                                        for (int i = 0; i < dinnerArrayList.size(); i++) {
                                            calculatedCalories = Integer.parseInt(dinnerArrayList.get(i).getCalories());
                                            totalCalories += calculatedCalories;
                                            dinnerCalories += calculatedCalories;
                                        }
                                        totalCals.setText(String.valueOf(totalCalories));
                                        dinnerCalorieTextView.setText(String.valueOf(dinnerCalories) + " kCal");
                                        mDinnerAdapter.notifyDataSetChanged();

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                snacksArrayList.clear();
                                databaseReferenceSnacks.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            Snacks snacks = snapshot.getValue(Snacks.class);
                                            String snacksKey = snapshot.getKey();
                                            snacks.setKey(snacksKey);
                                            if (selectedDate.equalsIgnoreCase(snacks.getDate())) {
                                                snacksArrayList.add(snacks);
                                            }
                                        }
                                        for (int i = 0; i < snacksArrayList.size(); i++) {
                                            calculatedCalories = Integer.parseInt(snacksArrayList.get(i).getCalories());
                                            totalCalories += calculatedCalories;
                                            snacksCalories += calculatedCalories;
                                        }
                                        totalCals.setText(String.valueOf(totalCalories));
                                        snacksCalorieTextView.setText(String.valueOf(snacksCalories) + " kCal");
                                        mSnacksAdapter.notifyDataSetChanged();

                                        int tCal = Integer.parseInt(totalCals.getText().toString());
                                        Log.d("totalCals", String.valueOf(tCal));
                                        int percentageCals = (int) ((double) tCal / 1000 * 100);
                                        Log.d("percentage", String.valueOf(percentageCals));
                                        int remainingCals = targetCalories - tCal;

                                        caloriesPercentage.setText(String.valueOf(percentageCals) + "%");

                                        if (remainingCals > 0) {
                                            caloriesRemaining.setText(String.valueOf(remainingCals) + " to goal");
                                        } else {
                                            caloriesRemaining.setText("Goal Reached!");
                                        }

                                        createBackSeries();
                                        createDataSeries1();
                                        createDataSeries2();
                                        createDataSeries3();
                                        createDataSeries4();
                                        createEvents(breakfastCalories, lunchCalories, dinnerCalories, snacksCalories);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });

        //Clearing the filter
        fab_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PhmsActivity.class);
                intent.putExtra("dietFlag", true);
                getContext().startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.addBreakfastIcon:
                addFoodFunction("breakfast");
                break;
            case R.id.addLunchIcon:
                addFoodFunction("lunch");
                break;
            case R.id.addDinnerIcon:
                addFoodFunction("dinner");
                break;
            case R.id.addSnacksIcon:
                addFoodFunction("snacks");
                break;
        }

    }

    private void addFoodFunction(final String diet) {

        final Dialog dialog = new Dialog(getContext());
        dialog.setTitle("Add Food");
        dialog.setContentView(R.layout.dialog_add_food);
        dialog.show();

        brandNameEditText = (TextInputEditText) dialog.findViewById(R.id.brandNameEditText);
        servingSizeEditText = (TextInputEditText) dialog.findViewById(R.id.servingSizeEditText);
        caloriesEditText = (TextInputEditText) dialog.findViewById(R.id.caloriesEditText);

        foodAutoComplete = (DelayAutoCompleteTextView) dialog.findViewById(R.id.foodAutoComplete);
        foodAutoComplete.setThreshold(4);
        foodAutoComplete.setAdapter(new FoodAutoCompleteAdapter(getContext()));
        foodAutoComplete.setLoadingIndicator((android.widget.ProgressBar) dialog.findViewById(R.id.pb_loading_indicator));
        foodAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Food food = (Food) parent.getItemAtPosition(position);
                foodAutoComplete.setText(food.getFoodName());
                brandNameEditText.setText(food.getBrandName());
                servingSizeEditText.setText(String.valueOf(food.getServingSize()));
                caloriesEditText.setText(String.valueOf(food.getCalories()));

            }
        });

        //Buttons from Dialog
        addFoodButton = (Button) dialog.findViewById(R.id.addFoodButton);
        cancelButton = (Button) dialog.findViewById(R.id.cancelButton);

        addFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String brandName = brandNameEditText.getText().toString().trim();
                String foodDescription = foodAutoComplete.getText().toString().trim();
                String servingSize = servingSizeEditText.getText().toString().trim();
                String calories = caloriesEditText.getText().toString().trim();
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                String formattedDate = df.format(c.getTime());

                if (!validateForm(brandName, foodDescription, servingSize, calories)) {
                    Toast.makeText(getContext(), "All fields are mandatory", Toast.LENGTH_SHORT).show();
                    return;
                }
                HashMap<String, String> dietMap = new HashMap<>();
                dietMap.put("date", formattedDate);
                dietMap.put("brandName", brandName);
                dietMap.put("foodDescription", foodDescription);
                dietMap.put("servingSize", servingSize);
                dietMap.put("calories", calories);
                switch (diet) {

                    case "breakfast":
                        databaseReferenceBreakfast.push().setValue(dietMap);
                        Toast.makeText(getContext(), "Breakfast Item successfully added", Toast.LENGTH_SHORT).show();
                        break;
                    case "lunch":
                        databaseReferenceLunch.push().setValue(dietMap);
                        Toast.makeText(getContext(), "Lunch Item successfully added", Toast.LENGTH_SHORT).show();
                        break;
                    case "dinner":
                        databaseReferenceDinner.push().setValue(dietMap);
                        Toast.makeText(getContext(), "Dinner Item successfully added", Toast.LENGTH_SHORT).show();
                        break;
                    case "snacks":
                        databaseReferenceSnacks.push().setValue(dietMap);
                        Toast.makeText(getContext(), "Snacks Item successfully added", Toast.LENGTH_SHORT).show();
                        break;
                }

                Intent intent = new Intent(getContext(), PhmsActivity.class);
                intent.putExtra("dietFlag", true);
                startActivity(intent);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private boolean validateForm(String brandName, String foodDescription, String servingSize, String calories) {
        boolean valid = true;
        if (TextUtils.isEmpty(brandName) || TextUtils.isEmpty(foodDescription) || TextUtils.isEmpty(servingSize) || TextUtils.isEmpty(calories)) {
            valid = false;
        }
        return valid;
    }

    private void createBackSeries() {
        SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFE2E2E2"))
                .setRange(0, mSeriesMax, 0)
                .setInitialVisibility(true)
                .build();

        mBackIndex = arcView.addSeries(seriesItem);
    }

    private void createDataSeries1() {
        final SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFD600"))
                .setRange(0, mSeriesMax, 0)
                .setInitialVisibility(false)
                .build();

//        final TextView textPercentage = (TextView) findViewById(R.id.textPercentage);
        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
//                float percentFilled = ((currentPosition - seriesItem.getMinValue()) / (seriesItem.getMaxValue() - seriesItem.getMinValue()));
//                textPercentage.setText(String.format("%.0f%%", percentFilled * 100f));
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });


//        final TextView textToGo = (TextView) findViewById(R.id.textRemaining);
        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
//                textToGo.setText(String.format("%.1f Km to goal", seriesItem.getMaxValue() - currentPosition));

            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

//        final TextView textActivity1 = (TextView) findViewById(R.id.textActivity1);
        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
//                textActivity1.setText(String.format("%.0f Km", currentPosition));
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        mSeries1Index = arcView.addSeries(seriesItem);
    }

    private void createDataSeries2() {
        final SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#8BC34A"))
                .setRange(0, mSeriesMax, 0)
                .setInitialVisibility(false)
                .build();

//        final TextView textActivity2 = (TextView) findViewById(R.id.textActivity2);

        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
//                textActivity2.setText(String.format("%.0f Km", currentPosition));

            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        mSeries2Index = arcView.addSeries(seriesItem);
    }

    private void createDataSeries3() {
        final SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FF6699FF"))
                .setRange(0, mSeriesMax, 0)
                .setInitialVisibility(false)
                .build();

//        final TextView textActivity3 = (TextView) findViewById(R.id.textActivity3);

        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
//                textActivity3.setText(String.format("%.2f Km", currentPosition));
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        mSeries3Index = arcView.addSeries(seriesItem);
    }

    private void createDataSeries4() {
        final SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#F44336"))
                .setRange(0, mSeriesMax, 0)
                .setInitialVisibility(false)
                .build();

//        final TextView textActivity3 = (TextView) findViewById(R.id.textActivity3);

        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
//                textActivity3.setText(String.format("%.2f Km", currentPosition));
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        mSeries4Index = arcView.addSeries(seriesItem);
    }

    private void createEvents(int breakfastCalories, int lunchCalories, int dinnerCalories, int snacksCalories) {
        arcView.executeReset();

        arcView.addEvent(new DecoEvent.Builder(mSeriesMax)
                .setIndex(mBackIndex)
                .setDuration(1000)
                .setDelay(100)
                .build());

        if (breakfastCalories > 0) {
            arcView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
                    .setIndex(mSeries1Index)
                    .setDuration(1000)
                    .setDelay(1250)
                    .build());

            arcView.addEvent(new DecoEvent.Builder(breakfastCalories)
                    .setIndex(mSeries1Index)
                    .setDelay(2250)
                    .build());
        }


        if (lunchCalories > 0) {
            arcView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
                    .setIndex(mSeries2Index)
                    .setDuration(1000)
                    .setEffectRotations(1)
                    .setDelay(3000)
                    .build());

            arcView.addEvent(new DecoEvent.Builder(lunchCalories)
                    .setIndex(mSeries2Index)
                    .setDelay(4000)
                    .build());

        }

        if (dinnerCalories > 0) {

            arcView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
                    .setIndex(mSeries3Index)
                    .setDuration(1000)
                    .setEffectRotations(1)
                    .setDelay(5000)
                    .build());

            arcView.addEvent(new DecoEvent.Builder(dinnerCalories)
                    .setIndex(mSeries3Index)
                    .setDelay(6000)
                    .build());

        }

        if (snacksCalories > 0) {
            arcView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
                    .setIndex(mSeries4Index)
                    .setDuration(1000)
                    .setEffectRotations(1)
                    .setDelay(7500)
                    .build());

            arcView.addEvent(new DecoEvent.Builder(snacksCalories)
                    .setIndex(mSeries4Index)
                    .setDelay(9000)
                    .build());
        }

        if (breakfastCalories + lunchCalories + dinnerCalories + snacksCalories > targetCalories) {


            arcView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_EXPLODE)
                    .setIndex(mSeries1Index)
                    .setDelay(12000)
                    .setDuration(3000)
                    .setDisplayText("GOAL!")
                    .setListener(new DecoEvent.ExecuteEventListener() {
                        @Override
                        public void onEventStart(DecoEvent decoEvent) {

                        }

                        @Override
                        public void onEventEnd(DecoEvent decoEvent) {

                        }
                    })
                    .build());
        }


    }

}