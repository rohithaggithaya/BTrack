package btracker.example.raggitha.btracker;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
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
    private int setNote1Counter1, setNoteCounter2;

    private Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    @Override
    public void onReceive(final Context context, Intent intent) {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        setNote1Counter1 = 1000;
        setNoteCounter2 = 100;

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
                            setNotificationself(context);// Say hbday to self
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
    private void setNotificationself(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent repeating_intent = new Intent(context, MainActivity.class);
        repeating_intent.setAction(Intent.ACTION_MAIN);
        repeating_intent.addCategory(Intent.CATEGORY_LAUNCHER);
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);

        NotificationCompat.InboxStyle bigData = new NotificationCompat.InboxStyle();

        String content[] = {"Hey " + firebaseAuth.getCurrentUser().getDisplayName(),
                "Happy Birthday!! Have a good one"
                ,"Hope we will get a nice treat!"
                ,};

        bigData.setBigContentTitle("Warm wishes from B-Track");
        for (int i=0; i<content.length; i++)
            bigData.addLine(content[i]);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 99, repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.cakeicon)
                .setSound(defaultSoundUri)
                .setContentText("Expand to view content")
                .setStyle(bigData)
                .setContentTitle("Notification from B-Track")
                .setAutoCancel(true);

        notificationManager.notify(99, builder.build());
    }


    //sending this notification to celebrate tomorrow's colleagues's birthday.
    private void setNotification2(Context context, DataSnapshot dataSnapshot) {

        Date ddate = null;
        try {
            ddate = new SimpleDateFormat("dd/MM/yyyy").parse(dataSnapshot.getValue(UserData.class).getDOB());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent repeating_intent = new Intent(context, homeProfileActivity.class);
        repeating_intent.putExtra("NameKey",dataSnapshot.getValue(UserData.class).getName());
        repeating_intent.putExtra("DOBKey",new SimpleDateFormat("dd/MMM").format(ddate));
        repeating_intent.putExtra("TeamKey",dataSnapshot.getValue(UserData.class).getTeam());
        repeating_intent.putExtra("EmailKey",dataSnapshot.getValue(UserData.class).getEmail());
        repeating_intent.putExtra("ManagerKey",dataSnapshot.getValue(UserData.class).getManager());
        repeating_intent.putExtra("GenderKey",dataSnapshot.getValue(UserData.class).getGender());
        repeating_intent.putExtra("curUserNameKey",firebaseAuth.getCurrentUser().getDisplayName());
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        repeating_intent.setAction(Intent.ACTION_MAIN);
        repeating_intent.addCategory(Intent.CATEGORY_LAUNCHER);

        NotificationCompat.InboxStyle bigData = new NotificationCompat.InboxStyle();

        String content[] = {"Hey " + firebaseAuth.getCurrentUser().getDisplayName(),
                "It's " + dataSnapshot.getValue(UserData.class).getName() + "'s Birthday tomorrow!"
                ,"Why don't we plan something to celebrate?"
                ,};

        bigData.setBigContentTitle("Reminder from B-Track");
        for (String aContent : content) bigData.addLine(aContent);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, setNote1Counter1, repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.cakeicon)
                .setSound(defaultSoundUri)
                .setStyle(bigData)
                .setContentText("Expand to view content")
                .setContentTitle("Notification from B-Track")
                .setAutoCancel(true);
        notificationManager.notify(setNote1Counter1, builder.build());
        setNote1Counter1++;
    }

    //checking if the dob returned is of different user. if yes, asking current user to wish him/her.
    private void setNotification1(Context context, DataSnapshot dataSnapshot) {
        Date ddate = null;
        try {
            ddate = new SimpleDateFormat("dd/MM/yyyy").parse(dataSnapshot.getValue(UserData.class).getDOB());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent repeating_intent = new Intent(context, homeProfileActivity.class);
        repeating_intent.putExtra("NameKey",dataSnapshot.getValue(UserData.class).getName());
        repeating_intent.putExtra("DOBKey",new SimpleDateFormat("dd/MMM").format(ddate));
        repeating_intent.putExtra("TeamKey",dataSnapshot.getValue(UserData.class).getTeam());
        repeating_intent.putExtra("EmailKey",dataSnapshot.getValue(UserData.class).getEmail());
        repeating_intent.putExtra("ManagerKey",dataSnapshot.getValue(UserData.class).getManager());
        repeating_intent.putExtra("GenderKey",dataSnapshot.getValue(UserData.class).getGender());
        repeating_intent.putExtra("curUserNameKey",firebaseAuth.getCurrentUser().getDisplayName());
        //repeating_intent.putExtra("CallingKey","Notification");
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        repeating_intent.setAction(Intent.ACTION_MAIN);
        repeating_intent.addCategory(Intent.CATEGORY_LAUNCHER);


        NotificationCompat.InboxStyle bigData = new NotificationCompat.InboxStyle();

        String content[] = {"Hey " + firebaseAuth.getCurrentUser().getDisplayName(),
                            "It's " + dataSnapshot.getValue(UserData.class).getName() + "'s Birthday today!"
                            ,"Don't forget to wish!"
                            ,};

        bigData.setBigContentTitle("Reminder from B-Track");
        for (int i=0; i<content.length; i++)
            bigData.addLine(content[i]);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, setNoteCounter2, repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSound(defaultSoundUri)
                .setSmallIcon(R.drawable.cakeicon)
                .setStyle(bigData)
                .setContentText("Expand to view content")
                .setContentText("Don't forget to wish!")
                .setContentTitle("Notification from B-Track")
                .setAutoCancel(true);

        notificationManager.notify(setNoteCounter2, builder.build());
        setNoteCounter2++;
    }
}
