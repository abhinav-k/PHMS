package com.project.group2.phms.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.project.group2.phms.R;
import com.project.group2.phms.model.Appointments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AppointmentsActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.doctorsNameInputLayout)
    protected TextInputLayout doctorNameInputLayout;
    @BindView(R.id.doctorsNameEditText)
    protected TextInputEditText doctorsNameEditText;
    @BindView(R.id.doctorSpecializationSpinner)
    protected Spinner doctorSpecializationSpinner;
    @BindView(R.id.doctorPhoneNumberInputLayout)
    protected TextInputLayout doctorPhoneNumberInputLayout;
    @BindView(R.id.doctorPhoneNumberEditText)
    protected TextInputEditText doctorPhoneNumberEditText;
    @BindView(R.id.doctorEmailInputLayout)
    protected TextInputLayout doctorEmailInputLayout;
    @BindView(R.id.doctorEmailEditText)
    protected TextInputEditText doctorEmailEditText;
    @BindView(R.id.appointmentDateInputLayout)
    protected TextInputLayout appointmentDateInputLayout;
    @BindView(R.id.appointmentDateEditText)
    protected TextInputEditText appointmentDateEditText;
    @BindView(R.id.appointmentTimeInputLayout)
    protected TextInputLayout appointmentTimeInputLayout;
    @BindView(R.id.appointmentTimeEditText)
    protected TextInputEditText appointmentTimeEditText;
    @BindView(R.id.purposeInputLayout)
    protected TextInputLayout purposeInputLayout;
    @BindView(R.id.purposeEditText)
    protected TextInputEditText purposeEditText;
    @BindView(R.id.prescriptionoutcomeTextInput)
    protected TextInputLayout prescriptionoutcomeTextInput;
    @BindView(R.id.prescriptionoutcomeEditText)
    protected TextInputEditText prescriptionoutcomeEditText;
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.appointments_key)
    protected TextView appoint_key_textView;
    @BindView(R.id.doneButton)
    protected FloatingActionButton doneButton;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;
    boolean firstTime = false;

    Appointments appointments=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            firstTime = extras.getBoolean("firstTime");

        }

        Intent intent = getIntent();
        String appointKey = null;

        if (intent != null) {
            appointKey = intent.getStringExtra("app_key");
        }


        if (getUid() != null) {
            String userId = getUid();
            firebaseAuth = FirebaseAuth.getInstance();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("appointments");

        } else {

            onAuthFailure();
        }

        if (appointKey == null) {
            appoint_key_textView.setText("");
        } else {
            final String appoint_key_value = appointKey;
            appoint_key_textView.setText(appointKey);
            valueEventListener = new ValueEventListener() {
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DataSnapshot snapshot = dataSnapshot.child(appoint_key_value);
                    appointments = snapshot.getValue(Appointments.class);
                    if (appointments != null) {
                        doctorsNameEditText.setText(appointments.getDoctorName());
                        doctorSpecializationSpinner.setSelection(selectSpec(appointments.getDoctorSpecialization()));
                        doctorPhoneNumberEditText.setText(appointments.getPhoneNumber());
                        doctorEmailEditText.setText(appointments.getEmailAddress());
                        appointmentDateEditText.setText(appointments.getAppointmentDate());
                        appointmentTimeEditText.setText(appointments.getAppointmentTime());
                        purposeEditText.setText(appointments.getPurpose());
                        prescriptionoutcomeEditText.setText(appointments.getPrescription());
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
            public void onClick(View v) {
                addAppointments();
            }
        });

        appointmentDateEditText.setOnClickListener(this);
        appointmentTimeEditText.setOnClickListener(this);

    }

    public int selectSpec(String specialization) {

        if (specialization.equalsIgnoreCase("Cardiologist")) {
            return 0;
        } else if (specialization.equalsIgnoreCase("Neurologist")) {
            return 1;
        } else if (specialization.equalsIgnoreCase("Oncologist")) {
            return 2;
        } else if (specialization.equalsIgnoreCase("Radiologist")) {
            return 3;
        } else if (specialization.equalsIgnoreCase("Orthologist")) {
            return 3;
        }else if (specialization.equalsIgnoreCase("General Practitioner")) {
            return 3;
        }else if (specialization.equalsIgnoreCase("Trauma")) {
            return 3;
        }else if (specialization.equalsIgnoreCase("Pediatrist")) {
            return 3;
        }else if (specialization.equalsIgnoreCase("Gynacologist")) {
            return 3;
        }else if (specialization.equalsIgnoreCase("Dermatologist")) {
            return 3;
        }else{
            return 0;
        }
    }

    private void addAppointments(){
        showProgressDialog("Saving...");
        HashMap<String, String> appointmentsMap = new HashMap<>();
         String doctorsName = doctorsNameEditText.getText().toString().trim();
         String doctorsSpecialization = doctorSpecializationSpinner.getSelectedItem().toString();
         String doctorPhoneNumber = doctorPhoneNumberEditText.getText().toString().trim();
         String doctorEmail = doctorEmailEditText.getText().toString().trim();
         String appointmentDate = appointmentDateEditText.getText().toString().trim();
         String appointmentTime = appointmentTimeEditText.getText().toString().trim();
         String purpose = purposeEditText.getText().toString().trim();
         String prescription = prescriptionoutcomeEditText.getText().toString().trim();

        if (!validateForm(doctorsName, doctorsSpecialization, doctorPhoneNumber, doctorEmail, appointmentDate,appointmentTime,purpose,prescription)) {
            hideProgressDialog();
            return;
        }
        appointmentsMap.put("doctorName", doctorsName);
        appointmentsMap.put("doctorSpecialization", doctorsSpecialization);
        appointmentsMap.put("phoneNumber", doctorPhoneNumber);
        appointmentsMap.put("emailAddress", doctorEmail);
        appointmentsMap.put("appointmentDate", appointmentDate);
        appointmentsMap.put("appointmentTime", appointmentTime);
        appointmentsMap.put("purpose", purpose);
        appointmentsMap.put("prescription", prescription);
        if (appoint_key_textView.getText().toString().equals("")) {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String formattedDate = df.format(c.getTime());
            appointmentsMap.put("date", formattedDate);
            databaseReference.push().setValue(appointmentsMap);

            hideProgressDialog();

            Toast.makeText(this, "Appointment saved!", Toast.LENGTH_SHORT).show();
        } else {
            appointmentsMap.put("date", appointments.getDate());
            databaseReference.child(appoint_key_textView.getText().toString()).updateChildren((java.util.HashMap)appointmentsMap);
            hideProgressDialog();
            Toast.makeText(this, "Appointment Updated!", Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(AppointmentsActivity.this,PhmsActivity.class);
        if (!firstTime) {
            intent.putExtra("appointmentsFlag", true);
        }
        startActivity(intent);
        finish();
    }

    private boolean validateForm(String doctorsName, String specialization, String phoneNumber, String emailAddress, String appointmentDate, String appointmentTime, String purpose, String prescription) {
        boolean valid=true;
        if (TextUtils.isEmpty(doctorsName) || TextUtils.isEmpty(specialization) || TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(emailAddress) || TextUtils.isEmpty(appointmentDate) || TextUtils.isEmpty(appointmentTime) || TextUtils.isEmpty(purpose) || TextUtils.isEmpty(prescription)) {
            return false;
        }
        Pattern patternEmail = Patterns.EMAIL_ADDRESS;
        Pattern patternPhone = Patterns.PHONE;
        if(patternEmail.matcher(emailAddress).matches()){
            doctorEmailInputLayout.setError(null);
        }else{
            doctorEmailInputLayout.setError("Please enter a valid Email address");
            valid = false;
        }if(patternPhone.matcher(phoneNumber).matches()){
            doctorPhoneNumberInputLayout.setError(null);
        }else{
            valid = false;
            doctorPhoneNumberInputLayout.setError("Please enter a valid Phone number");
        }

        return valid;
    }

    @Override
    public void onClick(View view) {
        int mYear;
        int mMonth;
        int mDay;
        if(view == appointmentDateEditText){
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
                            String visitDate = dateFormat.format(calendar.getTime());
                            try {
                                Date appointDate = dateFormat.parse(visitDate);
                                Date currentDate = new Date();
                                if(appointDate.before(currentDate)){
                                    appointmentDateInputLayout.setError("Date selected is not within range!");
                                }else{
                                    appointmentDateEditText.setText(visitDate);
                                    appointmentDateInputLayout.setError(null);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
            datePickerDialog.show();
        }

        if(view == appointmentTimeEditText){
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
                            appointmentTimeEditText.setText(hourString + ":" + minuteSting + " " + am_pm);
                        }

                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (firebaseAuth.getCurrentUser() == null) {
            onAuthFailure();
        }
    }
    private void onAuthFailure() {
        // Write new user
        Intent intent = new Intent(AppointmentsActivity.this, SignInSignUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }

}
