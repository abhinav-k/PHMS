package com.project.group2.phms.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.project.group2.phms.R;
import com.project.group2.phms.model.User;
import com.project.group2.phms.preferences.Preferences;
import com.satsuware.usefulviews.LabelledSpinner;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.regex.Pattern;

import mehdi.sakout.fancybuttons.FancyButton;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by vishwath on 2/26/17.
 */

public class ProfileFragment extends Fragment {
    TextView displayName;
    LabelledSpinner genderSpinner;
    Spinner weightUnitSpinner, heightUnitSpinner;
    TextInputEditText fullNameEditText, ageEditText, weightEditText, heightEditText;
    TextInputLayout nameLayout, ageLayout, weightLayout, heightLayout;
    DatabaseReference databaseReference;
    CircularImageView imageView;
    FancyButton galleryButton, cameraButton, removeButton;
    private StorageReference storageReference;
    public static final int RC_SIGN_IN = 123;
    private static final int RC_PHOTO_PICKER = 2;
    public static final int RC_CAMERA_CODE = 123;
    String userId;
    Toolbar toolbar;
    String gender = "Male";
    MenuItem edit, save;

    public ProfileFragment() {

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
        userId = sharedPreferences.getString(Preferences.USERID, null);
        if (userId != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
            storageReference = FirebaseStorage.getInstance().getReference();
        }
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.profile));
        imageView = (CircularImageView) view.findViewById(R.id.profilePicture);
        displayName = (TextView) view.findViewById(R.id.displayName);
        galleryButton = (FancyButton) view.findViewById(R.id.galleryButton);
        fullNameEditText = (TextInputEditText) view.findViewById(R.id.nameTextEditText);
        ageEditText = (TextInputEditText) view.findViewById(R.id.ageEditText);
        displayName.setText(sharedPreferences.getString(Preferences.NAME, ""));
        removeButton = (FancyButton) view.findViewById(R.id.removeButton);
        cameraButton = (FancyButton) view.findViewById(R.id.cameraButton);
        genderSpinner = (LabelledSpinner) view.findViewById(R.id.genderSpinner);
        weightUnitSpinner = (Spinner) view.findViewById(R.id.weightSpinner);
        heightUnitSpinner = (Spinner) view.findViewById(R.id.heightSpinner);
        weightEditText = (TextInputEditText) view.findViewById(R.id.weightEditText);
        heightEditText = (TextInputEditText) view.findViewById(R.id.heightEditText);

        nameLayout = (TextInputLayout) view.findViewById(R.id.nameTextInputLayout);
        ageLayout = (TextInputLayout) view.findViewById(R.id.ageInputLayout);

        genderSpinner.setVisibility(View.GONE);
        weightUnitSpinner.setEnabled(false);
        heightUnitSpinner.setEnabled(false);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                fullNameEditText.setText(user.getName());
                ageEditText.setText(user.getAge());
                String weightWithUnit = user.getWeight();
                String heightWithUnit = user.getHeight();
                String weight = weightWithUnit.replaceAll("\\D", "");
                String weightUnit = weightWithUnit.replaceAll("[0-9]", "");
                String height = heightWithUnit.replaceAll("\\D", "");
                String heightUnit = heightWithUnit.replaceAll("[0-9]", "");
                weightEditText.setText(weight);
                heightEditText.setText(height);
                Log.d(weightUnit, heightUnit);
                genderSpinner.setSelection(genderSelector(user.getGender()));
                weightUnitSpinner.setSelection(weightSelector(weightUnit));
                heightUnitSpinner.setSelection(heightSelector(heightUnit));
                Picasso.with(getContext()).load(user.getProfile()).error(R.mipmap.ic_error_outline_black_24dp).into(imageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        setHasOptionsMenu(true);

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

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("click", "remove");
                databaseReference.child("profile").setValue("https://firebasestorage.googleapis.com/v0/b/phms-65aa3.appspot.com/o/ic_account_circle_black_48dp.png?alt=media&token=20dba348-4406-4117-86ee-d2b0a06280d5");
                Toast.makeText(getContext(), "Profile Picture Removed", Toast.LENGTH_SHORT).show();
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, RC_CAMERA_CODE);
            }
        });

        return view;
    }

    public int genderSelector(String gender) {

        if (gender.equalsIgnoreCase("male")) {
            return 0;
        } else if (gender.equalsIgnoreCase("female")) {
            return 1;
        } else {
            return 2;
        }
    }

    public int weightSelector(String unit) {

        if (unit.equalsIgnoreCase("kgs")) {
            return 0;
        } else {
            return 1;
        }
    }

    public int heightSelector(String unit) {

        if (unit.equalsIgnoreCase("cm")) {
            return 0;
        } else if (unit.equalsIgnoreCase("m")) {
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {

            Uri selectedImageUri = data.getData();
            final StorageReference photoref = storageReference.child(userId).child(selectedImageUri.getLastPathSegment());
            photoref.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Picasso.with(getContext()).load(taskSnapshot.getDownloadUrl()).into(imageView);
                    databaseReference.child("profile").setValue(taskSnapshot.getDownloadUrl().toString());
                    Toast.makeText(getContext(), "Profile Picture Set", Toast.LENGTH_SHORT).show();

                }
            });

        } else if (requestCode == RC_CAMERA_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);

            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            Bitmap bitmap = imageView.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] d = baos.toByteArray();

            UploadTask uploadTask = storageReference.child(userId).putBytes(d);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    databaseReference.child("profile").setValue(taskSnapshot.getDownloadUrl().toString());
                    Toast.makeText(getContext(), "Profile Picture Set", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("fail", "fail");
                }
            });

        }
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
                fullNameEditText.setEnabled(true);
                genderSpinner.setVisibility(View.VISIBLE);
                ageEditText.setEnabled(true);
                weightEditText.setEnabled(true);
                weightUnitSpinner.setEnabled(true);
                heightUnitSpinner.setEnabled(true);
                heightEditText.setEnabled(true);
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
        String name = fullNameEditText.getText().toString().trim();
        String age = ageEditText.getText().toString().trim();
        String weight = weightEditText.getText().toString().trim();
        String weightUnit = weightUnitSpinner.getSelectedItem().toString();
        String height = heightEditText.getText().toString().trim();
        String heightUnit = heightUnitSpinner.getSelectedItem().toString();

        Log.d("name", name);

        if (!validateForm(name, age)) {

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
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                if (user != null) {
                    editor.putString(Preferences.NAME, user.getName());
                    editor.putString(Preferences.EMAIL, user.getEmail());
                }
                editor.apply();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        fullNameEditText.setEnabled(false);
        genderSpinner.setVisibility(View.GONE);
        ageEditText.setEnabled(false);
        weightEditText.setEnabled(false);
        weightUnitSpinner.setEnabled(false);
        heightUnitSpinner.setEnabled(false);
        heightEditText.setEnabled(false);
        edit.setVisible(true);
        save.setVisible(false);


    }

    private boolean validateForm(String name, String age) {
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
}
