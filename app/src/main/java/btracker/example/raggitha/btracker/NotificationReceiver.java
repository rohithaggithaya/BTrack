package btracker.example.raggitha.btracker;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by vaam on 22-04-2017.
 */

public class NotificationReceiver extends BroadcastReceiver {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    @Override
    public void onReceive(final Context context, Intent intent) {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String cdate = new SimpleDateFormat("dd/MM").format(new Date());

                Calendar tomDate = Calendar.getInstance();
                tomDate.add(Calendar.DATE, 1);

                String tdate = new SimpleDateFormat("dd/MM").format(tomDate.getTime());
                //String tdate = "23/04";//tomDate.get(Calendar.DAY_OF_MONTH) + "/" + tomDate.get(Calendar.MONTH);

                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    //String name = ds.getValue(UserData.class).getName();
                    String dob=(ds.getValue(UserData.class).getDOB().substring(0,5));

                    //checking if obtained date of birth from database is equal to today's date
                    if(dob.equals(cdate))
                    {
                        //checking if the dob returned is of the current user. If yes, wishing him.
                        if(ds.getValue(UserData.class).getEmail().equals(firebaseAuth.getCurrentUser().getEmail()))
                            setNotificationself(context, ds);// Say hbday to self
                        else
                            //checking if the dob returned is of different user. if yes, asking current user to wish him/her.
                            setNotification1(context, ds);//Say happy birthday to ds.getValue(UserData.class).getName();
                    }
                    //checking if obtained date of birth from database is equal to tomorrow's date
                    else if (dob.equals(tdate))
                    {
                        //checking if the dob returned is of different user. if yes, asking user to plan something.
                        if(!ds.getValue(UserData.class).getEmail().equals(firebaseAuth.getCurrentUser().getEmail()))
                            setNotification2(context, ds);//notification asking to plan for tomorrow's birthday of different user.
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    //wishing current user on his/her birthday
    private void setNotificationself(Context context, DataSnapshot dataSnapshot) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent repeating_intent = new Intent(context, MainActivity.class);
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        NotificationCompat.InboxStyle bigData = new NotificationCompat.InboxStyle();

        String content[] = {"Hey " + firebaseAuth.getCurrentUser().getDisplayName(),
                "Happy Birthday!! Have a good one"
                ,"Hope we will get a nice treat!"
                ,};

        bigData.setBigContentTitle("Warm wishes from B-Track");
        for (int i=0; i<content.length; i++)
            bigData.addLine(content[i]);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 101, repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.cakeicon)
                .setSound(defaultSoundUri)
                .setStyle(bigData)
                .setContentTitle("Notification from B-Track")
                .setAutoCancel(true);

        notificationManager.notify(101, builder.build());
    }


    //sending this notification to celebrate tomorrow's colleagues's birthday.
    private void setNotification2(Context context, DataSnapshot dataSnapshot) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent repeating_intent = new Intent(context, homeProfileActivity.class);
        repeating_intent.putExtra("NameKey",dataSnapshot.getValue(UserData.class).getName());
        repeating_intent.putExtra("DOBKey",dataSnapshot.getValue(UserData.class).getName());
        repeating_intent.putExtra("TeamKey",dataSnapshot.getValue(UserData.class).getTeam());
        repeating_intent.putExtra("EmailKey",dataSnapshot.getValue(UserData.class).getEmail());
        repeating_intent.putExtra("ManagerKey",dataSnapshot.getValue(UserData.class).getManager());
        repeating_intent.putExtra("GenderKey",dataSnapshot.getValue(UserData.class).getGender());
        repeating_intent.putExtra("curUserNameKey",firebaseAuth.getCurrentUser().getDisplayName());
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        NotificationCompat.InboxStyle bigData = new NotificationCompat.InboxStyle();

        String content[] = {"Hey " + firebaseAuth.getCurrentUser().getDisplayName(),
                "It's " + dataSnapshot.getValue(UserData.class).getName() + "'s Birthday tomorrow!"
                ,"Why don't we plan something to celebrate?"
                ,};

        bigData.setBigContentTitle("Reminder from B-Track");
        for (int i=0; i<content.length; i++)
            bigData.addLine(content[i]);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.cakeicon)
                .setSound(defaultSoundUri)
                .setStyle(bigData)
                .setContentTitle("Notification from B-Track")
                .setAutoCancel(true);
        notificationManager.notify(100, builder.build());
    }

    //checking if the dob returned is of different user. if yes, asking current user to wish him/her.
    private void setNotification1(Context context, DataSnapshot dataSnapshot) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent repeating_intent = new Intent(context, homeProfileActivity.class);
        repeating_intent.putExtra("NameKey",dataSnapshot.getValue(UserData.class).getName());
        repeating_intent.putExtra("DOBKey",dataSnapshot.getValue(UserData.class).getName());
        repeating_intent.putExtra("TeamKey",dataSnapshot.getValue(UserData.class).getTeam());
        repeating_intent.putExtra("EmailKey",dataSnapshot.getValue(UserData.class).getEmail());
        repeating_intent.putExtra("ManagerKey",dataSnapshot.getValue(UserData.class).getManager());
        repeating_intent.putExtra("GenderKey",dataSnapshot.getValue(UserData.class).getGender());
        repeating_intent.putExtra("curUserNameKey",firebaseAuth.getCurrentUser().getDisplayName());
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        NotificationCompat.InboxStyle bigData = new NotificationCompat.InboxStyle();

        String content[] = {"Hey " + firebaseAuth.getCurrentUser().getDisplayName(),
                            "It's " + dataSnapshot.getValue(UserData.class).getName() + "'s Birthday today!"
                            ,"Don't forget to wish!"
                            ,};

        bigData.setBigContentTitle("Reminder from B-Track");
        for (int i=0; i<content.length; i++)
            bigData.addLine(content[i]);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 102, repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSound(defaultSoundUri)
                .setSmallIcon(R.drawable.cakeicon)
                .setStyle(bigData)
                .setContentText("Don't forget to wish!")
                .setContentTitle("Notification from B-Track")
                .setAutoCancel(true);

        notificationManager.notify(102, builder.build());
    }
}
