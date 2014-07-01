package ua.com.florin.simpletasks.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import ua.com.florin.simpletasks.R;
import ua.com.florin.simpletasks.activity.MainActivity;
import ua.com.florin.simpletasks.db.DBNames;

/**
 * Created by Florin on 25.06.2014.
 */
public class TaskRemindReceiver extends BroadcastReceiver implements MyConstants, DBNames {

    /**
     * Logging tag constant
     */
    private static final String TAG = "TaskRemindReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        Log.d(TAG, "action = " + intent.getAction());
        Uri taskUri = intent.getData();
        createNotification(context, taskUri);
    }

    public void createNotification(Context context, Uri uri) {
        Cursor mTaskByIdCursor = context.getContentResolver()
                .query(uri, null, null, null, null);
        mTaskByIdCursor.moveToFirst();

        int idTitle = mTaskByIdCursor.getColumnIndexOrThrow(COL_TITLE);
        int idDate = mTaskByIdCursor.getColumnIndexOrThrow(COL_EXECUTION_TIME);
        int idRemind = mTaskByIdCursor.getColumnIndexOrThrow(COL_REMIND_TIME);

        Notification.Builder mBuilder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(mTaskByIdCursor.getString(idTitle));

        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        Notification taskNotification = mBuilder.getNotification();
        taskNotification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //TODO fix hard code
        mNotificationManager.notify(88, taskNotification);
        Log.d(TAG, "notification created: " + uri);
    }
}
