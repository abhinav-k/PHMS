package com.project.group2.phms.JSONUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class BackGroundTask extends AsyncTask<String, String, JSONObject> {

    List<NameValuePair> postparams = new ArrayList<NameValuePair>();
    String URL = null;
    String method = null;
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    public BackGroundTask(String url, String method, List<NameValuePair> params) {
        this.URL = url;
        this.postparams = params;
        this.method = method;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        // TODO Auto-generated method stub
        // Making HTTP request
        try {
            // Making HTTP request
            // check for request method

            if (method.equals("POST")) {
                // request method is POST
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(URL);
                httpPost.setEntity(new UrlEncodedFormEntity(postparams));

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();

            } else if (method == "GET") {
                // request method is GET
                DefaultHttpClient httpClient = new DefaultHttpClient();
                String paramString = URLEncodedUtils
                        .format(postparams, "utf-8");
                URL += "?" + paramString;
                HttpGet httpGet = new HttpGet(URL);

                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }

            // read input stream returned by request into a string using StringBuilder
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();

            // create a JSONObject from the json string
            jObj = new JSONObject(json);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // return JSONObject (this is a class variable and null is returned if something went bad)
        return jObj;

    }
}

