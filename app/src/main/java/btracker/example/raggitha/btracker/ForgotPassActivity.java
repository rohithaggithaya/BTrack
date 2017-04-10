package btracker.example.raggitha.btracker;

import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ForgotPassActivity extends AppCompatActivity {

    private EditText resetEmail;
    private Button resetButton;
    private AlertDialog.Builder alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        resetEmail = (EditText) findViewById(R.id.forgotmailID);
        resetButton = (Button) findViewById(R.id.resetButton);
        alertDialog = new AlertDialog.Builder(this);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.setMessage("Are you sure?");
                alertDialog.setTitle("Reset Password");
                alertDialog.setCancelable(true);
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                alertDialog.setNegativeButton("No",null);
                AlertDialog dialog = alertDialog.create();
                dialog.show();
            }
        });


    }
}
