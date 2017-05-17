package btracker.example.raggitha.btracker;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class SignUpActivity extends AppCompatActivity {

    private EditText enteredName, managerName;
    private RadioGroup genderGroup;
    private RadioButton checkedButton;
    private EditText emailID;
    private EditText DOB,password;
    private Spinner TEAM;
    private Button signUp;

    //for FireBase Authenticator
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    //For date Picker
    private Calendar calendar = Calendar.getInstance();

    //flag which is used to display data of only verified users.
    private boolean userVerified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        enteredName = (EditText) findViewById(R.id.SUNameID);
        genderGroup = (RadioGroup) findViewById(R.id.SUGenderID);
        emailID = (EditText) findViewById(R.id.SUEmailID);
        DOB = (EditText) findViewById(R.id.SUDateID);
        TEAM = (Spinner) findViewById(R.id.SUTeamID);
        signUp = (Button) findViewById(R.id.SUsignUpID);
        password = (EditText) findViewById(R.id.SUPasswordID);
        managerName = (EditText) findViewById(R.id.SUManagerID);
        ScrollView scrollView = (ScrollView) findViewById(R.id.suScrollView);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

       scrollView.setVerticalScrollBarEnabled(false);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    registerUser();
            }
        });

        enteredName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(enteredName.getText().toString().isEmpty())
                {
                    enteredName.setError("Required");
                }
            }
        });

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(password.getText().toString().isEmpty())
                    password.setError("Required");
            }
        });

        managerName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(managerName.getText().toString().isEmpty())
                    managerName.setError("Required");
            }
        });

        DOB.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                    if(DOB.hasFocus())
                        new DatePickerDialog(SignUpActivity.this, listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        emailID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String email = emailID.getText().toString().trim();
                if(TextUtils.isEmpty(email)||((!email.contains("@nokia.com"))))
                {
                    if(TextUtils.isEmpty(email))
                        emailID.setError("Required");
                    else{
                        emailID.setError("Nokia email please");
                    }
                    return;
                }
            }
        });
    }

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
        {
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

                DOB.setText(day+ "/" + mnth + "/" + year);

        }
    };
    private void registerUser() {
        String email = emailID.getText().toString().trim();
        String Enteredpassword = password.getText().toString().trim();
        String name = enteredName.getText().toString().trim();
        String dob = DOB.getText().toString().trim();
        String Manager = managerName.getText().toString().trim();

        if (TextUtils.isEmpty(name))
        {
            enteredName.setError("Required");
            enteredName.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(email)||((!email.contains("@nokia.com"))))
        {
            if(TextUtils.isEmpty(email))
                emailID.setError("Required");
            else
                emailID.setError("Invalid email");
            emailID.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(Enteredpassword))
        {
            password.setError("Required");
            password.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(dob))
        {
            DOB.setError("Required");
            DOB.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(Manager))
        {
            managerName.setError("Required");
            managerName.requestFocus();
            return;
        }

        if(genderGroup.getCheckedRadioButtonId()==-1)
        {
            Toast.makeText(getApplicationContext(),"Please select Gender",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TEAM.getSelectedItem().toString().equals("Select Team"))
        {
            Toast.makeText(getApplicationContext(),"Please select Team",Toast.LENGTH_SHORT).show();
            return;
        }

        //below code is used to check the dob format entered in edit text field.
        /*if(!dob.matches("[0-9][0-9]/[0-9][0-9]/[12][09][0-9][0-9]"))
        {
            DOB.setError("Invalid");
            DOB.requestFocus();
            return;
        }*/

        if(dob.equals(new SimpleDateFormat("dd/MM/yyyy").format(new Date())))
        {
            Toast.makeText(getApplicationContext(),"It cannot be today, right? \n Try Again",Toast.LENGTH_LONG).show();
            DOB.requestFocus();
            return;
        }

        Date ddate = new Date();
        try {
            ddate = new SimpleDateFormat("dd/MM/yyyy").parse(dob);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(ddate.after(new Date()))
        {
            Toast.makeText(getApplicationContext(),"Don't tell me you time-travelled! \n Try again",Toast.LENGTH_LONG).show();
            DOB.requestFocus();
            return;
        }

        progressDialog.setMessage("Creating Account...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,Enteredpassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            saveUserInfo();
                            firebaseAuth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getApplicationContext(), "Verification email sent to " + firebaseAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                                            finish();
                                        }
                                    });
                        }
                        else
                        {
                            progressDialog.cancel();
                            Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }


                });
    }

    private void saveUserInfo() {

        String email = emailID.getText().toString().trim().toLowerCase();
        String name = enteredName.getText().toString().trim();
        name = name.substring(0,1).toUpperCase() + name.substring(1);
        String dob = DOB.getText().toString().trim();
        String team = TEAM.getSelectedItem().toString();
        int i = genderGroup.getCheckedRadioButtonId();
        checkedButton =(RadioButton) findViewById(i);
        String gender = checkedButton.getText().toString().trim();
        String Manager = managerName.getText().toString().trim();
        userVerified = false;
        //below is to subscribe to topic. white spaces are not allowed hence the below line of code
        //String tempTeam = team.replaceAll(" ", "");

        UserData user= new UserData(name, dob, team, email, gender, Manager, userVerified);
        FirebaseUser USER = firebaseAuth.getCurrentUser();

        UserProfileChangeRequest userProfile = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        USER.updateProfile(userProfile);
        databaseReference.child(USER.getUid()).setValue(user);
        //will include this in next version.
        /*FirebaseMessaging.getInstance().subscribeToTopic(tempTeam);*/
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return  true;
        }
        else
            return super.onOptionsItemSelected(item);
    }
}
