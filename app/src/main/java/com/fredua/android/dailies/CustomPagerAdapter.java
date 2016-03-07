package com.fredua.android.dailies;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.fredua.android.dailies.data.Contract;


public  class CustomPagerAdapter extends FragmentStatePagerAdapter {

    protected Context mContext;
    static int NUM_ITEMS = 0;
    public static final String LOG_TAG = CustomPagerAdapter.class.getSimpleName();
    private SparseArray<DailyFragment> mPageReferenceMap
            = new SparseArray<DailyFragment>();

    public CustomPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    // This method returns the fragment associated with
    // the specified position.
    //
    // It is called when the Adapter needs a fragment
    // and it does not exists.
    public Fragment getItem(int position) {

        // Create fragment object
        Fragment fragment = new DailyFragment();


        // Attach some data to it that we'll
        // use to populate our fragment layouts
        Bundle args = new Bundle();
        args.putInt("page_position", position + 1);

        // Set the arguments on the fragment
        fragment.setArguments(args);


        mPageReferenceMap.put(position, (DailyFragment) fragment);

        return fragment;
    }

    @Override
    public int getCount() {



        if(NUM_ITEMS == 0){
            int count = countTipCount();
            NUM_ITEMS = count;
            Log.d(LOG_TAG, "Total Number of tips:" + count);
        }

        return NUM_ITEMS;
    }



    public int countTipCount() {


        Cursor countCursor = mContext.getContentResolver().query(Contract.TipEntry.CONTENT_URI,
                new String[]{"count(*) AS count"},
                null,
                null,
                null);

        countCursor.moveToFirst();
        int count = countCursor.getInt(0);

        return count;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //Remove the reference when destroy it
        mPageReferenceMap.remove(position);

        super.destroyItem(container, position, object);
    }

    public DailyFragment getFragment(int key) {

        return mPageReferenceMap.get(key);
    }

    public void destroyItem(View container, int position, Object object) {
        super.destroyItem(container, position, object);
        mPageReferenceMap.remove(position);
    }

    public Object instantiateItem(ViewGroup viewGroup, int position) {
        Object obj = super.instantiateItem(viewGroup, position);

        //Add the reference when fragment has been create or restore
        if (obj instanceof DailyFragment) {
            DailyFragment f= (DailyFragment)obj;
            mPageReferenceMap.put(position, f);
        }

        return obj;
    }



}
