package com.project.group2.phms.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.group2.phms.R;
import com.project.group2.phms.adapter.VitalsAdapter;
import com.project.group2.phms.fragments.ProfileFragment;
import com.project.group2.phms.fragments.VitalsFragment;
import com.project.group2.phms.model.Vitals;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by vishwath on 2/14/17.
 */

public class VitalsActivity extends BaseActivity {
    @BindView(R.id.systolicInputLayout)
    protected TextInputLayout systolicLayout;
    @BindView(R.id.systolicEditText)
    protected TextInputEditText systolicEditText;
    @BindView(R.id.diastolicInputLayout)
    protected TextInputLayout diastolicLayout;
    @BindView(R.id.diastolicEditText)
    protected TextInputEditText diastolicEditText;
    @BindView(R.id.glucoseInputLayout)
    protected TextInputLayout glucoseLayout;
    @BindView(R.id.glucoseEditText)
    protected TextInputEditText glucoseEditText;
    @BindView(R.id.cholesterolInputLayout)
    protected TextInputLayout cholesterolLayout;
    @BindView(R.id.cholesterolEditText)
    protected TextInputEditText cholesterolEditText;
    @BindView(R.id.doneButton)
    protected FloatingActionButton doneButton;
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.vitals_key)
    protected TextInputEditText keyEditText;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    Vitals vitals=null;
    ValueEventListener listner;
    boolean firstTime = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vitals);

        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            firstTime = extras.getBoolean("firstTime");

        }

        setSupportActionBar(toolbar);
        if (getUid() != null) {
            String userId = getUid();
            firebaseAuth = FirebaseAuth.getInstance();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("vitals");

        } else {

            onAuthFailure();
        }
        Intent intent = getIntent();
        String keyValue=null;
        if(intent!=null) {
            keyValue = intent.getStringExtra("Key");
        }
        if (keyValue==null)
        {
            keyEditText.setText("");
        }

        else
        {
            final String keyValue1=keyValue;
            keyEditText.setText(keyValue);
            listner=new ValueEventListener() {
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DataSnapshot snapshot=dataSnapshot.child(keyValue1);
                    vitals = snapshot.getValue(Vitals.class);
                    if(vitals!=null) {
                        systolicEditText.setText(vitals.getSystolic());
                        diastolicEditText.setText(vitals.getDiastolic());
                        glucoseEditText.setText(vitals.getGlucose());
                        cholesterolEditText.setText(vitals.getCholesterol());
                    }
                    }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };

            databaseReference.addValueEventListener(listner);
        }

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeVitals();
            }
        });

    }

    private void writeVitals() {
        showProgressDialog("Saving...");

            HashMap<String, String> vitalsMap = new HashMap<>();
            String systolic = systolicEditText.getText().toString().trim();
            String diastolic = diastolicEditText.getText().toString().trim();
            String glucose = glucoseEditText.getText().toString().trim();
            String cholesterol = cholesterolEditText.getText().toString().trim();

            if (!validateForm(systolic, diastolic, glucose, cholesterol)) {
                hideProgressDialog();
                return;
            }
        vitalsMap.put("systolic", systolic);
        vitalsMap.put("diastolic", diastolic);
        vitalsMap.put("glucose", glucose);
        vitalsMap.put("cholesterol", cholesterol);
        if(keyEditText.getText().toString().equals("")) {
            Calendar c = Calendar.getInstance();
            System.out.println("Current time => " + c.getTime());
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String formattedDate = df.format(c.getTime());

            vitalsMap.put("date", formattedDate);
            databaseReference.push().setValue(vitalsMap);

            hideProgressDialog();

            Toast.makeText(this, "Vitals saved!", Toast.LENGTH_SHORT).show();
        }
        else
        {

            vitalsMap.put("date", vitals.getDate());
            databaseReference.child(keyEditText.getText().toString()).updateChildren((java.util.HashMap)vitalsMap);
            hideProgressDialog();
            Toast.makeText(this, "Vitals Updated!", Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(VitalsActivity.this,PhmsActivity.class);
        if (!firstTime) {
            intent.putExtra("vitalsFlag", true);
        }
        startActivity(intent);
        finish();

    }

    private boolean validateForm(String systolic, String diastolic, String glucose, String cholesterol) {
        boolean valid = true;
        int s, d, g, c;
        if (TextUtils.isEmpty(systolic) || TextUtils.isEmpty(diastolic) || TextUtils.isEmpty(glucose) || TextUtils.isEmpty(cholesterol)) {
            return false;
        } else {
            s = Integer.valueOf(systolic);
            d = Integer.valueOf(diastolic);
            g = Integer.valueOf(glucose);
            c = Integer.valueOf(cholesterol);
        }

        if (s <= 300) {
            systolicLayout.setError(null);
        } else {
            systolicLayout.setError("Enter a valid value");
            valid = false;
        }
        if (d <= 200) {
            diastolicLayout.setError(null);
        } else {
            diastolicLayout.setError("Enter a valid value");
            valid = false;
        }
        if (g <= 900) {
            glucoseLayout.setError(null);
        } else {
            glucoseLayout.setError("Enter a valid value");
            valid = false;
        }
        if (c <= 3500) {
            cholesterolLayout.setError(null);
        } else {
            cholesterolLayout.setError("Enter a valid value");
            valid = false;
        }

        return valid;
    }

    private void onAuthFailure() {
        // Write new user
        Intent intent = new Intent(VitalsActivity.this, SignInSignUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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



}
