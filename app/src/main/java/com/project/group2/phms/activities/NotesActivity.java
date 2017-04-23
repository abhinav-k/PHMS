package com.project.group2.phms.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.project.group2.phms.R;
import com.project.group2.phms.model.Medication;
import com.project.group2.phms.model.Notes;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by vishwath on 4/9/17.
 */

public class NotesActivity extends BaseActivity {

    @BindView(R.id.titleEditText)
    protected EditText titleEditText;
    @BindView(R.id.notesEditText)
    protected EditText notesEditText;
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.notesKeyTextView)
    protected TextView notesKeyTextView;
    @BindView(R.id.doneButton)
    protected android.support.design.widget.FloatingActionButton doneButton;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;

    Notes notes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String notesKey = null;

        if (intent != null) {
            notesKey = intent.getStringExtra("notes_key");
        }


        if (getUid() != null) {
            String userId = getUid();
            firebaseAuth = FirebaseAuth.getInstance();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("notes");

        } else {

            onAuthFailure();
        }

        if (notesKey == null) {
            notesKeyTextView.setText("");
        } else {
            final String notes_key_value = notesKey;
            notesKeyTextView.setText(notesKey);
            valueEventListener = new ValueEventListener() {
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DataSnapshot snapshot = dataSnapshot.child(notes_key_value);
                    notes = snapshot.getValue(Notes.class);
                    if (notes != null) {
                        titleEditText.setText(notes.getTitle());
                        notesEditText.setText(notes.getNote());
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
                writeNote();
            }
        });

    }

    private void writeNote() {
        showProgressDialog("Saving...");
        final String notesTitle = titleEditText.getText().toString();
        final String notesText = notesEditText.getText().toString();

        if (!validateForm(notesTitle, notesText)) {
            hideProgressDialog();
            return;
        }

        HashMap<String, String> notesMap = new HashMap<>();
        notesMap.put("title", notesTitle);
        notesMap.put("note", notesText);
        if (notesKeyTextView.getText().toString().equals("")) {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMMM-yyyy");
            String formattedDate = df.format(c.getTime());
            notesMap.put("date", formattedDate);
        } else {
            notesMap.put("date", notes.getDate());
        }

        dbPushFunction(notesMap);
    }

    private void dbPushFunction(HashMap<String, String> notesMap) {
        if (notesKeyTextView.getText().toString().equals("")) {
            databaseReference.push().setValue(notesMap);
            hideProgressDialog();
            Toast.makeText(NotesActivity.this, "Note saved!", Toast.LENGTH_SHORT).show();
        } else {
            databaseReference.child(notesKeyTextView.getText().toString()).updateChildren((HashMap) notesMap);
            hideProgressDialog();
            Toast.makeText(NotesActivity.this, "Note Updated!", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(NotesActivity.this, PhmsActivity.class);
        intent.putExtra("notesFlag", true);
        startActivity(intent);
        finish();
    }

    private boolean validateForm(String notesTitle, String notesText) {
        if (TextUtils.isEmpty(notesTitle) || TextUtils.isEmpty(notesText)) {
            return false;
        }

        return true;
    }

    private void onAuthFailure() {
        // Write new user
        Intent intent = new Intent(NotesActivity.this, SignInSignUpActivity.class);
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

    @Override
    public void onBackPressed() {
        writeNote();
        super.onBackPressed();

    }
}
