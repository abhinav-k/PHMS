package com.project.group2.phms.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.group2.phms.R;
import com.project.group2.phms.activities.MedicationActivity;
import com.project.group2.phms.adapter.AppointmentsAdapter;
import com.project.group2.phms.adapter.MedicationsAdapter;
import com.project.group2.phms.adapter.NotesAdapter;
import com.project.group2.phms.adapter.VitalsAdapter;
import com.project.group2.phms.model.Appointments;
import com.project.group2.phms.model.Breakfast;
import com.project.group2.phms.model.Dinner;
import com.project.group2.phms.model.Lunch;
import com.project.group2.phms.model.Medication;
import com.project.group2.phms.model.Notes;
import com.project.group2.phms.model.Snacks;
import com.project.group2.phms.model.Vitals;
import com.project.group2.phms.preferences.Preferences;
import com.satsuware.usefulviews.LabelledSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by vishwath on 2/13/17.
 */

public class HomeFragment extends Fragment implements View.OnClickListener {
    Toolbar toolbar;
    RecyclerView recyclerView;
    SearchView searchView;
    DatabaseReference vitalsReference, medicationReference, notesReference, appointmentsReference;
    Switch filterSwitch;
    LinearLayout filterLayout, homeCardsLayout;
    LabelledSpinner categorySpinner;
    TextInputLayout fromDateLayout, toDateLayout, keywordLayout, nameLayout;
    TextInputEditText fromDateEditText, toDateEditText, keywordEditText, nameEditText;
    FloatingActionButton searchButton;
    String category = "vitals";
    JSONObject jsonObject;
    ArrayList<String> medicationList = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    AutoCompleteTextView medAutoTextView;
    private static final String TAG_DATA = "results";
    private static final String TAG_NAME = "term";
    private static final String MAP_API_URL = "https://api.fda.gov/drug/label.json?count=openfda.brand_name.exact&limit=1000";

    public HomeFragment() {

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
        toolbar.setVisibility(View.GONE);
        hideKeyboard();
        filterSwitch = (Switch) view.findViewById(R.id.filterSwitch);
        filterLayout = (LinearLayout) view.findViewById(R.id.filterLayout);
        searchView = (SearchView) view.findViewById(R.id.searchView);
        categorySpinner = (LabelledSpinner) view.findViewById(R.id.categorySpinner);
        fromDateLayout = (TextInputLayout) view.findViewById(R.id.fromDateLayout);
        fromDateEditText = (TextInputEditText) view.findViewById(R.id.fromDateEditText);
        toDateLayout = (TextInputLayout) view.findViewById(R.id.toDateLayout);
        toDateEditText = (TextInputEditText) view.findViewById(R.id.toDateEditText);
        searchButton = (FloatingActionButton) view.findViewById(R.id.searchButton);
        recyclerView = (RecyclerView) view.findViewById(R.id.commonRecyclerView);
        medAutoTextView = (AutoCompleteTextView) view.findViewById(R.id.medicationAutoTextView);
        homeCardsLayout = (LinearLayout) view.findViewById(R.id.homeCards);
        keywordLayout = (TextInputLayout) view.findViewById(R.id.keywordLayout);
        keywordEditText = (TextInputEditText) view.findViewById(R.id.keywordEditText);
        nameLayout = (TextInputLayout) view.findViewById(R.id.nameLayout);
        nameEditText = (TextInputEditText) view.findViewById(R.id.nameEditText);
        recyclerView.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String userId = sharedPreferences.getString(Preferences.USERID, null);
        if (userId != null) {
            vitalsReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("vitals");
            medicationReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("medications");
            notesReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("notes");
            appointmentsReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("appointments");
        }

        filterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    filterLayout.setVisibility(View.VISIBLE);
                } else {
                    filterLayout.setVisibility(View.GONE);
                }
            }
        });

        categorySpinner.setOnItemChosenListener(new LabelledSpinner.OnItemChosenListener() {
            @Override
            public void onItemChosen(View labelledSpinner, AdapterView<?> adapterView, View itemView, int position, long id) {
                category = adapterView.getSelectedItem().toString().toLowerCase();
                if (category.equalsIgnoreCase("medication")) {
                    medAutoTextView.setVisibility(View.VISIBLE);
                } else {
                    medAutoTextView.setVisibility(View.GONE);
                }
                if (category.equalsIgnoreCase("notes")) {
                    keywordLayout.setVisibility(View.VISIBLE);
                } else {
                    keywordLayout.setVisibility(View.GONE);
                }
                if (category.equalsIgnoreCase("appointments")) {
                    nameLayout.setVisibility(View.VISIBLE);
                } else {
                    nameLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingChosen(View labelledSpinner, AdapterView<?> adapterView) {
                category = adapterView.getSelectedItem().toString().toLowerCase();
                if (category.equalsIgnoreCase("medication")) {
                    medAutoTextView.setVisibility(View.VISIBLE);
                } else {
                    medAutoTextView.setVisibility(View.GONE);
                }
                if (category.equalsIgnoreCase("notes")) {
                    keywordLayout.setVisibility(View.VISIBLE);
                } else {
                    keywordLayout.setVisibility(View.GONE);
                }
            }
        });

        buildMedicationNamesDropdown();

        fromDateEditText.setOnClickListener(this);
        toDateEditText.setOnClickListener(this);
        searchButton.setOnClickListener(this);

        fromDateEditText.addTextChangedListener(new MyTextWatcher(fromDateEditText));
        toDateEditText.addTextChangedListener(new MyTextWatcher(toDateEditText));
        medAutoTextView.addTextChangedListener(new MyTextWatcher(medAutoTextView));
        keywordEditText.addTextChangedListener(new MyTextWatcher(keywordEditText));
        nameEditText.addTextChangedListener(new MyTextWatcher(nameEditText));

        searchViewSetup();
        initControls(view);
        return view;
    }

    private void buildMedicationNamesDropdown() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(MAP_API_URL)
                .addHeader("cache-control", "no-cache")
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getContext(), "Connection Error", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    try {
                        jsonObject = new JSONObject(responseData);
                        JSONArray medicationArray = jsonObject.getJSONArray(TAG_DATA);
                        for (int i = 0; i < medicationArray.length(); i++) {
                            JSONObject medNames = medicationArray.getJSONObject(i);
                            String medName = medNames.getString(TAG_NAME);
                            medicationList.add(medName);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, medicationList);
                            medAutoTextView.setAdapter(adapter);
                        }
                    });
                }
            }

        });
    }

    private void searchViewSetup() {
        SearchManager searchManager = (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setQueryHint("Search Here");
        searchView.setIconifiedByDefault(false);
        searchView.setPadding(0, 0, 0, 0);
        searchView.setPaddingRelative(0, 0, 0, 0);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                hideKeyboard();
                String function = query.toLowerCase();
                if (function.contains("vitals")) {
                    searchVitalsFunction(query);
                }
                if (function.contains("medication")) {
                    searchMedicationFunction(query);
                }
                if (function.contains("appointments")) {
                    searchAppointmentsFunction(query);
                }
                if (function.contains("notes")) {
                    searchNotesFunction(query);

                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (query.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    homeCardsLayout.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });
    }

    private void searchAppointmentsFunction(final String query) {

        final ArrayList<Appointments> appointmentsList = new ArrayList<>();
        final RecyclerView.Adapter mAdapter = new AppointmentsAdapter(getContext(), appointmentsList);
        final List<String> queryList = new ArrayList<>(Arrays.asList(query.split(" ")));
        if (!filterSwitch.isChecked()) {

        } else {

            appointmentsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Appointments appointments = snapshot.getValue(Appointments.class);
                        String key = snapshot.getKey();
                        appointments.setKey(key);
                        if (queryList.contains("from") || queryList.contains("to") || queryList.contains("on")) {

                            if (queryList.contains("from") && queryList.contains("to")) {
                                Date fromDate = dateFormatter(queryList.get(queryList.indexOf("from") + 1) + "-2017");
                                Date toDate = dateFormatter(queryList.get(queryList.indexOf("to") + 1) + "-2017");
                                Date appointmentsDate = dateFormatter(appointments.getAppointmentDate());
                                if (appointmentsDate.after(fromDate) && appointmentsDate.before(toDate)) {
                                    appointmentsList.add(appointments);
                                }
                            } else if (queryList.contains("from") && !queryList.contains("to")) {
                                Date fromDate = dateFormatter(queryList.get(queryList.indexOf("from") + 1) + "-2017");
                                Date appointmentsDate = dateFormatter(appointments.getAppointmentDate());
                                if (appointmentsDate.after(fromDate)) {
                                    appointmentsList.add(appointments);
                                }
                            } else if (!queryList.contains("from") && queryList.contains("on")) {
                                Date toDate = dateFormatter(queryList.get(queryList.indexOf("on") + 1) + "-2017");
                                Date appointmentsDate = dateFormatter(appointments.getAppointmentDate());
                                if (toDate.equals(appointmentsDate)) {
                                    appointmentsList.add(appointments);
                                }
                            }

                        } else {

                            if (queryList.size() == 1) {
                                appointmentsList.add(appointments);

                            } else {
                                String doctorName = "";
                                for (int i = 1; i < queryList.size(); i++) {
                                    doctorName = doctorName + queryList.get(i);
                                }
                                if (doctorName.equalsIgnoreCase(appointments.getDoctorName())){
                                    appointmentsList.add(appointments);
                                }

                            }
                        }
                    }
                    if (appointmentsList.size() == 0) {
                        Toast.makeText(getContext(), "No records found!", Toast.LENGTH_LONG).show();
                    } else {
                        homeCardsLayout.setVisibility(View.GONE);
                    }
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(mAdapter);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }

    private void searchNotesFunction(final String query) {
        final ArrayList<Notes> notesList = new ArrayList<>();
        final RecyclerView.Adapter mAdapter = new NotesAdapter(getContext(), notesList);
        final List<String> queryList = new ArrayList<>(Arrays.asList(query.split(" ")));
        if (!filterSwitch.isChecked()) {

        } else {

            notesReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("enter", query);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Notes notes = snapshot.getValue(Notes.class);
                        String key = snapshot.getKey();
                        notes.setKey(key);
                        if (queryList.contains("from") || queryList.contains("to") || queryList.contains("on")) {

                            if (queryList.contains("from") && queryList.contains("to")) {
                                Date fromDate = dateFormatter(queryList.get(queryList.indexOf("from") + 1) + "-2017");
                                Date toDate = dateFormatter(queryList.get(queryList.indexOf("to") + 1) + "-2017");
                                Date notesDate = dateFormatter(notes.getDate());
                                if (notesDate.after(fromDate) && notesDate.before(toDate)) {
                                    notesList.add(notes);
                                }
                            } else if (queryList.contains("from") && !queryList.contains("to")) {
                                Date fromDate = dateFormatter(queryList.get(queryList.indexOf("from") + 1) + "-2017");
                                Date notesDate = dateFormatter(notes.getDate());
                                if (notesDate.after(fromDate)) {
                                    notesList.add(notes);
                                }
                            } else if (!queryList.contains("from") && queryList.contains("on")) {
                                Date toDate = dateFormatter(queryList.get(queryList.indexOf("on") + 1) + "-2017");
                                Date notesDate = dateFormatter(notes.getDate());
                                if (toDate.equals(notesDate)) {
                                    notesList.add(notes);
                                }
                            }

                        } else {

                            if (queryList.size() == 1) {
                                notesList.add(notes);

                            } else {
                                String keyword = "";
                                for (int i = 1; i < queryList.size(); i++) {
                                    keyword = keyword + queryList.get(i);
                                }
                                final List<String> keyList = new ArrayList<>(Arrays.asList(keyword.toLowerCase().split(" ")));
                                final List<String> titleList = new ArrayList<>(Arrays.asList(notes.getTitle().toLowerCase().split(" ")));
                                for (String keyText : keyList) {
                                    if (titleList.contains(keyText)) {
                                        notesList.add(notes);
                                    }
                                }
                            }
                        }
                    }
                    if (notesList.size() == 0) {
                        Toast.makeText(getContext(), "No records found!", Toast.LENGTH_LONG).show();
                    } else {
                        homeCardsLayout.setVisibility(View.GONE);
                    }
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(mAdapter);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    private void searchVitalsFunction(final String query) {
        final ArrayList<Vitals> vitalsList = new ArrayList<>();
        final RecyclerView.Adapter mAdapter = new VitalsAdapter(getContext(), vitalsList);
        final List<String> queryList = new ArrayList<>(Arrays.asList(query.split(" ")));
        if (!filterSwitch.isChecked()) {
            vitalsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Vitals vitals = snapshot.getValue(Vitals.class);
                        String key = snapshot.getKey();
                        vitals.setKey(key);
                        if (queryList.contains("on") && queryList.size() > 3) {
                            String date;
                            int onIndex = queryList.indexOf("on");
                            if (TextUtils.isDigitsOnly(queryList.get(onIndex + 1))) {
                                date = queryList.get(onIndex + 1) + "-" + queryList.get(onIndex + 2) + "-2017";
                            } else {
                                date = queryList.get(onIndex + 2) + "-" + queryList.get(onIndex + 1) + "-2017";
                            }

                            if (vitals.getDate().toLowerCase().equals(date)) {
                                vitalsList.add(vitals);
                            }
                        } else {

                            vitalsList.add(vitals);
                        }
                    }
                    if (vitalsList.size() == 0) {
                        Toast.makeText(getContext(), "No records found!", Toast.LENGTH_LONG).show();
                    }
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(mAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            registerForContextMenu(recyclerView);

        } else {

            vitalsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Vitals vitals = snapshot.getValue(Vitals.class);
                        String key = snapshot.getKey();
                        vitals.setKey(key);
                        if (queryList.contains("from") && queryList.contains("to")) {
                            Date fromDate = dateFormatter(queryList.get(queryList.indexOf("from") + 1) + "-2017");
                            Date toDate = dateFormatter(queryList.get(queryList.indexOf("to") + 1) + "-2017");
                            Date vitalsDate = dateFormatter(vitals.getDate());
                            if (vitalsDate.after(fromDate) && vitalsDate.before(toDate)) {
                                vitalsList.add(vitals);
                            }
                        } else if (queryList.contains("from") && !queryList.contains("to")) {
                            Date fromDate = dateFormatter(queryList.get(queryList.indexOf("from") + 1) + "-2017");
                            Date vitalsDate = dateFormatter(vitals.getDate());
                            if (vitalsDate.after(fromDate)) {
                                vitalsList.add(vitals);
                            }
                        } else if (!queryList.contains("from") && queryList.contains("on")) {
                            Date toDate = dateFormatter(queryList.get(queryList.indexOf("on") + 1) + "-2017");
                            Date vitalsDate = dateFormatter(vitals.getDate());
                            if (toDate.equals(vitalsDate)) {
                                vitalsList.add(vitals);
                            }
                        } else if (!queryList.contains("from") && !queryList.contains("to")) {
                            vitalsList.add(vitals);
                        }
                    }
                    if (vitalsList.size() == 0) {
                        Toast.makeText(getContext(), "No records found!", Toast.LENGTH_LONG).show();
                    } else {
                        homeCardsLayout.setVisibility(View.GONE);
                    }
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(mAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    public Date dateFormatter(String d) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
        Date date = new Date();
        try {
            date = simpleDateFormat.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public void searchMedicationFunction(String query) {
        final ArrayList<Medication> medicationList = new ArrayList<>();
        final RecyclerView.Adapter mAdapter = new MedicationsAdapter(getContext(), medicationList);
        final List<String> queryList = new ArrayList<>(Arrays.asList(query.split(" ")));
        if (!filterSwitch.isChecked()) {
            medicationReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Medication medication = snapshot.getValue(Medication.class);
                        String key = snapshot.getKey();
                        medication.setKey(key);
                        if (queryList.contains("on") && queryList.size() > 3) {
                            String date;
                            int onIndex = queryList.indexOf("on");
                            if (TextUtils.isDigitsOnly(queryList.get(onIndex + 1))) {
                                date = queryList.get(onIndex + 1) + "-" + queryList.get(onIndex + 2) + "-2017";
                            } else {
                                date = queryList.get(onIndex + 2) + "-" + queryList.get(onIndex + 1) + "-2017";
                            }

                            if (medication.getDateMed().toLowerCase().equals(date)) {
                                medicationList.add(medication);
                            }
                        } else {

                            medicationList.add(medication);
                        }
                    }
                    if (medicationList.size() == 0) {
                        Toast.makeText(getContext(), "No records found!", Toast.LENGTH_LONG).show();
                    }
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(mAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            registerForContextMenu(recyclerView);

        } else {

            medicationReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Medication medication = snapshot.getValue(Medication.class);
                        String key = snapshot.getKey();
                        medication.setKey(key);
                        if (queryList.contains("from") || queryList.contains("to") || queryList.contains("on")) {
                            if (queryList.contains("from") && queryList.contains("to")) {
                                Date fromDate = dateFormatter(queryList.get(queryList.indexOf("from") + 1) + "-2017");
                                Date toDate = dateFormatter(queryList.get(queryList.indexOf("to") + 1) + "-2017");
                                Date medicationDate = dateFormatter(medication.getDateMed());
                                if (medicationDate.after(fromDate) && medicationDate.before(toDate)) {
                                    medicationList.add(medication);
                                }
                            } else if (queryList.contains("from") && !queryList.contains("to")) {
                                Date fromDate = dateFormatter(queryList.get(queryList.indexOf("from") + 1) + "-2017");
                                Date medicationDate = dateFormatter(medication.getDateMed());
                                if (medicationDate.after(fromDate)) {
                                    medicationList.add(medication);
                                }
                            } else if (!queryList.contains("from") && queryList.contains("on")) {
                                Date toDate = dateFormatter(queryList.get(queryList.indexOf("on") + 1) + "-2017");
                                Date medicationDate = dateFormatter(medication.getDateMed());
                                if (toDate.equals(medicationDate)) {
                                    medicationList.add(medication);
                                }
                            }
                        } else {
                            if (queryList.size() == 1) {
                                medicationList.add(medication);

                            } else {
                                String medicationName = "";
                                for (int i = 1; i < queryList.size(); i++) {
                                    medicationName = medicationName + queryList.get(i);
                                }
                                if (medicationName.equalsIgnoreCase(medication.getMedicationName())) {
                                    medicationList.add(medication);
                                }
                            }
                        }
                    }
                    if (medicationList.size() == 0) {
                        Toast.makeText(getContext(), "No records found!", Toast.LENGTH_LONG).show();
                    } else {
                        homeCardsLayout.setVisibility(View.GONE);
                    }
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(mAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }


    public void initControls(View view) {

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
                Fragment DietFragment = new DietFragment();
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


    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getActivity().getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(getContext());
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onClick(View view) {
        int mYear;
        int mMonth;
        int mDay;
        if (view == fromDateEditText) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            fromDateEditText.setText("");


            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year, monthOfYear, dayOfMonth);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMMM");
                            String startDate = dateFormat.format(calendar.getTime());
                            fromDateEditText.setText(startDate);


                        }
                    }, mYear, mMonth, mDay);
//            datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
            datePickerDialog.show();
        }
        if (view == toDateEditText) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            toDateEditText.setText("");

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year, monthOfYear, dayOfMonth);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMMM");
                            String endDate = dateFormat.format(calendar.getTime());
                            toDateEditText.setText(endDate);

                        }
                    }, mYear, mMonth, mDay);
//            datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
            datePickerDialog.show();
        }
        if (view == searchButton) {
            String fromDate = fromDateEditText.getText().toString();
            String toDate = toDateEditText.getText().toString();
            String query = "";
            if (TextUtils.isEmpty(fromDate) && TextUtils.isEmpty(toDate)) {
                query = category;
            } else if (!TextUtils.isEmpty(fromDate) && TextUtils.isEmpty(toDate)) {
                query = category + " from " + fromDate;
            } else if (!TextUtils.isEmpty(fromDate) && !TextUtils.isEmpty(toDate)) {
                query = category + " from " + fromDate + " to " + toDate;
            } else if (TextUtils.isEmpty(fromDate) && !TextUtils.isEmpty(toDate)) {
                query = category + " on " + toDate;
            }
            if (category.equalsIgnoreCase("medication")) {
                if (medAutoTextView.isEnabled()) {
                    query = category + " " + medAutoTextView.getText().toString();
                }
            }
            if (category.equalsIgnoreCase("notes")) {
                if (keywordEditText.isEnabled()) {
                    query = category + " " + keywordEditText.getText().toString();
                }
            }
            if (category.equalsIgnoreCase("appointments")) {
                if (nameEditText.isEnabled()) {
                    query = category + " " + nameEditText.getText().toString();
                }
            }
            fromDateEditText.setEnabled(true);
            toDateEditText.setEnabled(true);
            searchView.setQuery(query, true);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.fromDateEditText:
                    keywordEditText.setEnabled(false);
                    nameEditText.setEnabled(false);
                    medAutoTextView.setEnabled(false);
                    fromDateEditText.setEnabled(true);
                    toDateEditText.setEnabled(true);
                    if (fromDateEditText.getText().toString().isEmpty() && toDateEditText.getText().toString().isEmpty()) {
                        medAutoTextView.setEnabled(true);
                        keywordEditText.setEnabled(true);
                        nameEditText.setEnabled(true);
                    }
                    break;
                case R.id.toDateEditText:
                    keywordEditText.setEnabled(false);
                    nameEditText.setEnabled(false);
                    medAutoTextView.setEnabled(false);
                    fromDateEditText.setEnabled(true);
                    toDateEditText.setEnabled(true);
                    if (fromDateEditText.getText().toString().isEmpty() && toDateEditText.getText().toString().isEmpty()) {
                        medAutoTextView.setEnabled(true);
                        keywordEditText.setEnabled(true);
                        nameEditText.setEnabled(true);
                    }
                    break;
                case R.id.medicationAutoTextView:
                    medAutoTextView.setEnabled(true);
                    fromDateEditText.setEnabled(false);
                    toDateEditText.setEnabled(false);
                    if (medAutoTextView.getText().toString().isEmpty()) {
                        fromDateEditText.setEnabled(true);
                        toDateEditText.setEnabled(true);
                    }
                    break;

                case R.id.keywordEditText:
                    keywordEditText.setEnabled(true);
                    fromDateEditText.setEnabled(false);
                    toDateEditText.setEnabled(false);
                    if (keywordEditText.getText().toString().isEmpty()) {
                        fromDateEditText.setEnabled(true);
                        toDateEditText.setEnabled(true);
                    }
                    break;

                case R.id.nameEditText:
                    nameEditText.setEnabled(true);
                    fromDateEditText.setEnabled(false);
                    toDateEditText.setEnabled(false);
                    if (nameEditText.getText().toString().isEmpty()) {
                        fromDateEditText.setEnabled(true);
                        toDateEditText.setEnabled(true);
                    }
                    break;


            }
        }
    }
}
