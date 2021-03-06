package ua.com.florin.simpletasks.fragment;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

import ua.com.florin.simpletasks.R;
import ua.com.florin.simpletasks.db.DBNames;
import ua.com.florin.simpletasks.db.TasksProvider;
import ua.com.florin.simpletasks.fragment.dialog.DatePickerDialogFragment;
import ua.com.florin.simpletasks.fragment.dialog.TimePickerDialogFragment;
import ua.com.florin.simpletasks.util.MyConstants;
import ua.com.florin.simpletasks.util.MyHelper;
import ua.com.florin.simpletasks.util.TaskRemindReceiver;


/**
 * Created by Florin on 20.05.2014.
 */
public class AddTaskFragment extends Fragment implements DBNames, MyConstants {

    /**
     * Logging tag constant
     */
    private static final String TAG = "AddTaskFragment";

    /**
     * Database entry ID of current item
     */
    private long mCurrentID = NEW_TASK_CODE;

    /**
     * A callback reference to communicate with activity
     */
    private AddTaskFragmentCallbacks mCallbacks;

    private Calendar mTaskCal = Calendar.getInstance();
    private Calendar mRemindCal = MyHelper.getDefaultRemindCalendar();

    private EditText mTaskTitleView;
    private TextView mTaskDateView;
    private TextView mRemindTimeView;
    private TextView mRemindDateView;
    private Spinner mTaskRepeatSpinner;

    private long repeatInterval;

    /**
     * The container Activity must implement this interface
     * so that the fragment can deliver messages
     */
    public interface AddTaskFragmentCallbacks {
        //callback methods to communicate with Activity should be here
    }

    /**
     * Makes sure that the container activity has implemented
     * the callback interface. If not, it throws an exception.
     *
     * @param activity the container activity
     */
    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach");
        super.onAttach(activity);
        try {
            mCallbacks = (AddTaskFragmentCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement AddTaskFragmentCallbacks");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        // If activity is recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        if (savedInstanceState != null) {
            mCurrentID = savedInstanceState.getLong(ARG_TASK_ID);
        }

        return inflater.inflate(R.layout.fragment_add_task, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);

        mTaskTitleView = (EditText) view.findViewById(R.id.frag_add_title);
        mTaskDateView = (TextView) view.findViewById(R.id.frag_add_task_date);
        mRemindTimeView = (TextView) view.findViewById(R.id.frag_add_remind_time);
        mRemindDateView = (TextView) view.findViewById(R.id.frag_add_remind_date);
        mTaskRepeatSpinner = (Spinner) view.findViewById(R.id.repeat_terms_spinner);
//        mTaskRepeatView = (TextView) view.findViewById(R.id.frag_add_repeat);

        final Button saveButton = (Button) view.findViewById(R.id.frag_add_button_save);
        final Button cancelButton = (Button) view.findViewById(R.id.frag_add_button_cancel);
        final ImageButton clearTaskDateButton = (ImageButton) view.findViewById(R.id.frag_add_clear_task_date);
        final ImageButton clearRemindButton = (ImageButton) view.findViewById(R.id.frag_add_clear_remind);
        final ImageButton clearTaskRepeatButton = (ImageButton) view.findViewById(R.id.frag_add_clear_repeat);

        updateTaskDateView();
        updateRemindTimeView();
        updateRemindDateView();

        /**
         * A listener for Views that allow to show date and time pickers.
         */
        View.OnTouchListener editTextListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (v.getId()) {
                    case R.id.frag_add_task_date:
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            showTaskDatePicker();
                            return true;
                        }
                    case R.id.frag_add_remind_time:
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            showRemindTimePicker();
                            mRemindDateView.setVisibility(View.VISIBLE);
                            return true;
                        }
                    case R.id.frag_add_remind_date:
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            showRemindDatePicker();
                            return true;
                        }
                    default:
                        return false;
                }
            }
        };
        mTaskDateView.setOnTouchListener(editTextListener);
        mRemindTimeView.setOnTouchListener(editTextListener);
        mRemindDateView.setOnTouchListener(editTextListener);

        /**
         * A listener for task's save/cancel and clear buttons.
         * Contains the task saving and updating logic.
         */
        View.OnClickListener buttonsListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Activity mActivity = getActivity();
                switch (v.getId()) {
                    case R.id.frag_add_button_save:
                        ContentValues mContentValues = new ContentValues();
                        mContentValues.put(COL_CREATION_TIME, System.currentTimeMillis());
                        // save title
                        if (isEmpty(mTaskTitleView)) {
                            mContentValues.putNull(COL_TITLE);
                        } else {
                            mContentValues.put(COL_TITLE, mTaskTitleView.getText().toString());
                        }
                        // save task date
                        if (mTaskCal == null) {
                            mContentValues.putNull(COL_EXECUTION_TIME);
                        } else {
                            mContentValues.put(COL_EXECUTION_TIME, mTaskCal.getTimeInMillis());
                        }
                        // save remind date and time
                        if (mRemindCal == null) {
                            mContentValues.putNull(COL_REMIND_TIME);
                        } else {
                            mContentValues.put(COL_REMIND_TIME, mRemindCal.getTimeInMillis());
                        }
                        //TODO add repeat functionality
                        mContentValues.putNull(COL_REPEAT_INTERVAL);

                        if (mCurrentID != NEW_TASK_CODE) {
                            // update existing task
                            getActivity().getContentResolver()
                                    .update(TasksProvider.CONTENT_URI, mContentValues, "_id=" + mCurrentID, null);
                        } else {
                            // create new task
                            String newTaskId = getActivity().getContentResolver()
                                    .insert(TasksProvider.CONTENT_URI, mContentValues)
                                    .getLastPathSegment();
                            if (newTaskId != null) {
                                mCurrentID = Long.parseLong(newTaskId);
                            }
                        }

                        scheduleReminder();

                        //go to task list when save button is pressed
                        getActivity().getFragmentManager().popBackStack();
                        break;
                    case R.id.frag_add_button_cancel:
                        getActivity().getFragmentManager().popBackStack();
                        break;
                    case R.id.frag_add_clear_task_date:
                        mTaskCal = null;
                        updateTaskDateView();
                        break;
                    case R.id.frag_add_clear_remind:
                        mRemindCal = null;
                        mRemindDateView.setVisibility(View.GONE);
                        updateRemindTimeView();
                        updateRemindDateView();
                        break;
                    case R.id.frag_add_clear_repeat:
                        mTaskRepeatSpinner.setSelection(0);
                        break;
                }
            }
        };
        saveButton.setOnClickListener(buttonsListener);
        cancelButton.setOnClickListener(buttonsListener);
        clearTaskDateButton.setOnClickListener(buttonsListener);
        clearRemindButton.setOnClickListener(buttonsListener);
        clearTaskRepeatButton.setOnClickListener(buttonsListener);

        /**
         * A listener for repeat interval spinner.
         */
        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        mTaskRepeatSpinner.setOnItemSelectedListener(spinnerListener);

        ArrayAdapter<CharSequence> repeatTermsAdapter =
                ArrayAdapter.createFromResource(getActivity(), R.array.repeat_terms, R.layout.my_spinner_textview);
        mTaskRepeatSpinner.setAdapter(repeatTermsAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

        // During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that updates the task content.
        Bundle args = getArguments();
        if (args != null) {
            mCurrentID = args.getLong(ARG_TASK_ID);
            // Set task based on argument passed in
            updateTaskView(mCurrentID);
        } else if (mCurrentID != NEW_TASK_CODE) {
            // Set task based on saved instance state defined during onCreateView
            updateTaskView(mCurrentID);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu");
        if (menu != null) {
            menu.removeItem(R.id.action_add);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);

        // Save the current task selection in case we need to recreate the fragment
        outState.putLong(ARG_TASK_ID, mCurrentID);
    }

    /**
     * Updates task date in UI
     */
    private void updateTaskDateView() {
        if (mTaskCal == null) {
            mTaskDateView.setText(R.string.text_undated);
        } else {
            mTaskDateView.setText(sdfDate.format(mTaskCal.getTime()));
        }
    }

    /**
     * Updates remind date in UI
     */
    private void updateRemindDateView() {
        if (mRemindCal == null) {
            mRemindDateView.setText("");
        } else {
            mRemindDateView.setText(sdfDate.format(mRemindCal.getTime()));
        }
    }

    /**
     * Updates remind time in UI
     */
    private void updateRemindTimeView() {
        if (mRemindCal == null) {
            mRemindTimeView.setText(R.string.text_no_remind);
        } else {
            mRemindTimeView.setText(sdfTime.format(mRemindCal.getTime()));
        }
    }

    /**
     * Updates task and remind dates and remind time in UI
     */
    private void updateAllViews() {
        updateTaskDateView();
        updateRemindTimeView();
        updateRemindDateView();
    }

    /**
     * Gets task entry with given ID from database, fills calendars with task data
     * and updates all views in UI.
     *
     * @param id task ID in database
     */
    private void updateTaskView(long id) {

        Uri mTaskUri = ContentUris.withAppendedId(TasksProvider.CONTENT_URI, id);
        Cursor mTaskByIdCursor = getActivity().getContentResolver()
                .query(mTaskUri, null, null, null, null);
        mTaskByIdCursor.moveToFirst();

        int idTitle = mTaskByIdCursor.getColumnIndexOrThrow(COL_TITLE);
        int idDate = mTaskByIdCursor.getColumnIndexOrThrow(COL_EXECUTION_TIME);
        int idRemind = mTaskByIdCursor.getColumnIndexOrThrow(COL_REMIND_TIME);
        int idRepeat = mTaskByIdCursor.getColumnIndexOrThrow(COL_REPEAT_INTERVAL);

        mTaskTitleView.setText(mTaskByIdCursor.getString(idTitle));
        if (mTaskByIdCursor.getType(idDate) == Cursor.FIELD_TYPE_NULL) {
            mTaskCal = null;
        } else {
            mTaskCal.setTimeInMillis(mTaskByIdCursor.getLong(idDate));
        }
        if (mTaskByIdCursor.getType(idRemind) == Cursor.FIELD_TYPE_NULL) {
            mRemindCal = null;
        } else {
            mRemindCal.setTimeInMillis(mTaskByIdCursor.getLong(idRemind));
        }
        updateAllViews();
    }

    /**
     * Shows DatePicker for task date and passes to it previously set task calendar.
     * If previous calendar is not available - passes current calendar.
     * Updates task date to that chosen in picker and refreshes its interface view.
     */
    private void showTaskDatePicker() {
        Calendar mDatePickerCal = (mTaskCal != null) ? mTaskCal : Calendar.getInstance();
        DatePickerDialogFragment datePicker =
                DatePickerDialogFragment.newInstance(getActivity(), R.string.text_set_date, mDatePickerCal);
        datePicker.setDateDialogFragmentListener(new DatePickerDialogFragment.DatePickerDialogFragmentListener() {
            @Override
            public void dateDialogFragmentDateSet(Calendar date) {
                mTaskCal = date;
                updateTaskDateView();
            }
        });
        datePicker.show(getFragmentManager(), null);
    }

    /**
     * Shows DatePicker for remind date and passes to it previously set remind calendar.
     * If previous calendar is not available - passes current calendar.
     * Updates remind date to that chosen in picker and refreshes its interface view.
     */
    private void showRemindDatePicker() {
        Calendar mDatePickerCal = (mRemindCal != null) ? mRemindCal : MyHelper.getDefaultRemindCalendar();
        DatePickerDialogFragment datePicker =
                DatePickerDialogFragment.newInstance(getActivity(), R.string.text_set_date, mDatePickerCal);
        datePicker.setDateDialogFragmentListener(new DatePickerDialogFragment.DatePickerDialogFragmentListener() {
            @Override
            public void dateDialogFragmentDateSet(Calendar date) {
                mRemindCal = date;
                updateRemindDateView();
                updateRemindTimeView();
            }
        });
        datePicker.show(getFragmentManager(), null);
    }

    /**
     * Shows TimePicker for remind time and passes to it previously set remind calendar.
     * If previous calendar is not available - passes current calendar.
     * Updates remind time to that chosen in picker and refreshes its interface view.
     */
    private void showRemindTimePicker() {
        Calendar mTimePickerCal = (mRemindCal != null) ? mRemindCal : MyHelper.getDefaultRemindCalendar();
        TimePickerDialogFragment timePicker =
                TimePickerDialogFragment.newInstance(getActivity(), R.string.text_set_time, mTimePickerCal);
        timePicker.setTimeDialogFragmentListener(new TimePickerDialogFragment.TimePickerDialogFragmentListener() {
            @Override
            public void timeDialogFragmentTimeSet(Calendar date) {
                if (mRemindCal == null) {
                    mRemindCal = MyHelper.getDefaultRemindCalendar();
                }
                mRemindCal.set(Calendar.HOUR_OF_DAY, date.get(Calendar.HOUR_OF_DAY));
                mRemindCal.set(Calendar.MINUTE, date.get(Calendar.MINUTE));
                updateRemindTimeView();
                updateRemindDateView();
            }
        });
        timePicker.show(getFragmentManager(), null);
    }

    /**
     * Checks whether editText is empty or not.
     *
     * @param editText a field for checking
     * @return true if editText is empty
     */
    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    /**
     * Schedules a reminder for the current task
     */
    private void scheduleReminder() {
        // generate URI with appended task id
        Uri taskUri = TasksProvider.CONTENT_URI.buildUpon()
                .appendPath(String.valueOf(mCurrentID))
                .build();

        Intent createNotificationIntent = new Intent(getActivity(), TaskRemindReceiver.class);
        createNotificationIntent.setAction(ACTION_CREATE_NOTIFICATION);
        createNotificationIntent.setData(taskUri);

        PendingIntent createNotificationPendingIntent =
                PendingIntent.getBroadcast(getActivity(), 0, createNotificationIntent, 0);

        AlarmManager alarmManager =
                (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        if (mRemindCal != null) {
            // create new reminder, overriding any previously existing alarms for this task
            // note: starting from KitKat setExact() is the only alarms for which there is a
            // strong demand for exact-time delivery
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, mRemindCal.getTimeInMillis(), createNotificationPendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, mRemindCal.getTimeInMillis(), createNotificationPendingIntent);
            }
        } else {
            // cancel the existing reminder
            alarmManager.cancel(createNotificationPendingIntent);
        }
    }
    //TODO add repeat functionality, pass interval to next alarm in extra, then compare it with constant of one-time event and process
}
