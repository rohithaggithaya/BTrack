package btracker.example.raggitha.btracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class profileActivity extends AppCompatActivity {

    private ImageView profileIcon;
    private TextView profileName;
    private TextView profileGender, profileEmail, profileDOB, profileTeam;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileIcon = (ImageView) findViewById(R.id.pfIconID);
        profileName = (TextView) findViewById(R.id.pfNameID);
        profileGender = (TextView) findViewById(R.id.pfGenderID);
        profileEmail = (TextView) findViewById(R.id.pfEmailID);
        profileDOB = (TextView) findViewById(R.id.pfDOBID);
        profileTeam = (TextView) findViewById(R.id.pfTeamID);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        userID = user.getUid();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                displayData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void displayData(DataSnapshot dataSnapshot) {
        DataSnapshot ds = dataSnapshot.child(userID);
            if (ds==null)
            {
                Toast.makeText(getApplicationContext(),"Sorry! Your profile details have been cleared.",Toast.LENGTH_SHORT).show();
                return;
            }

            UserData ud = new UserData();
            profileName.setText(ds.getValue(UserData.class).getName());
            profileTeam.setText(ds.getValue(UserData.class).getTeam());
            profileDOB.setText(ds.getValue(UserData.class).getDOB());
            profileEmail.setText(ds.getValue(UserData.class).getEmail());
            profileGender.setText(ds.getValue(UserData.class).getGender());


    }

}
