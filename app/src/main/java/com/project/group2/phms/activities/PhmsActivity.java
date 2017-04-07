package com.project.group2.phms.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.project.group2.phms.R;
import com.project.group2.phms.fragments.AppointmentsFragment;
import com.project.group2.phms.fragments.DesigneeFragment;
import com.project.group2.phms.fragments.DietFragment;
import com.project.group2.phms.fragments.DietParentFragment;
import com.project.group2.phms.fragments.HomeFragment;
import com.project.group2.phms.fragments.MedicationFragment;
import com.project.group2.phms.fragments.NotesFragment;
import com.project.group2.phms.fragments.ProfileFragment;
import com.project.group2.phms.fragments.VitalsFragment;
import com.project.group2.phms.model.User;
import com.project.group2.phms.preferences.Preferences;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Queue;
import java.util.Stack;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by vishwath on 2/13/17.
 */

public class PhmsActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    private Drawer result = null;
    AccountHeader headerResult;
    boolean profileFlag, vitalsFlag = false, appointmentsFlag = false, medFlag = false, dietFlag = false, notesFlag = false, homeFlag = true;
    ArrayList<Fragment> fragmentList;
    Stack<PrimaryDrawerItem> fragmentStack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phms);
        ButterKnife.bind(this);
        firebaseAuth = FirebaseAuth.getInstance();
        setSupportActionBar(toolbar);

        fragmentList = new ArrayList<>();
        fragmentStack = new Stack<>();
        Fragment home_fragment = new HomeFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, home_fragment);
        transaction.commit();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            profileFlag = extras.getBoolean("profileFlag");
            vitalsFlag = extras.getBoolean("vitalsFlag");
            medFlag = extras.getBoolean("medFlag");
            dietFlag = extras.getBoolean("dietFlag");
            notesFlag = extras.getBoolean("notesFlag");
            homeFlag = extras.getBoolean("homeFlag");
            appointmentsFlag = extras.getBoolean("appointmentsFlag");
        }


        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (getUid() != null) {
            String userId = getUid();
            firebaseAuth = FirebaseAuth.getInstance();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        } else {
            onAuthFailure();
        }

        final PrimaryDrawerItem home = new PrimaryDrawerItem().withName("Home").withIdentifier(1).withIcon(GoogleMaterial.Icon.gmd_home);
        final PrimaryDrawerItem profile = new PrimaryDrawerItem().withName("Profile").withIdentifier(2).withIcon(GoogleMaterial.Icon.gmd_account);
        final PrimaryDrawerItem vitals = new PrimaryDrawerItem().withName("Vitals").withIdentifier(3).withIcon(FontAwesome.Icon.faw_stethoscope);
        final PrimaryDrawerItem medication = new PrimaryDrawerItem().withName("Medication").withIdentifier(4).withIcon(GoogleMaterial.Icon.gmd_local_hospital);
        final PrimaryDrawerItem diet = new PrimaryDrawerItem().withName("Diet").withIdentifier(5).withIcon(FontAwesome.Icon.faw_cutlery);
        final PrimaryDrawerItem notes = new PrimaryDrawerItem().withName("Notes").withIdentifier(6).withIcon(GoogleMaterial.Icon.gmd_calendar_note);
        final PrimaryDrawerItem appointments = new PrimaryDrawerItem().withName("Appointments").withIdentifier(7).withIcon(FontAwesome.Icon.faw_users);
        final PrimaryDrawerItem designee = new PrimaryDrawerItem().withName("Doctor & Designee").withIdentifier(8).withIcon(GoogleMaterial.Icon.gmd_account_box_phone);
        final PrimaryDrawerItem logout = new PrimaryDrawerItem().withName("Logout").withIdentifier(9).withIcon(FontAwesome.Icon.faw_sign_out);


        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder).fit().centerCrop().into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.with(imageView.getContext()).cancelRequest(imageView);
            }
        });

        String name = preferences.getString(Preferences.NAME, "");
        String email = preferences.getString(Preferences.EMAIL, "");
        final ProfileDrawerItem userProfile = new ProfileDrawerItem().withName(name).withEmail(email).withIcon(R.mipmap.ic_account_circle_white_24dp);

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .withSelectionListEnabledForSingleProfile(false)
                .addProfiles(userProfile)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .withDisplayBelowStatusBar(false)
                .withTranslucentStatusBar(true)
                .withSavedInstance(savedInstanceState)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(home)
                .addDrawerItems(profile)
                .addDrawerItems(vitals)
                .addDrawerItems(medication)
                .addDrawerItems(diet)
                .addDrawerItems(notes)
                .addDrawerItems(appointments)
                .addDrawerItems(designee)
                .addDrawerItems(new DividerDrawerItem())
                .addDrawerItems(logout)
                .buildForFragment();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                String profilePic = user.getProfile();
                if (profilePic != null && !profilePic.equals("")) {
                    userProfile.withIcon(profilePic);
                    headerResult.updateProfile(userProfile);
                } else {
                    userProfile.withIcon(R.mipmap.ic_account_circle_white_24dp);
                    headerResult.updateProfile(userProfile);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (vitalsFlag) {
            vitalsFlag = false;
            Fragment fragment = new VitalsFragment();
            startFragment(fragment);
            result.setSelection(vitals);
        }
        if (medFlag) {
            medFlag = false;
            Fragment fragment = new MedicationFragment();
            startFragment(fragment);
            result.setSelection(medication);
        }
        if (appointmentsFlag) {
            appointmentsFlag = false;
            Fragment fragment = new AppointmentsFragment();
            startFragment(fragment);
            result.setSelection(appointments);
        }
        if (dietFlag) {
            dietFlag = false;
            Fragment fragment = new DietParentFragment();
            startFragment(fragment);
            result.setSelection(diet);
        }

        result.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                int drawItemId = (int) drawerItem.getIdentifier();
                Intent intent;
                Fragment fragment;
                switch (drawItemId) {

                    case 1:
                        fragment = new HomeFragment();
                        fragmentStack.add(home);
                        break;
                    case 2:
                        fragment = new ProfileFragment();
                        fragmentStack.add(profile);
                        break;
                    case 3:
                        fragment = new VitalsFragment();
                        fragmentStack.add(vitals);
                        break;
                    case 4:
                        fragment = new MedicationFragment();
                        fragmentStack.add(medication);
                        break;
                    case 5:
                        fragment = new DietParentFragment();
                        fragmentStack.add(diet);
                        break;
                    case 6:
                        fragment = new NotesFragment();
                        fragmentStack.add(notes);
                        break;
                    case 7:
                        fragment = new AppointmentsFragment();
                        fragmentStack.add(appointments);
                        break;
                    case 8:
                        fragment = new DesigneeFragment();
                        fragmentStack.add(designee);
                        break;
                    default:
                        fragment = new HomeFragment();

                        break;
                }
                if (drawItemId == 9) {
                    FirebaseAuth.getInstance().signOut();
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.apply();
                    intent = new Intent(PhmsActivity.this, SplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.commit();
                return false;
            }
        });

    }

    private void startFragment(Fragment fragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();

    }

    private void onAuthFailure() {
        Intent intent = new Intent(PhmsActivity.this, SignInSignUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }

    @Override
    public void onBackPressed() {
        if (result.isDrawerOpen()) {
            result.closeDrawer();
        }
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
    protected void onRestart() {
        super.onRestart();
        result.closeDrawer();
//        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    protected void onResume() {
//        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
