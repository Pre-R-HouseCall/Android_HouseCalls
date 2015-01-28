package com.prer;

import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.os.Bundle;
import android.content.Intent;
import android.widget.ImageButton;

public class Doctors extends ActionBarActivity  {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);

        ImageButton form = (ImageButton) findViewById(R.id.doctor3_action);
        ImageButton bio = (ImageButton) findViewById(R.id.doctor3_profile);

        form.setOnClickListener(new View.OnClickListener() {

	        public void onClick(View view) {
	            Intent myIntent = new Intent(view.getContext(), MainActivity.class);
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
