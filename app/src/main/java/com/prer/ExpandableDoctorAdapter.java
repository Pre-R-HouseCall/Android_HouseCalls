package com.prer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static java.lang.Integer.parseInt;

/**
 * Created by Ryan on 4/6/2015.
 */
public class ExpandableDoctorAdapter extends BaseExpandableListAdapter{

    Context context;
    JSONArray content;
    String email;
    SharedPreferences logPrefs;
    SharedPreferences formPrefs;
    int status;

    public ExpandableDoctorAdapter(Context context, JSONArray content, String email, SharedPreferences logPrefs){
        this.context = context;
        this.content = content;
        this.email = email;
        this.logPrefs = logPrefs;

        formPrefs = context.getSharedPreferences("formDetails", 0);
        status = formPrefs.getInt("status", -1);
    }
    @Override
    public int getGroupCount() {
        //System.out.println("Group Count");
        return content.length();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        System.out.println("Children Count");
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        //System.out.println("Get Group");
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        System.out.println("Get Child");
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        //System.out.println("Get Group ID");
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        //System.out.println("Get Child ID");
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        //System.out.println("has Stable IDs");
        return false;
    }

    @Override
    public View getGroupView(int position, boolean isExpanded, View convertView, ViewGroup parent) {
        //System.out.println("Get Group View");;
        int id = -1;
        String str_id = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_doctor_item, parent, false);
        }
        JSONObject jsonObject = null;
        TextView docName = (TextView) convertView.findViewById(R.id.name);
        TextView docDistance = (TextView) convertView.findViewById(R.id.distance);

        try {
            jsonObject = (JSONObject) content.get(position);
            docName.setText(jsonObject.getString("FirstName") + " " + jsonObject.getString("LastName"));
            docDistance.setText(jsonObject.getString("Distance") + " miles away");
            str_id = jsonObject.getString("DoctorId")  ;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        JSONObject jsonObject;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_doctor_dropdown, parent, false);
        }
        try {
            TextView bio = (TextView) convertView.findViewById(R.id.shortBio);
            jsonObject = (JSONObject) content.get(groupPosition);
            System.out.println("Description: " + jsonObject.getString("Description"));
            bio.setText(jsonObject.getString("Description"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ImageButton form = (ImageButton) convertView.findViewById(R.id.form);
        ImageButton bio = (ImageButton) convertView.findViewById(R.id.profile);
        TextView seeMore = (TextView) convertView.findViewById(R.id.seeMore);

        final int ndx = groupPosition;

        form.setOnClickListener(new View.OnClickListener() {
            Intent myIntent;

            public void onClick(View view) {
                System.out.println("group position: " + ndx);

                if (email != null && status != 1) {
                    myIntent = new Intent(context, Form.class);
                } else if (status == 1) {
                    Toast.makeText(context, "You Have Already Requested A Call. Check the Waitroom.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Toast.makeText(context, "You Must Be Logged In To Sent A Form.", Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences.Editor editor = formPrefs.edit();
                editor.putInt("docID", ndx);
                editor.commit();

                context.startActivity(myIntent);
            }
        });

        seeMore.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                System.out.println("group position: " + ndx);
                Intent myIntent = new Intent(context, Bio.class);

                SharedPreferences.Editor editor = formPrefs.edit();
                editor.putInt("docID", ndx);
                editor.commit();

                context.startActivity(myIntent);
            }
        });

        bio.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                System.out.println("group position: " + ndx);
                Intent myIntent = new Intent(context, Bio.class);

                SharedPreferences.Editor editor = formPrefs.edit();
                editor.putInt("docID", ndx);
                editor.commit();

                context.startActivity(myIntent);
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
