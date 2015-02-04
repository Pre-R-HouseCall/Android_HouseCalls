package com.prer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Form extends Activity {
    String name;
    String contact;
    String medInfo;
    String symptoms;
    String other;
    EditText eName;
    EditText eContact;
    EditText eMedInfo;
    EditText eSymptoms;
    EditText eOther;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_form);

        eName = (EditText) findViewById(R.id.formName);
        eContact = (EditText) findViewById(R.id.formContact);
        eMedInfo = (EditText) findViewById(R.id.formMedInfo);
        eSymptoms = (EditText) findViewById(R.id.formSymptoms);
        eOther = (EditText) findViewById(R.id.formOther);

        SharedPreferences formPrefs = getSharedPreferences("formDetails", 0);
        name = formPrefs.getString("name", null);

        if (name != null) {
            contact = formPrefs.getString("contact", null);
            medInfo = formPrefs.getString("medInfo", null);
            symptoms = formPrefs.getString("symptoms", null);
            other = formPrefs.getString("other", null);

            eName.setText(name);
            eContact.setText(contact);
            eMedInfo.setText(medInfo);
            eSymptoms.setText(symptoms);
            eOther.setText(other);
        }

        Button done = (Button) findViewById(R.id.done_button);
        done.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new Thread(new Runnable() {
                    public void run() {
                        form();
                    }
                }).start();
            }
        });
	}

    void form() {
        SharedPreferences sp = getSharedPreferences("formDetails", 0);
        SharedPreferences.Editor spEdit = sp.edit();
        spEdit.putString("name", name);
        spEdit.putString("contact", contact);
        spEdit.putString("medInfo", medInfo);
        spEdit.putString("symptoms", symptoms);
        spEdit.putString("other", other);
        spEdit.commit();

        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(Form.this, "Form Sent", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Form.this, Waitlist.class));
            }
        });
    }
}
