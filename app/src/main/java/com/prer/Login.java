package com.prer;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class Login extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		Button next = (Button) findViewById(R.id.btnLogin);
		next.setOnClickListener(new View.OnClickListener() {
        	
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), Doctors.class);
                startActivityForResult(myIntent, 0);
            }
        });
	}
}
