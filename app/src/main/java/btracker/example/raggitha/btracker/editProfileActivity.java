package btracker.example.raggitha.btracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class editProfileActivity extends AppCompatActivity {

    private Button updateButton, cancelButton;
    private EditText updateName, currentPassword, updateDOB;
    private Spinner updateTeam;
    private TextView EPEmail;

    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private AlertDialog.Builder alertDialog;

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
        currentPassword = (EditText) findViewById(R.id.EPPasswordID);

        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseAuth = FirebaseAuth.getInstance();
        EPEmail.setText(firebaseAuth.getCurrentUser().getEmail());
        alertDialog = new AlertDialog.Builder(this);

        String newName = updateName.getText().toString().trim();
        String newTeam = updateTeam.getSelectedItem().toString().trim();
        String newDOB = updateDOB.getText().toString().trim();
        final String cPassword = currentPassword.getText().toString().trim();
        final String email = firebaseAuth.getCurrentUser().getEmail();

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.setTitle("Update?");
                alertDialog.setMessage("Are you sure?");
                alertDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseAuth.signInWithEmailAndPassword(email,cPassword)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful())
                                            updateProfile();
                                        else
                                            onFailure(task.getException());
                                    }
                                });
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
                alertDialog.setMessage("Are you sure?");
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

    private void onFailure(Exception exception) {
        Toast.makeText(getApplicationContext(),exception.toString(),Toast.LENGTH_LONG).show();
    }

    private void updateProfile() {

    }
}
