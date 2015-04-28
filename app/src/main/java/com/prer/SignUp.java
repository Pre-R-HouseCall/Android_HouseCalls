package com.prer;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SignUp extends ActionBarActivity implements AdapterView.OnItemClickListener {
    EditText eName, eEmail, ePass, eNumber, eAddr, eCity, eState, eZip;
    Button b;
    TextView tv;
    HttpPost httppost;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;
    ProgressDialog dialog = null;
    CheckBox checkBox;
    private DrawerLayout drawerLayout;
    private ListView listView;
    private ActionBarDrawerToggle drawerListener;
    private NavAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout2);
        listView = (ListView) findViewById(R.id.drawerList2);

        myAdapter = new NavAdapter(this);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(this);

        drawerListener = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.drawer_open, R.string.drawer_close) {
        };

        drawerLayout.setDrawerListener(drawerListener);
        drawerLayout.setScrimColor(Color.TRANSPARENT);

        b = (Button) findViewById(R.id.btnSignUp);
        tv = (TextView)findViewById(R.id.tv0);

        eName = (EditText) findViewById(R.id.signUpName);
        eEmail = (EditText) findViewById(R.id.signUpEmail);
        ePass = (EditText) findViewById(R.id.signUpPassword);
        eNumber = (EditText) findViewById(R.id.signUpNumber);
        eAddr = (EditText) findViewById(R.id.signUpAddr);
        eCity = (EditText) findViewById(R.id.signUpCity);
        eState = (EditText) findViewById(R.id.signUpState);
        eZip = (EditText) findViewById(R.id.signUpZipCode);

        checkBox = (CheckBox) findViewById(R.id.signUpCheckBox);

        b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked() == false) {
                    showAlert2();
                } else {
                    dialog = ProgressDialog.show(SignUp.this, "",
                            "Adding user...", true);
                    new Thread(new Runnable() {
                        public void run() {
                            signUp();
                        }
                    }).start();
                }
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
                startActivity(new Intent(this, Login.class));
                break;
            case 2:
                startActivity(new Intent(this, SignUp.class));
                break;

            default:
                break;
        }
    }

    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    void signUp(){
        try{
            httpclient=new DefaultHttpClient();
            httppost= new HttpPost("http://54.191.98.90/api/test1/add_user.php"); // make sure the url is correct.
            //add your data
            nameValuePairs = new ArrayList<NameValuePair>(2);
            // Always use the same variable name for posting i.e the android side variable name and php side variable name should be similar,
            nameValuePairs.add(new BasicNameValuePair("password",ePass.getText().toString().trim()));
            nameValuePairs.add(new BasicNameValuePair("name",eName.getText().toString().trim()));
            nameValuePairs.add(new BasicNameValuePair("email",eEmail.getText().toString().trim()));
            nameValuePairs.add(new BasicNameValuePair("number",eNumber.getText().toString().trim()));
            nameValuePairs.add(new BasicNameValuePair("addr",eAddr.getText().toString().trim()));
            nameValuePairs.add(new BasicNameValuePair("city",eCity.getText().toString().trim()));
            nameValuePairs.add(new BasicNameValuePair("state",eState.getText().toString().trim()));
            nameValuePairs.add(new BasicNameValuePair("zip",eZip.getText().toString().trim()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            //Execute HTTP Post Request
            ResponseHandler<String> responseHandler = new BasicResponseHandler();

            final String response = httpclient.execute(httppost, responseHandler);

            System.out.println("Response : " + response);
            runOnUiThread(new Runnable() {
                public void run() {
                    tv.setText("Response from PHP : " + response);
                    dialog.dismiss();
                }
            });

            if (response.contains("Email Already Exists")) {
                showAlert0();
            } else if (response.contains("Missing Required field(s)")) {
                showAlert1();
            } else {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(SignUp.this,"SignUp Success", Toast.LENGTH_SHORT).show();
                    }
                });

                startActivity(new Intent(SignUp.this, Login.class));
            }
        } catch (Exception e){
            dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());
        }
    }

    public void showAlert0() {
        SignUp.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                builder.setTitle("SignUp Error:");
                builder.setMessage("Username Already Exist. Try Again.")
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

    public void showAlert1() {
        SignUp.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                builder.setTitle("SignUp Error:");
                builder.setMessage("Incomplete Sign Up. Try Again.")
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

    public void showAlert2() {
        SignUp.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                builder.setTitle("SignUp Error:");
                builder.setMessage("You Must Agree To The Disclaimer.")
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
