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
		setContentView(R.layout.activity_doctors);
		
		ImageButton next = (ImageButton) findViewById(R.id.waitlistform);
	    next.setOnClickListener(new View.OnClickListener() {
    	
	        public void onClick(View view) {
	            Intent myIntent = new Intent(view.getContext(), Waitlist.class);
	            startActivityForResult(myIntent, 0);
	        }
	    });
	}
}
