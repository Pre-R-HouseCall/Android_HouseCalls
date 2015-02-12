package com.prer;

import com.loopj.android.http.*;
import org.apache.http.Header;
import org.json.JSONObject;
import org.json.JSONException;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

        queuePosition();
	}

    private void queuePosition() {
        RequestParams params = new RequestParams("UserId", "1");

        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String queuePosition;
                int qp;
                CharSequence title;
                TextView display = (android.widget.TextView) findViewById(R.id.waitroomPosition);

                try {
                    queuePosition = response.getString("QueuePosition");
                    qp = Integer.parseInt(queuePosition);
                    System.out.println("QueuePosition " + queuePosition);

                    if (qp == 1) {
                        queuePosition = queuePosition.concat("st");
                    }
                    else if (qp == 2) {
                        queuePosition = queuePosition.concat("nd");
                    }
                    else if (qp == 3) {
                        queuePosition = queuePosition.concat("rd");
                    }
                    else {
                        queuePosition = queuePosition.concat("th");
                    }

                    title = new String("You are " + queuePosition + " in line for Dr. Slishman.");
                    display.setText(title);
                }
                catch (JSONException e) {

                }
            }
        };

        RestClient.get("queuePosition.php", params, responseHandler);
    }
}
