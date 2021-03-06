package btracker.example.raggitha.btracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class homepage_activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Spinner selectTeamFilter;

    private ListView birthdaysList;
    private BirthdayListViewAdapter birthdayListViewAdapter;
    ArrayList<HashMap<String,String>> birthdaysListMap = new ArrayList<>();

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        birthdaysList = (ListView) findViewById(R.id.homepageBListID);

        selectTeamFilter = (Spinner) findViewById(R.id.hpTeamFilterID);
        progressDialog = new ProgressDialog(this);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayoutID);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle
                (this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        final ImageView profileImage = (ImageView) headerView.findViewById(R.id.profileImageViewOnNavID);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Yes, that's you!", Toast.LENGTH_LONG).show();
            }
        });
        TextView profileName = (TextView) headerView.findViewById(R.id.nameOnNavID);
        profileName.setText(firebaseAuth.getCurrentUser().getDisplayName());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Photos").child(firebaseAuth.getCurrentUser().getEmail());
        try{
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(getApplicationContext()).load(uri.toString()).centerCrop().fit().into(profileImage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    profileImage.setImageResource(R.drawable.profilenavicon);
                }
            });
        }
        catch(Exception e)
        {
        }

        if(!netowrkIsAvailable())
            Toast.makeText(getApplicationContext(),"Data load error! Please connect to Internet", Toast.LENGTH_LONG).show();
        else{
            if(birthdaysList.getCount()==0)
            {
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(progressDialog.isShowing())
                    {
                        progressDialog.cancel();
                        firebaseAuth.signOut();
                        Toast.makeText(getApplicationContext(),"Oops! Data load error... \n Please sign in again",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(homepage_activity.this, SignInActivity.class));
                        finish();
                    }
                }
            },20000);
        }

        if(firebaseAuth.getCurrentUser().isEmailVerified())
            databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("userVerified").setValue(true);

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

                            if(!ds.getValue(UserData.class).getUserVerified())
                                continue;

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

                        Collections.sort(birthdaysListMap, new Comparator<HashMap<String, String>>() {
                                    @Override
                                    public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {
                                        Date d1=null,d2=null;
                                        try {
                                            d1 = new SimpleDateFormat("dd/MMM").parse(o1.get("DOBKey"));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        try {
                                            d2 = new SimpleDateFormat("dd/MMM").parse(o2.get("DOBKey"));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        return d1.compareTo(d2);
                                    }
                                });

                        birthdayListViewAdapter = new BirthdayListViewAdapter(getApplicationContext(),birthdaysListMap);
                        birthdaysList.setAdapter(birthdayListViewAdapter);

                        if(birthdaysList.getCount()==0)
                            Toast.makeText(getApplicationContext(),"Nobody registered to B-Track from \n" + selectTeamFilter.getSelectedItem().toString(),Toast.LENGTH_LONG).show();

                        if(progressDialog.isShowing() && birthdaysList.getCount()!=0)
                        {
                            progressDialog.dismiss();
                        }

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
                                intent.putExtra("curUserNameKey",firebaseAuth.getCurrentUser().getDisplayName());
                                //intent.putExtra("CallingKey","Home");
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

    //below code inflates menu item (account) and creates option on right top corner with 3 dots as icon.
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.account,menu);
        return true;
    }*/

    //Below code is used when above tool bar options are used.
    /*@Override
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
        else if(item.getItemId() == R.id.reportBugID)
        {
            StringBuilder body = new StringBuilder();
            body.append("Hello B-Track Team, \n \n");
            body.append("I found a bug while using B-Track app.\n ");
            body.append("Please do the needful and verify this. Details below.\n \n");
            body.append(" Enter your bug details here  \n");
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
        }

        else
        {
            StringBuilder body = new StringBuilder();
            body.append("Hello B-Track Team, \n \n");
            body.append(" Please fill in your feedback/grievances  \n");
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
    }*/

    @Override
    public void onBackPressed() {
            DrawerLayout layout = (DrawerLayout)findViewById(R.id.drawerLayoutID);
            if (layout.isDrawerOpen(GravityCompat.START)) {
                layout.closeDrawer(GravityCompat.START);
            } else {
                AlertDialog.Builder alertDialog;
                alertDialog = new AlertDialog.Builder(homepage_activity.this);
                alertDialog.setTitle("Exit?");
                alertDialog.setIcon(R.drawable.exiticon);
                alertDialog.setMessage("Do you really want to close B-Track?");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        homepage_activity.this.finish();
                        Toast.makeText(getApplicationContext(), "Good bye!", Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialog.setNegativeButton("No", null);
                alertDialog.setCancelable(true);
                AlertDialog dialog = alertDialog.create();
                dialog.show();
            }
    }

    private boolean netowrkIsAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id==R.id.logoutID)
        {
            firebaseAuth.signOut();
            startActivity(new Intent(homepage_activity.this, SignInActivity.class));
            Toast.makeText(getApplicationContext(),"Logged out successfully!",Toast.LENGTH_SHORT).show();
            finish();
        }
        else if (id==R.id.profileID)
        {
            startActivity(new Intent(homepage_activity.this, profileActivity.class));
            finish();
        }
        else if (id == R.id.changePasswordID)
        {
            startActivity(new Intent(homepage_activity.this, updatePasswordActivity.class));
            finish();
        }
        else if (id == R.id.contactUsID)
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Developers");
            alertDialog.setIcon(R.drawable.developericon);
            alertDialog.setMessage("Varun A M \nvarun.a_m@nokia.com \n\nRohith S Aggithaya \nrohith.aggithaya@nokia.com");
            alertDialog.setPositiveButton("Send Email", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    StringBuilder body = new StringBuilder();
                    body.append("Hello B-Track Team, \n \n");
                    body.append(" Please fill in your feedback/grievances  \n");
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
            });
            alertDialog.setNegativeButton("Ok",null);
            alertDialog.create().show();
        }

        else
        {
            Toast.makeText(getApplicationContext(),"Coming soon!", Toast.LENGTH_LONG).show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayoutID);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}