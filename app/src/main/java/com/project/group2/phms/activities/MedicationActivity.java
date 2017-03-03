package com.project.group2.phms.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.project.group2.phms.JSONUtils.BackGroundTask;
import com.project.group2.phms.R;
import com.project.group2.phms.model.Medication;
import com.satsuware.usefulviews.LabelledSpinner;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by vishwath on 2/14/17.
 */

public class MedicationActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.medicationNameSpinner)
    protected Spinner medicationNameSpinner;
    @BindView(R.id.medicationDosageInputText)
    protected TextInputLayout medicationDosageInputText;
    @BindView(R.id.medicationDosageEditText)
    protected TextInputEditText medicationDosageEditText;
    @BindView(R.id.initialTimeEditText)
    protected TextInputEditText initialTimeEditText;
    @BindView(R.id.startDateEditText)
    protected TextInputEditText startDateEditText;
    @BindView(R.id.endDateEditText)
    protected TextInputEditText endDateEditText;
    @BindView(R.id.frequencyDaysEditText)
    protected EditText frequencyDaysEditText;
    @BindView(R.id.frequencySpinner)
    protected Spinner frequencySpinner;
    @BindView(R.id.doneButton)
    protected FloatingActionButton doneButton;
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.medication_key)
    protected TextView medication_keyTextView;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;

    Medication medication;
    String medicationName = "Drug A";

    //Changes for populating the spinner with JSON data - Ramji
    ArrayList<String> medicationList=new ArrayList<String>();
    ArrayAdapter<String> adapter;

    //JSON_URL node information
    private static final String TAG_DATA = "results";
    private static final String TAG_NAME = "term";
    private static final String MAP_API_URL = "https://api.fda.gov/drug/label.json?count=openfda.brand_name.exact&limit=1000";
    private BackGroundTask backgroundTask;

    //Changes for populating the spinner with JSON data - Ramji End

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_medication_add);
        ButterKnife.bind(this);


        setSupportActionBar(toolbar);
        Intent intent = getIntent();

        //Changes after Sprint 2 - Ramji
        buildMedicationNamesDropdown();

        /*adapter=new ArrayAdapter<String>(this,
                R.layout.spinnertext,R.id.medicationNameText,medicationNameItems);
        medicationNameSpinner.setCustomAdapter(adapter);
        Changes after Sprint 2 - Ramji End */


        String medicationKey = null;

        if (intent != null) {
            medicationKey = intent.getStringExtra("medications_key");
        }

        if (getUid() != null) {
            String userId = getUid();
            firebaseAuth = FirebaseAuth.getInstance();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("medications");

        } else {

            onAuthFailure();
        }

        if (medicationKey == null) {
            medication_keyTextView.setText("");
        } else {
            final String medication_key_value = medicationKey;
            medication_keyTextView.setText(medicationKey);
            valueEventListener = new ValueEventListener() {
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DataSnapshot snapshot = dataSnapshot.child(medication_key_value);
                    medication = snapshot.getValue(Medication.class);
                    if (medication != null) {
                        medicationNameSpinner.setSelection(getIndex(medicationNameSpinner,medication.getMedicationName()));
                        medicationDosageEditText.setText(medication.getDosage());
                        initialTimeEditText.setText(medication.getInitialTime());
                        startDateEditText.setText(medication.getStartDate());
                        endDateEditText.setText(medication.getEndDate());
                        frequencyDaysEditText.setText(medication.getFrequency().substring(0, 1));
                        frequencySpinner.setSelection(frequencySelector(medication.getFrequency().substring(1)));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            };

            databaseReference.addValueEventListener(valueEventListener);
        }


      /* medicationNameSpinner.setLabelText(R.string.medicationName);
        //medicationNameSpinner.setItemsArray(R.array.medicationNameArray);
        medicationNameSpinner.setOnItemChosenListener(new LabelledSpinner.OnItemChosenListener() {
            @Override
            public void onItemChosen(View labelledSpinner, AdapterView<?> adapterView, View itemView, int position, long id) {

                medicationName = adapterView.getSelectedItem().toString();

            }

            @Override
            public void onNothingChosen(View labelledSpinner, AdapterView<?> adapterView) {
                medicationName = adapterView.getSelectedItem().toString();
            }
        });*/


        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeMedications();
            }
        });

        startDateEditText.setOnClickListener(this);
        endDateEditText.setOnClickListener(this);
        initialTimeEditText.setOnClickListener(this);
    }

    //Changes after Sprint 2 - Populate medicationName spinner - Start
    public void buildMedicationNamesDropdown() {
        List<NameValuePair> apiParams = new ArrayList<NameValuePair>(1);
        apiParams.add(new BasicNameValuePair("call", "medicationsList"));

        backgroundTask = new BackGroundTask(MAP_API_URL, "GET", apiParams);
        try{
            JSONObject medicationNamesJSON = backgroundTask.execute().get();
            JSONArray medicationNames = medicationNamesJSON.getJSONArray(TAG_DATA);

            for(int i=0 ; i< medicationNames.length() ; i++){
                JSONObject medNames = medicationNames.getJSONObject(i);
                String medName = medNames.getString(TAG_NAME);
                medicationList.add(medName);
                adapter = new ArrayAdapter<String>(this,R.layout.spinnertext,R.id.medicationNameText,medicationList);

                medicationNameSpinner.setAdapter(adapter);

                medicationNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private int getIndex(Spinner spinner, String myString) {

        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(myString)) {
                index = i;
            }
        }
        return index;
    }

    //Changes after Sprint 2 - Populate medicationName spinner - End

    public int frequencySelector(String frequency) {

        if (frequency.equalsIgnoreCase("Hours")) {
            return 0;
        } else if (frequency.equalsIgnoreCase("Days")) {
            return 1;
        } else if (frequency.equalsIgnoreCase("Weeks")) {
            return 2;
        } else if (medicationName.equalsIgnoreCase("Months")) {
            return 3;
        }else {
            return 1;
        }
    }

    @Override
    public void onClick(View view) {
        int mYear;
        int mMonth;
        int mDay;
        if (view == startDateEditText) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year, monthOfYear, dayOfMonth);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                            String startDate = dateFormat.format(calendar.getTime());
                            //startDateEditText.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            startDateEditText.setText(startDate);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (view == endDateEditText) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year, monthOfYear, dayOfMonth);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                            String endDate = dateFormat.format(calendar.getTime());
                            endDateEditText.setText(endDate);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (view == initialTimeEditText) {
            final Calendar c = Calendar.getInstance();
            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            String hourString;
                            if (hourOfDay < 10)
                                hourString = "0" + hourOfDay;
                            else
                                hourString = "" + hourOfDay;

                            String am_pm = (hourOfDay < 12) ? "AM" : "PM";

                            String minuteSting;
                            if (minute < 10)
                                minuteSting = "0" + minute;
                            else
                                minuteSting = "" + minute;
                            initialTimeEditText.setText(hourString + ":" + minuteSting + " " + am_pm);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }

    private void writeMedications() {
        showProgressDialog("Saving...");
        HashMap<String, String> medicationsMap = new HashMap<>();
        String medicationName = medicationNameSpinner.getSelectedItem().toString();
        String dosage = medicationDosageEditText.getText().toString().trim();
        String initialTime = initialTimeEditText.getText().toString().trim();
        String startDate = startDateEditText.getText().toString().trim();
        String endDate = endDateEditText.getText().toString().trim();
        String frequencyDays = frequencyDaysEditText.getText().toString().trim();
        String frequency = frequencySpinner.getSelectedItem().toString();

        if (!validateForm(medicationName, dosage, initialTime, startDate, endDate)) {
            hideProgressDialog();
            return;
        }
        medicationsMap.put("medicationName", medicationName);
        medicationsMap.put("dosage", dosage);
        medicationsMap.put("initialTime", initialTime);
        medicationsMap.put("startDate", startDate);
        medicationsMap.put("endDate", endDate);
        medicationsMap.put("frequency", frequencyDays + "" + frequency);
        if (medication_keyTextView.getText().toString().equals("")) {
            Calendar c = Calendar.getInstance();
            System.out.println("Current time => " + c.getTime());
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String formattedDate = df.format(c.getTime());

            medicationsMap.put("dateMed", formattedDate);

            databaseReference.push().setValue(medicationsMap);
            hideProgressDialog();

            Toast.makeText(this, "Medications saved!", Toast.LENGTH_SHORT).show();
        } else {
            medicationsMap.put("dateMed", medication.getDateMed());
            databaseReference.child(medication_keyTextView.getText().toString()).updateChildren((java.util.HashMap) medicationsMap);
            hideProgressDialog();
            Toast.makeText(this, "Medications Updated!", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(MedicationActivity.this, PhmsActivity.class);
        intent.putExtra("medFlag", true);
        startActivity(intent);
        finish();


    }

    private boolean validateForm(String medicationName, String dosage, String initialTime, String startDate, String endDate) {
        boolean valid = true;
        if (TextUtils.isEmpty(medicationName) || TextUtils.isEmpty(dosage) || TextUtils.isEmpty(initialTime) || TextUtils.isEmpty(startDate) || TextUtils.isEmpty(endDate)) {
            return false;
        }
        Date startDateParsed = null;
        Date endDateParsed = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            startDateParsed = dateFormat.parse(startDate);
            endDateParsed = dateFormat.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (endDateParsed.after(startDateParsed)) {
            endDateEditText.setError(null);
            valid = true;
        } else {
            endDateEditText.setError("End date should be after Start Date");
            valid = false;
        }
        return valid;
    }

    private void onAuthFailure() {
        // Write new user
        Intent intent = new Intent(MedicationActivity.this, SignInSignUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();

    }


    @Override
    public void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (firebaseAuth.getCurrentUser() == null) {
            onAuthFailure();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }


}
