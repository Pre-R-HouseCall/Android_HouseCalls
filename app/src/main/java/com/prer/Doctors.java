package com.prer;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;

public class Doctors extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_doctor_list);
		
		ImageButton form = (ImageButton) findViewById(R.id.doctor3_action);
	    form.setOnClickListener(new View.OnClickListener() {
    	
	        public void onClick(View view) {
	            Intent myIntent = new Intent(view.getContext(), Waitlist.class);
	            startActivityForResult(myIntent, 0);
	        }
	    });
	}
}
