package com.project.group2.phms.adapter;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.group2.phms.R;
import com.project.group2.phms.activities.AlertReceiver;
import com.project.group2.phms.activities.MedicationActivity;
import com.project.group2.phms.activities.PhmsActivity;
import com.project.group2.phms.model.Medication;

import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * Created by ramajseepha on 2/24/17.
 */

public class MedicationsAdapter extends RecyclerView.Adapter<MedicationsAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Medication> mMedicationList;

    public MedicationsAdapter(Context context, ArrayList<Medication> medicationsList) {
        mContext = context;
        mMedicationList = medicationsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.medications_adapter, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Medication medication = mMedicationList.get(position);
        holder.dateMed.setText(medication.getDateMed());
        holder.medicationName.setText(medication.getMedicationName());
        holder.dosage.setText(medication.getDosage() + " " + mContext.getString(R.string.dosageUnit));
        holder.initialTime.setText(medication.getInitialTime());
        holder.startDate.setText(medication.getStartDate());
        holder.endDate.setText(medication.getEndDate());
        holder.frequency.setText(medication.getFrequency());
        holder.medication_key.setText(medication.getKey());
        holder.totalQuantity.setText(medication.getTotalQuantity());
    }

    @Override
    public int getItemCount() {
        if (mMedicationList == null) {
            return 0;
        } else {
            return mMedicationList.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView dateMed, medicationName, dosage, totalQuantity, initialTime, startDate, endDate, frequency, medication_key;


        ViewHolder(View v) {
            super(v);
            v.setOnCreateContextMenuListener(this);
            dateMed = (TextView) v.findViewById(R.id.dateMedAdd);
            medicationName = (TextView) v.findViewById(R.id.medicationName);
            dosage = (TextView) v.findViewById(R.id.dosage);
            // TODO: 4/11/17 Added totalQuantity 
            totalQuantity = (TextView) v.findViewById(R.id.totalQuantity);
            initialTime = (TextView) v.findViewById(R.id.initialTime);
            startDate = (TextView) v.findViewById(R.id.startDate);
            endDate = (TextView) v.findViewById(R.id.endDate);
            frequency = (TextView) v.findViewById(R.id.frequency);
            medication_key = (TextView) v.findViewById(R.id.medication_key);

        }

        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Medications Options");
            MenuItem editMed = menu.add(0, v.getId(), 0, "Edit");//groupId, itemId, order, title
            MenuItem deleteMed = menu.add(0, v.getId(), 0, "Delete");
            editMed.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Intent editMedIntent = new Intent(mContext, MedicationActivity.class);
                    editMedIntent.putExtra("medications_key", medication_key.getText().toString());
                    mContext.startActivity(editMedIntent);
                    return false;
                }
            });
            deleteMed.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Toast.makeText(mContext, "Medication Deleted", Toast.LENGTH_LONG).show();
                    deleteVitalsByKey(medication_key.getText().toString());
                    return false;
                }
            });
        }

        private void deleteVitalsByKey(String key) {
            DatabaseReference databaseReference;
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("medications").child(key);
            databaseReference.removeValue();
            mMedicationList.remove(getAdapterPosition());
            notifyDataSetChanged();

        }
    }
}