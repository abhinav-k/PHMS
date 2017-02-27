package com.project.group2.phms.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
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
import com.mikepenz.iconics.context.IconicsContextWrapper;
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
import com.project.group2.phms.fragments.ProfileFragment;
import com.project.group2.phms.fragments.VitalsFragment;
import com.project.group2.phms.model.User;
import com.project.group2.phms.preferences.Preferences;
import com.squareup.picasso.Picasso;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phms);
        ButterKnife.bind(this);
        firebaseAuth = FirebaseAuth.getInstance();
        setSupportActionBar(toolbar);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (getUid() != null) {
            String userId = getUid();
            firebaseAuth = FirebaseAuth.getInstance();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        } else {
            onAuthFailure();
        }

        final PrimaryDrawerItem profile = new PrimaryDrawerItem().withName("Profile").withIdentifier(1).withIcon(GoogleMaterial.Icon.gmd_account);
        PrimaryDrawerItem vitals = new PrimaryDrawerItem().withName("Vitals").withIdentifier(2).withIcon(FontAwesome.Icon.faw_stethoscope);
        PrimaryDrawerItem medication = new PrimaryDrawerItem().withName("Medication").withIdentifier(3).withIcon(GoogleMaterial.Icon.gmd_local_hospital);
        PrimaryDrawerItem diet = new PrimaryDrawerItem().withName("Diet").withIdentifier(4).withIcon(FontAwesome.Icon.faw_cutlery);
        PrimaryDrawerItem notes = new PrimaryDrawerItem().withName("Notes").withIdentifier(5).withIcon(GoogleMaterial.Icon.gmd_calendar_note);
        PrimaryDrawerItem logout = new PrimaryDrawerItem().withName("Logout").withIdentifier(6).withIcon(FontAwesome.Icon.faw_sign_out);


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

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                String profilePic = user.getProfile();
                if (!profilePic.equals("")){
                    Log.d("profile",profilePic);
                    userProfile.withIcon(profilePic);
                    headerResult.updateProfile(userProfile);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .withDisplayBelowStatusBar(false)
                .withTranslucentStatusBar(true)
                .withSavedInstance(savedInstanceState)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(profile)
                .addDrawerItems(vitals)
                .addDrawerItems(medication)
                .addDrawerItems(diet)
                .addDrawerItems(notes)
                .addDrawerItems(new DividerDrawerItem())
                .addDrawerItems(logout)
                .buildForFragment();

        result.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                int drawItemId = (int) drawerItem.getIdentifier();
                Intent intent;
                FragmentTransaction transaction;
                switch (drawItemId) {

                    case 1:
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, new ProfileFragment()).addToBackStack(null);
                        transaction.commit();
                        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        break;
                    case 2:
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, new VitalsFragment()).addToBackStack(null);
                        transaction.commit();
                        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    case 6:
                        FirebaseAuth.getInstance().signOut();
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.apply();
                        intent = new Intent(PhmsActivity.this, SplashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        break;
                }
                return false;
            }
        });

    }

    private void onAuthFailure() {
        Intent intent = new Intent(PhmsActivity.this, SignInSignUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }

    @Override
    public void onBackPressed() {
        if (result.isDrawerOpen()){
            result.closeDrawer();
        }else {
            super.onBackPressed();
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }
}
