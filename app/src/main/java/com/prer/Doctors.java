package com.prer;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONObject;

public class Doctors extends ActionBarActivity  {
    String username;
    Intent myIntent;
    SharedPreferences logPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);

        ImageButton form = (ImageButton) findViewById(R.id.doctor3_action);
        ImageButton bio = (ImageButton) findViewById(R.id.doctor3_profile);
        Button logout = (Button) findViewById(R.id.logout);

        logPrefs = getSharedPreferences("loginDetails", 0);
        username = logPrefs.getString("username", null);

        logout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                SharedPreferences.Editor editor = logPrefs.edit();
                editor.clear();
                editor.commit();
                username = null;

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(Doctors.this, "Logged Out", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });



        // response is the json string sent back from the Doctors database
//        JSONObject json = new JSONObject(response);

        SharedPreferences sp = getSharedPreferences("loginDetails", 0);
        SharedPreferences.Editor spEdit = sp.edit();

        // docID is the key name in the "loginDetails"
        // Assuming an entry from the Doctor database is json
        // json.getInt("DoctorId") places the value of DoctorId into docID
//        spEdit.putInt("docID", json.getInt("DoctorId"));

        // ex: comment out the json lines and hardcode DoctorID
        spEdit.putInt("docID", 3);
        spEdit.commit();




        form.setOnClickListener(new View.OnClickListener() {

	        public void onClick(View view) {
                if (username != null) {
                    myIntent = new Intent(view.getContext(), Form.class);
                } else {
                    myIntent = new Intent(view.getContext(), MainActivity.class);
                }

	            startActivityForResult(myIntent, 0);
	        }
	    });

        bio.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), Bio.class);
                startActivityForResult(myIntent, 0);
            }
        });

    }
}
