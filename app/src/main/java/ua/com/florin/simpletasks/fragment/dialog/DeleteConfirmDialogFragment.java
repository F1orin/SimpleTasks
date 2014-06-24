package ua.com.florin.simpletasks.fragment.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ua.com.florin.simpletasks.R;

/**
 * Created by Florin on 20.06.2014.
 */
public class DeleteConfirmDialogFragment extends DialogFragment {

    /**
     * Logging tag constant
     */
    private static final String TAG = "DeleteConfirmDialogFragment";

    // Use this instance of the interface to deliver action events
    private DeleteConfirmDialogListener mListener;

    /**
     * The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it.
     */
    public interface DeleteConfirmDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);

        public void onDialogNegativeClick(DialogFragment dialog);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host fragment implements the callback interface
        try {
            // Instantiate the DeleteConfirmDialogListener so we can send events to the host
            mListener = (DeleteConfirmDialogListener)getTargetFragment();
        } catch (ClassCastException e) {
            // The fragment doesn't implement the interface, throw exception
            throw new ClassCastException("Host fragment must implement DeleteConfirmDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_delete)
                .setPositiveButton(R.string.text_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host fragment
                        mListener.onDialogPositiveClick(DeleteConfirmDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host fragment
                        mListener.onDialogNegativeClick(DeleteConfirmDialogFragment.this);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
