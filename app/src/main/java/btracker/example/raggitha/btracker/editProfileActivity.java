package btracker.example.raggitha.btracker;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class editProfileActivity extends profileActivity {

    private Button updateButton, cancelButton;
    private EditText updateName, updateDOB, updateManager;
    private Spinner updateTeam;
    private TextView EPEmail, EPGender;
    private Calendar calendar = Calendar.getInstance();
    private ImageView calendarIcon, profileImage;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private AlertDialog.Builder alertDialog;
    private FirebaseDatabase firebaseDatabase;
    private StorageReference storageReference;

    private  String currentGender, currentEmail, currentName, currentDOB, currentTeam, currentManager;
    private static final int GALLERY_INTENT = 2;
    private ProgressDialog progressDialog;
    private boolean imageUploaded = false, removeProfileImage = false, updateProfileImage = false;

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
        profileImage = (ImageView) findViewById(R.id.EPIconID);

        progressDialog = new ProgressDialog(this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        EPEmail.setText(firebaseAuth.getCurrentUser().getEmail());
        alertDialog = new AlertDialog.Builder(this);
        databaseReference = firebaseDatabase.getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

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

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(profileImage.getDrawable().getConstantState() != getResources().getDrawable(R.drawable.editprofileicon).getConstantState()) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(editProfileActivity.this);
                    String items[] = {"Choose from Gallery", "Remove Profile Image"};
                    alertDialog.setTitle("Choose an option");
                    alertDialog.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(intent, GALLERY_INTENT);
                                updateProfileImage = true;
                            } else {
                                    profileImage.setImageResource(R.drawable.editprofileicon);
                                    removeProfileImage = true;
                                    imageUploaded = true;
                                }
                            }
                        });
                    AlertDialog ad = alertDialog.create();
                    ad.show();
                }
                else
                {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, GALLERY_INTENT);
                    updateProfileImage = true;
                }
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
                            updateProfilePic();
                            if(removeProfileImage)
                                removeProfilePic();
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
                        if(updateProfileImage)
                            removeProfilePic();
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

    private void removeProfilePic() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Photos").child(firebaseAuth.getCurrentUser().getEmail());
        progressDialog.setMessage("Removing...");
        progressDialog.setCancelable(false);
        //progressDialog.show();
        storageReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //progressDialog.dismiss();
                        //Toast.makeText(getApplicationContext(),"Removed profile image successfully.", Toast.LENGTH_SHORT).show();
                            profileImage.setImageResource(R.drawable.editprofileicon);
                            imageUploaded = true;
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

        updateProfilePicHere();

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
            updateDOB.requestFocus();
            return;
        }

        if(newDOB.equals(new SimpleDateFormat("dd/MM/yyyy").format(new Date())))
        {
            updateDOB.setError("Invalid");
            updateDOB.requestFocus();
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
            updateDOB.requestFocus();
            return;
        }

        if(newName.equals(currentName) && (newDOB.equals(currentDOB)) && (newTeam.equals(currentTeam)) && (newManager.equals(currentManager)) && (imageUploaded == false))
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setIcon(R.drawable.alerticon);
            alertDialog.setTitle("No Changes Detected!");
            alertDialog.setPositiveButton("ok", null);
            final AlertDialog ad = alertDialog.create();
            ad.show();
        }
        else {
            UserData ud = new UserData(newName, newDOB, newTeam, currentEmail, currentGender, newManager, firebaseAuth.getCurrentUser().isEmailVerified());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if((requestCode == GALLERY_INTENT) && (resultCode == RESULT_OK))
        {
            progressDialog.setMessage("Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            Uri uri = data.getData();
            StorageReference filePath = storageReference.child("Photos").child(firebaseAuth.getCurrentUser().getEmail());
            filePath.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @SuppressWarnings("VisibleForTests")
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Picasso.with(editProfileActivity.this).load(taskSnapshot.getDownloadUrl()).centerCrop().fit().into(profileImage);
                            Toast.makeText(getApplicationContext(),"Upload Done!",Toast.LENGTH_SHORT).show();
                            UserProfileChangeRequest userProfile = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(taskSnapshot.getDownloadUrl())
                                    .build();
                            firebaseAuth.getCurrentUser().updateProfile(userProfile);
                            imageUploaded = true;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Failed! Please try again",Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updateProfilePicHere() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Photos").child(firebaseAuth.getCurrentUser().getEmail());
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                UserProfileChangeRequest userProfile = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(uri)
                        .build();
                firebaseAuth.getCurrentUser().updateProfile(userProfile);
                Picasso.with(editProfileActivity.this).load(firebaseAuth.getCurrentUser().getPhotoUrl()).fit().centerCrop().into(profileImage);
            }
        });

    }
}
