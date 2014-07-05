package ua.com.florin.simpletasks.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import ua.com.florin.simpletasks.R;
import ua.com.florin.simpletasks.db.DBNames;
import ua.com.florin.simpletasks.util.MyConstants;


/**
 * Created by Florin on 08.06.2014.
 */
public class TasksCursorAdapter extends SimpleCursorAdapter implements MyConstants, DBNames {

    /**
     * Logging tag constant
     */
    private static String TAG = "TasksCursorAdapter";
    private final LayoutInflater mInflater;

    public TasksCursorAdapter(Context context, Cursor c) {
        super(context,
                R.layout.list_item_task,
                c,
                new String[]{COL_TITLE, COL_EXECUTION_TIME, COL_REMIND_TIME, COL_REPEAT_INTERVAL},
                new int[]{R.id.list_item_task_title,
                        R.id.list_item_task_date_text_view,
                        R.id.list_item_task_remind_text_view,
                        R.id.list_item_task_repeat_text_view},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        );
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.list_item_task, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);
        TextView titleTextView = (TextView) view.findViewById(R.id.list_item_task_title);
        TextView dateTextView = (TextView) view.findViewById(R.id.list_item_task_date_text_view);
        TextView remindTextView = (TextView) view.findViewById(R.id.list_item_task_remind_text_view);
        TextView repeatTextView = (TextView) view.findViewById(R.id.list_item_task_repeat_text_view);

        int idTitle = cursor.getColumnIndexOrThrow(COL_TITLE);
        int idDate = cursor.getColumnIndexOrThrow(COL_EXECUTION_TIME);
        int idRemind = cursor.getColumnIndexOrThrow(COL_REMIND_TIME);
        int idRepeat = cursor.getColumnIndexOrThrow(COL_REPEAT_INTERVAL);
        if (cursor.getType(idTitle) == Cursor.FIELD_TYPE_NULL) {
            titleTextView.setText(R.string.text_untitled);
        } else {
            titleTextView.setText(cursor.getString(idTitle));
        }
        if (cursor.getType(idDate) == Cursor.FIELD_TYPE_NULL) {
            dateTextView.setText(R.string.text_undated);
        } else {
            dateTextView.setText(sdfDate.format(cursor.getLong(idDate)));
        }
        if (cursor.getType(idRemind) == Cursor.FIELD_TYPE_NULL) {
            remindTextView.setText(R.string.text_no_remind);
        } else {
            remindTextView.setText(sdfDateTime.format(cursor.getLong(idRemind)));
        }
        if (cursor.getType(idRepeat) == Cursor.FIELD_TYPE_NULL) {
            repeatTextView.setText(R.string.text_no_repeat);
        } else {
            repeatTextView.setText("not null");
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        return v;
    }
}
