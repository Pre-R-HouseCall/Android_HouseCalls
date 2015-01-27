package com.prer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class Bio extends ActionBarActivity {
    TextView name;
    TextView description;
    HttpGet httpget;
    HttpClient httpclient;
    int id;
    String bioQuery;
    HttpResponse response;
    String url;
    int isLoaded = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JSONAsyncTask task = new JSONAsyncTask();
        task.execute(new String[] { "http://54.191.98.90/api/bioTest/bioQuery.php?doctorID=1" });
    }

    private class JSONAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                for (String url: urls) {
                    HttpGet httpget = new HttpGet(url);
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpResponse response = httpclient.execute(httpget);
                    String result = EntityUtils.toString(response.getEntity());
                    return result;
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            try {
                JSONObject json = new JSONObject(result);
                setContentView(R.layout.activity_bio);
                TextView name = (TextView) findViewById(R.id.name);
                TextView description = (TextView) findViewById(R.id.description);
                Button form = (Button) findViewById(R.id.bio_form_button);
                Button back = (Button) findViewById(R.id.bio_back_button);
                name.setText(json.getString("FirstName") + " " + json.getString("LastName"));
                description.setText(json.getString("Description"));
                form.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View view) {
                        Intent myIntent = new Intent(view.getContext(), Form.class);
                        startActivityForResult(myIntent, 0);
                    }
                });

                back.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View view) {
                        Intent myIntent = new Intent(view.getContext(), Doctors.class);
                        startActivityForResult(myIntent, 0);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
