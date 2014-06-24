package ua.com.florin.simpletasks.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Florin on 04.06.2014.
 */
public class TasksDB extends SQLiteOpenHelper implements DBNames {

    /**
     * Logging tag constant
     */
    private static final String TAG = "TasksDB";

    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase tasksSQLiteDB;

    public TasksDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.tasksSQLiteDB = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME + "("
                + COL_ID + " INTEGER UNIQUE PRIMARY KEY AUTOINCREMENT, "
                + COL_TITLE + " TEXT, "
                + COL_CREATION_TIME + " INTEGER NOT NULL, "
                + COL_EXECUTION_TIME + " INTEGER, "
                + COL_REMIND_TIME + " INTEGER, "
                + COL_REPEAT_INTERVAL + " INTEGER, "
                + "UNIQUE (" + COL_ID + ") ON CONFLICT REPLACE);";
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion +
                ", which will destroy all old data");
        String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(SQL_DROP_TABLE);
        onCreate(db);
    }

    public long insert(ContentValues contentValues) {
        long rowID = tasksSQLiteDB.insert(TABLE_NAME, null, contentValues);
        return rowID;
    }

    public int update(ContentValues contentValues, String rowID) {
        int count = tasksSQLiteDB.update(TABLE_NAME, contentValues, "_id=" + rowID, null);
        return count;
    }

    public int delete(String rowID) {
        int count = tasksSQLiteDB.delete(TABLE_NAME, "_id=" + rowID, null);
        return count;
    }

    public Cursor getAllEntries() {
        return tasksSQLiteDB.query(TABLE_NAME, COLUMNS, null, null, null, null, null);
    }

    public Cursor getEntryByID(String rowID) {
        return tasksSQLiteDB.query(TABLE_NAME, COLUMNS, "_id=" + rowID, null, null, null, null);
    }
}
