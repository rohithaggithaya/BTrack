package btracker.example.raggitha.btracker;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity implements OnFailureListener {

    private EditText userName;
    private EditText password;
    private Button signInButton;
    private TextView signUpTextView;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private TextView forgotButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        userName = (EditText) findViewById(R.id.userNameID);
        password = (EditText) findViewById(R.id.passwordID);
        signInButton = (Button) findViewById(R.id.mainSignInID);
        signUpTextView = (TextView) findViewById(R.id.mainSignUpID);
        firebaseAuth = FirebaseAuth.getInstance();
        forgotButton = (TextView) findViewById(R.id.mainForgotID) ;

        progressDialog = new ProgressDialog(this);

        if((firebaseAuth.getCurrentUser()!=null) && (firebaseAuth.getCurrentUser().isEmailVerified()))
        {
            startActivity(new Intent(SignInActivity.this,homepage_activity.class));
            finish();
        }

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userlogin();
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                finish();
            }
        });

        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, ForgotPassActivity.class));
                finish();
            }
        });
    }

    private void userlogin() {
        String usname = userName.getText().toString().trim();
        String pword = password.getText().toString().trim();

        if (TextUtils.isEmpty(usname))
        {
            userName.setError("Required");
            return;
        }

        if(TextUtils.isEmpty(pword))
        {
            password.setError("Required");
            return;
        }

        progressDialog.setMessage("Logging-in, please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(usname,pword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){
                    if(!firebaseAuth.getCurrentUser().isEmailVerified())
                    {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignInActivity.this);
                        alertDialog.setIcon(R.drawable.alerticon);
                        alertDialog.setTitle("Alert");
                        alertDialog.setMessage("Please verify your email to login");
                        alertDialog.setPositiveButton("Send mail again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                firebaseAuth.getCurrentUser().sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                    Toast.makeText(getApplicationContext(),"Verification email sent to " + firebaseAuth.getCurrentUser().getEmail(), Toast.LENGTH_LONG).show();
                                                else
                                                    Toast.makeText(getApplicationContext(),task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        });
                        alertDialog.setNegativeButton("ok",null);
                        alertDialog.setCancelable(false);
                        AlertDialog ad = alertDialog.create();
                        ad.show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Logged in Successfully!",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignInActivity.this,homepage_activity.class));
                        finish();
                    }
                }
                else
                    onFailure(task.getException());
            }
        });
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog;
        alertDialog = new AlertDialog.Builder(SignInActivity.this);
        alertDialog.setTitle("Exit?");
        alertDialog.setMessage("Do you really want to close B-Track?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SignInActivity.this.finish();
                Toast.makeText(getApplicationContext(),"Good bye!",Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.setNegativeButton("No", null);
        alertDialog.setCancelable(false);
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }
}
