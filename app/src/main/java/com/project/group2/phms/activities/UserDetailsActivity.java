package com.project.group2.phms.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.group2.phms.R;
import com.project.group2.phms.model.User;
import com.project.group2.phms.preferences.Preferences;
import com.satsuware.usefulviews.LabelledSpinner;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by vishwath on 2/12/17.
 */

public class UserDetailsActivity extends BaseActivity {

    @BindView(R.id.nameTextInputLayout)
    protected TextInputLayout nameLayout;
    @BindView(R.id.nameTextEditText)
    protected TextInputEditText nameEditText;
    @BindView(R.id.ageInputLayout)
    protected TextInputLayout ageLayout;
    @BindView(R.id.ageEditText)
    protected TextInputEditText ageEditText;
    @BindView(R.id.weightInputLayout)
    protected TextInputLayout weightLayout;
    @BindView(R.id.weightEditText)
    protected TextInputEditText weightEditText;
    @BindView(R.id.heightInputLayout)
    protected TextInputLayout heightLayout;
    @BindView(R.id.heightEditText)
    protected TextInputEditText heightEditText;
    @BindView(R.id.nextButton)
    protected FloatingActionButton doneButton;
    @BindView(R.id.genderSpinner)
    protected LabelledSpinner genderSpinner;
    @BindView(R.id.heightSpinner)
    protected Spinner heightSpinner;
    @BindView(R.id.weightSpinner)
    protected Spinner weightSpinner;
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    String gender = "Male";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getUid() != null) {
            String userId = getUid();
            firebaseAuth = FirebaseAuth.getInstance();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        } else {
            onAuthFailure();
        }
        genderSpinner.setLabelText(R.string.gender);
        genderSpinner.setOnItemChosenListener(new LabelledSpinner.OnItemChosenListener() {
            @Override
            public void onItemChosen(View labelledSpinner, AdapterView<?> adapterView, View itemView, int position, long id) {

                gender = adapterView.getSelectedItem().toString();

            }

            @Override
            public void onNothingChosen(View labelledSpinner, AdapterView<?> adapterView) {
                gender = adapterView.getSelectedItem().toString();
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeNewUser();
            }
        });


    }

    private void writeNewUser() {
        showProgressDialog("Saving...");
        String name = nameEditText.getText().toString().trim();
        String age = ageEditText.getText().toString().trim();
        String weight = weightEditText.getText().toString().trim();
        String weightUnit = weightSpinner.getSelectedItem().toString();
        String height = heightEditText.getText().toString().trim();
        String heightUnit = heightSpinner.getSelectedItem().toString();

        if (!validateForm(name, age, weight, height)) {
            hideProgressDialog();
            return;
        }

        databaseReference.child("name").setValue(name);
        databaseReference.child("gender").setValue(gender);
        databaseReference.child("age").setValue(age);
        databaseReference.child("weight").setValue(weight + weightUnit);
        databaseReference.child("height").setValue(height + heightUnit);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(UserDetailsActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                if(user != null) {
                    editor.putString(Preferences.NAME, user.getName());
                    editor.putString(Preferences.EMAIL, user.getEmail());
                }
                editor.putString(Preferences.USERID, getUid());
                editor.apply();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        hideProgressDialog();
        Toast.makeText(this, "User details saved!",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(UserDetailsActivity.this, VitalsActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("firstTime",true);
        startActivity(intent);
//        finish();

    }

    private boolean validateForm(String name, String age, String weight, String height) {
        boolean valid = true;
        String textOnlyRegex = "^[\\p{L} .'-]+$";
        if (TextUtils.isEmpty(name) || !Pattern.matches(textOnlyRegex, name)) {
            nameLayout.setError("Enter a valid name");
            valid = false;
        } else {
            nameLayout.setError(null);
        }
        if (TextUtils.isEmpty(age)) {
            ageLayout.setError("Required");
            valid = false;
        } else {
            ageLayout.setError(null);
        }

        return valid;
    }

    private void onAuthFailure() {
        // Write new user
        Intent intent = new Intent(UserDetailsActivity.this, SignInSignUpActivity.class);
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

    private void onAuthSuccess() {
        startActivity(new Intent(UserDetailsActivity.this, VitalsActivity.class));
        finish();

    }
}
