package com.prer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;


public class Waitroom extends ActionBarActivity implements AdapterView.OnItemClickListener {
    HttpPost httppost;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;
    int userID;
    private DrawerLayout drawerLayout;
    private ListView listView;
    private ActionBarDrawerToggle drawerListener;
    private NavAdapter myAdapter;
    SharedPreferences logPrefs;
    SharedPreferences formPrefs;
    int status;
    String dateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waitroom);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        logPrefs = getSharedPreferences("loginDetails", 0);
        userID = logPrefs.getInt("userID", -1);
        formPrefs = getSharedPreferences("formDetails", 0);
        dateTime = formPrefs.getString("dateTime", null);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout3);
        listView = (ListView) findViewById(R.id.drawerList3);

        myAdapter = new NavAdapter(this);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(this);

        drawerListener = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.drawer_open, R.string.drawer_close) {
        };

        drawerLayout.setDrawerListener(drawerListener);
        drawerLayout.setScrimColor(Color.TRANSPARENT);

        new Thread(new Runnable() {
            public void run() {
                getStatus();
            }
        }).start();

        Button withdrawBtn = (Button) findViewById(R.id.btnWithdraw);
        withdrawBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {
                        withdraw();
                    }
                }).start();
            }
        });

        Button updateFormBtn = (Button) findViewById(R.id.btnUpdateForm);
        updateFormBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), Form.class);
                startActivityForResult(myIntent, 0);
            }
        });

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerListener.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerListener.syncState();
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
        listView.setItemChecked(position, true);
        listView.setSelection(position);
//            setTitle(navMenuTitles[position]);
        drawerLayout.closeDrawer(listView);

        switch (position) {
            case 0:
                startActivity(new Intent(this, Doctors.class));
                break;
            case 1:
                if (status == 1)
                    startActivity(new Intent(this, Waitroom.class));
                else if (status == -1)
                    startActivity(new Intent(this, Login.class));
                break;
            case 2:
                if (userID == -1) {
                    startActivity(new Intent(this, SignUp.class));
                } else {
                    SharedPreferences.Editor editor = logPrefs.edit();
                    editor.clear();
                    editor.commit();
                    SharedPreferences.Editor edit = formPrefs.edit();
                    edit.clear();
                    edit.commit();

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

    void getStatus() {
        try {
            httpclient = new DefaultHttpClient();
            // WRITE A SCRIPT AND PASS IT "docId" TO SEND THE FORM ONLY TO THAT DOCTOR
            httppost = new HttpPost("http://54.191.98.90/api/test1/waitroom.php"); // make sure the url is correct.
            //add your data
            nameValuePairs = new ArrayList<NameValuePair>(2);
            // Always use the same variable name for posting i.e the android side variable name and php side variable name should be similar,

            nameValuePairs.add(new BasicNameValuePair("userID", String.valueOf(userID)));
            nameValuePairs.add(new BasicNameValuePair("dateTime", dateTime));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            System.out.println(userID + " " + dateTime);

            //Execute HTTP Post Request
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            System.out.println(response);

            if (response.contains("1")) {

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(Waitroom.this, "You're Requested Has Been Answered. You May Request Another Call",
                         Toast.LENGTH_SHORT).show();
                    }
                });

                SharedPreferences.Editor editor = formPrefs.edit();
                editor.putInt("status", 0);
                editor.putString("dateTime", null);
                editor.commit();

                startActivity(new Intent(this, Doctors.class));
            }
        } catch (Exception e) {
            System.out.println("Exception : " + e.getMessage());
        }
    }

    void withdraw() {
        try {
//            Intent intent = getActivity().getIntent();
//            String docId = intent.getStringExtra("docId");

            httpclient = new DefaultHttpClient();
            // WRITE A SCRIPT AND PASS IT "docId" TO SEND THE FORM ONLY TO THAT DOCTOR
            httppost = new HttpPost("http://54.191.98.90/api/test1/delete_form.php"); // make sure the url is correct.
            //add your data
            nameValuePairs = new ArrayList<NameValuePair>(2);
            // Always use the same variable name for posting i.e the android side variable name and php side variable name should be similar,

            nameValuePairs.add(new BasicNameValuePair("userID", String.valueOf(userID)));
//            nameValuePairs.add(new BasicNameValuePair("docID", docId));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            //Execute HTTP Post Request
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            System.out.println(response);

            if (response.contains("Form deleted")) {
                SharedPreferences pref = getSharedPreferences("formDetails", 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("status", 0);
                editor.putString("dateTime", null);
                editor.commit();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(Waitroom.this, "Form Withdrawn", Toast.LENGTH_SHORT).show();
                    }
                });
                startActivity(new Intent(Waitroom.this, Doctors.class));
            } else {
                showAlert();
            }
        } catch (Exception e) {
            System.out.println("Exception : " + e.getMessage());
        }
    }

    public void showAlert() {
        runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(Waitroom.this);
                builder.setTitle("Error: ");
                builder.setMessage("Form failed to be withdrawn.")
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
