package btracker.example.raggitha.btracker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class updatePasswordActivity extends AppCompatActivity {

    private EditText currentPassword, newPassword, retypedNewPassword;
    private Button updateButton;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        currentPassword = (EditText) findViewById(R.id.UPCurrentPasswordID);
        newPassword = (EditText) findViewById(R.id.UPNewPasswordID);
        retypedNewPassword = (EditText) findViewById(R.id.UPRetypeNewPasswordID);
        updateButton = (Button) findViewById(R.id.UPUpdateButtonID);

        firebaseAuth = FirebaseAuth.getInstance();

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curpass = currentPassword.getText().toString().trim();
                String newpass = newPassword.getText().toString().trim();
                String retypepass = retypedNewPassword.getText().toString().trim();

                if (TextUtils.isEmpty(curpass)) {
                    currentPassword.setError("Required");
                    return;
                }
                if (TextUtils.isEmpty(newpass)) {
                    newPassword.setError("Required");
                    return;
                }
                if (TextUtils.isEmpty(retypepass)) {
                    retypedNewPassword.setError("Required");
                    return;
                }
                if (!retypepass.equals(newpass)) {
                    retypedNewPassword.setError("Passwords do not match");
                    return;
                }
                if (curpass.equals(newpass)) {
                    newPassword.setError("New password cannot be same as old password");
                    return;
                }

                firebaseAuth.getCurrentUser().updatePassword(retypepass)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    firebaseAuth.signOut();
                                    startActivity(new Intent(updatePasswordActivity.this, SignInActivity.class));
                                    finish();
                                    Toast.makeText(getApplicationContext(), "Password Update Successful", Toast.LENGTH_SHORT).show();
                                } else
                                    onFailure(task.getException());
                            }

                        });
            }
        });
    }

    private void onFailure(Exception exception) {
        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(updatePasswordActivity.this, homepage_activity.class));
        finish();
    }
}