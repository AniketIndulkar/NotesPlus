package com.notes.android.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import android.util.Log;

import com.notes.android.R;
import com.notes.android.home.HomeActivity;
import com.notes.android.note.Note;
import com.notes.android.utilities.AppConstants;

import java.util.List;

/**
 * Created by appstartmac1 on 05/01/17.
 */

public class AlarmReceiver extends BroadcastReceiver {
    Context mContext;
    private String TAG = "AlarmReceiver";
    private String data;
    private int noteId;
    private SharedPreferences pref;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "onReceive: Alarm service " + intent.getParcelableExtra("ReminderNoteData"));


        if (intent.getParcelableExtra("ReminderNoteData") != null) {
            Bundle dataBundle = intent.getParcelableExtra("ReminderNoteData");
            data = dataBundle.getString("ReminderData");
            noteId = dataBundle.getInt(AppConstants.NOTE_ID);
            Log.d(TAG, "onReceive: Data is not null " + data);
        }
        mContext = context;
        pref = mContext.getSharedPreferences(AppConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        sendNotification();
    }

    private void sendNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext).setSmallIcon(R.drawable.notification);

        if (data != null) {
            mBuilder.setContentTitle("Notes+")
                    .setContentText(data);
        } else {
            mBuilder.setContentTitle("Notes+ Reminder ")
                    .setContentText("You need to do something check Note+ App");
        }

        mBuilder.setVibrate(new long[]{0, 100, 1000, 100, 0, 0, 100, 1000, 100, 0, 0, 100, 1000, 100, 0, 0, 100, 1000, 100, 0, 0, 100, 1000, 100, 0});

        Intent resultIntent = new Intent(mContext, Note.class);
        resultIntent.putExtra("NotesData", data);
        resultIntent.putExtra(AppConstants.NOTE_ID, noteId);
        resultIntent.putExtra("From", "Notification");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(HomeActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1234, mBuilder.build());
    }

    private CharSequence getToDoString(List<String> todoList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String todo : todoList) {
            stringBuilder.append(" \u25CF " + todo);
        }
        return stringBuilder.toString();
    }
}
