package com.project.group2.phms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.group2.phms.R;
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
    }

    @Override
    public int getItemCount() {
        if (mVitalsList == null) {
            return 0;
        } else {
            return mVitalsList.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView date,systolic,diastolic,cholesterol,glucose;


        ViewHolder(View v) {
            super(v);
            date = (TextView) v.findViewById(R.id.date);
            systolic = (TextView) v.findViewById(R.id.systolic);
            diastolic = (TextView) v.findViewById(R.id.diastolic);
            cholesterol = (TextView) v.findViewById(R.id.cholesterol);
            glucose = (TextView) v.findViewById(R.id.glucose);
        }


    }

}
