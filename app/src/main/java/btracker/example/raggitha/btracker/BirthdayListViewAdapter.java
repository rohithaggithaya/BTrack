package btracker.example.raggitha.btracker;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

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

    private StorageReference storageReference;

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

        View view = convertView;

        //below commented section of code does the same thing as of the uncommented section below. Only difference is that ViewHolder is used in the below commented code.
        /*ViewHolder holder;

        if(view == null || view.getTag() == null)
        {
            view = inflater.inflate(R.layout.list_row, null);

            holder = new ViewHolder();

            holder.name = (TextView) view.findViewById(R.id.listNameID);
            holder.dob = (TextView) view.findViewById(R.id.listDOBID);
            holder.team = (TextView) view.findViewById(R.id.listTeamID);
            holder.birthdayIcon = (ImageView) view.findViewById(R.id.bdayIcon);
            holder.profileImagePreview = (ImageView) view.findViewById(R.id.cameraIconID);

            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) view.getTag();
        }

        final HashMap<String,String> mBirthdays;
        mBirthdays = birthdays.get(position);

        holder.name.setText(mBirthdays.get("NameKey"));
        holder.dob.setText(mBirthdays.get("DOBKey"));
        holder.team.setText(mBirthdays.get("TeamKey"));

        if (mBirthdays.get("DOBKey").equals(new SimpleDateFormat("dd/MMM").format(new Date()))) {
            holder.birthdayIcon.setImageResource(R.drawable.cakeicon);
            holder.name.setTextColor(view.getResources().getColor(R.color.colorPrimary));
            holder.team.setTextColor(view.getResources().getColor(R.color.colorPrimary));
            holder.dob.setTextColor(ContextCompat.getColor(view.getContext(), R.color.colorPrimary));
        }
        else
        {
            holder.birthdayIcon.setImageResource(R.drawable.bdayicon);
        }

        storageReference = FirebaseStorage.getInstance().getReference().child("Photos").child(mBirthdays.get("EmailKey"));

        final ViewHolder finalholder = holder;
        try{
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(mContext.getApplicationContext()).load(uri.toString()).centerCrop().fit().into(finalholder.profileImagePreview);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if(mBirthdays.get("GenderKey").equals("Male"))
                        finalholder.profileImagePreview.setImageResource(R.drawable.malepficon);
                    else
                        finalholder.profileImagePreview.setImageResource(R.drawable.femalepficon);
                }
            });
        }
        catch (Exception e)
        {
            Toast.makeText(mContext.getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }*/

        if(view == null)
        {
            view = inflater.inflate(R.layout.list_row, null);

            TextView ListName = (TextView) view.findViewById(R.id.listNameID);
            TextView ListDob = (TextView) view.findViewById(R.id.listDOBID);
            TextView ListTeam = (TextView) view.findViewById(R.id.listTeamID);
            ImageView bdayIcon = (ImageView) view.findViewById(R.id.bdayIcon);
            final ImageView cameraIcon = (ImageView) view.findViewById(R.id.cameraIconID);

            final HashMap<String,String> mBirthdays;
            mBirthdays = birthdays.get(position);

            ListName.setText(mBirthdays.get("NameKey"));
            ListDob.setText(mBirthdays.get("DOBKey"));
            ListTeam.setText(mBirthdays.get("TeamKey"));

            storageReference = FirebaseStorage.getInstance().getReference().child("Photos").child(mBirthdays.get("EmailKey"));

            try{
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(mContext.getApplicationContext()).load(uri.toString()).centerCrop().fit().into(cameraIcon);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(mBirthdays.get("GenderKey").equals("Male"))
                            cameraIcon.setImageResource(R.drawable.malepficon);
                        else
                            cameraIcon.setImageResource(R.drawable.femalepficon);
                    }
                });
            }
            catch (Exception e)
            {
                Toast.makeText(mContext.getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }

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
            TextView ListName = (TextView) view.findViewById(R.id.listNameID);
            TextView ListDob = (TextView) view.findViewById(R.id.listDOBID);
            TextView ListTeam = (TextView) view.findViewById(R.id.listTeamID);
            ImageView bdayIcon = (ImageView) view.findViewById(R.id.bdayIcon);
            final ImageView cameraIcon = (ImageView) view.findViewById(R.id.cameraIconID);

            final HashMap<String,String> mBirthdays;
            mBirthdays = birthdays.get(position);

            ListName.setText(mBirthdays.get("NameKey"));
            ListDob.setText(mBirthdays.get("DOBKey"));
            ListTeam.setText(mBirthdays.get("TeamKey"));

            storageReference = FirebaseStorage.getInstance().getReference().child("Photos").child(mBirthdays.get("EmailKey"));

            try{
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(mContext.getApplicationContext()).load(uri.toString()).centerCrop().fit().into(cameraIcon);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(mBirthdays.get("GenderKey").equals("Male"))
                            cameraIcon.setImageResource(R.drawable.malepficon);
                        else
                            cameraIcon.setImageResource(R.drawable.femalepficon);
                    }
                });
            }
            catch (Exception e)
            {
                Toast.makeText(mContext.getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }

            if (mBirthdays.get("DOBKey").equals(new SimpleDateFormat("dd/MMM").format(new Date()))) {
                bdayIcon.setImageResource(R.drawable.cakeicon);
                ListName.setTextColor(view.getResources().getColor(R.color.colorPrimary));
                ListTeam.setTextColor(view.getResources().getColor(R.color.colorPrimary));
                ListDob.setTextColor(ContextCompat.getColor(view.getContext(), R.color.colorPrimary));
            }
            else
            {
                bdayIcon.setImageResource(R.drawable.bdayicon);
                ListName.setTextColor(view.getResources().getColor(R.color.black));
                ListTeam.setTextColor(view.getResources().getColor(R.color.black));
                ListDob.setTextColor(ContextCompat.getColor(view.getContext(), R.color.black));
            }
            return view;
        }
    }
    /*private class ViewHolder {
        ImageView profileImagePreview;
        TextView name;
        TextView dob;
        TextView team;
        ImageView birthdayIcon;
    }*/
}
