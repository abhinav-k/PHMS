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
import com.project.group2.phms.activities.AppointmentsActivity;
import com.project.group2.phms.activities.PhmsActivity;
import com.project.group2.phms.fragments.DietFragment;
import com.project.group2.phms.model.Appointments;
import com.project.group2.phms.model.Breakfast;
import com.project.group2.phms.preferences.Preferences;


import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import static com.project.group2.phms.R.id.caloriesEditText;
import static com.project.group2.phms.R.id.doctorsNameEditText;
import static com.project.group2.phms.R.id.foodDescriptionEditText;
import static com.project.group2.phms.R.id.servingSizeEditText;

/**
 * Created by ramajseepha on 3/24/17.
 */

public class BreakfastAdapter extends RecyclerView.Adapter<BreakfastAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Breakfast> mBreakfastList;
    DatabaseReference databaseReferenceBreakfast;
    String userId;
    ValueEventListener valueEventListener;

    TextInputEditText brandNameEditText;
    TextInputEditText foodDescriptionEditText;
    TextInputEditText servingSizeEditText;
    TextInputEditText caloriesEditText;

    Button addButton;
    Button cancelButton;

    Breakfast breakfast=null;

    public BreakfastAdapter(Context context, ArrayList<Breakfast> breakfastArrayList) {
        mContext = context;
        mBreakfastList = breakfastArrayList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.diet_adapter, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Breakfast breakfast = mBreakfastList.get(position);


        holder.brandName.setText(breakfast.getBrandName());
        holder.foodDescription.setText(breakfast.getFoodDescription());
        holder.servingSize.setText(breakfast.getServingSize());
        holder.calories.setText(breakfast.getCalories());
        holder.date.setText(breakfast.getDate());
        holder.key.setText(breakfast.getKey());


    }

    @Override
    public int getItemCount() {
        if (mBreakfastList == null) {
            return 0;
        } else {
            return mBreakfastList.size();
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
                        databaseReferenceBreakfast = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("diet").child("breakfast");
                    }
                    final String breakfastKey = key.getText().toString();
                    if(breakfastKey == null){
                        Log.d("Nothing to Edit", breakfastKey);
                    }else{
                        final Dialog dialog = new Dialog(mContext);
                        dialog.setTitle("Add Food");
                        dialog.setContentView(R.layout.dialog_add_food);
                        valueEventListener = new ValueEventListener() {
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                DataSnapshot snapshot = dataSnapshot.child(breakfastKey);
                                breakfast = snapshot.getValue(Breakfast.class);
                                if (breakfast != null) {
                                    brandNameEditText = (TextInputEditText) dialog.findViewById(R.id.brandNameEditText);
                                    foodDescriptionEditText = (TextInputEditText) dialog.findViewById(R.id.foodDescriptionEditText);
                                    servingSizeEditText = (TextInputEditText) dialog.findViewById(R.id.servingSizeEditText);
                                    caloriesEditText = (TextInputEditText) dialog.findViewById(R.id.caloriesEditText);

                                    brandNameEditText.setText(breakfast.getBrandName());
                                    foodDescriptionEditText.setText(breakfast.getFoodDescription());
                                    servingSizeEditText.setText(breakfast.getServingSize());
                                    caloriesEditText.setText(breakfast.getCalories());

                                    addButton = (Button) dialog.findViewById(R.id.addFoodButton);
                                    cancelButton = (Button) dialog.findViewById(R.id.cancelButton);

                                    addButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String brandName = brandNameEditText.getText().toString().trim();
                                            String foodDescription = foodDescriptionEditText.getText().toString().trim();
                                            String servingSize = servingSizeEditText.getText().toString().trim();
                                            String calories = caloriesEditText.getText().toString().trim();

                                            HashMap<String,String> breakfastMap = new HashMap<>();

                                            breakfastMap.put("brandName", brandName);
                                            breakfastMap.put("foodDescription", foodDescription);
                                            breakfastMap.put("servingSize", servingSize);
                                            breakfastMap.put("calories", calories);
                                            databaseReferenceBreakfast.child(breakfastKey.toString()).updateChildren((java.util.HashMap)breakfastMap);
                                            Toast.makeText(mContext, "Breakfast Updated Successfully", Toast.LENGTH_SHORT).show();
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

                        databaseReferenceBreakfast.addValueEventListener(valueEventListener);
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
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("diet").child("breakfast").child(key);
            databaseReference.removeValue();
            mBreakfastList.remove(getAdapterPosition());
            Intent intent = new Intent(mContext, PhmsActivity.class);
            intent.putExtra("dietFlag", true);
            mContext.startActivity(intent);

        }

    }

}