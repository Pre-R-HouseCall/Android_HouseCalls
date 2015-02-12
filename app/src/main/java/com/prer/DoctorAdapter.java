package com.prer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ryan on 2/11/2015.
 */
public class DoctorAdapter extends BaseAdapter{
    Context context;
    JSONArray content;

    public DoctorAdapter(Context context, JSONArray content) {
        this.context = context;
        this.content = content;
    }

    @Override
    public int getCount() {
        return content.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return content.get(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Object();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.doctor_item, parent, false);
        }
        JSONObject jsonObject = null;
        TextView docName = (TextView) convertView.findViewById(R.id.name);
        TextView docDistance = (TextView) convertView.findViewById(R.id.distance);

        try {
            jsonObject = (JSONObject) content.get(position);
            docName.setText(jsonObject.getString("FirstName") + " " + jsonObject.getString("LastName"));
            docDistance.setText(jsonObject.getString("Distance"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
