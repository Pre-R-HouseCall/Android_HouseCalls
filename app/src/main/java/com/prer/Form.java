package com.prer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class Form extends Activity {
    String name;
    String number;
    String medInfo;
    EditText eName;
    EditText eNumber;
    EditText eMedInfo;
    EditText eSymptoms;
    EditText eOther;
    HttpPost httppost;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;
    int userID;
    int docID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_form);

        eName = (EditText) findViewById(R.id.formName);
        eNumber = (EditText) findViewById(R.id.formNumber);
        eMedInfo = (EditText) findViewById(R.id.formMedInfo);
        eSymptoms = (EditText) findViewById(R.id.formSymptoms);
        eOther = (EditText) findViewById(R.id.formOther);

        SharedPreferences logPrefs = getSharedPreferences("loginDetails", 0);
        userID = logPrefs.getInt("userID", -1);
        docID = logPrefs.getInt("docID", -1);

        SharedPreferences formPrefs = getSharedPreferences("formDetails", 0);
        name = formPrefs.getString("name", null);

        if (name != null) {
            number = formPrefs.getString("number", null);
            medInfo = formPrefs.getString("medInfo", null);

            eName.setText(name);
            eNumber.setText(number);
            eMedInfo.setText(medInfo);
        }

        Button done = (Button) findViewById(R.id.done_button);
        done.setOnClickListener(new View.OnClickListener() {
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
            name = eName.getText().toString().trim();
            number = eNumber.getText().toString().trim();
            medInfo = eMedInfo.getText().toString().trim();

            httpclient=new DefaultHttpClient();
            httppost= new HttpPost("http://54.191.98.90/api/test1/add_form.php"); // make sure the url is correct.
            //add your data
            nameValuePairs = new ArrayList<NameValuePair>(2);
            // Always use the same variable name for posting i.e the android side variable name and php side variable name should be similar,
            nameValuePairs.add(new BasicNameValuePair("name", name));  // $Edittext_value = $_POST['Edittext_value'];
            nameValuePairs.add(new BasicNameValuePair("number", number));
            nameValuePairs.add(new BasicNameValuePair("medInfo", medInfo));
            nameValuePairs.add(new BasicNameValuePair("symptoms", eSymptoms.getText().toString().trim()));
            nameValuePairs.add(new BasicNameValuePair("other", eOther.getText().toString().trim()));
            nameValuePairs.add(new BasicNameValuePair("userID", String.valueOf(userID)));
            nameValuePairs.add(new BasicNameValuePair("docID", String.valueOf(docID)));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            //Execute HTTP Post Request
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            System.out.println(response);

            if (response.equalsIgnoreCase("Form added\n") || response.equalsIgnoreCase("Form updated\n")) {
                SharedPreferences pref = getSharedPreferences("formDetails", 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("name", name);
                editor.putString("number", number);
                editor.putString("medInfo", medInfo);
                editor.commit();

                runOnUiThread(new Runnable() {
                    public void run() {
                        if (response.equalsIgnoreCase("Form added\n")) {
                            Toast.makeText(Form.this, "Form Sent", Toast.LENGTH_SHORT).show();
                        } else{
                            Toast.makeText(Form.this, "Form Updated", Toast.LENGTH_SHORT).show();
                        }
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
