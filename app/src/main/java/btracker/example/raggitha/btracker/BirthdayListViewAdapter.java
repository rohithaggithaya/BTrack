package btracker.example.raggitha.btracker;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by raggitha on 07-Apr-17.
 */

public class BirthdayListViewAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<HashMap<String, String>> birthdays;
    private static LayoutInflater inflater = null;


    public BirthdayListViewAdapter(Context context, ArrayList<HashMap<String, String>> data)
    {
        mContext = context;
        birthdays = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return birthdays.size();
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

        View view;
        if(convertView == null)
        {
            view = inflater.inflate(R.layout.list_row, null);

            TextView ListName = (TextView) view.findViewById(R.id.listNameID);
            TextView ListDob = (TextView) view.findViewById(R.id.listDOBID);
            TextView ListTeam = (TextView) view.findViewById(R.id.listTeamID);
            ImageView bdayIcon = (ImageView) view.findViewById(R.id.bdayIcon);

            HashMap<String,String> mBirthdays;
            mBirthdays = birthdays.get(position);

            ListName.setText(mBirthdays.get("NameKey"));
            ListDob.setText(mBirthdays.get("DOBKey"));
            ListTeam.setText(mBirthdays.get("TeamKey"));

            if (mBirthdays.get("DOBKey").equals(new SimpleDateFormat("dd/MMM").format(new Date()))) {
                bdayIcon.setImageResource(R.drawable.cakeicon);
                ListName.setTextColor(view.getResources().getColor(R.color.colorPrimary));
                ListTeam.setTextColor(view.getResources().getColor(R.color.colorPrimary));
                ListDob.setTextColor(ContextCompat.getColor(view.getContext(), R.color.colorPrimary));
            }
            return view;
        }
        else
        {
            view = convertView;
            TextView ListName = (TextView) view.findViewById(R.id.listNameID);
            TextView ListDob = (TextView) view.findViewById(R.id.listDOBID);
            TextView ListTeam = (TextView) view.findViewById(R.id.listTeamID);
            ImageView bdayIcon = (ImageView) view.findViewById(R.id.bdayIcon);

            HashMap<String,String> mBirthdays;
            mBirthdays = birthdays.get(position);

            ListName.setText(mBirthdays.get("NameKey"));
            ListDob.setText(mBirthdays.get("DOBKey"));
            ListTeam.setText(mBirthdays.get("TeamKey"));

            if (mBirthdays.get("DOBKey").equals(new SimpleDateFormat("dd/MMM").format(new Date()))) {
                bdayIcon.setImageResource(R.drawable.cakeicon);
                ListName.setTextColor(view.getResources().getColor(R.color.colorPrimary));
                ListTeam.setTextColor(view.getResources().getColor(R.color.colorPrimary));
                ListDob.setTextColor(ContextCompat.getColor(view.getContext(), R.color.colorPrimary));
            }

            return view;
        }
    }
}
