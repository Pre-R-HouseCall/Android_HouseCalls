package com.prer;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by Winifred on 4/21/2015.
 */
public class NavAdapter extends BaseAdapter {
    private Context context;
    String[] list;
    String email;

    public NavAdapter (Context context) {
        this.context = context;

        SharedPreferences logPrefs = context.getSharedPreferences("loginDetails", 0);
        email = logPrefs.getString("email", null);

        if (email == null)
            list = context.getResources().getStringArray(R.array.Guestlist);
        else
            list = context.getResources().getStringArray(R.array.Userlist);
    }

    @Override
    public int getCount() {
        return list.length;
    }

    @Override
    public Object getItem(int position) {
        return list[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.custom_row, parent, false);
        } else {
            row = convertView;
        }

        TextView titleTextView = (TextView) row.findViewById(R.id.textView);
        titleTextView.setText(list[position]);

        return row;
    }
}
