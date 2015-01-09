package com.prer;

import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class Bio extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bio);

        Button form = (Button) findViewById(R.id.bio_form_button);
        Button back = (Button) findViewById(R.id.bio_back_button);

        form.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), Doctors.class);
                startActivityForResult(myIntent, 0);
            }
        });
    }
}
