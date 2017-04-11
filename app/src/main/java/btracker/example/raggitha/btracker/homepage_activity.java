package btracker.example.raggitha.btracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class homepage_activity extends AppCompatActivity {

    private ListView birthdaysList;
    private BirthdayListViewAdapter birthdayListViewAdapter;
    ArrayList<HashMap<String,String>> birthdaysListMap = new ArrayList<>();

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        birthdaysList = (ListView) findViewById(R.id.homepageBListID);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                birthdaysListMap.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    HashMap<String, String> birthdayHashMap = new HashMap<String, String>();
                    birthdayHashMap.put("NameKey",ds.getValue(UserData.class).getName());
                    birthdayHashMap.put("DOBKey",ds.getValue(UserData.class).getDOB());
                    birthdayHashMap.put("TeamKey",ds.getValue(UserData.class).getTeam());

                    birthdaysListMap.add(birthdayHashMap);
                }

                birthdayListViewAdapter = new BirthdayListViewAdapter(getApplicationContext(),birthdaysListMap);
                birthdaysList.setAdapter(birthdayListViewAdapter);

                birthdaysList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        int myPosition = position;
                        Toast.makeText(getApplicationContext(),"Coming soon!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
            Toast.makeText(getApplicationContext(),"Good bye! Have a nice day",Toast.LENGTH_SHORT).show();
            finish();
        }
        else
        {
            startActivity(new Intent(homepage_activity.this, profileActivity.class));
        }
        return  true;
    }
}
