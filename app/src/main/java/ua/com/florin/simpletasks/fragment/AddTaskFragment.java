package ua.com.florin.simpletasks.fragment;

import android.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Calendar;

import ua.com.florin.simpletasks.R;
import ua.com.florin.simpletasks.db.DBNames;
import ua.com.florin.simpletasks.db.TasksProvider;
import ua.com.florin.simpletasks.fragment.dialog.DatePickerDialogFragment;
import ua.com.florin.simpletasks.fragment.dialog.TimePickerDialogFragment;
import ua.com.florin.simpletasks.util.MyConstants;


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

    private Calendar mTaskCal = Calendar.getInstance();
    private Calendar mRemindCal = Calendar.getInstance();

    private EditText mTaskTitleView;
    private TextView mTaskDateView;
    private TextView mRemindTimeView;
    private TextView mRemindDateView;
    private TextView mTaskRepeatView;

    /**
     * The container Activity must implement this interface so that the frag can deliver messages
     */
    public interface OnPickerCalledListener {

        /**
         * Realizes logic that executes when picker is called
         *
         * @param pickerCode picker code specified as constant
         *                   in {@link ua.com.florin.simpletasks.util.MyConstants}
         */
        public void onPickerCalled(int pickerCode, long defaultTime);
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
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mTaskTitleView = (EditText) view.findViewById(R.id.frag_add_title);
        mTaskDateView = (TextView) view.findViewById(R.id.frag_add_task_date);
        mRemindTimeView = (TextView) view.findViewById(R.id.frag_add_remind_time);
        mRemindDateView = (TextView) view.findViewById(R.id.frag_add_remind_date);
        mTaskRepeatView = (TextView) view.findViewById(R.id.frag_add_repeat);

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
         * A listener for save and cancel buttons.
         * Contains the task saving and updating logic.
         */
        View.OnClickListener buttonsListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.frag_add_button_save:
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(COL_CREATION_TIME, System.currentTimeMillis());
                        if (isEmpty(mTaskTitleView)) {
                            contentValues.putNull(COL_TITLE);
                        } else {
                            contentValues.put(COL_TITLE, mTaskTitleView.getText().toString());
                        }
                        if (mTaskCal == null) {
                            contentValues.putNull(COL_EXECUTION_TIME);
                        } else {
                            contentValues.put(COL_EXECUTION_TIME, mTaskCal.getTimeInMillis());
                        }
                        if (mRemindCal == null) {
                            contentValues.putNull(COL_REMIND_TIME);
                        } else {
                            contentValues.put(COL_REMIND_TIME, mRemindCal.getTimeInMillis());
                        }
                        //TODO add repeat functionality
                        contentValues.putNull(COL_REPEAT_INTERVAL);

                        if (mCurrentID != NEW_TASK_CODE) {
                            // update existing task
                            getActivity().getContentResolver()
                                    .update(TasksProvider.CONTENT_URI, contentValues, "_id=" + mCurrentID, null);
                        } else {
                            // create new task
                            getActivity().getContentResolver()
                                    .insert(TasksProvider.CONTENT_URI, contentValues);
                        }

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
                        updateRemindTimeView();
                        updateRemindDateView();
                        break;
                    case R.id.frag_add_clear_repeat:
                        break;
                }
            }
        };
        saveButton.setOnClickListener(buttonsListener);
        cancelButton.setOnClickListener(buttonsListener);
        clearTaskDateButton.setOnClickListener(buttonsListener);
        clearRemindButton.setOnClickListener(buttonsListener);
        clearTaskRepeatButton.setOnClickListener(buttonsListener);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
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
        if (mRemindCal == null /*|| !mRemindDateSet*/) {
            mRemindDateView.setText("");
        } else {
            mRemindDateView.setText(sdfDate.format(mRemindCal.getTime()));
        }
    }

    /**
     * Updates remind time in UI
     */
    private void updateRemindTimeView() {
        if (mRemindCal == null /*|| !mRemindTimeSet*/) {
            mRemindTimeView.setText(R.string.text_no_remind);
        } else {
            mRemindTimeView.setText(sdfTime.format(mRemindCal.getTime()));
        }
    }

    private void updateAllViews() {
        updateTaskDateView();
        updateRemindTimeView();
        updateRemindDateView();
    }

    private void updateTaskView(long id) {

        Cursor mTaskByIdCursor = getActivity().getContentResolver()
                .query(TasksProvider.CONTENT_URI, null, "_id=" + id, null, null);

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
        Calendar mDatePickerCal = (mRemindCal != null) ? mRemindCal : Calendar.getInstance();
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
        Calendar mTimePickerCal = (mRemindCal != null) ? mRemindCal : Calendar.getInstance();
        TimePickerDialogFragment timePicker =
                TimePickerDialogFragment.newInstance(getActivity(), R.string.text_set_time, mTimePickerCal);
        timePicker.setTimeDialogFragmentListener(new TimePickerDialogFragment.TimePickerDialogFragmentListener() {
            @Override
            public void timeDialogFragmentTimeSet(Calendar date) {
                if (mRemindCal == null) {
                    mRemindCal = Calendar.getInstance();
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
}