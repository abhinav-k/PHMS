package com.project.group2.phms.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.group2.phms.R;

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

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vitals);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getUid() != null) {
            String userId = getUid();
            firebaseAuth = FirebaseAuth.getInstance();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("vitals");

        } else {

            onAuthFailure();
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
        String systolic = systolicEditText.getText().toString().trim();
        String diastolic = diastolicEditText.getText().toString().trim();
        String glucose = glucoseEditText.getText().toString().trim();
        String cholesterol = cholesterolEditText.getText().toString().trim();

        if (!validateForm(systolic, diastolic, glucose, cholesterol)) {
            hideProgressDialog();
            return;
        }

        databaseReference.child("systolic").setValue(systolic);
        databaseReference.child("diastolic").setValue(diastolic);
        databaseReference.child("glucose").setValue(glucose);
        databaseReference.child("cholesterol").setValue(cholesterol);
        hideProgressDialog();
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putBoolean(Preferences.LOGGED_IN, true);
//        editor.apply();
        Toast.makeText(this, "Vitals saved!",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(VitalsActivity.this, PhmsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
