package ua.com.florin.simpletasks.fragment.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by Florin on 19.06.2014.
 */
public class DatePickerDialogFragment extends DialogFragment {
    private static final String TAG = "DatePickerDialogFragment";

    static Context sContext;
    static Calendar sDate;
    static DatePickerDialogFragmentListener sListener;

    public static DatePickerDialogFragment newInstance(Context context, int titleResource,
                                                       Calendar date) {
        DatePickerDialogFragment dialog = new DatePickerDialogFragment();

        sContext = context;
        sDate = date;

        Bundle args = new Bundle();
        args.putInt("title", titleResource);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int mYear = sDate.get(Calendar.YEAR);
        int mMonth = sDate.get(Calendar.MONTH);
        int mDay = sDate.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(sContext, dateSetListener, mYear, mMonth, mDay);
    }

    public void setDateDialogFragmentListener(DatePickerDialogFragmentListener listener) {
        sListener = listener;
    }

    private DatePickerDialog.OnDateSetListener dateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {

                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, monthOfYear, dayOfMonth);

                    //call back to the DatePickerDialogFragment listener
                    sListener.dateDialogFragmentDateSet(newDate);
                }
            };

    public interface DatePickerDialogFragmentListener {
        public void dateDialogFragmentDateSet(Calendar date);
    }
}
