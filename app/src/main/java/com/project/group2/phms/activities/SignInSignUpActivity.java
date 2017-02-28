package com.project.group2.phms.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.project.group2.phms.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by vishwath on 2/14/17.
 */

public class SignInSignUpActivity extends AppCompatActivity {

    @BindView(R.id.signInButton) protected FancyButton signInButton;
    @BindView(R.id.signUpButton) protected FancyButton signOutButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_signup);
        ButterKnife.bind(this);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SignInSignUpActivity.this, SignInActivity.class));

            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SignInSignUpActivity.this, RegisterActivity.class));
            }
        });
    }
}
