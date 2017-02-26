package com.project.group2.phms.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.group2.phms.R;
import com.project.group2.phms.model.User;
import com.project.group2.phms.preferences.Preferences;

import mehdi.sakout.fancybuttons.FancyButton;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by vishwath on 2/26/17.
 */

public class ProfileFragment extends Fragment {
    TextView displayName;
    TextInputEditText fullNameEditText,ageEditText;
    DatabaseReference databaseReference;
    FancyButton galleryButton;
    private StorageReference storageReference;
    public static final int RC_SIGN_IN = 123;
    private static final int RC_PHOTO_PICKER =  2;
    String userId;
    public ProfileFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userId = sharedPreferences.getString(Preferences.USERID,null);
        if (userId != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
            storageReference = FirebaseStorage.getInstance().getReference();
        }
        displayName = (TextView) view.findViewById(R.id.displayName);
        galleryButton = (FancyButton) view.findViewById(R.id.galleryButton);
        fullNameEditText = (TextInputEditText) view.findViewById(R.id.nameTextEditText);
        ageEditText = (TextInputEditText) view.findViewById(R.id.ageEditText);
        displayName.setText(sharedPreferences.getString(Preferences.NAME,""));

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                fullNameEditText.setText(user.getName());
                ageEditText.setText(user.getAge());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();
            final StorageReference photoref = storageReference.child(userId).child(selectedImageUri.getLastPathSegment());
            photoref.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    databaseReference.child("profile").setValue(taskSnapshot.getDownloadUrl());
                }
            });

        }
    }
}
