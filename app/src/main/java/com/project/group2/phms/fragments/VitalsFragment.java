package com.project.group2.phms.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.group2.phms.R;
import com.project.group2.phms.activities.VitalsActivity;
import com.project.group2.phms.adapter.VitalsAdapter;
import com.project.group2.phms.model.Vitals;
import com.project.group2.phms.other.SimpleDividerItemDecoration;
import com.project.group2.phms.preferences.Preferences;

import java.util.ArrayList;

import static android.R.attr.key;

/**
 * Created by vishwath on 2/13/17.
 */

public class VitalsFragment extends Fragment  {

    RecyclerView recyclerView;
    Toolbar toolbar;
    FloatingActionButton addButton;
    DatabaseReference databaseReference;
    ArrayList<Vitals> vitalsList;
    public VitalsFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vitals, container, false);
        vitalsList = new ArrayList<>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String userId = sharedPreferences.getString(Preferences.USERID,null);
        if (userId != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("vitals");
        }
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.vitals));
        toolbar.setVisibility(View.VISIBLE);
        addButton = (FloatingActionButton) view.findViewById(R.id.addButton);
        recyclerView = (RecyclerView) view.findViewById(R.id.vitals_recycler);
        recyclerView.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        final RecyclerView.Adapter mAdapter = new VitalsAdapter(getContext(),vitalsList);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Vitals vitals = snapshot.getValue(Vitals.class);
                    String key=snapshot.getKey();
                    vitals.setKey(key);
                    Log.d("Vitals", "Invoked");
                    vitalsList.add(vitals);
                }
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        registerForContextMenu(recyclerView);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), VitalsActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }


}
