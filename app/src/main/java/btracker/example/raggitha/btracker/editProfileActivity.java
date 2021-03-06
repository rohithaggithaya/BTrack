package btracker.example.raggitha.btracker;


import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
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
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

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
    private ImageView calendarIcon, profileImage;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private AlertDialog.Builder alertDialog;
    private FirebaseDatabase firebaseDatabase;
    private StorageReference storageReference;

    private  String currentGender, currentEmail, currentName, currentDOB, currentTeam, currentManager;
    private static final int GALLERY_INTENT = 2;
    private boolean imageUploaded = false, removeProfileImage = false;
    private ProgressDialog progressDialog;
    private Uri profileUri;
    private int uploadCount = 0;

    private static final int WELCOME_DURATION = 5000;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 10;

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
        ScrollView scrollView = (ScrollView) findViewById(R.id.epScrollView);

        scrollView.setVerticalScrollBarEnabled(false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        EPEmail.setText(firebaseAuth.getCurrentUser().getEmail());
        alertDialog = new AlertDialog.Builder(this);
        databaseReference = firebaseDatabase.getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);

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
                if(ContextCompat.checkSelfPermission(editProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(editProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))
                    {
                        //Toast.makeText(getApplicationContext(),"Rationale",Toast.LENGTH_LONG).show();
                            alertDialog.setTitle("Requires permission");
                            alertDialog.setMessage("This app requires permission to read device storage to update profile picture");
                            alertDialog.setCancelable(false);
                            alertDialog.setPositiveButton("Ask me again", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(editProfileActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                                }
                            });
                            alertDialog.setNegativeButton("ok",null);
                            alertDialog.create().show();
                    }
                    else
                    {
                        ActivityCompat.requestPermissions(editProfileActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                }
                else
                {
                    selectPhotoFromGallery();
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
                        progressDialog.setMessage("Updating profile...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        if(imageUploaded)
                            uploadProfileImage();
                        if(removeProfileImage)
                            removeProfilePic();
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

    private void uploadProfileImage() {
        StorageReference filePath = storageReference.child("Photos").child(firebaseAuth.getCurrentUser().getEmail());
        filePath.putFile(profileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        UserProfileChangeRequest userProfile = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(profileUri)
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

    private void removeProfilePic() {
        try {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Photos").child(firebaseAuth.getCurrentUser().getEmail());
            storageReference.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            profileImage.setImageResource(R.drawable.editprofileicon);
                        }
                    });
        }catch (Exception e)
        {
            Log.d("EditProfile",e.getMessage());
        }
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

        if(newName.isEmpty()) {
            updateName.setError("Required");
            updateName.requestFocus();
            return;
        }

        if(newDOB.isEmpty())
        {
            updateDOB.setError("Required");
            updateDOB.requestFocus();
            return;
        }
        if(newManager.isEmpty())
        {
            updateManager.setError("Required");
            updateManager.requestFocus();
            return;
        }

        //will verify and update the below in next version. subscribing user in onclick listerer of sign up button
/*        if(!currentTeam.equals(newTeam))
        {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(currentTeam);
            FirebaseMessaging.getInstance().subscribeToTopic(newTeam);
        }*/

        if(newName.equals(currentName) && (newDOB.equals(currentDOB)) && (newTeam.equals(currentTeam)) && (newManager.equals(currentManager)) && (imageUploaded == false) && (removeProfileImage == false))
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
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(editProfileActivity.this, profileActivity.class));
                    Toast.makeText(getApplicationContext(), "Update Successful!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            },WELCOME_DURATION);
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
            profileUri = data.getData();
            profileImage.setImageURI(profileUri);
            Picasso.with(editProfileActivity.this)
                    .load(profileUri)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .centerCrop()
                    .fit()
                    .into(profileImage);
            imageUploaded = true;
        }
    }

    private void updateProfilePicHere() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Photos").child(firebaseAuth.getCurrentUser().getEmail());
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                profileUri = uri;
                Picasso.with(editProfileActivity.this).load(uri).fit().centerCrop().into(profileImage);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progressDialog!=null){
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    //Toast.makeText(getApplicationContext(),"Permission Granted", Toast.LENGTH_LONG).show();
                    selectPhotoFromGallery();

                } else {

                    Toast.makeText(getApplicationContext(),"Permission Denied", Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
        }

        // other 'case' lines to check for other
        // permissions this app might request
    }

    private void selectPhotoFromGallery() {
        try
        {
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
                        } else {
                            profileImage.setImageResource(R.drawable.editprofileicon);
                            removeProfileImage = true;
                            imageUploaded = false;

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
            }
        }
        catch (Exception e)
        {
            if(uploadCount>2)
            {
                Toast.makeText(getApplicationContext(),"Please try again...", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(editProfileActivity.this, profileActivity.class));
                finish();
            }
            else{
                Toast.makeText(getApplicationContext(),"Please wait...", Toast.LENGTH_SHORT).show();
                uploadCount++;
            }
        }
    }
}
