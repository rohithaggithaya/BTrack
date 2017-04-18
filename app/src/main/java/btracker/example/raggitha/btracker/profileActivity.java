package btracker.example.raggitha.btracker;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class profileActivity extends AppCompatActivity {

    private ImageView profileIcon;
    private TextView profileName;
    private TextView profileGender, profileEmail, profileDOB, profileTeam;
    private ImageView editImage;

    private ProgressDialog progressDialog;

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
        editImage = (ImageView) findViewById(R.id.pfEditID);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        userID = firebaseAuth.getCurrentUser().getUid();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                displayData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog;
                alertDialog = new AlertDialog.Builder(profileActivity.this);
                alertDialog.setTitle("Edit Profile");
                alertDialog.setMessage("Are you sure?");
                alertDialog.setNegativeButton("No",null);
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        validateUser();
                    }
                });
                AlertDialog diag;
                diag = alertDialog.create();
                diag.show();
            }
        });
    }

    private void validateUser() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        final EditText providePassword = new EditText(profileActivity.this);
        providePassword.setHint("Enter Password");
        alertDialog.setView(providePassword);
        alertDialog.setTitle("Validate User");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.setMessage("Validating..");
                progressDialog.show();
                String password = providePassword.getText().toString().trim();
                String email = firebaseAuth.getCurrentUser().getEmail();

                if(password.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Password required",Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(getApplicationContext(),"Validation Successful",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(profileActivity.this,editProfileActivity.class);
                                    startActivity(intent);
                                    finish();
                                    progressDialog.dismiss();
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    return;
                                }
                            }
                        });
            }
        });
        alertDialog.setNegativeButton("Cancel",null);


        AlertDialog ad = alertDialog.create();
        ad.show();
    }

    private void displayData(DataSnapshot dataSnapshot) {
        DataSnapshot ds = dataSnapshot.child(userID);
            if (ds==null)
            {
                Toast.makeText(getApplicationContext(),"Sorry! Your profile details have been cleared.",Toast.LENGTH_SHORT).show();
                return;
            }

            profileName.setText(ds.getValue(UserData.class).getName());
            profileTeam.setText(ds.getValue(UserData.class).getTeam());
            profileDOB.setText(ds.getValue(UserData.class).getDOB());
            profileEmail.setText(ds.getValue(UserData.class).getEmail());
            profileGender.setText(ds.getValue(UserData.class).getGender());
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(profileActivity.this,homepage_activity.class));
        finish();
    }
}
