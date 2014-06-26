package ua.com.florin.simpletasks.util;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import ua.com.florin.simpletasks.activity.MainActivity;
import ua.com.florin.simpletasks.db.TasksProvider;

/**
 * Created by Florin on 25.06.2014.
 */
public class MyHelper implements MyConstants {

    /**
     * Logging tag constant
     */
    private static final String TAG = "MyHelper";

    /**
     * Calls {@link ua.com.florin.simpletasks.activity.MainActivity#onFragmentAttached(int)}
     * and passes to it the position of selected item in Navigation Drawer.
     *
     * @param fragment fragment that has been selected in Navigation Drawer
     * @param activity activity that operates with fragments
     */
    public static void callOnFragmentAttached(Fragment fragment, Activity activity) {
        if (activity instanceof MainActivity) {
            Bundle args = fragment.getArguments();
            if (args != null) {
                MainActivity mainActivity = (MainActivity) activity;
                int position = args.getInt(ARG_DRAWER_POSITION);
                mainActivity.onFragmentAttached(position);
            }
        }
    }

    public static void getTaskCursorForId(Context context, long id) {
        Cursor mTaskByIdCursor = context.getContentResolver()
                .query(TasksProvider.CONTENT_URI, null, "_id=" + id, null, null);

        mTaskByIdCursor.moveToFirst();
    }
}
