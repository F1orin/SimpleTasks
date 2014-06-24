package ua.com.florin.simpletasks.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Florin on 07.06.2014.
 */
public class TasksProvider extends ContentProvider implements DBNames {

    /**
     * Logging tag constant
     */
    private static final String TAG = "TasksProvider";

    static final String AUTHORITY = "ua.com.florin.simpletasks";
    static final String PATH = "tasks";

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + PATH);

    static final String ALL_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + PATH;
    static final String ITEM_CONTENT_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + PATH;

    static final int TASKS = 1;
    static final int TASK_BY_ID = 2;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, PATH, TASKS);
        uriMatcher.addURI(AUTHORITY, PATH + "/#", TASK_BY_ID);
    }

    SQLiteOpenHelper tasksDB;
    SQLiteDatabase sqLiteDatabase;

    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate");
        tasksDB = new TasksDB(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query - " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case TASKS:
                Log.d(TAG, "TASKS");
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = COL_CREATION_TIME + " DESC";
                }
                break;
            case TASK_BY_ID:
                String id = uri.getLastPathSegment();
                Log.d(TAG, "TASK_BY_ID " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = COL_ID + " = " + id;
                } else {
                    selection = selection + " AND " + COL_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        sqLiteDatabase = tasksDB.getWritableDatabase();
        Log.d(TAG, "Query -  " +
                "projection - " + projection +
                "selection - " + selection +
                "selectionArgs - " + selectionArgs +
                "sortOrder - " + sortOrder);
        Cursor cursor = sqLiteDatabase.query(TABLE_NAME, projection, selection,
                selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), CONTENT_URI);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(TAG, "insert - " + uri.toString());
        if (uriMatcher.match(uri) != TASKS) {
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        sqLiteDatabase = tasksDB.getWritableDatabase();
        long rowID;
        sqLiteDatabase.beginTransaction();
        try {
            rowID = sqLiteDatabase.insert(TABLE_NAME, null, values);
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
        Uri resultUri = ContentUris.withAppendedId(CONTENT_URI, rowID);
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int numInserted = 0;
        if (uriMatcher.match(uri) != TASKS) {
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        sqLiteDatabase = tasksDB.getWritableDatabase();
        sqLiteDatabase.beginTransaction();
        try {
            for (ContentValues cv : values) {
                if (sqLiteDatabase.insert(TABLE_NAME, null, cv) <= 0) {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
            }
            sqLiteDatabase.setTransactionSuccessful();
            numInserted = values.length;
        } finally {
            sqLiteDatabase.endTransaction();
        }
        Log.d(TAG, "bulkInsert - " + uri.toString());
        getContext().getContentResolver().notifyChange(uri, null);
        return numInserted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(TAG, "update, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case TASKS:
                Log.d(TAG, "TASKS");
                break;
            case TASK_BY_ID:
                String id = uri.getLastPathSegment();
                Log.d(TAG, "TASK_BY_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = COL_ID + " = " + id;
                } else {
                    selection = selection + " AND " + COL_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        sqLiteDatabase = tasksDB.getWritableDatabase();
        int cnt = sqLiteDatabase.update(TABLE_NAME, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "delete, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case TASKS:
                Log.d(TAG, "TASKS");
                break;
            case TASK_BY_ID:
                String id = uri.getLastPathSegment();
                Log.d(TAG, "TASK_BY_ID - " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = COL_ID + " = " + id;
                } else {
                    selection = selection + " AND " + COL_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        sqLiteDatabase = tasksDB.getWritableDatabase();
        int cnt = -1;
        try {
            sqLiteDatabase.beginTransaction();
            cnt = sqLiteDatabase.delete(TABLE_NAME, selection, selectionArgs);
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case TASKS:
                return ALL_CONTENT_TYPE;
            case TASK_BY_ID:
                return ITEM_CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}
