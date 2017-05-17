package btracker.example.raggitha.btracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

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
        forgotButton = (TextView) findViewById(R.id.mainForgotID) ;
        ScrollView scrollView = (ScrollView) findViewById(R.id.siScrollView);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        if((firebaseAuth.getCurrentUser()!=null) && (firebaseAuth.getCurrentUser().isEmailVerified()))
        {
            startActivity(new Intent(SignInActivity.this,homepage_activity.class));
            finish();
        }

        scrollView.setVerticalScrollBarEnabled(false);

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
            userName.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(pword))
        {
            password.setError("Required");
            password.requestFocus();
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
                if(task.isSuccessful())
                {
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
                        //configuring calendar to push notification everyday using alarm service
                        //writing this here to trigger notification only after verifying the user and signing in.
                        Calendar calendar = Calendar.getInstance();
                        //Calendar now = Calendar.getInstance();

                        calendar.set(Calendar.HOUR_OF_DAY, 9);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);

                        /*if (now.after(calendar))
                        {
                            Log.d("Hey," , "Added a day");
                            calendar.add(Calendar.DATE,1);
                        }*/

                        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
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
        alertDialog.setCancelable(true);
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }
}
