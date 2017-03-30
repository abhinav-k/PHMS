package com.project.group2.phms.adapter;

import android.content.Context;
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
import com.project.group2.phms.model.Lunch;

import java.util.ArrayList;

/**
 * Created by vishwath on 3/29/17.
 */

public class LunchAdapter extends RecyclerView.Adapter<LunchAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Lunch> mLunchList;

    public LunchAdapter(Context context, ArrayList<Lunch> lunchArrayList) {
        mContext = context;
        mLunchList = lunchArrayList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.diet_adapter, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Lunch lunch = mLunchList.get(position);


        holder.brandName.setText(lunch.getBrandName());
        holder.foodDescription.setText(lunch.getFoodDescription());
        holder.servingSize.setText(lunch.getServingSize());
        holder.calories.setText(lunch.getCalories());
        holder.date.setText(lunch.getDate());
        holder.key.setText(lunch.getKey());


    }

    @Override
    public int getItemCount() {
        if (mLunchList == null) {
            return 0;
        } else {
            return mLunchList.size();
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

                    String lunchKey = key.getText().toString();
                    Toast.makeText(mContext,"Edit button Pressed", Toast.LENGTH_SHORT);
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
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("diet").child("lunch").child(key);
            databaseReference.removeValue();
            mLunchList.remove(getAdapterPosition());
            notifyDataSetChanged();

        }

    }

}
