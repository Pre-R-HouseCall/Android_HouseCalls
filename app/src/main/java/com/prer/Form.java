package com.prer;

import android.app.Activity;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_form);

        eName = (EditText) findViewById(R.id.formName);
        eNumber = (EditText) findViewById(R.id.formNumber);
        eMedInfo = (EditText) findViewById(R.id.formMedInfo);
        eSymptoms = (EditText) findViewById(R.id.formSymptoms);
        eOther = (EditText) findViewById(R.id.formOther);

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
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            //Execute HTTP Post Request
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            System.out.println(response);

            SharedPreferences pref = getSharedPreferences("formDetails", 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("name", name);
            editor.putString("number", number);
            editor.putString("medInfo", medInfo);
            editor.commit();

            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(Form.this, "Form Sent", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Form.this, Waitlist.class));
                }
            });
        } catch(Exception e) {
            System.out.println("Exception : " + e.getMessage());
        }
    }
}
