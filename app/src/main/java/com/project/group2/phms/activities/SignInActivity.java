package com.project.group2.phms.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.group2.phms.R;
import com.project.group2.phms.model.User;
import com.project.group2.phms.preferences.Preferences;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by vishwath on 2/14/17.
 */

public class SignInActivity extends BaseActivity implements View.OnFocusChangeListener {
    FirebaseAuth firebaseAuth;
    @BindView(R.id.emailTextInputLayout)
    protected TextInputLayout emailLayout;
    @BindView(R.id.emailTextEditText)
    protected TextInputEditText emailEditText;
    @BindView(R.id.passwordTextInputLayout)
    protected TextInputLayout passwordLayout;
    @BindView(R.id.passwordTextEditText)
    protected TextInputEditText passwordEditText;
    @BindView(R.id.doneButton)
    protected FloatingActionButton doneButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        ButterKnife.bind(this);
        firebaseAuth = FirebaseAuth.getInstance();

        emailEditText.setOnFocusChangeListener(this);
        passwordEditText.setOnFocusChangeListener(this);
        emailEditText.addTextChangedListener(new MyTextWatcher(emailEditText));
        passwordEditText.addTextChangedListener(new MyTextWatcher(passwordEditText));

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser();
            }
        });


    }

    private void signInUser() {
        String emailText = emailEditText.getText().toString().trim();
        String passwordText = passwordEditText.getText().toString().trim();

        if (!validateEmail(emailText)) {
            return;
        }
        if (!validateSetPass(passwordText)) {
            return;
        }

        showProgressDialog("Signing in...");
        firebaseAuth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            hideProgressDialog();
                            Toast.makeText(SignInActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } else if (task.isSuccessful()) {
                            hideProgressDialog();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(getUid());
                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.getValue(User.class);
                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SignInActivity.this);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString(Preferences.NAME,user.getName());
                                    editor.putString(Preferences.EMAIL,user.getEmail());
                                    editor.apply();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            Intent intent = new Intent(SignInActivity.this, PhmsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private boolean validateEmail(String emailText) {

        if (emailText.isEmpty() || !isValidEmail(emailText)) {
            emailLayout.setError("Enter a valid email");
            return false;
        } else {
            emailLayout.setError(null);
        }

        return true;
    }

    private boolean validateSetPass(String setPass) {
        if (TextUtils.isEmpty(setPass)) {
            passwordLayout.setError("Password cannot be empty");
            return false;
        } else {
            passwordLayout.setError(null);
        }
        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        switch (v.getId()) {

            case R.id.emailTextEditText:
                if (!hasFocus) {
                    validateEmail(emailEditText.getText().toString().trim());
                } else {
                    emailLayout.setError(null);
                }

                break;
            case R.id.passwordTextEditText:
                if (!hasFocus) {
                    validateSetPass(passwordEditText.getText().toString().trim());
                } else {
                    passwordLayout.setError(null);
                }

                break;
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
                case R.id.emailTextEditText:
                    emailLayout.setError(null);
                    break;
                case R.id.passwordTextEditText:
                    passwordLayout.setError(null);
                    break;

            }
        }
    }
}
