package com.project.group2.phms.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.group2.phms.R;
import com.project.group2.phms.activities.AppointmentsActivity;
import com.project.group2.phms.model.Appointments;

import java.util.ArrayList;

/**
 * Created by vishwath on 3/29/17.
 */

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Appointments> mAppointmentsList;

    public AppointmentsAdapter(Context context, ArrayList<Appointments> appointmentsList) {
        mContext = context;
        mAppointmentsList = appointmentsList;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointments_adapter, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Appointments appointments = mAppointmentsList.get(position);

        holder.date.setText(appointments.getDate());
        holder.doctorsName.setText(appointments.getDoctorName());
        holder.specialization.setText(appointments.getDoctorSpecialization());
        holder.phoneNumber.setText(appointments.getPhoneNumber());
        holder.emailAddress.setText(appointments.getEmailAddress());
        holder.appointmentDate.setText(appointments.getAppointmentDate());
        holder.appointmentTime.setText(appointments.getAppointmentTime());
        holder.purpose.setText(appointments.getPurpose());
        holder.prescription.setText(appointments.getPrescription());
        holder.key.setText(appointments.getKey());


    }

    @Override
    public int getItemCount() {
        if (mAppointmentsList == null) {
            return 0;
        } else {
            return mAppointmentsList.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView date, doctorsName, specialization, phoneNumber, emailAddress, appointmentDate, appointmentTime, purpose, prescription, key;


        ViewHolder(View v) {
            super(v);
            v.setOnCreateContextMenuListener(this);
            date = (TextView) v.findViewById(R.id.appointDate);
            doctorsName = (TextView) v.findViewById(R.id.doctorName);
            specialization = (TextView) v.findViewById(R.id.specialization);
            phoneNumber = (TextView) v.findViewById(R.id.number);
            emailAddress = (TextView) v.findViewById(R.id.email);
            appointmentDate = (TextView) v.findViewById(R.id.appointmentDate);
            appointmentTime = (TextView) v.findViewById(R.id.appointmentTime);
            purpose = (TextView) v.findViewById(R.id.purpose);
            prescription = (TextView) v.findViewById(R.id.notes);
            key = (TextView) v.findViewById(R.id.appointments_recycler_key);

        }

        public void onCreateContextMenu(ContextMenu menu, final View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Appointments Options");
            MenuItem edit = menu.add(0, v.getId(), 0, "Edit");//groupId, itemId, order, title
            MenuItem delete = menu.add(0, v.getId(), 0, "Delete");
            Log.d("check", "view " + v);
            edit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Intent intent = new Intent(mContext, AppointmentsActivity.class);
                    intent.putExtra("app_key", key.getText().toString());
                    mContext.startActivity(intent);
                    return false;
                }
            });
            delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    deleteAppointmentByKey(key.getText().toString());
                    Toast.makeText(mContext, "Appointment Deleted", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });


        }

        private void deleteAppointmentByKey(String key) {
            DatabaseReference databaseReference;
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("appointments").child(key);
            databaseReference.removeValue();
            mAppointmentsList.remove(getAdapterPosition());
            notifyDataSetChanged();

        }

    }

}
