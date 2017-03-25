package com.project.group2.phms.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.project.group2.phms.R;
import com.project.group2.phms.model.DesigneeDoctor;
import com.project.group2.phms.model.User;
import com.project.group2.phms.preferences.Preferences;

import java.util.regex.Pattern;

/**
 * Created by vishwath on 3/24/17.
 */

public class DesigneeFragment extends Fragment {

    Toolbar toolbar;
    MenuItem edit, save;
    String userId;
    TextInputLayout doctorNameLayout, doctorEmailLayout, doctorPhoneLayout, designeeNameLayout, designeeEmailLayout,
            designeePhoneLayout, relationshipLayout;
    TextInputEditText doctorNameEditText, doctorEmailEditText, doctorPhoneEditText, designeeNameEditText, designeeEmailEditText,
            designeePhoneEditText, relationshipEditText;
    DatabaseReference databaseReference;

    public DesigneeFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_designee, container, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userId = sharedPreferences.getString(Preferences.USERID, null);
        if (userId != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("doctor_and_designee");
        }
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.dnd));
        doctorNameLayout = (TextInputLayout) view.findViewById(R.id.doctorNameLayout);
        doctorEmailLayout = (TextInputLayout) view.findViewById(R.id.doctorEmailLayout);
        doctorPhoneLayout = (TextInputLayout) view.findViewById(R.id.doctorPhoneLayout);
        doctorNameEditText = (TextInputEditText) view.findViewById(R.id.doctorNameEditText);
        doctorEmailEditText = (TextInputEditText) view.findViewById(R.id.doctorEmailEditText);
        doctorPhoneEditText = (TextInputEditText) view.findViewById(R.id.doctorPhoneEditText);

        designeeNameLayout = (TextInputLayout) view.findViewById(R.id.designeeNameLayout);
        designeeEmailLayout = (TextInputLayout) view.findViewById(R.id.designeeEmailLayout);
        designeePhoneLayout = (TextInputLayout) view.findViewById(R.id.designeePhoneLayout);
        designeeNameEditText = (TextInputEditText) view.findViewById(R.id.designeeNameEditText);
        designeeEmailEditText = (TextInputEditText) view.findViewById(R.id.designeeEmailEditText);
        designeePhoneEditText = (TextInputEditText) view.findViewById(R.id.designeePhoneEditText);
        relationshipLayout = (TextInputLayout) view.findViewById(R.id.relationshipLayout);
        relationshipEditText = (TextInputEditText) view.findViewById(R.id.relationshipEditText);

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        edit = menu.add("Edit").setIcon(R.mipmap.ic_edit_white_24dp).setShowAsActionFlags(1);
        save = menu.add("Save").setIcon(R.mipmap.ic_save_white_24dp).setVisible(false).setShowAsActionFlags(1);
        edit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                edit.setVisible(false);
                save.setVisible(true);
                doctorPhoneEditText.setEnabled(true);
                doctorNameEditText.setEnabled(true);
                doctorEmailEditText.setEnabled(true);
                designeePhoneEditText.setEnabled(true);
                designeeNameEditText.setEnabled(true);
                designeeEmailEditText.setEnabled(true);
                relationshipEditText.setEnabled(true);
                return false;
            }
        });

        save.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                saveFunction();
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void saveFunction() {
        String doctorName = doctorNameEditText.getText().toString().trim();
        String doctorEmail = doctorEmailEditText.getText().toString().trim();
        String doctorPhone = doctorPhoneEditText.getText().toString().trim();
        String designeeName = designeeNameEditText.getText().toString().trim();
        String designeeEmail = designeeEmailEditText.getText().toString().trim();
        String designeePhone = designeePhoneEditText.getText().toString().trim();
        String relationship = relationshipEditText.getText().toString().trim();


        if (!validateForm(designeeName, designeeEmail, designeePhone, relationship, doctorName, doctorEmail, doctorPhone)) {

            return;
        }

        databaseReference.child("doctorName").setValue(doctorName);
        databaseReference.child("doctorEmail").setValue(doctorEmail);
        databaseReference.child("doctorPhone").setValue(doctorPhone);
        databaseReference.child("designeeName").setValue(designeeName);
        databaseReference.child("designeeEmail").setValue(designeeEmail);
        databaseReference.child("designeePhone").setValue(designeePhone);
        databaseReference.child("relationship").setValue(relationship);

        doctorPhoneEditText.setEnabled(false);
        doctorNameEditText.setEnabled(false);
        doctorEmailEditText.setEnabled(false);
        designeePhoneEditText.setEnabled(false);
        designeeNameEditText.setEnabled(false);
        designeeEmailEditText.setEnabled(false);
        relationshipEditText.setEnabled(false);
        edit.setVisible(true);
        save.setVisible(false);

    }

    private boolean validateForm(String designeeName, String designeeEmail, String designeePhone, String relationship, String doctorName, String doctorEmail, String doctorPhone) {
        boolean valid = true;
        String textOnlyRegex = "^[\\p{L} .'-]+$";
        String phoneRegex = "\\d{10}";
        if (TextUtils.isEmpty(doctorName) || !Pattern.matches(textOnlyRegex, doctorName)) {
            doctorNameLayout.setError("Enter a valid name");
            valid = false;
        } else {
            doctorNameLayout.setError(null);
        }
        if (doctorEmail.isEmpty() || !isValidEmail(doctorEmail)) {
            doctorEmailLayout.setError("Enter a valid email");
            valid = false;
        } else {
            doctorEmailLayout.setError(null);
        }
        if (!Pattern.matches(phoneRegex, doctorPhone)) {
            doctorPhoneLayout.setError("Enter a valid number");
            valid = false;
        } else {
            doctorPhoneLayout.setError(null);
        }

        if (TextUtils.isEmpty(designeeName) || !Pattern.matches(textOnlyRegex, designeeName)) {
            designeeNameLayout.setError("Enter a valid name");
            valid = false;
        } else {
            designeeNameLayout.setError(null);
        }
        if (designeeEmail.isEmpty() || !isValidEmail(designeeEmail)) {
            designeeEmailLayout.setError("Enter a valid email");
            valid = false;
        } else {
            designeeEmailLayout.setError(null);
        }
        if (!Pattern.matches(phoneRegex, designeePhone)) {
            designeePhoneLayout.setError("Enter a valid number");
            valid = false;
        } else {
            designeePhoneLayout.setError(null);
        }

        return valid;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
