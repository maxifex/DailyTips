
package com.fredua.android.dailies;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fredua.android.dailies.data.Contract;

/**
 * Encapsulates fetching  Daily tips and displaying it as a page in fragment pager adapter
 */
public class DailyFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String LOG_TAG = DailyFragment.class.getSimpleName();
    int page_postion = 0;
    private static final String SELECTED_KEY = "selected_position";

    private static final int DAILY_LOADER = 0;


    // Specify the columns needed.
    private static final String[] TIP_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name
            Contract.TipEntry.TABLE_NAME + "." + Contract.TipEntry._ID,
            Contract.TipEntry.COLUMN_DATE,
            Contract.TipEntry.COLUMN_AUDIO_URL,
            Contract.TipEntry.COLUMN_INDEX,
            Contract.TipEntry.COLUMN_INDEX_LABEL,
            Contract.TipEntry.COLUMN_MESSAGE,
            Contract.TipEntry.COLUMN_TITLE,
            Contract.TipEntry.COLUMN_URL,
    };

    // These indices are tied to TIP_COLUMNS.  If TIP_COLUMNS changes, these must change.
    static final int COL_TIP_ID = 0;
    static final int COL_TIP_DATE = 1;
    static final int COL_TIP_AUDIO_URL = 2;
    static final int COL_TIP_INDEX = 3;
    static final int COL_TIP_INDEX_LABEL = 4;
    static final int COL_TIP_MESSAGE = 5;
    static final int COL_TIP_TITLE = 6;
    static final int COL_COORD_URL = 7;


    private TextView mTitleView;
    private TextView mMessageView;
    private TextView mLabelView;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */

    public interface Callback {
        /**
         * DailyFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }

    public DailyFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set position of fragment page
        page_postion = getArguments() != null ? getArguments().getInt("page_position") : 9;

        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.dailyfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //if (id == R.id.update) {
        //    do something
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //sette elements of the fragment pager
        View rootView = inflater.inflate(R.layout.fragment_pager, container, false);
        Log.d(LOG_TAG, "Setting page at position " + page_postion);

        mTitleView = (TextView) rootView.findViewById(R.id.title_textview);
        mMessageView = (TextView) rootView.findViewById(R.id.tip_textview);
        mLabelView = (TextView) rootView.findViewById(R.id.label_textview);

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DAILY_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
          super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so no need to check the ID

        // Sort order:  Ascending, by index no.
        String sortOrder = Contract.TipEntry.COLUMN_INDEX + " ASC";

        Log.d(LOG_TAG, "Getting tip at index: " + page_postion);
        Uri tipWithIndexUri = Contract.TipEntry.buildTipWithIndex(page_postion);

        return new CursorLoader(getActivity(),
                tipWithIndexUri,
                TIP_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null && data.moveToFirst()) {


            // Read title from cursor and update view
            String label = data.getString(COL_TIP_INDEX_LABEL);
            mLabelView.setText(label);
            String title = data.getString(COL_TIP_TITLE);
            mTitleView.setText(title);
            String message = data.getString(COL_TIP_MESSAGE);
            //mMessageView.setText(message);
            mMessageView.setText(Html.fromHtml(message));
            //Log.d(LOG_TAG, "#####################" + message);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}