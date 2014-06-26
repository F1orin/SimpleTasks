package ua.com.florin.simpletasks.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Florin on 25.06.2014.
 */
public class TaskRemindReceiver extends BroadcastReceiver {

    /**
     * Logging tag constant
     */
    private static final String TAG = "TaskRemindReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        Log.d(TAG, "action = " + intent.getAction());
        Log.d(TAG, "extra = " + intent.getStringExtra("extra"));
    }
}
