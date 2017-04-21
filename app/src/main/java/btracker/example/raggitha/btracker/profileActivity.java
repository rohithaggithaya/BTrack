package btracker.example.raggitha.btracker;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

import java.util.ArrayList;
import java.util.HashMap;


public class profileActivity extends AppCompatActivity {

    private ImageView profileIcon;
    private TextView profileName;
    private ImageView editImage;
    private ListView pfDetailsListView;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private StorageReference storageReference;
    private String userID;
    private profileListViewAdapter adapter;

    private static int gender;

    private static final int GALLERY_INTENT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileIcon = (ImageView) findViewById(R.id.pfIconID);
        profileName = (TextView) findViewById(R.id.pfNameID);
        editImage = (ImageView) findViewById(R.id.pfEditID);
        pfDetailsListView = (ListView) findViewById(R.id.pfprofileDetailsID);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        userID = firebaseAuth.getCurrentUser().getUid();

        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Edit profile to update Image", Toast.LENGTH_SHORT).show();
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(userID).getValue(UserData.class).getGender().equals("Male"))
                    gender=1;
                else
                    gender = 0;

                if(gender==1)
                    profileIcon.setImageResource(R.drawable.malepficon);
                else
                    profileIcon.setImageResource(R.drawable.femalepficon);

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
        providePassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        alertDialog.setView(providePassword);
        alertDialog.setTitle("Validate User");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.setMessage("Validating..");
                progressDialog.show();
                progressDialog.setCancelable(false);
                String password = providePassword.getText().toString().trim();
                String email = firebaseAuth.getCurrentUser().getEmail();

                if(password.isEmpty())
                {
                    progressDialog.dismiss();
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

        String pfGender = ds.getValue(UserData.class).getGender();
        String pfDOB = ds.getValue(UserData.class).getDOB();
        String pfEmail = ds.getValue(UserData.class).getEmail();
        String pfManager = ds.getValue(UserData.class).getManager();
        String pfTeam = ds.getValue(UserData.class).getTeam();

        int title[] = {R.drawable.maleicon, R.drawable.emailicon , R.drawable.birthday_icon, R.drawable.linemanagericon, R.drawable.teamicon};

        if(pfGender.equals("Male"))
            title[0] = R.drawable.maleicon;
        else
            title[0] = R.drawable.femaleicon;

        String values[] = {pfGender, pfEmail, pfDOB, pfManager, pfTeam};

        ArrayList<HashMap<String,String>> pfDetailsList = new ArrayList<>();

        if (ds==null)
        {
            Toast.makeText(getApplicationContext(),"Sorry! Your profile details have been cleared.",Toast.LENGTH_SHORT).show();
            return;
        }

        for(int i=0; i<title.length; i++)
        {
            HashMap<String, String> pfHashMap = new HashMap<>();
            pfHashMap.put("pfTitleKey",String.valueOf(title[i]));
            pfHashMap.put("pfValueKey",values[i]);

            pfDetailsList.add(pfHashMap);
        }

        adapter = new profileListViewAdapter(getApplicationContext(),pfDetailsList);
        pfDetailsListView.setAdapter(adapter);

            profileName.setText(ds.getValue(UserData.class).getName());
            updateProfilePic();
    }

    protected void updateProfilePic() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Photos").child(firebaseAuth.getCurrentUser().getEmail());
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(profileActivity.this).load(firebaseAuth.getCurrentUser().getPhotoUrl()).fit().centerCrop().into(profileIcon);
            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(profileActivity.this,homepage_activity.class));
        finish();
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
                            Picasso.with(profileActivity.this).load(taskSnapshot.getDownloadUrl()).centerCrop().fit().into(profileIcon);
                            Toast.makeText(getApplicationContext(),"Upload Done!",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Failed! Please try again",Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        //below part of code handles image captured from camera. parked for time being. Will be out in future versions.
        /*if((requestCode == CAMERA_REQUEST_CODE) && (resultCode == RESULT_OK))
        {
            progressDialog.setMessage("Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            Uri uri = data.getData();
            StorageReference filepath = storageReference.child("Photos").child(firebaseAuth.getCurrentUser().getEmail());
            filepath.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Upload Done!",Toast.LENGTH_SHORT).show();
                        }
                    });
        }*/
    }
}
