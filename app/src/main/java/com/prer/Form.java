package com.prer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

public class Form extends ActionBarActivity {
    String name, email, number, addr, city, state, zip;
    EditText eName, eEmail, eNumber, eAddr, eCity, eState, eZip;
    EditText eSymptoms;
    HttpPost httppost;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;
    int userID;

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
        eZip = (EditText) findViewById(R.id.formState);
        eSymptoms = (EditText) findViewById(R.id.formSymptoms);

        SharedPreferences logPrefs = getSharedPreferences("loginDetails", 0);
        userID = logPrefs.getInt("userID", -1);

        SharedPreferences formPrefs = getSharedPreferences("formDetails", 0);
        name = formPrefs.getString("name", null);

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
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            //Execute HTTP Post Request
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            System.out.println(response);

            if (response.contains("Form added") || response.contains("Form updated")) {
                SharedPreferences pref = getSharedPreferences("formDetails", 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("name", name);
                editor.putString("email", email);
                editor.putString("number", number);
                editor.putString("addr", addr);
                editor.putString("city", city);
                editor.putString("state", state);
                editor.putString("zip", zip);
                editor.commit();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(Form.this, "Form Sent", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Form.this, Waitlist.class));
                    }
                });
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
