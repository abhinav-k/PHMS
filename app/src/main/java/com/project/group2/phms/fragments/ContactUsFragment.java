package com.project.group2.phms.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.group2.phms.R;
import com.project.group2.phms.activities.PhmsActivity;
import com.project.group2.phms.model.User;
import com.project.group2.phms.preferences.Preferences;

//import static com.project.group2.phms.R.id.contact_email;
//import static com.project.group2.phms.R.id.contact_name;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactUsFragment extends Fragment {

    Toolbar toolbar;
    DatabaseReference databaseReference;
    FloatingActionButton submitButton;
    EditText contact_subject;

    ValueEventListener listener;
    EditText contact_body;

    User user = null;


    public ContactUsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);


        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Contact Us");
        toolbar.setVisibility(View.VISIBLE);
        contact_subject = (EditText) view.findViewById(R.id.contact_subject);

        contact_body = (EditText) view.findViewById(R.id.contact_body);
        submitButton = (FloatingActionButton) view.findViewById(R.id.submit_button);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String userId = sharedPreferences.getString(Preferences.USERID, null);
        if (userId != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        }


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendEmail();
            }
        });


        return view;
    }


    protected void sendEmail() {

        if (contact_subject.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Please enter a subject", Toast.LENGTH_SHORT).show();
        }

        if (contact_body.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Please enter message", Toast.LENGTH_SHORT).show();
        } else {


            listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    String userId = sharedPreferences.getString(Preferences.USERID, null);

                    DataSnapshot snapshot = dataSnapshot.child(userId);
                    User user = snapshot.getValue(User.class);

                    String name = user.getName();
                    String email = user.getEmail();
                    String body = " From :"+name +"\n Email :"+email+"\n Body :"+ contact_body.getText().toString();
                    String subject = "PHMS Feedback: " + contact_subject.getText().toString();


                    BackgroundMail.newBuilder(getActivity())
                            .withUsername("phmsgroup2@gmail.com")
                            .withPassword("science100")
                            .withMailto("vishwathmathi@gmail.com")
                            .withType(BackgroundMail.TYPE_PLAIN)
                            .withSubject(subject)
                            .withBody(body)
                            .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                                @Override
                                public void onSuccess() {
                                    //do some magic
                                    Log.d("Email", "Sent Success");
                                    Toast.makeText(getContext(), "Feedback submitted", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getContext(),PhmsActivity.class);

                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);

                                }
                            })
                            .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                                @Override
                                public void onFail() {
                                    //do some magic
                                    Toast.makeText(getContext(), "Feedback not submitted, try again", Toast.LENGTH_LONG).show();

                                }
                            })
                            .send();





                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }


            };

            databaseReference.addValueEventListener(listener);




        }

    }

}