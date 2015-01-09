package com.prer;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class SignUp extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		
		Button next = (Button) findViewById(R.id.btnLogin);
		next.setOnClickListener(new View.OnClickListener() {
        	
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), Form.class);
                startActivityForResult(myIntent, 0);
            }
        });
	}
}
