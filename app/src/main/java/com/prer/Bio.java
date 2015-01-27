package com.prer;

import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
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


public class Bio extends Activity {

    Button form;
    Button back;
    TextView name;
    TextView description;
    HttpGet httpget;
    HttpClient httpclient;
    int id;
    String bioQuery;
    HttpResponse response;
    JSONObject json;
    String result;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bio);
        Button form = (Button) findViewById(R.id.bio_form_button);
        Button back = (Button) findViewById(R.id.bio_back_button);

        name = (TextView) findViewById(R.id.name);
        description = (TextView) findViewById(R.id.description);
        form = (Button) findViewById(R.id.bio_form_button);
        back = (Button) findViewById(R.id.bio_back_button);
        id = 1;
        bioQuery = "http://54.191.98.90/api/bioTest/bioQuery.php?doctorID=1";

        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet(bioQuery);
                    response = client.execute(request);
                    result = EntityUtils.toString(response.getEntity());
                    json = new JSONObject(result);
                    name.setText(json.getString("FirstName") + " " + json.getString("LastName"));
                    description.setText(json.getString("Description"));
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();


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
    }
}
