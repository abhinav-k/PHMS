package com.project.group2.phms.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.group2.phms.R;
import com.project.group2.phms.activities.PhmsActivity;
import com.project.group2.phms.activities.VitalsActivity;

import butterknife.ButterKnife;

/**
 * Created by vishwath on 2/13/17.
 */

public class HomeFragment extends Fragment {
    Toolbar toolbar;

    public HomeFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.home));
        initControls(view);
        return view;
    }


    public void initControls(View view){

        CardView vitalsCard = (CardView) view.findViewById(R.id.cardViewVitals);
        CardView medicationsCard = (CardView) view.findViewById(R.id.cardViewMedications);
        CardView appointmentCard = (CardView) view.findViewById(R.id.cardViewAppointments);
        CardView notesCard = (CardView) view.findViewById(R.id.cardViewNotes);
        CardView dietCard = (CardView) view.findViewById(R.id.cardViewDiet);
        CardView designeeCard = (CardView) view.findViewById(R.id.cardViewDesignee);

        vitalsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment VitalsFragment = new VitalsFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, VitalsFragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

                ft.commit();
            }
        });
        medicationsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment MedicationFragment = new MedicationFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, MedicationFragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

                ft.commit();
            }
        });
        appointmentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment AppointmentsFragment = new AppointmentsFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, AppointmentsFragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        notesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment NotesFragment = new NotesFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, NotesFragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        dietCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment DietFragment = new DietParentFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, DietFragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        designeeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment DesigneeFragment = new DesigneeFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, DesigneeFragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }
}
