package com.prer;


import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Waitlist extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_waitlist);
		
		Button next = (Button) findViewById(R.id.home);
	    next.setOnClickListener(new View.OnClickListener() {
    	
	        public void onClick(View view) {
	            Intent myIntent = new Intent(view.getContext(), Doctors.class);
	            startActivityForResult(myIntent, 0);
	        }
	    });
	}
}
