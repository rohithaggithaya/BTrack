package btracker.example.raggitha.btracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpActivity extends AppCompatActivity {

    private EditText enteredName;
    private RadioGroup genderGroup;
    private RadioButton checkedButton;
    private EditText emailID;
    private EditText DOB,password;
    private Spinner TEAM;
    private Button signUp;
    private TextView signIn;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

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

        databaseReference = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

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
            }
        });
    }

    private void updateUser() {
    }

    private void registerUser() {
        String email = emailID.getText().toString().trim();
        String Enteredpassword = password.getText().toString().trim();
        String name = enteredName.getText().toString().trim();
        String dob = DOB.getText().toString().trim();

        if (TextUtils.isEmpty(name))
        {
            enteredName.setError("Required");
            return;
        }

        if(TextUtils.isEmpty(email)/*||((!email.contains("@nokia.com")))*/)
        {
            if(TextUtils.isEmpty(email))
                emailID.setError("Required");
            else
                emailID.setError("Invalid email");

            return;
        }

        if (TextUtils.isEmpty(Enteredpassword))
        {
            password.setError("Required");
            return;
        }

        if (TextUtils.isEmpty(dob))
        {
            DOB.setError("Required");
            return;
        }


        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,Enteredpassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            saveUserInfo();
                            FirebaseMessaging.getInstance().subscribeToTopic("Bday");
                            progressDialog.cancel();
                            Toast.makeText(getApplicationContext(),"Registration Successful",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                            finish();
                        }
                        else
                        {
                            progressDialog.cancel();
                            Toast.makeText(getApplicationContext(),"Failed! Please try again",Toast.LENGTH_SHORT).show();
                        }
                    }


                });
    }

    private void saveUserInfo() {

        String email = emailID.getText().toString().trim().toLowerCase();
        String name = enteredName.getText().toString().trim();
        name = name.substring(0,1).toUpperCase()+name.substring(1);
        String dob = DOB.getText().toString().trim();
        String team = TEAM.getSelectedItem().toString();
        int i = genderGroup.getCheckedRadioButtonId();
        checkedButton =(RadioButton) findViewById(i);
        String gender = checkedButton.getText().toString().trim();

        UserData user= new UserData(name, dob, team, email, gender);
        FirebaseUser USER = firebaseAuth.getCurrentUser();
        databaseReference.child(USER.getUid()).setValue(user);
    }
}
