package com.prer;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Email extends Activity {
	Button btnOK;
    EditText txtTo;
    EditText txtSubject;
    EditText txtMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_email);
		btnOK = (Button) findViewById(R.id.btnOK);
        txtTo = (EditText) findViewById(R.id.etTo);
        txtTo.requestFocus();
        txtSubject = (EditText) findViewById(R.id.etSubject);
        txtMessage = (EditText) findViewById(R.id.etMessage);
        
        Button form = (Button) findViewById(R.id.btnForm);
        form.setOnClickListener(new View.OnClickListener() {
        	
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), Form.class);
                startActivityForResult(myIntent, 0);
            }
        });
        
        btnOK.setOnClickListener(new View.OnClickListener() {
            
        	public void onClick(View view) {
                String to = txtTo.getText().toString();
                String subject = txtSubject.getText().toString();
                String message = txtMessage.getText().toString();
                Intent mail = new Intent(Intent.ACTION_SEND);
                mail.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
                mail.putExtra(Intent.EXTRA_SUBJECT, subject);
                mail.putExtra(Intent.EXTRA_TEXT, message);
                mail.setType("message/rfc822");
                startActivity(Intent.createChooser(mail, "Send email via:"));                
            }    	
        });
	}
}
