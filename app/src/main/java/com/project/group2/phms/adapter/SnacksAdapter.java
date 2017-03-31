package com.project.group2.phms.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.group2.phms.R;
import com.project.group2.phms.activities.PhmsActivity;
import com.project.group2.phms.model.Snacks;
import com.project.group2.phms.preferences.Preferences;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ramajseepha on 3/24/17.
 */

public class SnacksAdapter extends RecyclerView.Adapter<SnacksAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Snacks> mSnacksList;

    DatabaseReference databaseReferenceSnacks;
    String userId;
    ValueEventListener valueEventListener;

    TextInputEditText brandNameEditText;
    TextInputEditText foodDescriptionEditText;
    TextInputEditText servingSizeEditText;
    TextInputEditText caloriesEditText;

    Button addButton;
    Button cancelButton;

    Snacks snacks=null;

    public SnacksAdapter(Context context, ArrayList<Snacks> snacksArrayList) {
        mContext = context;
        mSnacksList = snacksArrayList;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.diet_adapter, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Snacks snacks = mSnacksList.get(position);


        holder.brandName.setText(snacks.getBrandName());
        holder.foodDescription.setText(snacks.getFoodDescription());
        holder.servingSize.setText(snacks.getServingSize());
        holder.calories.setText(snacks.getCalories());
        holder.date.setText(snacks.getDate());
        holder.key.setText(snacks.getKey());


    }

    @Override
    public int getItemCount() {
        if (mSnacksList == null) {
            return 0;
        } else {
            return mSnacksList.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView brandName, foodDescription, servingSize, calories, date, key;


        ViewHolder(View v) {
            super(v);
            v.setOnCreateContextMenuListener(this);
            brandName = (TextView) v.findViewById(R.id.brandName);
            foodDescription = (TextView) v.findViewById(R.id.foodDescription);
            servingSize = (TextView) v.findViewById(R.id.servingSize);
            calories = (TextView) v.findViewById(R.id.calories);
            date = (TextView) v.findViewById(R.id.dateDiet);
            key = (TextView) v.findViewById(R.id.diet_recycler_key);

        }

        public void onCreateContextMenu(ContextMenu menu, final View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Diet Options");
            MenuItem edit = menu.add(0, v.getId(), 0, "Edit");//groupId, itemId, order, title
            MenuItem delete = menu.add(0, v.getId(), 0, "Delete");
            Log.d("check", "view " + v);
            edit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                    userId = sharedPreferences.getString(Preferences.USERID, null);
                    if (userId != null) {
                        databaseReferenceSnacks = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("diet").child("snacks");
                    }
                    final String snacksKey = key.getText().toString();
                    if(snacksKey == null){
                        Log.d("Nothing to Edit", snacksKey);
                    }else{
                        final Dialog dialog = new Dialog(mContext);
                        dialog.setTitle("Add Food");
                        dialog.setContentView(R.layout.dialog_add_food);
                        valueEventListener = new ValueEventListener() {
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                DataSnapshot snapshot = dataSnapshot.child(snacksKey);
                                snacks = snapshot.getValue(Snacks.class);
                                if (snacks != null) {
                                    brandNameEditText = (TextInputEditText) dialog.findViewById(R.id.brandNameEditText);
                                    foodDescriptionEditText = (TextInputEditText) dialog.findViewById(R.id.foodDescriptionEditText);
                                    servingSizeEditText = (TextInputEditText) dialog.findViewById(R.id.servingSizeEditText);
                                    caloriesEditText = (TextInputEditText) dialog.findViewById(R.id.caloriesEditText);

                                    brandNameEditText.setText(snacks.getBrandName());
                                    foodDescriptionEditText.setText(snacks.getFoodDescription());
                                    servingSizeEditText.setText(snacks.getServingSize());
                                    caloriesEditText.setText(snacks.getCalories());

                                    addButton = (Button) dialog.findViewById(R.id.addFoodButton);
                                    cancelButton = (Button) dialog.findViewById(R.id.cancelButton);

                                    addButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String brandName = brandNameEditText.getText().toString().trim();
                                            String foodDescription = foodDescriptionEditText.getText().toString().trim();
                                            String servingSize = servingSizeEditText.getText().toString().trim();
                                            String calories = caloriesEditText.getText().toString().trim();

                                            HashMap<String,String> snacksMap = new HashMap<>();

                                            snacksMap.put("brandName", brandName);
                                            snacksMap.put("foodDescription", foodDescription);
                                            snacksMap.put("servingSize", servingSize);
                                            snacksMap.put("calories", calories);
                                            databaseReferenceSnacks.child(snacksKey.toString()).updateChildren((java.util.HashMap)snacksMap);
                                            Toast.makeText(mContext, "Snacks Updated Successfully", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(mContext,PhmsActivity.class);
                                            intent.putExtra("dietFlag", true);
                                            mContext.startActivity(intent);
                                        }
                                    });

                                    cancelButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog.show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                        };

                        databaseReferenceSnacks.addValueEventListener(valueEventListener);
                    }
                    return false;
                }
            });
            delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    deleteDietByKey(key.getText().toString());
                    Toast.makeText(mContext, "Diet Deleted", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });


        }

        private void deleteDietByKey(String key) {
            DatabaseReference databaseReference;
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("diet").child("snacks").child(key);
            databaseReference.removeValue();
            mSnacksList.remove(getAdapterPosition());
            Intent intent = new Intent(mContext, PhmsActivity.class);
            intent.putExtra("dietFlag", true);
            mContext.startActivity(intent);

        }

    }

}