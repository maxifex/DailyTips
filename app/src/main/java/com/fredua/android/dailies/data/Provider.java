/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fredua.android.dailies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class Provider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DbHelper mOpenHelper;

    static final int TIP = 400;
    static final int TIP_WITH_DATE = 401;
    static final int TIP_WITH_INDEX = 402;
    static final int TIP_COUNT = 403;

    private static final SQLiteQueryBuilder sTipQueryBuilder;

    static {
        sTipQueryBuilder = new SQLiteQueryBuilder();
        sTipQueryBuilder.setTables(Contract.TipEntry.TABLE_NAME);
    }

    //tip date >= ?
    private static final String sTipWithDateSelection =
            Contract.TipEntry.TABLE_NAME+
                    "." + Contract.TipEntry.COLUMN_DATE + " >= ? ";

    private static final String sTipWithIndexSelection =
            Contract.TipEntry.TABLE_NAME+
                    "." + Contract.TipEntry.COLUMN_INDEX + " = ? ";

    private Cursor getTipByDate(
            Uri uri, String[] projection, String sortOrder) {
        long date = Contract.TipEntry.getDateFromUri(uri);

        return sTipQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sTipWithDateSelection,
                new String[]{Long.toString(date)},
                null,
                null,
                sortOrder
        );
    }

    public Cursor getTipTotalCount(
            Uri uri, String[] projection, String sortOrder) {
        Integer index = (int) Contract.TipEntry.getIndexFromUri(uri);

        return  sTipQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                new String[] {"count(*) AS count"},
                null,
                null,
                null,
                null,
                null
        );

    }

    private Cursor getTipByIndex(
            Uri uri, String[] projection, String sortOrder) {
        Integer index = (int) Contract.TipEntry.getIndexFromUri(uri);

        return sTipQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sTipWithIndexSelection,
                new String[]{Integer.toString(index)},
                null,
                null,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = Contract.CONTENT_AUTHORITY;

        matcher.addURI(authority, Contract.PATH_TIP, TIP);
//        matcher.addURI(authority, Contract.PATH_TIP + "/*", TIP_WITH_DATE);
        matcher.addURI(authority, Contract.PATH_TIP + "/*", TIP_WITH_INDEX);
        return matcher;
    }

    /*
        Students: We've coded this for you.  We just create a new DbHelper for later use
        here.
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new DbHelper(getContext());
        return true;
    }

    /*
        Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
        test this by uncommenting testGetType in TestProvider.

     */
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case TIP:
                return Contract.TipEntry.CONTENT_TYPE;
            case TIP_WITH_DATE:
                return Contract.TipEntry.CONTENT_ITEM_TYPE;
            case TIP_WITH_INDEX:
                return Contract.TipEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            // "tip"
            case TIP_WITH_DATE: {
                retCursor = getTipByDate(uri, projection, sortOrder);
                break;
            }
            case TIP_WITH_INDEX: {
                retCursor = getTipByIndex(uri, projection, sortOrder);
                break;
            }
            case TIP_COUNT: {
                retCursor = getTipTotalCount(uri, projection, sortOrder);
                break;
            }
            case TIP: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Contract.TipEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*
        Student: Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {

            case TIP: {
                normalizeTipDate(values);
                long _id = db.insert(Contract.TipEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = Contract.TipEntry.buildTipUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {

            case TIP:
                rowsDeleted = db.delete(
                        Contract.TipEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }



    private void normalizeTipDate(ContentValues values) {
        // normalize the date value
        /*if (values.containsKey(Contract.TipEntry.COLUMN_DATE)) {
            long dateValue = values.getAsLong(Contract.TipEntry.COLUMN_DATE);
            values.put(Contract.TipEntry.COLUMN_DATE, Contract.normalizeDate(dateValue));
        }*/
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {

            case TIP:
                normalizeTipDate(values);
                rowsUpdated = db.update(Contract.TipEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {

            case TIP:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        normalizeTipDate(value);
                        long _id = db.insert(Contract.TipEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}