package com.project.group2.phms.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.group2.phms.R;
import com.project.group2.phms.model.User;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends BaseActivity implements View.OnFocusChangeListener {

    @BindView(R.id.emailTextInputLayout)
    protected TextInputLayout emailLayout;
    @BindView(R.id.passwordTextInputLayout)
    protected TextInputLayout passwordLayout;
    @BindView(R.id.emailTextEditText)
    protected TextInputEditText emailEditText;
    @BindView(R.id.passwordTextEditText)
    protected TextInputEditText passwordEditText;
    @BindView(R.id.confirmpasswordTextEditText)
    protected TextInputEditText confirmPasswordEditText;
    @BindView(R.id.confirmpasswordTextInputLayout)
    protected TextInputLayout confirmPasswordLayout;
    @BindView(R.id.nextButton)
    protected FloatingActionButton button;
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    private FirebaseAuth firebaseAuth;
    String TAG = RegisterActivity.class.getSimpleName();
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        setSupportActionBar(toolbar);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerFunction();
            }
        });

        emailEditText.addTextChangedListener(new MyTextWatcher(emailEditText));
        passwordEditText.addTextChangedListener(new MyTextWatcher(passwordEditText));
        confirmPasswordEditText.addTextChangedListener(new MyTextWatcher(confirmPasswordEditText));

        emailEditText.setOnFocusChangeListener(this);
        passwordEditText.setOnFocusChangeListener(this);
        confirmPasswordEditText.setOnFocusChangeListener(this);

    }

    private void registerFunction() {
        String emailText = emailEditText.getText().toString().trim();
        String passwordText = passwordEditText.getText().toString().trim();
        String confirmPasswordText = confirmPasswordEditText.getText().toString().trim();

        if (!validateEmail(emailText)) {
            return;
        }
        if (!validateSetPass(passwordText)) {
            return;
        }
        if (!validateConfirmPass(passwordText, confirmPasswordText)) {
            return;
        }

        showProgressDialog("Registering the user...");


        firebaseAuth.createUserWithEmailAndPassword(emailText, confirmPasswordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    hideProgressDialog();
//                    Toast.makeText(RegisterActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                    onAuthSuccess(task.getResult().getUser());
                } else {
                    hideProgressDialog();
                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validateSetPass(String setPass) {
        if (setPass.length() < 8) {
            passwordLayout.setError("Minimum 8 character");
            return false;
        } else {
            passwordLayout.setError(null);
        }
        return true;
    }

    private boolean validateConfirmPass(String setPass, String confirmPass) {
        if (confirmPass.compareTo(setPass) != 0) {
            confirmPasswordLayout.setError("Passwords don't match");
            return false;
        } else {
            confirmPasswordLayout.setError(null);
        }
        return true;
    }

    private void onAuthSuccess(FirebaseUser user) {
        // Write new user
        writeNewUser(user.getUid(), user.getEmail());
        startActivity(new Intent(RegisterActivity.this, UserDetailsActivity.class));
        finish();
    }

    private void writeNewUser(String userId, String email) {

        databaseReference.child("users").child(userId);
        databaseReference.child("users").child(userId).child("email").setValue(email);
        startActivity(new Intent(RegisterActivity.this, UserDetailsActivity.class));
        finish();

    }

    @Override
    public void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (firebaseAuth.getCurrentUser() != null) {
            onAuthSuccess(firebaseAuth.getCurrentUser());
        }
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

            case R.id.confirmpasswordTextEditText:
                if (!hasFocus) {
                    validateConfirmPass(passwordEditText.getText().toString().trim(), confirmPasswordEditText.getText().toString().trim());
                } else {
                    confirmPasswordLayout.setError(null);
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
                case R.id.confirmpasswordTextEditText:
                    confirmPasswordLayout.setError(null);
                    break;

            }
        }
    }
}
