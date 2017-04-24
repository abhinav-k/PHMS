package com.project.group2.phms.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.group2.phms.R;
import com.project.group2.phms.activities.MedicationActivity;
import com.project.group2.phms.activities.PhmsActivity;
import com.project.group2.phms.model.Breakfast;
import com.project.group2.phms.other.DelayAutoCompleteTextView;
import com.project.group2.phms.preferences.Preferences;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by ramajseepha on 3/24/17.
 */

public class BreakfastAdapter extends RecyclerView.Adapter<BreakfastAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Breakfast> mBreakfastList;
    private DatabaseReference databaseReferenceBreakfast;
    private String userId;
    private ValueEventListener valueEventListener;

    private TextInputEditText brandNameEditText;
    private DelayAutoCompleteTextView foodAutoComplete;
    private TextInputEditText servingSizeEditText;
    private TextInputEditText caloriesEditText;

    private Button addButton;
    private Button cancelButton;

    private Breakfast breakfast = null;

    public BreakfastAdapter(Context context, ArrayList<Breakfast> breakfastArrayList) {
        mContext = context;
        mBreakfastList = breakfastArrayList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.diet_pager, parent, false);
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
        fetchImageUri(breakfast.getFoodDescription(), holder);

    }

    private void fetchImageUri(final String foodName, final ViewHolder holder) {
        AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String foodDescription = foodName.replaceAll("\\P{L}"," ").trim();
                final List<String> queryList = new ArrayList<>(Arrays.asList(foodDescription.split(" ")));
                if (queryList.size() > 1) {
                    foodDescription = queryList.get(0) + "+" + queryList.get(1) ;
                }else {
                    foodDescription = queryList.get(0);
                }
                Log.d("foodName",foodDescription);
                String imageLink = "";
                String API_LINK = "https://api.cognitive.microsoft.com/bing/v5.0/images/search?q=" + foodDescription;
                URL url;
                try {
                    url = new URL(API_LINK);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setRequestProperty("Ocp-Apim-Subscription-Key","a2c8ce5075cf46a8b75346edcc190c57");
                    int responseCode = urlConnection.getResponseCode();
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONArray imageArray = jsonObject.getJSONArray("value");
                    if (imageArray.length() > 0) {
                        JSONObject imageObject = imageArray.getJSONObject(0);
                        imageLink = imageObject.getString("thumbnailUrl");
                        Log.d("response", imageLink);
                    }


                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                return imageLink;
        }

        @Override
        protected void onPostExecute (String s){
            if (!s.isEmpty()) {
                Picasso.with(mContext).load(s).centerInside().resize(300,300).error(R.drawable.food).into(holder.dietPic);
            }
            super.onPostExecute(s);


        }
    });


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
    ImageView dietPic;


    ViewHolder(View v) {
        super(v);
        v.setOnCreateContextMenuListener(this);
        brandName = (TextView) v.findViewById(R.id.brandName);
        foodDescription = (TextView) v.findViewById(R.id.foodName);
        servingSize = (TextView) v.findViewById(R.id.servingSize);
        calories = (TextView) v.findViewById(R.id.calories);
        date = (TextView) v.findViewById(R.id.date);
        dietPic = (ImageView) v.findViewById(R.id.dietPicture);
        key = (TextView) v.findViewById(R.id.dietkey);

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
                if (breakfastKey == null) {
                    Log.d("Nothing to Edit", breakfastKey);
                } else {
                    final Dialog dialog = new Dialog(mContext);
                    dialog.setTitle("Add Food");
                    dialog.setContentView(R.layout.dialog_add_food);
                    valueEventListener = new ValueEventListener() {
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            DataSnapshot snapshot = dataSnapshot.child(breakfastKey);
                            breakfast = snapshot.getValue(Breakfast.class);
                            if (breakfast != null) {
                                brandNameEditText = (TextInputEditText) dialog.findViewById(R.id.brandNameEditText);
                                foodAutoComplete = (DelayAutoCompleteTextView) dialog.findViewById(R.id.foodAutoComplete);
                                servingSizeEditText = (TextInputEditText) dialog.findViewById(R.id.servingSizeEditText);
                                caloriesEditText = (TextInputEditText) dialog.findViewById(R.id.caloriesEditText);

                                brandNameEditText.setText(breakfast.getBrandName());
                                foodAutoComplete.setText(breakfast.getFoodDescription());
                                servingSizeEditText.setText(breakfast.getServingSize());
                                caloriesEditText.setText(breakfast.getCalories());

                                addButton = (Button) dialog.findViewById(R.id.addFoodButton);
                                cancelButton = (Button) dialog.findViewById(R.id.cancelButton);

                                addButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String brandName = brandNameEditText.getText().toString().trim();
                                        String foodDescription = foodAutoComplete.getText().toString().trim();
                                        String servingSize = servingSizeEditText.getText().toString().trim();
                                        String calories = caloriesEditText.getText().toString().trim();

                                        HashMap<String, String> breakfastMap = new HashMap<>();

                                        breakfastMap.put("brandName", brandName);
                                        breakfastMap.put("foodDescription", foodDescription);
                                        breakfastMap.put("servingSize", servingSize);
                                        breakfastMap.put("calories", calories);
                                        databaseReferenceBreakfast.child(breakfastKey.toString()).updateChildren((java.util.HashMap) breakfastMap);
                                        Toast.makeText(mContext, "Breakfast Updated Successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(mContext, PhmsActivity.class);
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