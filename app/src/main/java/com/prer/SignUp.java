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

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignUp extends ActionBarActivity {
    EditText eName, eEmail, eUser, ePass, eNumber, eAddr, eCity, eState, eZip;
    Button b;
    TextView tv;
    HttpPost httppost;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;
    ProgressDialog dialog = null;
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        b = (Button) findViewById(R.id.btnSignUp);
        tv = (TextView)findViewById(R.id.tv0);

        eName = (EditText) findViewById(R.id.signUpName);
        eEmail = (EditText) findViewById(R.id.signUpEmail);
        eUser = (EditText) findViewById(R.id.signUpUsername);
        ePass = (EditText) findViewById(R.id.signUpPassword);
        eNumber = (EditText) findViewById(R.id.signUpNumber);
        eAddr = (EditText) findViewById(R.id.signUpAddr);
        eCity = (EditText) findViewById(R.id.signUpCity);
        eState = (EditText) findViewById(R.id.signUpState);
        eZip = (EditText) findViewById(R.id.signUpZipCode);

        checkBox = (CheckBox) findViewById(R.id.signUpCheckBox);

/*        checkBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (((CheckBox) view).isChecked()) {
                    check();
                }
            }
        });
*/
        b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = ProgressDialog.show(SignUp.this, "",
                        "Adding user...", true);
                new Thread(new Runnable() {
                    public void run() {
                        signUp();
                    }
                }).start();
            }
        });
    }

    void signUp(){
        try{
            httpclient=new DefaultHttpClient();
            httppost= new HttpPost("http://54.191.98.90/api/test1/add_user.php"); // make sure the url is correct.
            //add your data
            nameValuePairs = new ArrayList<NameValuePair>(2);
            // Always use the same variable name for posting i.e the android side variable name and php side variable name should be similar,
            nameValuePairs.add(new BasicNameValuePair("username",eUser.getText().toString().trim()));  // $Edittext_value = $_POST['Edittext_value'];
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

            System.out.println("return: " + response.contains("User Found"));

            if (response.contains("User Found")) {
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
}
