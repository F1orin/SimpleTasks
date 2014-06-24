package ua.com.florin.simpletasks.util;

import java.text.SimpleDateFormat;

/**
 * Created by Florin on 10.06.2014.
 */
public interface MyConstants {

    int NEW_TASK_CODE = -88;

    String ARG_DRAWER_POSITION = "drawer_position";
    String ARG_TASK_ID = "task_id";
    String ARG_DELETE_CONFIRMED = "delete_confirmed";

    String[] DRAWER_ELEMENTS = {"Tasks", "About"};

    SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy");
    SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
    SimpleDateFormat sdfDateTime = new SimpleDateFormat("dd.MM.yyyy HH:mm");
}
