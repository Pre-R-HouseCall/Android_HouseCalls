package com.prer;

import com.loopj.android.http.*;
import org.apache.http.Header;
import org.json.JSONObject;
import org.json.JSONException;

import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Waitlist extends ActionBarActivity {

    private int queuePosition;
    private int mNotifyId;
    private CharSequence msg;
    NotificationCompat.Builder mBuilder;
    NotificationManager mNotifyMgr;

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

        Button refresh = (Button) findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                refresh();
            }
        });

        mNotifyId = 001;
        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        getQueuePosition();
	}

    private void getQueuePosition() {

        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                String qp;
                TextView display = (android.widget.TextView) findViewById(R.id.waitroomPosition);

                try {
                    qp = response.getString("QueuePosition");
                    queuePosition = Integer.parseInt(qp);

                    if (queuePosition == 1) {
                        qp = qp.concat("st");
                    }
                    else if (queuePosition == 2) {
                        qp = qp.concat("nd");
                    }
                    else if (queuePosition == 3) {
                        qp = qp.concat("rd");
                    }
                    else {
                        qp = qp.concat("th");
                    }

                    msg = new String("You are " + qp + " in line for Dr. Slishman");
                    display.setText(msg);
                    createNotif();
                }
                catch (JSONException e) {

                }
            }
        };

        RestClient.get(this, "getQueuePosition/1", null, responseHandler);
    }

    private void createNotif() {

        mBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Waitroom Position")
                        .setContentText(msg);

        mNotifyMgr.notify(mNotifyId, mBuilder.build());
    }

    private void refresh() {
        //Update screen message?
    }
}
