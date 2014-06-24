package ua.com.florin.simpletasks.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import ua.com.florin.simpletasks.R;
import ua.com.florin.simpletasks.activity.MainActivity;
import ua.com.florin.simpletasks.adapter.TasksCursorAdapter;
import ua.com.florin.simpletasks.db.DBNames;
import ua.com.florin.simpletasks.db.TasksProvider;
import ua.com.florin.simpletasks.fragment.dialog.DeleteConfirmDialogFragment;
import ua.com.florin.simpletasks.util.MyConstants;


public class TasksFragment extends Fragment implements DBNames, MyConstants,
        DeleteConfirmDialogFragment.DeleteConfirmDialogListener {

    /**
     * Logging tag constant
     */
    private static final String TAG = "TasksFragment";

    /**
     * A label to identify if user confirmed the task deletion if AlertDialog
     */
    private boolean deleteConfirmed;

    /**
     * A callback reference to communicate with activity
     */
    private OnTaskSelectedListener mCallback;

    /**
     * Reference to ListView that contains task items
     */
    private AbsListView mListView;

    /**
     * Reference to TasksCursorAdapter
     */
    private BaseAdapter mTasksAdapter;

    /**
     * List of selected tasks to perform actions with in Contextual Action Bar
     */
    private List<Long> mSelectedTaskIds;

    /**
     * Default constructor
     */
    public TasksFragment() {
    }

    /**
     * The container Activity must implement this interface so that the frag can deliver messages
     */
    public interface OnTaskSelectedListener {

        /**
         * Realizes logic that executes when task item is selected
         *
         * @param ID entry ID in {@link ua.com.florin.simpletasks.db.TasksDB}
         */
        public void onTaskSelected(long ID);
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
        Log.d(TAG, "onAttach");
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnTaskSelectedListener) activity;
            ((MainActivity) activity).onFragmentAttached(getArguments().getInt(ARG_DRAWER_POSITION));
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTaskSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        getActivity().getLoaderManager().initLoader(1, null, cursorLoaderCallbacks);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        Cursor mTasksCursor = getActivity().getContentResolver()
                .query(TasksProvider.CONTENT_URI, null, null, null, null);
        mTasksAdapter = new TasksCursorAdapter(getActivity(), mTasksCursor);

        mListView = (AbsListView) view.findViewById(R.id.tasksListView);
        mListView.setAdapter(mTasksAdapter);
        mListView.setOnItemClickListener(onItemClickListener);
        mListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setMultiChoiceModeListener(multiChoiceModeListener);
        return view;
    }

    private AbsListView.OnItemClickListener onItemClickListener =
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mCallback.onTaskSelected(getIdByPosition(position));
                }
            };

    private AbsListView.MultiChoiceModeListener multiChoiceModeListener =
            new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                    int selectCount = mListView.getCheckedItemCount();
                    switch (selectCount) {
                        case 1:
                            mode.setTitle(R.string.single_selection);
                            break;
                        default:
                            StringBuilder sb = new StringBuilder();
                            sb.append(selectCount)
                                    .append(" ")
                                    .append(getResources().getString(R.string.multiple_selection));
                            mode.setTitle(sb.toString());
                            break;
                    }
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.contextual_list_view, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_delete:
                            SparseBooleanArray mCheckedItemPositions
                                    = mListView.getCheckedItemPositions();
                            setSelectedTaskIds(mCheckedItemPositions);
                            showDeleteConfirmDialog();
                            mode.finish();
                            return true;
                    }
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            };

    private LoaderManager.LoaderCallbacks<Cursor> cursorLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    Log.d(TAG, "onCreateLoader");
                    return new CursorLoader(getActivity(), TasksProvider.CONTENT_URI, null, null, null, null);
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                    Log.d(TAG, "onLoadFinished");
                    if (mTasksAdapter != null)
                        ((TasksCursorAdapter) mTasksAdapter).swapCursor(data);
                }

                @Override
                public void onLoaderReset(Loader<Cursor> loader) {
                    Log.d(TAG, "onLoaderReset");
                    if (mTasksAdapter != null)
                        ((TasksCursorAdapter) mTasksAdapter).swapCursor(null);
                }
            };

    /**
     * Returns an entry ID in {@link ua.com.florin.simpletasks.db.TasksDB}
     * from item position in {@link TasksFragment#mListView}.
     *
     * @param position index number of item in list view.
     * @return Entry ID in {@link ua.com.florin.simpletasks.db.TasksDB}
     */
    private long getIdByPosition(int position) {
        Cursor mTasksCursor = ((TasksCursorAdapter) mListView.getAdapter()).getCursor();
        mTasksCursor.moveToPosition(position);
        long mId = mTasksCursor.getLong(0);
        return mId;
    }

    /**
     * Initializes {@link TasksFragment#mSelectedTaskIds}
     *
     * @param checkedItemPositions SparseBooleanArray of selected tasks positions
     */
    private void setSelectedTaskIds(SparseBooleanArray checkedItemPositions) {
        mSelectedTaskIds = new ArrayList<Long>();
        long selectedTaskId;
        for (int i = 0; i < checkedItemPositions.size(); i++) {
            if (checkedItemPositions.valueAt(i)) {
                selectedTaskId = getIdByPosition(checkedItemPositions.keyAt(i));
                mSelectedTaskIds.add(selectedTaskId);
            }
        }
    }

    /**
     * Creates new Dialog for confirmation to delete selected tasks and shows it.
     */
    private void showDeleteConfirmDialog() {
        DialogFragment dialogFragment = new DeleteConfirmDialogFragment();
        dialogFragment.setTargetFragment(this, 0);
        dialogFragment.show(getFragmentManager(), "DeleteConfirmDialog");
    }

    /**
     * Deletes tasks with IDs defined in {@link TasksFragment#mSelectedTaskIds}
     */
    private void deleteSelectedTasks() {
        for (int i = 0; i < mSelectedTaskIds.size(); i++) {
            long id = mSelectedTaskIds.get(i);
            Uri mItemUri = ContentUris.withAppendedId(TasksProvider.CONTENT_URI, id);
            getActivity().getContentResolver().delete(mItemUri, null, null);
        }
        mSelectedTaskIds = null;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        deleteSelectedTasks();
        dialog.dismiss();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        deleteConfirmed = false;
        dialog.dismiss();
    }
}
