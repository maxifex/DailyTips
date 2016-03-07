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

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Defines table and column names for the database.
 */
public class Contract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.fredua.android.dailies";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.fredua.android.dailies.app/tip/ is a valid path for
    // looking at tip data. content://com.fredua.android.dailies.app/dummyroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "dummyroot".
    public static final String PATH_TIP = "tip";

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    /* Inner class that defines the table contents of the tips table */
    public static final class TipEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TIP).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TIP;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TIP;

        public static final String TABLE_NAME = "tip";

        // Tip id as returned by API,
        public static final String COLUMN_TIP_ID = "tip_id";

        // Tip numerical index as returned by API,
        public static final String COLUMN_INDEX = "index_no";

        // User visible tip url index as returned by API,
        public static final String COLUMN_INDEX_LABEL = "index_label";

        // title of tip, as provided by API.
        // e.g "food and health"
        public static final String COLUMN_TITLE = "title";

        // Tip message, as provided by API.
        // e.g "in order to keep and healthy skin you must bla bla".
        public static final String COLUMN_MESSAGE = "message";

        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_DATE = "date";

        // URL for further information
        public static final String COLUMN_URL = "url";

        // URL for further information
        public static final String COLUMN_AUDIO_URL = "audio_url";


        public static Uri buildTipUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildTipWithDate(long date) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(normalizeDate(date))).build();
        }

        public static Uri buildTipWithIndex(Integer index) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(index)).build();
        }

        public static long getDateFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static long getIndexFromUri(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(1));
        }

    }

}
