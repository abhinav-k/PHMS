package com.project.group2.phms.activities;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.project.group2.phms.R;
import com.project.group2.phms.model.DesigneeDoctor;
import com.project.group2.phms.model.Medication;
import com.project.group2.phms.preferences.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by vishwath on 2/14/17.
 */

public class MedicationActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.startDateLayout)
    protected TextInputLayout startDateLayout;
    @BindView(R.id.medicationDosageInputText)
    protected TextInputLayout medicationDosageInputText;
    @BindView(R.id.medicationDosageEditText)
    protected TextInputEditText medicationDosageEditText;
    // TODO: 4/11/17 Added totalQuantityField to delibarately flag medication taken - start
    @BindView(R.id.totalQuantityInputLayout)
    protected TextInputLayout totalQuantityInputLayout;
    @BindView(R.id.totalQuantityEditText)
    protected TextInputEditText totalQuantityEditText;
    // TODO: 4/11/17 Added totalQuantityField to delibarately flag medication taken - End
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
    @BindView(R.id.frequencyDaysLayout)
    protected TextInputLayout frequencyDaysLayout;
    @BindView(R.id.medication_key)
    protected TextView medication_keyTextView;
    @BindView(R.id.medicationAutoTextView)
    protected AutoCompleteTextView medAutoTextView;
    @BindView(R.id.endDateLayout)
    protected TextInputLayout endDateLayout;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference, designeeReference;
    ValueEventListener valueEventListener;

    Medication medication;

    ArrayList<String> medicationList = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    HashMap<String, String> conflictsMap;
    ArrayList<String> medList;

    JSONObject jsonObject;
    // TODO: 4/11/17  Declared medicationKey as global to pass the key to the medicationNotificationActivity
    String medicationKey = null;

    //JSON_URL node information
    private static final String TAG_DATA = "results";
    private static final String TAG_NAME = "term";
    private static final String MAP_API_URL = "https://api.fda.gov/drug/label.json?count=openfda.brand_name.exact&limit=1000";
    HashMap<String, String> medicationsMap;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_add);
        ButterKnife.bind(this);


        setSupportActionBar(toolbar);
        Intent intent = getIntent();

        //Changes after Sprint 2 - Ramji
        buildMedicationNamesDropdown();
        medicationsMap = new HashMap<>();
        conflictsMap = new HashMap<>();
        medList = new ArrayList<>();
        createConflictsMap();
        // TODO: 4/11/17 Change
        medicationKey = null;

        if (intent != null) {
            medicationKey = intent.getStringExtra("medications_key");
        }


        if (getUid() != null) {
            String userId = getUid();
            firebaseAuth = FirebaseAuth.getInstance();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("medications");
            designeeReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("doctor_and_designee");

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
                        medAutoTextView.setText(medication.getMedicationName());
                        medicationDosageEditText.setText(medication.getDosage());
                        // TODO: 4/11/17 Added totalQuantity
                        totalQuantityEditText.setText(medication.getTotalQuantity());
                        initialTimeEditText.setText(medication.getInitialTime());
                        startDateEditText.setText(medication.getStartDate());
                        endDateEditText.setText(medication.getEndDate());
                        frequencyDaysEditText.setText(medication.getFrequency().substring(6, 7));
                        frequencySpinner.setSelection(frequencySelector(medication.getFrequency().substring(8)));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            };

            databaseReference.addValueEventListener(valueEventListener);
        }


        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeMedications();
            }
        });

        startDateEditText.setOnClickListener(this);
        endDateEditText.setOnClickListener(this);
        initialTimeEditText.setOnClickListener(this);


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Medication med = snapshot.getValue(Medication.class);
                    Log.d("med", med.getMedicationName());
                    medList.add(med.getMedicationName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void createConflictsMap() {
        conflictsMap.put("conflict1", "Gabapentin-Aspirin");
        conflictsMap.put("conflict2", "Aspirin-Gabapentin");
        conflictsMap.put("conflict3", "Diphenhydramine Hydrochloride-azithromycin");
        conflictsMap.put("conflict4", "azithromycin-Diphenhydramine Hydrochloride");
        conflictsMap.put("conflict5", "Alprazolam-Oxycodone Hydrochloride");
        conflictsMap.put("conflict6", "Oxycodone Hydrochloride-Alprazolam");
    }

    public void buildMedicationNamesDropdown() {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(MAP_API_URL)
                .addHeader("cache-control", "no-cache")
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(MedicationActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
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

                MedicationActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new ArrayAdapter<>(MedicationActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, medicationList);
                        medAutoTextView.setAdapter(adapter);
                    }
                });
            }

        });
    }

    //Changes after Sprint 2 - Populate medicationName spinner - End

    public int frequencySelector(String frequency) {

        if (frequency.equalsIgnoreCase("Hours")) {
            return 0;
        } else if (frequency.equalsIgnoreCase("Days")) {
            return 1;
        } else if (frequency.equalsIgnoreCase("Weeks")) {
            return 2;
        } else if (frequency.equalsIgnoreCase("Months")) {
            return 3;
        } else {
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
                            // TODO: 4/11/17 Deleted the filter for startDate - Added in validateForm() method
                            startDateEditText.setText(startDate);
                            //startDateEditText.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);


                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
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

                            if (hourOfDay > 12) {
                                hourString = String.valueOf(hourOfDay - 12);
                            }

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
        final String medicationName = medAutoTextView.getText().toString();
        final String dosage = medicationDosageEditText.getText().toString().trim();
        // TODO: 4/11/17 totalQuantity
        final String totalQuantity = totalQuantityEditText.getText().toString().trim();
        final String initialTime = initialTimeEditText.getText().toString().trim();
        final String startDate = startDateEditText.getText().toString().trim();
        final String endDate = endDateEditText.getText().toString().trim();
        final String frequencyDays = frequencyDaysEditText.getText().toString().trim();
        final String frequency = frequencySpinner.getSelectedItem().toString();

        //// TODO: 4/11/17 Passed totalQuantity
        if (!validateForm(medicationName, dosage, totalQuantity, initialTime, startDate, endDate, frequencyDays)) {
            hideProgressDialog();
            return;
        }
        medicationsMap.clear();
        medicationsMap.put("medicationName", medicationName);
        medicationsMap.put("dosage", dosage);
        // TODO: 4/11/17 Added totalQuantity
        medicationsMap.put("totalQuantity", totalQuantity);
        medicationsMap.put("initialTime", initialTime);
        medicationsMap.put("startDate", startDate);
        medicationsMap.put("endDate", endDate);
        medicationsMap.put("frequency", "Every " + frequencyDays + " " + frequency);
        if (medication_keyTextView.getText().toString().equals("")) {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String formattedDate = df.format(c.getTime());
            medicationsMap.put("dateMed", formattedDate);
            // TODO: 4/11/17 Added Alarm notifications
//            setAlarm(medicationName, initialTime, startDate,endDate, Integer.parseInt(frequencyDays), frequency);
        } else {
            medicationsMap.put("dateMed", medication.getDateMed());
            // TODO: 4/11/17 Added Alarm notifications
//            setAlarm(medicationName, initialTime, startDate,endDate, Integer.parseInt(frequencyDays), frequency);
        }
        boolean conflict = false;
        String oldMed = "";
        for (String med : medList) {
            String c = med + "-" + medicationName;
            if (conflictsMap.containsValue(c)) {
                conflict = true;
                oldMed = med;
                break;
            }
            oldMed = med;
        }
        if (conflict) {
            AlertDialog.Builder alertDialogBuilder;
            alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Warning");
            alertDialogBuilder.setIcon(R.mipmap.ic_warning_black_24dp);
            alertDialogBuilder.setMessage(medicationName + " may cause health effects when taken with " + oldMed + ". Do " +
                    "you want to still add it ?");
            final String finalOldMed = oldMed;
            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    designeeReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            DesigneeDoctor designeeDoctor = dataSnapshot.getValue(DesigneeDoctor.class);
                            // TODO: 4/11/17 Added null check
                            if (designeeDoctor != null) {
                                String designeeEmail = designeeDoctor.getDesigneeEmail();
                                String doctorEmail = designeeDoctor.getDoctorEmail();
                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MedicationActivity.this);
                                String fullName = sharedPreferences.getString(Preferences.NAME, "");
                                String subject = "Warning!";
                                String body = "Hi,\nThis email is to inform you that Mr." + fullName + " has added " + finalOldMed + " and " + medicationName
                                        + " to the list of medications to be taken. It is advised to contact " + fullName + " immediately.\n\nRegards,\nPHMS.";
                                if (!TextUtils.isEmpty(designeeEmail) || !TextUtils.isEmpty(doctorEmail)) {
                                    BackgroundMail.newBuilder(MedicationActivity.this)
                                            .withUsername("phmsgroup2@gmail.com")
                                            .withPassword("science100")
                                            .withMailto(designeeEmail + "," + doctorEmail)
                                            .withType(BackgroundMail.TYPE_PLAIN)
                                            .withSubject(subject)
                                            .withBody(body)
                                            .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                                                @Override
                                                public void onSuccess() {
                                                    //do some magic
                                                    Log.d("Email", "Sent Success");
                                                    dbPushFunction(medicationName, initialTime, startDate, endDate, frequencyDays, frequency);
                                                }
                                            })
                                            .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                                                @Override
                                                public void onFail() {
                                                    //do some magic
                                                }
                                            })
                                            .send();
                                } else {
                                    Toast.makeText(MedicationActivity.this, "Add designee and doctor contact details", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(MedicationActivity.this, "Add designee and doctor contact details", Toast.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
            });
            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    hideProgressDialog();

                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            alertDialog.setCancelable(false);
        } else {
            dbPushFunction(medicationName, initialTime, startDate, endDate, frequencyDays, frequency);
        }
    }

    // TODO: 4/11/17 Added set Alarm function to handle alarms - Start
    public void setAlarm(String medicationName, String initialTime, String startDate, String endDate, int frequencyDays, String frequency, String key) {
        //Long alertTime = new GregorianCalendar().getTimeInMillis()+5*1000;

        int startDateday = 0;
        int startDatemonth = 0;
        int startDateyear = 0;

        int endDateDay = 0;
        int endDateMonth = 0;
        int endDateYear = 0;
        String[] startDateArray = startDate.split("-");
        String[] endDateArray = endDate.split("-");

        if (startDateArray.length > 0) {
            startDateday = Integer.parseInt(startDateArray[0]);
            startDatemonth = convertMonthToInt(startDateArray[1]);
            Log.d("Month value is :", String.valueOf(startDatemonth));
            Log.d("Date Array", startDateArray[1]);
            startDateyear = Integer.parseInt(startDateArray[2]);
        }

        if (endDateArray.length > 0) {
            endDateDay = Integer.parseInt(endDateArray[0]);
            endDateMonth = convertMonthToInt(endDateArray[1]);
            endDateYear = Integer.parseInt(endDateArray[2]);
        }

        String[] timeArray = initialTime.split(":");
        int hours = 0;
        int minutes = 0;
        String amPm = "AM";
        if (timeArray.length > 0) {
            hours = Integer.parseInt(timeArray[0]);
            String[] array2 = timeArray[1].split(" ");
            minutes = Integer.parseInt(array2[0]);
            amPm = array2[1];
        }


        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, hours);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.AM_PM, amPm.equalsIgnoreCase("AM") ? 0 : 1);
        calendar.set(Calendar.DAY_OF_MONTH, startDateday);
        calendar.set(Calendar.MONTH, startDatemonth);
        calendar.set(Calendar.YEAR, startDateyear);

        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.DAY_OF_MONTH, endDateDay);
        calendar1.set(Calendar.MONTH, endDateMonth);
        calendar1.set(Calendar.YEAR, endDateYear);


        Log.d("Milli", String.valueOf(calendar.getTimeInMillis()));

        Intent alertIntent = new Intent(this, AlertReceiver.class);
        alertIntent.putExtra("medName", medicationName);
        alertIntent.putExtra("key", key);
//        Log.d("key",medicationKey);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                PendingIntent.getBroadcast(this, (int) (calendar.getTimeInMillis() % 2147483646), alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        long startTimeMilliSec = calendar.getTimeInMillis();
        long endTimeInMilliSec = calendar1.getTimeInMillis() + 1000 * 60 * 60 * 24;
        long mulFactor = getMultiplicationFactor(frequency) * frequencyDays;
        for (long frequencyAlarm = startTimeMilliSec + mulFactor; frequencyAlarm < endTimeInMilliSec; frequencyAlarm = frequencyAlarm + mulFactor) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, frequencyAlarm,
                    PendingIntent.getBroadcast(this, (int) frequencyAlarm % 2147483646, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        }

    }
    // TODO: 4/11/17 Added set Alarm function to handle alarms - End

    public void dbPushFunction(String medicationName, String initialTime, String startDate, String endDate, String frequencyDays, String frequency) {
        DatabaseReference keyRef;
        String key = "";
        if (medication_keyTextView.getText().toString().equals("")) {
            keyRef = databaseReference.push();
            keyRef.setValue(medicationsMap);
            key = keyRef.getKey();
            hideProgressDialog();
            Toast.makeText(MedicationActivity.this, "Medications saved!", Toast.LENGTH_SHORT).show();
        } else {
            databaseReference.child(medication_keyTextView.getText().toString()).updateChildren((java.util.HashMap) medicationsMap);
            key = medicationKey;
            hideProgressDialog();
            Toast.makeText(MedicationActivity.this, "Medications Updated!", Toast.LENGTH_SHORT).show();
        }
        Log.d("KEY", key);
        setAlarm(medicationName, initialTime, startDate, endDate, Integer.parseInt(frequencyDays), frequency, key);
        Intent intent = new Intent(MedicationActivity.this, PhmsActivity.class);
        intent.putExtra("medFlag", true);
        startActivity(intent);
        finish();
    }

    // TODO: 4/11/17 Added totalQuantity
    private boolean validateForm(String medicationName, String dosage, String totalQuantity, String initialTime, String startDate, String endDate, String frequencyDays) {
        boolean valid = true;
        if (TextUtils.isEmpty(medicationName) || TextUtils.isEmpty(dosage) || TextUtils.isEmpty(initialTime) || TextUtils.isEmpty(startDate) || TextUtils.isEmpty(endDate)) {
            return false;
        }
        Date startDateParsed = null;
        Date endDateParsed = null;
        int freqDays = Integer.parseInt(frequencyDays);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            startDateParsed = dateFormat.parse(startDate);
            endDateParsed = dateFormat.parse(endDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        // TODO: 4/11/17 Changed startDate check condtion to include current date 
        if (startDateParsed.before(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000))) {
            startDateLayout.setError("Date range should be within the date range");
            valid = false;
        } else {
            startDateLayout.setError(null);
        }
        if (endDateParsed.after(startDateParsed)) {
            endDateLayout.setError(null);
        } else {
            endDateLayout.setError("End date should be after Start Date");
            valid = false;
        }
        if ((freqDays <= 0) || freqDays > 30) {
            frequencyDaysLayout.setError("Enter a valid in the range of 1-30");
            valid = false;
        } else {
            frequencyDaysLayout.setError(null);
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

    // TODO: 4/11/17 Repeating alarms for Hours/Weeks/Months and also for Converting Months in the calendar - Start
    public long getMultiplicationFactor(String frequency) {
        long multiplicationFactor = 10;
        if (frequency.equalsIgnoreCase("Hours")) {
            multiplicationFactor = 1000 * 60 * 60;
        } else if (frequency.equalsIgnoreCase("Days")) {
            multiplicationFactor = 1000 * 60 * 60 * 24;
        } else if (frequency.equalsIgnoreCase("Weeks")) {
            multiplicationFactor = 1000 * 60 * 60 * 24 * 7;
        } else if (frequency.equalsIgnoreCase("Months")) {
            multiplicationFactor = 1000 * 60 * 60 * 24 * 31;
        }
        return multiplicationFactor;
    }

    public int convertMonthToInt(String mon) {
        int val = 4;
        val = (mon.equalsIgnoreCase("Jan")) ? 1 : val;
        val = (mon.equalsIgnoreCase("Feb")) ? 2 : val;
        val = (mon.equalsIgnoreCase("Mar")) ? 3 : val;
        val = (mon.equalsIgnoreCase("Apr")) ? 4 : val;
        val = (mon.equalsIgnoreCase("May")) ? 5 : val;
        val = (mon.equalsIgnoreCase("Jun")) ? 6 : val;
        val = (mon.equalsIgnoreCase("Jul")) ? 7 : val;
        val = (mon.equalsIgnoreCase("Aug")) ? 8 : val;
        val = (mon.equalsIgnoreCase("Sep")) ? 9 : val;
        val = (mon.equalsIgnoreCase("Oct")) ? 10 : val;
        val = (mon.equalsIgnoreCase("Nov")) ? 11 : val;
        val = (mon.equalsIgnoreCase("Dec")) ? 12 : val;
        return val - 1;
    }
    // TODO: 4/11/17 Repeating alarms for Hours/Weeks/Months and also for Converting Months in the calendar - End
}
