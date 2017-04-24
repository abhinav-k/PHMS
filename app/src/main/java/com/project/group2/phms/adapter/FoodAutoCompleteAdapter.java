package com.project.group2.phms.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.project.group2.phms.R;
import com.project.group2.phms.model.Food;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by vishwath on 4/23/17.
 */

public class FoodAutoCompleteAdapter extends BaseAdapter implements Filterable {

    private ArrayList<Food> foodList = new ArrayList<>();
    private static final int MAX_RESULTS = 10;
    private Context context;
    private String API_URL = "https://api.nutritionix.com/v1_1/search/";
    private String API_KEY = "?fields=item_name%2Cbrand_name%2Cnf_calories&appId=aed023be&appKey=f2d30bcbc42ba58903513203ca77dc05";

    public FoodAutoCompleteAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return foodList.size();
    }

    @Override
    public Food getItem(int index) {
        return foodList.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.simple_dropdown_item_2line, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.text1)).setText(getItem(position).getFoodName());
        ((TextView) convertView.findViewById(R.id.text2)).setText(getItem(position).getBrandName());
        return convertView;
    }

    @Override
    public Filter getFilter() {

        Filter myFilter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // A class that queries a web API, parses the data and returns an ArrayList<Style>
//
                    try {
                        foodList = new DownloadFood().execute(constraint.toString()).get();

                    } catch (Exception e) {
//                        Log.e("myException", e.getMessage());
                    }
                    // Now assign the values and count to the FilterResults object
                    filterResults.values = foodList;
                    filterResults.count = foodList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence contraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }

        };

        return myFilter;

    }


    private class DownloadFood extends AsyncTask<String, Void, ArrayList<Food>> {

        @Override
        protected ArrayList<Food> doInBackground(String... constraint) {
            final ArrayList<Food> mFoodArrayList = new ArrayList<>();
            try {
                URL url = new URL(API_URL + constraint[0].replaceAll(" ", "+") + API_KEY);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                int responseCode = urlConnection.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                Log.d("response", response.toString());
                JSONObject jsonObject = new JSONObject(response.toString());
                JSONArray jsonHits = jsonObject.getJSONArray("hits");
                for (int i = 0; i < jsonHits.length(); i++) {
                    JSONObject foodObject = jsonHits.getJSONObject(i);
                    JSONObject fieldObject = foodObject.getJSONObject("fields");
                    Food food = new Food();
                    food.setFoodName(fieldObject.getString("item_name"));
                    food.setBrandName(fieldObject.getString("brand_name"));
                    food.setCalories(fieldObject.getInt("nf_calories"));
                    food.setServingSize(fieldObject.getInt("nf_serving_size_qty"));
                    Log.d("adapter", String.valueOf(food.getCalories()));
                    mFoodArrayList.add(food);

                }


            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            Log.d("array_size", String.valueOf(mFoodArrayList.size()));
            return mFoodArrayList;
        }


    }


}
