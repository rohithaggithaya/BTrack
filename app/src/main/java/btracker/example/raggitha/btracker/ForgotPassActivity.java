package btracker.example.raggitha.btracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.preference.DialogPreference;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassActivity extends AppCompatActivity
{

    private EditText resetEmail;
    private Button resetButton;
    private AlertDialog.Builder alertDialog;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        resetEmail = (EditText) findViewById(R.id.forgotmailID);
        resetButton = (Button) findViewById(R.id.resetButton);
        alertDialog = new AlertDialog.Builder(this);
        auth = FirebaseAuth.getInstance();

        resetButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                final String emailReset = resetEmail.getText().toString().trim();
                if (emailReset.isEmpty())
                    resetEmail.setError("Required");
                else {
                    alertDialog.setMessage("Are you sure?");
                    alertDialog.setTitle("Reset Password");
                    alertDialog.setCancelable(true);
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            auth.sendPasswordResetEmail(emailReset)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Password reset email sent!", Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(ForgotPassActivity.this, SignInActivity.class));
                                                finish();
                                            } else
                                                onFailure(task.getException());
                                        }
                                    });
                        }
                    });

                    alertDialog.setNegativeButton("No", null);
                    AlertDialog dialog = alertDialog.create();
                    dialog.show();
                }
            }

        });
    }

    private void onFailure(Exception exception) {
        String message = exception.getMessage();
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ForgotPassActivity.this,SignInActivity.class));
        finish();
    }
}
