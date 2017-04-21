package btracker.example.raggitha.btracker;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vaam on 21-04-2017.
 */

public class profileListViewAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<HashMap<String,String>> profileList;
    private static LayoutInflater layoutInflater = null;

    public profileListViewAdapter(Context context, ArrayList<HashMap<String, String>> data) {
        mContext = context;
        profileList = data;
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return profileList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (convertView == null)
            view = layoutInflater.inflate(R.layout.list_row_profile, null);

            ImageView title = (ImageView) view.findViewById(R.id.pfListRowTitleID);
            TextView value = (TextView) view.findViewById(R.id.pfListRowValueID);

            HashMap<String, String> mPFList;
            mPFList = profileList.get(position);

            title.setImageResource(Integer.parseInt(mPFList.get("pfTitleKey")));
            value.setText(mPFList.get("pfValueKey"));

        return view;
    }
}
