package com.project.group2.phms.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.group2.phms.R;
import com.project.group2.phms.activities.PhmsActivity;
import com.project.group2.phms.activities.VitalsActivity;
import com.project.group2.phms.adapter.BreakfastAdapter;
import com.project.group2.phms.adapter.DinnerAdapter;
import com.project.group2.phms.adapter.LunchAdapter;
import com.project.group2.phms.adapter.SnacksAdapter;
import com.project.group2.phms.model.Breakfast;
import com.project.group2.phms.model.Dinner;
import com.project.group2.phms.model.Lunch;
import com.project.group2.phms.model.Snacks;
import com.project.group2.phms.preferences.Preferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by vishwath on 2/13/17.
 */

public class DietFragment extends Fragment {
    Toolbar toolbar;
    TextView addBreakfast;
    TextView addLunch;
    TextView addDinner;
    TextView addSnacks;

    String userId;

    DatabaseReference databaseReferenceBreakfast;
    DatabaseReference databaseReferenceLunch;
    DatabaseReference databaseReferenceDinner;
    DatabaseReference databaseReferenceSnacks;

    TextInputEditText brandNameEditText;
    TextInputEditText foodDescriptionEditText;
    TextInputEditText servingSizeEditText;
    TextInputEditText caloriesEditText;

    Button cancelButton;
    Button addFoodButton;

    TextView totalCals;

    RecyclerView recyclerViewBreakfast;
    RecyclerView recyclerViewLunch;
    RecyclerView recyclerViewDinner;
    RecyclerView recyclerViewSnacks;

    ArrayList<Breakfast> breakfastArrayList;
    ArrayList<Lunch> lunchArrayList;
    ArrayList<Dinner> dinnerArrayList;
    ArrayList<Snacks> snacksArrayList;


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
        breakfastArrayList = new ArrayList<>();
        lunchArrayList = new ArrayList<>();
        dinnerArrayList = new ArrayList<>();
        snacksArrayList = new ArrayList<>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userId = sharedPreferences.getString(Preferences.USERID, null);
        if (userId != null) {
            databaseReferenceBreakfast = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("diet").child("breakfast");
            databaseReferenceLunch = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("diet").child("lunch");
            databaseReferenceDinner = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("diet").child("dinner");
            databaseReferenceSnacks = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("diet").child("snacks");
        }
        recyclerViewBreakfast = (RecyclerView) view.findViewById(R.id.breakfast_recycler);
        recyclerViewBreakfast.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        final RecyclerView.Adapter mBreakFastAdapter = new BreakfastAdapter(getContext(), breakfastArrayList);
        recyclerViewBreakfast.setLayoutManager(mLayoutManager);

        totalCals = (TextView) view.findViewById(R.id.totalCals);

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

        recyclerViewLunch = (RecyclerView) view.findViewById(R.id.lunch_recycler);
        recyclerViewLunch.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager mLayoutManagerLunch = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        final RecyclerView.Adapter mLunchAdapter = new LunchAdapter(getContext(), lunchArrayList);
        recyclerViewLunch.setLayoutManager(mLayoutManagerLunch);

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

        recyclerViewDinner = (RecyclerView) view.findViewById(R.id.dinner_recycler);
        recyclerViewDinner.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager mLayoutManagerDinner = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        final RecyclerView.Adapter mDinnerAdapter = new DinnerAdapter(getContext(), dinnerArrayList);
        recyclerViewDinner.setLayoutManager(mLayoutManagerDinner);
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

        recyclerViewSnacks = (RecyclerView) view.findViewById(R.id.snacks_recycler);
        recyclerViewSnacks.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager mLayoutManagerSnacks = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        final RecyclerView.Adapter mSnacksAdapter = new SnacksAdapter(getContext(), snacksArrayList);
        recyclerViewSnacks.setLayoutManager(mLayoutManagerSnacks);

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
        addBreakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setTitle("Add Food");
                dialog.setContentView(R.layout.dialog_add_food);
                dialog.show();

                //Edit Texts from Dialog
                brandNameEditText = (TextInputEditText) dialog.findViewById(R.id.brandNameEditText);
                foodDescriptionEditText = (TextInputEditText) dialog.findViewById(R.id.foodDescriptionEditText);
                servingSizeEditText = (TextInputEditText) dialog.findViewById(R.id.servingSizeEditText);
                caloriesEditText = (TextInputEditText) dialog.findViewById(R.id.caloriesEditText);

                //Buttons from Dialog
                addFoodButton = (Button) dialog.findViewById(R.id.addFoodButton);
                cancelButton = (Button) dialog.findViewById(R.id.cancelButton);

                addFoodButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String brandName = brandNameEditText.getText().toString().trim();
                        String foodDescription = foodDescriptionEditText.getText().toString().trim();
                        String servingSize = servingSizeEditText.getText().toString().trim();
                        String calories = caloriesEditText.getText().toString().trim();
                        totalCals.setText(calories);

                        HashMap<String, String> breakfastMap = new HashMap<>();

                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                        String formattedDate = df.format(c.getTime());
                        breakfastMap.put("date", formattedDate);
                        breakfastMap.put("brandName", brandName);
                        breakfastMap.put("foodDescription", foodDescription);
                        breakfastMap.put("servingSize", servingSize);
                        breakfastMap.put("calories", calories);
                        databaseReferenceBreakfast.push().setValue(breakfastMap);
                        Toast.makeText(getContext(), "Breakfast Item successfully added", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(),PhmsActivity.class);
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

        });

        addLunch = (TextView) view.findViewById(R.id.addLunchIcon);
        addLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setTitle("Add Food");
                dialog.setContentView(R.layout.dialog_add_food);
                dialog.show();

                //Edit Texts from Dialog
                brandNameEditText = (TextInputEditText) dialog.findViewById(R.id.brandNameEditText);
                foodDescriptionEditText = (TextInputEditText) dialog.findViewById(R.id.foodDescriptionEditText);
                servingSizeEditText = (TextInputEditText) dialog.findViewById(R.id.servingSizeEditText);
                caloriesEditText = (TextInputEditText) dialog.findViewById(R.id.caloriesEditText);

                //Buttons from Dialog
                addFoodButton = (Button) dialog.findViewById(R.id.addFoodButton);
                cancelButton = (Button) dialog.findViewById(R.id.cancelButton);

                addFoodButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String brandName = brandNameEditText.getText().toString().trim();
                        String foodDescription = foodDescriptionEditText.getText().toString().trim();
                        String servingSize = servingSizeEditText.getText().toString().trim();
                        String calories = caloriesEditText.getText().toString().trim();

                        HashMap<String, String> lunchMap = new HashMap<>();

                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                        String formattedDate = df.format(c.getTime());
                        lunchMap.put("date", formattedDate);
                        lunchMap.put("brandName", brandName);
                        lunchMap.put("foodDescription", foodDescription);
                        lunchMap.put("servingSize", servingSize);
                        lunchMap.put("calories", calories);
                        databaseReferenceLunch.push().setValue(lunchMap);
                        Toast.makeText(getContext(), "Lunch Item successfully added", Toast.LENGTH_SHORT).show();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


            }

        });

        addDinner = (TextView) view.findViewById(R.id.addDinnerIcon);
        addDinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setTitle("Add Food");
                dialog.setContentView(R.layout.dialog_add_food);
                dialog.show();

                //Edit Texts from Dialog
                brandNameEditText = (TextInputEditText) dialog.findViewById(R.id.brandNameEditText);
                foodDescriptionEditText = (TextInputEditText) dialog.findViewById(R.id.foodDescriptionEditText);
                servingSizeEditText = (TextInputEditText) dialog.findViewById(R.id.servingSizeEditText);
                caloriesEditText = (TextInputEditText) dialog.findViewById(R.id.caloriesEditText);

                //Buttons from Dialog
                addFoodButton = (Button) dialog.findViewById(R.id.addFoodButton);
                cancelButton = (Button) dialog.findViewById(R.id.cancelButton);

                addFoodButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String brandName = brandNameEditText.getText().toString().trim();
                        String foodDescription = foodDescriptionEditText.getText().toString().trim();
                        String servingSize = servingSizeEditText.getText().toString().trim();
                        String calories = caloriesEditText.getText().toString().trim();

                        HashMap<String, String> dinnerMap = new HashMap<>();

                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                        String formattedDate = df.format(c.getTime());
                        dinnerMap.put("date", formattedDate);
                        dinnerMap.put("brandName", brandName);
                        dinnerMap.put("foodDescription", foodDescription);
                        dinnerMap.put("servingSize", servingSize);
                        dinnerMap.put("calories", calories);
                        databaseReferenceDinner.push().setValue(dinnerMap);
                        Toast.makeText(getContext(), "Dinner Item successfully added", Toast.LENGTH_SHORT).show();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }

        });

        addSnacks = (TextView) view.findViewById(R.id.addSnacksIcon);
        addSnacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setTitle("Add Food");
                dialog.setContentView(R.layout.dialog_add_food);
                dialog.show();

                //Edit Texts from Dialog
                brandNameEditText = (TextInputEditText) dialog.findViewById(R.id.brandNameEditText);
                foodDescriptionEditText = (TextInputEditText) dialog.findViewById(R.id.foodDescriptionEditText);
                servingSizeEditText = (TextInputEditText) dialog.findViewById(R.id.servingSizeEditText);
                caloriesEditText = (TextInputEditText) dialog.findViewById(R.id.caloriesEditText);

                //Buttons from Dialog
                addFoodButton = (Button) dialog.findViewById(R.id.addFoodButton);
                cancelButton = (Button) dialog.findViewById(R.id.cancelButton);

                addFoodButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String brandName = brandNameEditText.getText().toString().trim();
                        String foodDescription = foodDescriptionEditText.getText().toString().trim();
                        String servingSize = servingSizeEditText.getText().toString().trim();
                        String calories = caloriesEditText.getText().toString().trim();

                        HashMap<String, String> snacksMap = new HashMap<>();

                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                        String formattedDate = df.format(c.getTime());
                        snacksMap.put("date", formattedDate);
                        snacksMap.put("brandName", brandName);
                        snacksMap.put("foodDescription", foodDescription);
                        snacksMap.put("servingSize", servingSize);
                        snacksMap.put("calories", calories);
                        databaseReferenceSnacks.push().setValue(snacksMap);
                        Toast.makeText(getContext(), "Snack successfully added", Toast.LENGTH_SHORT).show();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }

        });
        return view;
    }

}
