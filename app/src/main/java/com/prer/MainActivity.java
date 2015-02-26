package com.prer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final Button login_button = (Button) findViewById(R.id.login_button);
		final Button sign_up_button = (Button) findViewById(R.id.sign_up_button);
		
		
		login_button.setOnClickListener(this);
		sign_up_button.setOnClickListener(this);

        Button back = (Button) findViewById(R.id.main_back_button);
        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), Doctors.class);
                startActivityForResult(myIntent, 0);
            }
        });
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String button_type= new String(this.getResources().getResourceEntryName(v.getId()));
		Intent login = new Intent(this, Login.class);
		Intent sign_up = new Intent(this, SignUp.class);
		Intent selected_intent;
		
		selected_intent = button_type.equals("login_button") ? login : sign_up;
		
		this.startActivity(selected_intent);
		
	}
}
