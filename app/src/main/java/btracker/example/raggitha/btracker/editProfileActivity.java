package btracker.example.raggitha.btracker;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class editProfileActivity extends AppCompatActivity {

    private Button updateButton, cancelButton;
    private EditText updateName, updateDOB, updateManager;
    private Spinner updateTeam;
    private TextView EPEmail, EPGender;
    private Calendar calendar = Calendar.getInstance();
    private ImageView calendarIcon;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private AlertDialog.Builder alertDialog;
    private FirebaseDatabase firebaseDatabase;

    private  String currentGender, currentEmail, currentName, currentDOB, currentTeam, currentManager;

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
        calendarIcon = (ImageView) findViewById(R.id.EPCalendarIconID);
        EPGender = (TextView) findViewById(R.id.EPGenderID);
        updateManager = (EditText) findViewById(R.id.EPManagerID) ;

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

        calendarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DatePickerDialog(editProfileActivity.this, listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                    alertDialog.setTitle("Update?");
                    alertDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            updateProfile();
                        }
                    });
                    alertDialog.setNegativeButton("No", null);
                    AlertDialog dialog = alertDialog.create();
                    dialog.show();
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

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            month++;
            String day,mnth;
            if(dayOfMonth<10)
                day = "0"+String.valueOf(dayOfMonth);
            else
                day = String.valueOf(dayOfMonth);

            if((month)<10)
                mnth="0"+String.valueOf(month);
            else
                mnth = String.valueOf(month);

            updateDOB.setText(day+ "/" + mnth + "/" + year);

        }
    };

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

        currentDOB = ds.getValue(UserData.class).getDOB();
        updateDOB.setText(currentDOB);
        currentEmail = ds.getValue(UserData.class).getEmail();
        currentGender = ds.getValue(UserData.class).getGender();
        EPGender.setText(currentGender);
        currentManager = ds.getValue(UserData.class).getManager();
        updateManager.setText(currentManager);
        currentName = ds.getValue(UserData.class).getName();
        currentTeam = ds.getValue(UserData.class).getTeam();
    }

    private void updateProfile() {
        String newName = updateName.getText().toString().trim();
        String newTeam = updateTeam.getSelectedItem().toString().trim();
        String newDOB = updateDOB.getText().toString().trim();
        String newManager = updateManager.getText().toString().trim();

        if(!newDOB.matches("[0-9][0-9]/[0-9][0-9]/[12][09][0-9][0-9]"))
        {
            updateDOB.setError("Invalid");
            return;
        }

        if(newDOB.equals(new SimpleDateFormat("dd/MM/yyyy").format(new Date())))
        {
            updateDOB.setError("Invalid");
            return;
        }

        Date ddate = new Date();
        try {
            ddate = new SimpleDateFormat("dd/MM/yyyy").parse(newDOB);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(ddate.after(new Date()))
        {
            updateDOB.setError("Invalid");
            return;
        }

        if(newName.equals(currentName) && (newDOB.equals(currentDOB)) && (newTeam.equals(currentTeam)) && (newManager.equals(currentManager)))
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setIcon(R.drawable.alerticon);
            alertDialog.setTitle("No Changes Detected!");
            alertDialog.setPositiveButton("ok", null);
            final AlertDialog ad = alertDialog.create();
            ad.show();
        }
        else {
            UserData ud = new UserData(newName, newDOB, newTeam, currentEmail, currentGender, newManager);
            databaseReference.child(firebaseAuth.getCurrentUser().getUid()).setValue(ud);
            Toast.makeText(getApplicationContext(), "Update Successful!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(editProfileActivity.this, profileActivity.class));
            finish();
        }
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
