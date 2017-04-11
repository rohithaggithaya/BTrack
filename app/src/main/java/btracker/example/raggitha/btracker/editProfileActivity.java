package btracker.example.raggitha.btracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class editProfileActivity extends AppCompatActivity {

    private Button updateButton, cancelButton;
    private EditText updateName, currentPassword, updateDOB;
    private Spinner updateTeam;
    private TextView EPEmail;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private AlertDialog.Builder alertDialog;
    private FirebaseDatabase firebaseDatabase;

    private  String currentGender, currentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        updateButton = (Button) findViewById(R.id.EPUpdateButtonID);
        cancelButton = (Button) findViewById(R.id.EPCancelButtonID);
        updateName = (EditText) findViewById(R.id.EPNameID);
        updateDOB = (EditText) findViewById(R.id.EPDateID);
        updateTeam = (Spinner) findViewById(R.id.EPTeamID);
        EPEmail = (TextView) findViewById(R.id.EPEmailID);
        currentPassword = (EditText) findViewById(R.id.EPPasswordID);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        EPEmail.setText(firebaseAuth.getCurrentUser().getEmail());
        alertDialog = new AlertDialog.Builder(this);
        databaseReference = firebaseDatabase.getReference();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                populateData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        updateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                final String cPassword = currentPassword.getText().toString().trim();
                final String email = firebaseAuth.getCurrentUser().getEmail();
                if(cPassword.isEmpty())
                    currentPassword.setError("Required");
                else
                {
                    alertDialog.setTitle("Update?");
                    alertDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            firebaseAuth.signInWithEmailAndPassword(email,cPassword)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful()) {
                                                updateProfile();
                                                Toast.makeText(getApplicationContext(),"Update Successful!",Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(editProfileActivity.this,profileActivity.class));
                                                finish();
                                            }
                                            else
                                                onFailure(task.getException());
                                        }
                                    });
                        }
                    });
                    alertDialog.setNegativeButton("No", null);
                    AlertDialog dialog = alertDialog.create();
                    dialog.show();
                }
                }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.setTitle("Cancel?");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(editProfileActivity.this,profileActivity.class));
                        finish();
                    }
                });
                alertDialog.setNegativeButton("No", null);
                alertDialog.setCancelable(true);
                AlertDialog dialog;
                dialog = alertDialog.create();
                dialog.show();
            }
        });
    }

    private void populateData(DataSnapshot dataSnapshot)
    {
        DataSnapshot ds = dataSnapshot.child(firebaseAuth.getCurrentUser().getUid());
        if (ds==null)
        {
            Toast.makeText(getApplicationContext(),"Sorry! Your profile details have been cleared.",Toast.LENGTH_SHORT).show();
            return;
        }

        updateName.setText(ds.getValue(UserData.class).getName());

        String spinnerValue = ds.getValue(UserData.class).getTeam();
        ArrayAdapter myAdap = (ArrayAdapter) updateTeam.getAdapter();
        int spinnerPosition = myAdap.getPosition(spinnerValue);
        updateTeam.setSelection(spinnerPosition);

        updateDOB.setText(ds.getValue(UserData.class).getDOB());
        currentEmail = ds.getValue(UserData.class).getEmail();
        currentGender = ds.getValue(UserData.class).getGender();
    }

    private void onFailure(Exception exception) {
        Toast.makeText(getApplicationContext(),exception.getMessage(),Toast.LENGTH_LONG).show();
    }

    private void updateProfile() {
        String newName = updateName.getText().toString().trim();
        String newTeam = updateTeam.getSelectedItem().toString().trim();
        String newDOB = updateDOB.getText().toString().trim();

        UserData ud = new UserData(newName, newDOB, newTeam, currentEmail, currentGender);
        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).setValue(ud);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog;
        alertDialog = new AlertDialog.Builder(editProfileActivity.this);
        alertDialog.setTitle("Cancel?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(editProfileActivity.this,profileActivity.class));
                finish();
            }
        });
        alertDialog.setNegativeButton("No", null);
        alertDialog.setCancelable(false);
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }
}
