package com.prer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Feedback extends ActionBarActivity implements AdapterView.OnItemClickListener {
    HttpPost httppost;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;
    int docID;
    JSONObject json;
    private DrawerLayout drawerLayout;
    private ListView drawerView;
    private ActionBarDrawerToggle drawerListener;
    private NavAdapter myAdapter;
    SharedPreferences formPrefs;
    String feedbackStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        formPrefs = getSharedPreferences("formDetails", 0);
        docID = formPrefs.getInt("docID", -1);

        JSONAsyncTask task = new JSONAsyncTask();
        task.execute(new String[] { "http://54.191.98.90/api/bioTest/bioQuery.php?doctorID=" + String.valueOf(docID) });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerListener.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerListener.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectItem(position);
    }

    public void selectItem(int position) {
        // update the main content by replacing fragments
        // update selected item and title, then close the drawer
        drawerView.setItemChecked(position, true);
        drawerView.setSelection(position);
//            setTitle(navMenuTitles[position]);
        drawerLayout.closeDrawer(drawerView);

        switch (position) {
            case 0:
                startActivity(new Intent(this, Doctors.class));
                break;
            case 1:
                Toast.makeText(Feedback.this, "You Have Not Requested A Call", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                SharedPreferences logPrefs = getSharedPreferences("loginDetails", 0);
                SharedPreferences.Editor editor = logPrefs.edit();
                editor.clear();
                editor.commit();

                SharedPreferences.Editor edit = formPrefs.edit();
                edit.clear();
                edit.commit();

                startActivity(new Intent(this, Doctors.class));
                break;
            default:
                break;
        }
    }

    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
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
                json = new JSONObject(result);
                Button feedbackBtn = (Button) findViewById(R.id.btnFeedback);
                Button donate = (Button) findViewById(R.id.btnDonate);

                feedbackBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        new Thread(new Runnable() {
                            public void run() {
                                feedback();
                            }
                        }).start();
                    }
                });

                donate.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        try {
                            System.out.println(json.getString("DonateBtn"));
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(json.getString("DonateBtn")));
                            startActivity(browserIntent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout6);
                drawerView = (ListView) findViewById(R.id.drawerList6);

                myAdapter = new NavAdapter(Feedback.this);
                drawerView.setAdapter(myAdapter);
                drawerView.setOnItemClickListener(Feedback.this);

                drawerListener = new ActionBarDrawerToggle(Feedback.this, drawerLayout,
                        R.string.drawer_open, R.string.drawer_close) {
                };

                drawerLayout.setDrawerListener(drawerListener);
                drawerLayout.setScrimColor(Color.TRANSPARENT);

                getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
                getSupportActionBar().setCustomView(R.layout.action_bar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
                drawerListener.syncState();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    void feedback() {
        try {
            EditText eFeedback = (EditText) findViewById(R.id.feedback);
            feedbackStr = eFeedback.getText().toString().trim();

            httpclient = new DefaultHttpClient();
            httppost = new HttpPost("http://54.191.98.90/api/test1/add_feedback.php"); // make sure the url is correct.
            //add your data
            nameValuePairs = new ArrayList<NameValuePair>(2);
            // Always use the same variable name for posting i.e the android side variable name and php side variable name should be similar,

            nameValuePairs.add(new BasicNameValuePair("docID", String.valueOf(docID)));
            nameValuePairs.add(new BasicNameValuePair("feedback", feedbackStr));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            //Execute HTTP Post Request
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            System.out.println(response);

            if (response.contains("Feedback Sent")) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(Feedback.this, "Feedback Sent", Toast.LENGTH_SHORT).show();
                    }
                });
                startActivity(new Intent(Feedback.this, Doctors.class));
            } else {
                showAlert();
            }
        } catch (Exception e) {
            System.out.println("Exception : " + e.getMessage());
        }
    }

    public void showAlert() {
        Feedback.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(Feedback.this);
                builder.setTitle("Error.");
                builder.setMessage("Feedback Box Is Empty. Try Again.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }
}
