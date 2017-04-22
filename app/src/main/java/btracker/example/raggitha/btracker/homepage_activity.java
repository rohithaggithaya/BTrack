package btracker.example.raggitha.btracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class homepage_activity extends AppCompatActivity {

    private Spinner selectTeamFilter;

    private ListView birthdaysList;
    private BirthdayListViewAdapter birthdayListViewAdapter;
    ArrayList<HashMap<String,String>> birthdaysListMap = new ArrayList<>();

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_homepage_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //below code displays a back button on action bar.
        /*
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
*/

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        birthdaysList = (ListView) findViewById(R.id.homepageBListID);

        selectTeamFilter = (Spinner) findViewById(R.id.hpTeamFilterID);

        if(!netowrkIsAvailable())
            Toast.makeText(getApplicationContext(),"Data load error! Please connect to Internet", Toast.LENGTH_LONG).show();

        selectTeamFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()  {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        birthdaysListMap.clear();
                        for(DataSnapshot ds: dataSnapshot.getChildren())
                        {
                            if(ds.getValue(UserData.class).getEmail().equals(firebaseAuth.getCurrentUser().getEmail()))
                                continue;
                            if(!selectTeamFilter.getSelectedItem().toString().equals("ALL"))
                            {
                                if(!ds.getValue(UserData.class).getTeam().equals(selectTeamFilter.getSelectedItem().toString()))
                                    continue;
                            }

                            Date ddate = null;
                            try {
                                ddate = new SimpleDateFormat("dd/MM/yyyy").parse(ds.getValue(UserData.class).getDOB());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            HashMap<String, String> birthdayHashMap = new HashMap<String, String>();
                            birthdayHashMap.put("NameKey",ds.getValue(UserData.class).getName());
                            birthdayHashMap.put("DOBKey",new SimpleDateFormat("dd/MMM").format(ddate));
                            birthdayHashMap.put("TeamKey",ds.getValue(UserData.class).getTeam());
                            birthdayHashMap.put("EmailKey",ds.getValue(UserData.class).getEmail());
                            birthdayHashMap.put("GenderKey",ds.getValue(UserData.class).getGender());
                            birthdayHashMap.put("ManagerKey",ds.getValue(UserData.class).getManager());
                            birthdaysListMap.add(birthdayHashMap);

                        }

                        birthdayListViewAdapter = new BirthdayListViewAdapter(getApplicationContext(),birthdaysListMap);
                        birthdaysList.setAdapter(birthdayListViewAdapter);

                        birthdaysList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent= new Intent(homepage_activity.this,homeProfileActivity.class);
                                intent.putExtra("NameKey",birthdaysListMap.get(position).get("NameKey"));
                                intent.putExtra("DOBKey",birthdaysListMap.get(position).get("DOBKey"));
                                intent.putExtra("TeamKey",birthdaysListMap.get(position).get("TeamKey"));
                                intent.putExtra("EmailKey",birthdaysListMap.get(position).get("EmailKey"));
                                intent.putExtra("ManagerKey",birthdaysListMap.get(position).get("ManagerKey"));
                                intent.putExtra("GenderKey",birthdaysListMap.get(position).get("GenderKey"));
                                intent.putExtra("curUserNameKey",dataSnapshot.child(firebaseAuth.getCurrentUser().getUid()).getValue(UserData.class).getName());
                                startActivity(intent);
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.account,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.logoutID)
        {
            firebaseAuth.signOut();
            startActivity(new Intent(homepage_activity.this, SignInActivity.class));
            Toast.makeText(getApplicationContext(),"Logged out successfully!",Toast.LENGTH_SHORT).show();
            finish();
        }
        else if (item.getItemId()==R.id.profileID)
        {
            startActivity(new Intent(homepage_activity.this, profileActivity.class));
            finish();
        }
        else if (item.getItemId() == R.id.changePasswordID)
        {
            startActivity(new Intent(homepage_activity.this, updatePasswordActivity.class));
            finish();
        }

        //below code gives "Report Bug" option in tool bar and on click takes user to send email with details populated.
        //if this is uncommented, make sure you make changes in menu.xml also.
        /*else if(item.getItemId() == R.id.reportBugID)
        {
            StringBuilder body = new StringBuilder();
            body.append("Hello B-Track Team, \n \n");
            body.append("I found a bug while using B-Track app.\n ");
            body.append("Please do the needful and verify this. Details below.\n \n");
            body.append("*//* Enter your bug details here *//* \n");
            body.append("\n Regards, \n");
            body.append(firebaseAuth.getCurrentUser().getDisplayName());
            String developers[] = {"varun.a_m@nokia.com", "rohith.aggithaya@nokia.com"};
            String developers2[] = {"varunvgnc@gmail.com","aggithaya@gmail.com"};

            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","",null));
            intent.putExtra(Intent.EXTRA_SUBJECT, "B-Track - Bug Report by " + firebaseAuth.getCurrentUser().getDisplayName());
            intent.putExtra(Intent.EXTRA_EMAIL, developers);
            intent.putExtra(Intent.EXTRA_CC, developers2);
            intent.putExtra(Intent.EXTRA_TEXT, body.toString());
            startActivity(intent);
        }*/

        else
        {
            StringBuilder body = new StringBuilder();
            body.append("Hello B-Track Team, \n \n");
            body.append("/* Please fill in your feedback/grievances */ \n");
            body.append("\n Regards, \n");
            body.append(firebaseAuth.getCurrentUser().getDisplayName());
            String developers[] = {"varun.a_m@nokia.com", "rohith.aggithaya@nokia.com"};
            String developers2[] = {"varunvgnc@gmail.com","aggithaya@gmail.com"};

            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","",null));
            intent.putExtra(Intent.EXTRA_SUBJECT, "B-Track - " + firebaseAuth.getCurrentUser().getDisplayName()+" wants to cantact you");
            intent.putExtra(Intent.EXTRA_EMAIL, developers);
            intent.putExtra(Intent.EXTRA_CC, developers2);
            intent.putExtra(Intent.EXTRA_TEXT, body.toString());
            startActivity(intent);
        }

        return  true;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog;
        alertDialog = new AlertDialog.Builder(homepage_activity.this);
        alertDialog.setTitle("Exit?");
        alertDialog.setMessage("Do you really want to close B-Track?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                homepage_activity.this.finish();
                Toast.makeText(getApplicationContext(),"Good bye!",Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.setNegativeButton("No", null);
        alertDialog.setCancelable(true);
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    private boolean netowrkIsAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}