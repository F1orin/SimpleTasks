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


/**
 * Created by Florin on 20.05.2014.
 */
public class AboutFragment extends Fragment implements MyConstants {

    public AboutFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        return rootView;
    }

    /**
     * This method calls {@link MainActivity#onFragmentAttached(int)} and passes to it
     * a position of selected item in Navigation Drawer.
     *
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        ((MainActivity) activity).onFragmentAttached(getArguments().getInt(ARG_DRAWER_POSITION));
    }
}
