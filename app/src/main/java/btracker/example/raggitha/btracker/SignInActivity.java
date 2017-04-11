package btracker.example.raggitha.btracker;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        if(firebaseAuth.getCurrentUser()!=null )
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
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(usname,pword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Logged in Successfully!",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignInActivity.this,homepage_activity.class));
                    finish();
                }
                else
                    onFailure(task.getException());
                    //Toast.makeText(getApplicationContext(),"Failed! Please try again",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        String message = e.getMessage();
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
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
