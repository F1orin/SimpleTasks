package ua.com.florin.simpletasks.db;

/**
 * Created by Florin on 04.06.2014.
 */
public interface DBNames {

    String DATABASE_NAME = "tasks.db";
    String TABLE_NAME = "tasks";

    String COL_ID = "_id";
    String COL_TITLE = "title";
    String COL_CREATION_TIME = "creation_time";
    String COL_EXECUTION_TIME = "execution_time";
    String COL_REMIND_TIME = "remind_time";
    String COL_REPEAT_INTERVAL = "repeat_interval";

    String[] COLUMNS = {
            COL_ID,
            COL_TITLE,
            COL_CREATION_TIME,
            COL_EXECUTION_TIME,
            COL_REMIND_TIME,
            COL_REPEAT_INTERVAL
    };

}
