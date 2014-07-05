package ua.com.florin.simpletasks.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import ua.com.florin.simpletasks.R;
import ua.com.florin.simpletasks.db.DBNames;
import ua.com.florin.simpletasks.db.TasksProvider;
import ua.com.florin.simpletasks.fragment.AboutFragment;
import ua.com.florin.simpletasks.fragment.AddTaskFragment;
import ua.com.florin.simpletasks.fragment.NavigationDrawerFragment;
import ua.com.florin.simpletasks.fragment.TasksFragment;
import ua.com.florin.simpletasks.fragment.TestFragment;
import ua.com.florin.simpletasks.util.MyConstants;


public class MainActivity extends Activity implements DBNames, MyConstants,
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        TasksFragment.OnTaskSelectedListener,
        AddTaskFragment.AddTaskFragmentCallbacks {

    /**
     * Logging tag constant
     */
    private static final String TAG = "MainActivity";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    /**
     * String array holding Navigation Drawer headers, that correspond to fragment names.
     */
    private String[] mDrawerElements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        mDrawerElements = getResources().getStringArray(R.array.drawer_items);

        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add:
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new AddTaskFragment())
                        .addToBackStack(null)
                        .commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onFragmentAttached(int position) {
        Log.d(TAG, "Attach: " + mDrawerElements[position]);
        mTitle = mDrawerElements[position];
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    /**
     * Here the order of references in navigation drawer is determined.
     * To change the order constructor calls in cases should be rearranged
     * and "drawer_items" array is res/values/arrays should also be changed accordingly.
     *
     * @param position position of selected item in Navigation Drawer, starting from 0.
     */
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Log.d(TAG, "onNavigationDrawerItemSelected");
        // update the main content by replacing fragments
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new TasksFragment();
                break;
            case 1:
                fragment = new AboutFragment();
                break;
            case 2:
                fragment = new TestFragment();
                break;
            default:
                fragment = new TasksFragment();
        }

        Bundle args = new Bundle();
        args.putInt(ARG_DRAWER_POSITION, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    @Override
    public void onTaskSelected(long id) {
        // The user selected the headline of an article from the HeadlinesFragment

        // Capture the article fragment from the activity layout
/*        AddTaskFragment taskFragment = (AddTaskFragment)
                getFragmentManager().findFragmentById(R.id.article_fragment);

            if (taskFragment != null) {
                // If article frag is available, we're in two-pane layout...

                // Call a method in the ArticleFragment to update its content
                taskFragment.updateArticleView(position);

            } else {*/
        // If the frag is not available, we're in the one-pane layout and must swap frags...

        // Create fragment and give it an argument for the selected article
        AddTaskFragment newFragment = new AddTaskFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_TASK_ID, id);
        newFragment.setArguments(args);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.container, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
//        }
    }

//    @Override
//    public void createNotification(Uri uri) {
//        Cursor mTaskByIdCursor = getContentResolver()
//                .query(uri, null, null, null, null);
//        mTaskByIdCursor.moveToFirst();
//
//        int idTitle = mTaskByIdCursor.getColumnIndexOrThrow(COL_TITLE);
//        int idDate = mTaskByIdCursor.getColumnIndexOrThrow(COL_EXECUTION_TIME);
//        int idRemind = mTaskByIdCursor.getColumnIndexOrThrow(COL_REMIND_TIME);
//
//        Notification.Builder mBuilder = new Notification.Builder(this)
//                .setSmallIcon(R.drawable.ic_launcher)
//                .setContentTitle(mTaskByIdCursor.getString(idTitle));
//
//        Intent resultIntent = new Intent(this, MainActivity.class);
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//        stackBuilder.addParentStack(MainActivity.class);
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent =
//                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//        mBuilder.setContentIntent(resultPendingIntent);
//        NotificationManager mNotificationManager =
//                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        mNotificationManager.notify(88, mBuilder.getNotification());
//    }
}
