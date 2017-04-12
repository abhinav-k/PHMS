package com.project.group2.phms.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
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
import com.project.group2.phms.activities.NotesActivity;
import com.project.group2.phms.model.Notes;

import java.util.ArrayList;

/**
 * Created by vishwath on 4/9/17.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Notes> mNotesList;

    public NotesAdapter(Context context, ArrayList<Notes> notesList) {
        mContext = context;
        mNotesList = notesList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_adapter, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Notes notes = mNotesList.get(position);
        holder.notesKey.setText(notes.getKey());
        holder.title.setText(notes.getTitle());
        holder.notes.setText(notes.getNote());
        holder.date.setText(notes.getDate());
    }

    @Override
    public int getItemCount() {
        if (mNotesList == null) {
            return 0;
        } else {
            return mNotesList.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView title, date, notes, notesKey;


        ViewHolder(View v) {
            super(v);
            v.setOnCreateContextMenuListener(this);
            title = (TextView) v.findViewById(R.id.titleTextView);
            date = (TextView) v.findViewById(R.id.date);
            notes = (TextView) v.findViewById(R.id.notesTextView);
            notesKey = (TextView) v.findViewById(R.id.notes_recycler_key);

        }

        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Notes Options");
            MenuItem editMed = menu.add(0, v.getId(), 0, "Edit");//groupId, itemId, order, title
            MenuItem deleteMed = menu.add(0, v.getId(), 0, "Delete");
            editMed.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Intent editNotesIntent = new Intent(mContext, NotesActivity.class);
                    editNotesIntent.putExtra("notes_key", notesKey.getText());
                    mContext.startActivity(editNotesIntent);
                    return false;
                }
            });
            deleteMed.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Toast.makeText(mContext, "Notes Deleted", Toast.LENGTH_LONG).show();
                    deleteVitalsByKey(notesKey.getText().toString());
                    return false;
                }
            });
        }

        private void deleteVitalsByKey(String key) {
            DatabaseReference databaseReference;
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notes").child(key);
            databaseReference.removeValue();
            mNotesList.remove(getAdapterPosition());
            notifyDataSetChanged();

        }
    }
}
