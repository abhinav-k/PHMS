package com.project.group2.phms.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.group2.phms.R;
import com.project.group2.phms.fragments.MedicationFragment;
import com.project.group2.phms.model.DesigneeDoctor;
import com.project.group2.phms.model.Medication;
import com.project.group2.phms.preferences.Preferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MedicationNotification extends BaseActivity implements View.OnClickListener {

    FirebaseAuth firebaseAuth;
    DatabaseReference designeeReference;
    DatabaseReference databaseReferenceMedication;

    ValueEventListener valueEventListener;


    String medicationName;
    String dosage;
    String initialTime;
    String startDate;
    String endDate;
    String frequency;
    String totalQuantity;


    String medicationNameDisplay;
    String key;
    int quantityUpdate;

    Medication medication = null;

    HashMap<String, String> medicationsMap;

    @BindView(R.id.snoozeButton)
    protected Button snoozeButton;

    @BindView(R.id.takeMedicationButton)
    protected Button takeMedicationButton;

    @BindView(R.id.denyMedicationButton)
    protected Button denyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_notification);
        ButterKnife.bind(this);

        medicationsMap = new HashMap<>();
        if (getUid() != null) {
            String userId = getUid();
            firebaseAuth = FirebaseAuth.getInstance();
            designeeReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("doctor_and_designee");
            databaseReferenceMedication = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("medications");

        } else {

            onAuthFailure();
        }

        Intent intent = getIntent();

        if (intent != null) {
            medicationNameDisplay = intent.getStringExtra("medicationName");
            key = intent.getStringExtra("key");
            Toast.makeText(this, "key" + key, Toast.LENGTH_SHORT).show();
        }


        snoozeButton.setOnClickListener(this);
        takeMedicationButton.setOnClickListener(this);
        denyButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view == snoozeButton) {
            long timeInMilliseconds = System.currentTimeMillis() + 1000;
            // TODO: 4/11/17 Change long timeInMilliseconds = System.currentTimeMillis() + 5*1000; for the sake of demo
            Intent alertIntent = new Intent(this, AlertReceiver.class);
            alertIntent.putExtra("medName", medicationNameDisplay);
            alertIntent.putExtra("key", key);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMilliseconds,
                    PendingIntent.getBroadcast(this, (int) timeInMilliseconds % 2147483646, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));
            Toast.makeText(this, "Snoozed! Alarm will remind you in the next 15 minutes to take your medication", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, PhmsActivity.class);
            intent.putExtra("medFlag", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }


        if (view == takeMedicationButton) {

            databaseReferenceMedication.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DataSnapshot snap = dataSnapshot.child(key);
                    Medication medication = snap.getValue(Medication.class);
                    if (medication != null) {
                        totalQuantity = medication.getTotalQuantity();
                        quantityUpdate = Integer.parseInt(totalQuantity);
                        quantityUpdate--;
                        totalQuantity = String.valueOf(quantityUpdate);
                    }
                    databaseReferenceMedication.child(key).child("totalQuantity").setValue(totalQuantity);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            Intent intent = new Intent(this, PhmsActivity.class);
            intent.putExtra("medFlag", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        if (view == denyButton) {
            designeeReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DesigneeDoctor designeeDoctor = dataSnapshot.getValue(DesigneeDoctor.class);
                    String designeeEmail = designeeDoctor.getDesigneeEmail();
                    String doctorEmail = designeeDoctor.getDoctorEmail();
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MedicationNotification.this);
                    String fullName = sharedPreferences.getString(Preferences.NAME, "");
                    if (medicationNameDisplay == null) {
                        medicationNameDisplay = "medicine";
                    }
                    String subject = "Warning!";
                    String body = "Hi,\nThis email is to inform you that Mr." + fullName + " has not taken the " + medicationNameDisplay
                            + " to be taken as per prescription. It is advised to contact " + fullName + " and immediately advise him to stick to his medication schedule.\n\nRegards,\nPHMS.";
                    if (!TextUtils.isEmpty(designeeEmail) || !TextUtils.isEmpty(doctorEmail)) {
                        BackgroundMail.newBuilder(MedicationNotification.this)
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
                                        Intent intent = new Intent(MedicationNotification.this, PhmsActivity.class);
                                        intent.putExtra("medFlag", true);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                                    @Override
                                    public void onFail() {
                                        //do some magic
                                        Toast.makeText(MedicationNotification.this, "Add designee and doctor contact details", Toast.LENGTH_LONG).show();

                                    }
                                })
                                .send();

                    }
//                    else {
//                        }
//                    Intent intent = new Intent(MedicationNotification.this, PhmsActivity.class);
//                    intent.putExtra("medFlag", true);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                    finish();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    private void onAuthFailure() {
        // Write new user
        Intent intent = new Intent(MedicationNotification.this, SignInSignUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();

    }
}
