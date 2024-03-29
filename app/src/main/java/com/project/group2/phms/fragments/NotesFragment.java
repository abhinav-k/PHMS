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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.group2.phms.R;
import com.project.group2.phms.activities.NotesActivity;
import com.project.group2.phms.adapter.NotesAdapter;
import com.project.group2.phms.model.Notes;
import com.project.group2.phms.other.SimpleDividerItemDecoration;
import com.project.group2.phms.preferences.Preferences;

import java.util.ArrayList;

/**
 * Created by vishwath on 2/13/17.
 */

public class NotesFragment extends Fragment {
    RecyclerView recyclerView;
    Toolbar toolbar;
    FloatingActionButton addNotesButton;
    DatabaseReference databaseReference;
    ArrayList<Notes> notesArrayList;
    public NotesFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        notesArrayList = new ArrayList<>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String userId = sharedPreferences.getString(Preferences.USERID,null);
        if (userId != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("notes");
        }
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.notes));
        toolbar.setVisibility(View.VISIBLE);
        addNotesButton = (FloatingActionButton) view.findViewById(R.id.addNotesButton);
        recyclerView = (RecyclerView) view.findViewById(R.id.notes_recycler);
        recyclerView.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Notes notes = snapshot.getValue(Notes.class);
                    String notesKey = snapshot.getKey();
                    notes.setKey(notesKey);
                    notesArrayList.add(notes);
                }

                recyclerView.setAdapter(new NotesAdapter(getContext(), notesArrayList));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        registerForContextMenu(recyclerView);

        addNotesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NotesActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}
