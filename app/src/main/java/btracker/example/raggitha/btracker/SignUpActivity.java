package btracker.example.raggitha.btracker;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.Random;


public class SignUpActivity extends AppCompatActivity {

    private EditText enteredName, managerName;
    private RadioGroup genderGroup;
    private RadioButton checkedButton;
    private EditText emailID;
    private EditText DOB,password;
    private Spinner TEAM;
    private Button signUp;
    private TextView signIn;
    private ImageView calendarIcon;

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

        enteredName = (EditText) findViewById(R.id.SUNameID);
        genderGroup = (RadioGroup) findViewById(R.id.SUGenderID);
        emailID = (EditText) findViewById(R.id.SUEmailID);
        DOB = (EditText) findViewById(R.id.SUDateID);
        TEAM = (Spinner) findViewById(R.id.SUTeamID);
        signUp = (Button) findViewById(R.id.SUsignUpID);
        signIn = (TextView) findViewById(R.id.SULoginID);
        password = (EditText) findViewById(R.id.SUPasswordID);
        calendarIcon = (ImageView) findViewById(R.id.SUCalendarIconID);
        managerName = (EditText) findViewById(R.id.SUManagerID);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        Random rand = new Random();
        int a = rand.nextInt(100);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    registerUser();
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                finish();
            }
        });

        calendarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DatePickerDialog(SignUpActivity.this, listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
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

        if(TextUtils.isEmpty(email)/*||((!email.contains("@nokia.com")))*/)
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

        if(!dob.matches("[0-9][0-9]/[0-9][0-9]/[12][09][0-9][0-9]"))
        {
            DOB.setError("Invalid");
            DOB.requestFocus();
            return;
        }

        if(dob.equals(new SimpleDateFormat("dd/MM/yyyy").format(new Date())))
        {
            DOB.setError("Invalid");
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
            DOB.setError("Invalid");
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

        UserData user= new UserData(name, dob, team, email, gender, Manager, userVerified);
        FirebaseUser USER = firebaseAuth.getCurrentUser();

        UserProfileChangeRequest userProfile = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        USER.updateProfile(userProfile);
        databaseReference.child(USER.getUid()).setValue(user);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
        finish();
    }
}
