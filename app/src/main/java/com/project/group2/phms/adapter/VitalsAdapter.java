package com.project.group2.phms.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.group2.phms.R;
import com.project.group2.phms.activities.BaseActivity;
import com.project.group2.phms.activities.PhmsActivity;
import com.project.group2.phms.activities.SignInActivity;
import com.project.group2.phms.activities.VitalsActivity;
import com.project.group2.phms.fragments.VitalsFragment;
import com.project.group2.phms.model.Vitals;

import java.util.ArrayList;

/**
 * Created by vishwath on 2/24/17.
 */

public class VitalsAdapter extends RecyclerView.Adapter<VitalsAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Vitals> mVitalsList;

    public VitalsAdapter(Context context, ArrayList<Vitals> vitalsList) {
        mContext = context;
        mVitalsList = vitalsList;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.vitals_adapter, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Vitals vitals = mVitalsList.get(position);

        holder.date.setText(vitals.getDate());
        holder.systolic.setText(vitals.getSystolic() + " " + mContext.getString(R.string.bpUnit));
        holder.diastolic.setText(vitals.getDiastolic() + " " + mContext.getString(R.string.bpUnit));
        holder.cholesterol.setText(vitals.getCholesterol() + " " + mContext.getString(R.string.cholesterolUnit));
        holder.glucose.setText(vitals.getGlucose() + " " + mContext.getString(R.string.glucoseUnit));
        holder.key.setText(vitals.getKey());

    }

    @Override
    public int getItemCount() {
        if (mVitalsList == null) {
            return 0;
        } else {
            return mVitalsList.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView date, systolic, diastolic, cholesterol, glucose, key;


        ViewHolder(View v) {
            super(v);
            v.setOnCreateContextMenuListener(this);
            date = (TextView) v.findViewById(R.id.date);
            systolic = (TextView) v.findViewById(R.id.systolic);
            diastolic = (TextView) v.findViewById(R.id.diastolic);
            cholesterol = (TextView) v.findViewById(R.id.cholesterol);
            glucose = (TextView) v.findViewById(R.id.glucose);
            key = (TextView) v.findViewById(R.id.vitals_recycler_key);

        }

        public void onCreateContextMenu(ContextMenu menu, final View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Vitals Options");
            MenuItem edit = menu.add(0, v.getId(), 0, "Edit");//groupId, itemId, order, title
            MenuItem delete = menu.add(0, v.getId(), 0, "Delete");
            Log.d("check", "view " + v);
            edit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Intent intent = new Intent(mContext, VitalsActivity.class);
                    intent.putExtra("Key", key.getText().toString());
                    mContext.startActivity(intent);
                    return false;
                }
            });
            delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    deleteVitalsByKey(key.getText().toString());
                    Toast.makeText(mContext, "Vital Deleted", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });


        }

        private void deleteVitalsByKey(String key) {
            DatabaseReference databaseReference;
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("vitals").child(key);
            databaseReference.removeValue();
            mVitalsList.remove(getAdapterPosition());
            notifyDataSetChanged();

        }

    }

}
