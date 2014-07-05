package ua.com.florin.simpletasks.fragment;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Calendar;

import ua.com.florin.simpletasks.R;
import ua.com.florin.simpletasks.util.MyConstants;
import ua.com.florin.simpletasks.util.MyHelper;
import ua.com.florin.simpletasks.util.TaskRemindReceiver;

/**
 * Created by Florin on 25.06.2014.
 */
public class TestFragment extends Fragment implements MyConstants {
    /**
     * Logging tag constant
     */
    private static final String TAG = "TestFragment";

    NotificationManager notificationManager;
    AlarmManager alarmManager;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MyHelper.callOnFragmentAttached(this, activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Button testButton = (Button) view.findViewById(R.id.testButton);

        View.OnClickListener buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.testButton) {
                    Log.d(TAG, "test button pressed");
                    Calendar testCal = MyHelper.getDefaultRemindCalendar();
                    Log.d(TAG, testCal.toString());
                }
            }
        };
        testButton.setOnClickListener(buttonListener);
    }

}
