package ua.com.florin.simpletasks.fragment.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Florin on 19.06.2014.
 */
public class TimePickerDialogFragment extends DialogFragment {
    private static final String TAG = "DatePickerDialogFragment";

    static Context sContext;
    static Calendar sDate;
    static TimePickerDialogFragmentListener sListener;

    public static TimePickerDialogFragment newInstance(Context context, int titleResource,
                                                       Calendar date) {
        TimePickerDialogFragment dialog = new TimePickerDialogFragment();

        sContext = context;
        sDate = date;

        Bundle args = new Bundle();
        args.putInt("title", titleResource);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int mHour = sDate.get(Calendar.HOUR_OF_DAY);
        int mMinute = sDate.get(Calendar.MINUTE);
        return new TimePickerDialog(sContext, timeSetListener, mHour, mMinute, true);
    }

    public void setTimeDialogFragmentListener(TimePickerDialogFragmentListener listener) {
        sListener = listener;
    }

    private TimePickerDialog.OnTimeSetListener timeSetListener =
            new TimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    Calendar newTime = Calendar.getInstance();
                    newTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    newTime.set(Calendar.MINUTE, minute);

                    //call back to the TimePickerDialogFragment listener
                    sListener.timeDialogFragmentTimeSet(newTime);
                }
            };

    public interface TimePickerDialogFragmentListener {
        public void timeDialogFragmentTimeSet(Calendar date);
    }
}
