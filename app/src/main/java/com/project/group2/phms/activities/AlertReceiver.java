package com.project.group2.phms.activities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.project.group2.phms.R;

/**
 * Created by ramajseepha on 4/8/17.
 */

public class AlertReceiver extends BroadcastReceiver {

    int notificationId = 6324;

    @Override
    public void onReceive(Context context, Intent intent) {

        String medName = intent.getStringExtra("medName");
        String key = intent.getStringExtra("key");
        createNotification(context, "Medication Notification" , "It is time to take your medication:" + medName, "Message From PHMS", medName, key);
    }

    public void createNotification(Context context, String msgTitle, String msgContentText, String msgTicker, String medName, String key){

        Intent medicationNotificationIntent = new Intent(context, MedicationNotification.class);
        medicationNotificationIntent.putExtra("medicationName", medName);
        medicationNotificationIntent.putExtra("key", key);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, medicationNotificationIntent
                , PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.cardiogram)
                .setContentTitle(msgTitle)
                .setTicker(msgTicker)
                .setContentText(msgContentText);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(uri);

        long[] vibrate = {500,1000};
        mBuilder.setVibrate(vibrate);

       // mBuilder.addAction(R.drawable.ic_snooze_black_24dp,"Snooze",pendingIntent);
       // mBuilder.addAction(R.drawable.ic_alarm_off_black_24dp,"Dismiss", pendingIntent);
       // mBuilder.addAction(R.drawable.ic_check_circle_black_24dp,"Take Medication", pendingIntent);

        mBuilder.setContentIntent(pendingIntent);

        mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);

        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(notificationId, mBuilder.build());
    }

}
