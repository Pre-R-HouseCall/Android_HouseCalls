package com.prer;

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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class Bio extends ActionBarActivity implements AdapterView.OnItemClickListener {
    String email;
    Intent myIntent;
    String docId;
    JSONObject json;
    private DrawerLayout drawerLayout;
    private ListView drawerView;
    private ActionBarDrawerToggle drawerListener;
    private NavAdapter myAdapter;
    SharedPreferences logPrefs;
    SharedPreferences formPrefs;
    int status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logPrefs = getSharedPreferences("loginDetails", 0);
        email = logPrefs.getString("email", null);

        formPrefs = getSharedPreferences("formDetails", 0);
        status = formPrefs.getInt("status", -1);

        Intent intent = getIntent();
        docId = intent.getStringExtra("docId");

        System.out.println("Bio Page - docId = " + docId);
        JSONAsyncTask task = new JSONAsyncTask();
        task.execute(new String[] { "http://54.191.98.90/api/bioTest/bioQuery.php?doctorID=" + docId });
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
                if (status == 1)
                    startActivity(new Intent(this, Waitroom.class));
                else if (status == -1)
                    startActivity(new Intent(this, Login.class));
                else
                    Toast.makeText(Bio.this, "You Have Not Requested A Call", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                if (email == null) {
                    startActivity(new Intent(this, SignUp.class));
                } else {
                    SharedPreferences.Editor editor = logPrefs.edit();
                    editor.clear();
                    editor.commit();
                    SharedPreferences.Editor edit = formPrefs.edit();
                    edit.clear();
                    edit.commit();
                    email = null;

                    startActivity(new Intent(this, Doctors.class));
                }
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
                setContentView(R.layout.activity_bio);
                TextView name = (TextView) findViewById(R.id.name);
                TextView description = (TextView) findViewById(R.id.description);
                Button form = (Button) findViewById(R.id.bio_form_button);
                Button donate = (Button) findViewById(R.id.donateBtn);
                name.setText(json.getString("FirstName") + " " + json.getString("LastName"));
                description.setText(json.getString("Description"));

                form.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View view) {
                        if (email != null && status != 1) {
                            myIntent = new Intent(view.getContext(), Form.class);
                        } else if (status == 1) {
                            Toast.makeText(Bio.this, "You Have Already Requested A Call. Check the Waitroom.", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            myIntent = new Intent(view.getContext(), Login.class);
                        }
                        myIntent.putExtra("docId", docId);
                        startActivityForResult(myIntent, 0);
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

                drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout4);
                drawerView = (ListView) findViewById(R.id.drawerList4);

                myAdapter = new NavAdapter(Bio.this);
                drawerView.setAdapter(myAdapter);
                drawerView.setOnItemClickListener(Bio.this);

                drawerListener = new ActionBarDrawerToggle(Bio.this, drawerLayout,
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
}
