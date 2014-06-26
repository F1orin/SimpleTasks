package ua.com.florin.simpletasks.data;

import java.util.Calendar;

/**
 * Created by Task on 26.06.2014.
 */
public class Task {

    /**
     * Logging tag constant
     */
    private static final String TAG = "Task";

    private long id;
    private String title;
    private Calendar creationTime;
    private Calendar executionTime;
    private Calendar remindTime;
    //repeat interval

    private Task() {
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Calendar getCreationTime() {
        return creationTime;
    }

    public Calendar getExecutionTime() {
        return executionTime;
    }

    public Calendar getRemindTime() {
        return remindTime;
    }

    public static Task getInstanceById(long id) {
        Task mTask = new Task();
        //build on id
        return mTask;
    }
}
