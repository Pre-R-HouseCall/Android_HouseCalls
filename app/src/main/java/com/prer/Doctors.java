package com.prer;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.os.Bundle;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


public class Doctors extends ActionBarActivity implements AdapterView.OnItemClickListener {
    String email;
    SharedPreferences logPrefs;
    SharedPreferences formPrefs;
    JSONArray json;
    ExpandableDoctorAdapter adapter;
    ExpandableListView listView;
    View curGroup;
    private DrawerLayout drawerLayout;
    private ListView drawerView;
    private ActionBarDrawerToggle drawerListener;
    private NavAdapter myAdapter;
    int status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        JSONAsyncTask task = new JSONAsyncTask();
        // Execute task that grabs all doctors
        task.execute(new String[] { "http://54.191.98.90/api/ios_connect/getAllDoctors.php" });
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
                    Toast.makeText(Doctors.this, "You Have Not Requested A Call", Toast.LENGTH_SHORT).show();
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
            logPrefs = getSharedPreferences("loginDetails", 0);
            formPrefs = getSharedPreferences("formDetails", 0);
            email = logPrefs.getString("email", null);
            status = formPrefs.getInt("status", -1);

            try {
                json = new JSONArray(result);
                setContentView(R.layout.activity_doctor_list);
                listView = (ExpandableListView) findViewById(R.id.listView);
                adapter = new ExpandableDoctorAdapter(Doctors.this, json, email, logPrefs);

                for(int i =0; i < json.length(); i++) {
                    adapter.getGroupView(i, false, null, null);
                    adapter.getChildView(i, 0, true, null, null);
                }
                //System.out.println("SETTING THE ADAPTER");
                listView.setAdapter((ExpandableListAdapter) adapter);
                //System.out.println("SET THE ADAPTER");
                listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                        System.out.println("Clicked a group");
                        return false;
                    }
                });
                listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v,
                                                int groupPosition, int childPosition, long id) {
                        System.out.println("Clicked a doctor");
                        parent.expandGroup(groupPosition);
                        return false;
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }

            drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout0);
            drawerView = (ListView) findViewById(R.id.drawerList0);

            myAdapter = new NavAdapter(Doctors.this);
            drawerView.setAdapter(myAdapter);
            drawerView.setOnItemClickListener(Doctors.this);

            drawerListener = new ActionBarDrawerToggle(Doctors.this, drawerLayout,
                    R.string.drawer_open, R.string.drawer_close) {
            };

            drawerLayout.setDrawerListener(drawerListener);
            drawerLayout.setScrimColor(Color.TRANSPARENT);

            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.action_bar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            drawerListener.syncState();
        }
    }
}
