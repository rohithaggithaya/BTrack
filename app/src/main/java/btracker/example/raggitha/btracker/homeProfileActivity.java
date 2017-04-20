package btracker.example.raggitha.btracker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class homeProfileActivity extends AppCompatActivity {

    private ImageView hmProfileIcon,hmProfileEmailIcon;
    private TextView hmProfileName, hmProfileGender, hmProfileEmail, hmProfileDOB, hmProfileTeam,hmManager;
    private ImageView genderIcon,lmicon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_profile);

        hmProfileIcon = (ImageView) findViewById(R.id.hmpfIconID);
        hmProfileName = (TextView) findViewById(R.id.hmpfNameID);
        hmProfileGender = (TextView) findViewById(R.id.hmpfGenderID);
        hmProfileEmail = (TextView) findViewById(R.id.hmpfEmailID);
        hmProfileDOB = (TextView) findViewById(R.id.hmpfDOBID);
        hmProfileTeam = (TextView) findViewById(R.id.hmpfTeamID);
        hmProfileEmailIcon = (ImageView) findViewById(R.id.hmPfEmailIconID);
        genderIcon = (ImageView) findViewById(R.id.hmpfGenderIconID);
        lmicon = (ImageView) findViewById(R.id.hmpfmanagerIconID);
        hmManager = (TextView) findViewById(R.id.hmpfmanagerID);

        final Bundle extras = getIntent().getExtras();

        hmProfileName.setText(extras.get("NameKey").toString());
        hmProfileGender.setText(extras.get("GenderKey").toString());
        hmProfileEmail.setText(extras.get("EmailKey").toString());
        hmProfileDOB.setText(extras.get("DOBKey").toString());
        hmProfileTeam.setText(extras.get("TeamKey").toString());
        hmManager.setText(extras.get("ManagerKey").toString());


        if(extras.get("GenderKey").toString().equals("Male"))
            genderIcon.setImageResource(R.drawable.maleicon);
        else
            genderIcon.setImageResource(R.drawable.femaleicon);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Photos").child(extras.get("EmailKey").toString());
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(homeProfileActivity.this).load(uri.toString()).centerCrop().fit().into(hmProfileIcon);
            }
        });

        hmProfileEmailIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder body = new StringBuilder();
                String name = extras.get("NameKey").toString();
                String toEmail = extras.get("EmailKey").toString();
                String currentUserName = extras.get("curUserNameKey").toString();

                body.append("Hey " + name + ",\n");
                body.append("\n Wishing you a very happy birthday!! \n");
                body.append("\n Regards, \n " + currentUserName );
                Intent Emailintent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",toEmail,null));
                Emailintent.putExtra(Intent.EXTRA_SUBJECT,currentUserName + " wishes you through B-Track");
                Emailintent.putExtra(Intent.EXTRA_TEXT,body.toString());

                startActivity(Emailintent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(homeProfileActivity.this,homepage_activity.class));
        finish();
    }
}
