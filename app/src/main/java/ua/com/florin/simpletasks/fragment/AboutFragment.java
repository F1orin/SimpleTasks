package ua.com.florin.simpletasks.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ua.com.florin.simpletasks.R;
import ua.com.florin.simpletasks.activity.MainActivity;
import ua.com.florin.simpletasks.util.MyConstants;
import ua.com.florin.simpletasks.util.MyHelper;


/**
 * Created by Florin on 20.05.2014.
 */
public class AboutFragment extends Fragment implements MyConstants {

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MyHelper.callOnFragmentAttached(this, activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        return rootView;
    }


}
