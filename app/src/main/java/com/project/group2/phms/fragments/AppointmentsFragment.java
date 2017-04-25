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
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.group2.phms.R;
import com.project.group2.phms.activities.AppointmentsActivity;
import com.project.group2.phms.adapter.AppointmentsAdapter;
import com.project.group2.phms.model.Appointments;
import com.project.group2.phms.other.SimpleDividerItemDecoration;
import com.project.group2.phms.preferences.Preferences;

import java.util.ArrayList;

/**
 * Created by vishwath on 2/13/17.
 */

public class AppointmentsFragment extends Fragment {
    RecyclerView recyclerView;
    Toolbar toolbar;
    FloatingActionButton addAppointmentsButton;
    DatabaseReference databaseReference;
    ArrayList<Appointments> appointmentsArrayList;
    public AppointmentsFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointments, container, false);
        appointmentsArrayList = new ArrayList<>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String userId = sharedPreferences.getString(Preferences.USERID,null);
        if (userId != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("appointments");
        }
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.appointment));
        toolbar.setVisibility(View.VISIBLE);
        addAppointmentsButton = (FloatingActionButton) view.findViewById(R.id.addAppointmentsButton);
        recyclerView = (RecyclerView) view.findViewById(R.id.appointments_recycler);
        recyclerView.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        final RecyclerView.Adapter mAdapter = new AppointmentsAdapter(getContext(),appointmentsArrayList);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Appointments appointments = snapshot.getValue(Appointments.class);
                    String appointmentsKey = snapshot.getKey();
                    appointments.setKey(appointmentsKey);
                    appointmentsArrayList.add(appointments);
                    Log.d("Appointments Array list", "" + appointmentsArrayList);
                }

                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        registerForContextMenu(recyclerView);

        addAppointmentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AppointmentsActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}
