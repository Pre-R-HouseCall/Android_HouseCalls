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
import android.widget.CheckBox;
import android.widget.EditText;
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

public class Form extends ActionBarActivity implements AdapterView.OnItemClickListener {
    String name, email, number, addr, city, state, zip;
    EditText eName, eEmail, eNumber, eAddr, eCity, eState, eZip;
    EditText eSymptoms;
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
		setContentView(R.layout.activity_form);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        eName = (EditText) findViewById(R.id.formName);
        eEmail = (EditText) findViewById(R.id.formEmail);
        eNumber = (EditText) findViewById(R.id.formNumber);
        eAddr = (EditText) findViewById(R.id.formAddr);
        eCity = (EditText) findViewById(R.id.formCity);
        eState = (EditText) findViewById(R.id.formState);
        eZip = (EditText) findViewById(R.id.formZipCode);
        eSymptoms = (EditText) findViewById(R.id.formSymptoms);

        logPrefs = getSharedPreferences("loginDetails", 0);
        userID = logPrefs.getInt("userID", -1);

        formPrefs = getSharedPreferences("formDetails", 0);
        name = formPrefs.getString("name", null);
        status = formPrefs.getInt("status", -1);
        dateTime = formPrefs.getString("dateTime", null);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout5);
        listView = (ListView) findViewById(R.id.drawerList5);

        myAdapter = new NavAdapter(this);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(this);

        drawerListener = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.drawer_open, R.string.drawer_close) {
        };

        drawerLayout.setDrawerListener(drawerListener);
        drawerLayout.setScrimColor(Color.TRANSPARENT);

        if (name != null) {
            email = formPrefs.getString("email", null);
            number = formPrefs.getString("number", null);
            addr = formPrefs.getString("addr", null);
            city = formPrefs.getString("city", null);
            state = formPrefs.getString("state", null);
            zip = formPrefs.getString("zip", null);

            eName.setText(name);
            eEmail.setText(email);
            eNumber.setText(number);
            eAddr.setText(addr);
            eCity.setText(city);
            eState.setText(state);
            eZip.setText(zip);
        }

        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        checkBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (((CheckBox) view).isChecked()) {
                    check();
                }
            }
        });

        Button callBtn = (Button) findViewById(R.id.callBtn);
        callBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {
                        form();
                    }
                }).start();
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
                else
                    Toast.makeText(Form.this, "You Have Not Requested A Call", Toast.LENGTH_SHORT).show();
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
                    email = null;

                    startActivity(new Intent(this, Doctors.class));
                }
                break;

            default:
                break;
        }
    }

    void form() {
        try {
            Intent intent = getIntent();
            String docId = intent.getStringExtra("docId");

            name = eName.getText().toString().trim();
            email = eEmail.getText().toString().trim();
            number = eNumber.getText().toString().trim();
            addr = eAddr.getText().toString().trim();
            city = eCity.getText().toString().trim();
            state = eState.getText().toString().trim();
            zip = eZip.getText().toString().trim();

            httpclient=new DefaultHttpClient();
            // WRITE A SCRIPT AND PASS IT "docId" TO SEND THE FORM ONLY TO THAT DOCTOR
            httppost= new HttpPost("http://54.191.98.90/api/test1/add_form.php"); // make sure the url is correct.
            //add your data
            nameValuePairs = new ArrayList<NameValuePair>(2);
            // Always use the same variable name for posting i.e the android side variable name and php side variable name should be similar,
            nameValuePairs.add(new BasicNameValuePair("name", name));  // $Edittext_value = $_POST['Edittext_value'];
            nameValuePairs.add(new BasicNameValuePair("email", email));
            nameValuePairs.add(new BasicNameValuePair("number", number));
            nameValuePairs.add(new BasicNameValuePair("addr", addr));
            nameValuePairs.add(new BasicNameValuePair("city", city));
            nameValuePairs.add(new BasicNameValuePair("state", state));
            nameValuePairs.add(new BasicNameValuePair("zip", zip));
            nameValuePairs.add(new BasicNameValuePair("symptoms", eSymptoms.getText().toString().trim()));
            nameValuePairs.add(new BasicNameValuePair("userID", String.valueOf(userID)));
            nameValuePairs.add(new BasicNameValuePair("docID", docId));
            nameValuePairs.add(new BasicNameValuePair("status", "0")); // checks if there is an answered request
            nameValuePairs.add(new BasicNameValuePair("dateTime", dateTime));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            //Execute HTTP Post Request
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String response = httpclient.execute(httppost, responseHandler);
            System.out.println(response);

            int index = response.indexOf("\n");
            dateTime = response.substring(index + 1);

            if (!response.contains("Missing Name, Number, or Symptoms")) {
                SharedPreferences pref = getSharedPreferences("formDetails", 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("name", name);
                editor.putString("email", email);
                editor.putString("number", number);
                editor.putString("addr", addr);
                editor.putString("city", city);
                editor.putString("zip", zip);
                editor.putString("state", state);
                editor.putInt("status", 1);
                editor.putString("dateTime", dateTime);
                editor.commit();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(Form.this, "Form Sent", Toast.LENGTH_SHORT).show();
                    }
                });
                startActivity(new Intent(Form.this, Waitroom.class));
            } else {
                showAlert();
            }
        } catch(Exception e) {
            System.out.println("Exception : " + e.getMessage());
        }
    }

    public void check() {
        Form.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(Form.this);
                builder.setMessage("Box is checked.")
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

    public void showAlert() {
        Form.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(Form.this);
                builder.setTitle("Error: ");
                builder.setMessage("Missing Name, Number, or Symptoms. Try Again.")
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
