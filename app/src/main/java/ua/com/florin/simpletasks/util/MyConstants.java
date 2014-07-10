package ua.com.florin.simpletasks.util;

import java.text.SimpleDateFormat;

/**
 * Created by Florin on 10.06.2014.
 */
public interface MyConstants {

    int NEW_TASK_CODE = -88;
    int TASK_NOTIFICATION_ID = 99;

    String ARG_DRAWER_POSITION = "drawer_position";
    String ARG_TASK_ID = "task_id";

    String ACTION_CREATE_NOTIFICATION = "ua.com.florin.simpletasks.CREATE_NOTIFICATION";

    String[] DRAWER_ELEMENTS = {"Tasks", "About", "Test"};

    SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy");
    SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
    SimpleDateFormat sdfDateTime = new SimpleDateFormat("dd.MM.yyyy HH:mm");
}
