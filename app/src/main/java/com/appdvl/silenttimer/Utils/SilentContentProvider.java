package com.appdvl.silenttimer.Utils;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.appdvl.silenttimer.Models.Silencer;

import java.util.ArrayList;
import java.util.List;

public class SilentContentProvider extends ContentProvider
{
    public static final Uri SILENCER_CONTENT_URI = Uri.parse("content://com.yarik.silentprovider/silencers");

    //Silencers table column names
    public static final String SILENCER_ID = "_id";
    public static final String SILENCER_NAME = "_name";
    public static final String SILENCER_START_TIME = "start_time";
    public static final String SILENCER_END_TIME = "end_time";
    public static final String SILENCER_SUNDAY = "sunday";
    public static final String SILENCER_MONDAY = "monday";
    public static final String SILENCER_TUESDAY = "tuesday";
    public static final String SILENCER_WEDNESDAY = "wednesday";
    public static final String SILENCER_THURSDAY = "thursday";
    public static final String SILENCER_FRIDAY = "friday";
    public static final String SILENCER_SATURDAY = "saturday";
    public static final String SILENCER_IS_CHECKED = "is_checked";


    public SilentDatabaseHelper dbHelper;

    @Override
    public boolean onCreate()
    {
        Context context = getContext();

        dbHelper = new SilentDatabaseHelper(context, SilentDatabaseHelper.DATABASE_NAME, null, SilentDatabaseHelper.DATABASE_VERSION);

        return true;
    }

    private static final int _SILENCER = 1;
    private static final int _SILENCER_ID = 2;

    private static final UriMatcher uriMatcher;

    static
    {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("com.yarik.silentprovider", "silencers", _SILENCER);
        uriMatcher.addURI("com.yarik.silentprovider", "silencers/#", _SILENCER_ID);
    }

    @Override
    public String getType(Uri uri)
    {
        switch (uriMatcher.match(uri))
        {
            case _SILENCER: return "vnd.android.cursor.dir/vnd.yarik.silenttime.silencers";
            case _SILENCER_ID: return "vnd.android.cursor.item/vnd.yarik.silenttime.silencers";
            default: throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sort)
    {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        //If no sort order is specified, sort by date/time
        String orderBy = "_id";

        //If this is a row query, limit the result set to the passed in row.
        switch (uriMatcher.match(uri))
        {
            case _SILENCER:
            {
                qb.setTables(SilentDatabaseHelper.SILENCERS_TABLE);

                if (TextUtils.isEmpty(sort))
                {
                    orderBy = SILENCER_ID;
                }
                else
                {
                    orderBy = sort;
                }

                break;
            }
            case _SILENCER_ID:
            {
                qb.setTables(SilentDatabaseHelper.SILENCERS_TABLE);
                qb.appendWhere(SILENCER_ID + "=" + uri.getPathSegments().get(1));
                break;
            }
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Cursor cursor = qb.query(database, projection, selection, selectionArgs, null, null, orderBy);

        //Register the contexts ContentResolver to be notified if the cursor result set changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri _uri, ContentValues _initialValues)
    {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        //Insert the new row. The call to database.insert will return the row number if it is successful
        long rowID = 0;

        switch (uriMatcher.match(_uri))
        {
            case _SILENCER:
            {
                rowID = database.insert(SilentDatabaseHelper.SILENCERS_TABLE, "silencer", _initialValues);
                break;
            }
            default:
                break;
        }

        //Return a URI to the newly inserted row on success
        if (rowID > 0)
        {
            Uri uri = ContentUris.withAppendedId(_uri, rowID);
            getContext().getContentResolver().notifyChange(uri, null);
            return uri;
        }

        throw new SQLException("Failed to insert row into " + _uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs)
    {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        int count;
        switch (uriMatcher.match(uri))
        {
            case _SILENCER:
            {
                count = database.delete(SilentDatabaseHelper.SILENCERS_TABLE, where, whereArgs);
                break;
            }
            case _SILENCER_ID:
            {
                String segment = uri.getPathSegments().get(1);
                count = database.delete(SilentDatabaseHelper.SILENCERS_TABLE,
                        SILENCER_ID + "="
                                + segment
                                + (!TextUtils.isEmpty(where) ? " AND ("
                                + where + ')' : ""), whereArgs);
                break;
            }
            default: throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values ,String where, String[] whereArgs)
    {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        int count;
        switch (uriMatcher.match(uri))
        {
            case _SILENCER:
            {
                count = database.update(SilentDatabaseHelper.SILENCERS_TABLE, values, where, whereArgs);
                break;
            }
            case _SILENCER_ID:
            {
                String segment = uri.getPathSegments().get(1);
                count = database.update(SilentDatabaseHelper.SILENCERS_TABLE, values,
                        SILENCER_ID + "="
                                + segment
                                + (!TextUtils.isEmpty(where) ? " AND ("
                                + where + ')' : ""), whereArgs);
                break;
            }
            default: throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }


    public static class SilentDatabaseHelper extends SQLiteOpenHelper
    {
        //The underlying database
        private SQLiteDatabase silentDB;

        public static final String TAG = "SilentProvider";

        public static final String DATABASE_NAME = "silencers.db";
        public static final int DATABASE_VERSION = 1;
        public static final String SILENCERS_TABLE = "silencers";
        public static final String GEOSILENCERS_TABLE = "geosilencers";

        public static final String SILENCERS_DATABASE_CREATE =
                "create table " + SILENCERS_TABLE + " ("
                        + SILENCER_ID + " integer primary key autoincrement, "
                        + SILENCER_NAME + " text, "
                        + SILENCER_START_TIME + " text, "
                        + SILENCER_END_TIME + " text, "
                        + SILENCER_SUNDAY + " integer, "
                        + SILENCER_MONDAY + " integer, "
                        + SILENCER_TUESDAY + " integer, "
                        + SILENCER_WEDNESDAY + " integer, "
                        + SILENCER_THURSDAY + " integer, "
                        + SILENCER_FRIDAY + " integer, "
                        + SILENCER_SATURDAY + " integer, "
                        + SILENCER_IS_CHECKED + " boolean); ";


        public SilentDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
        {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(SILENCERS_DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + SILENCERS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + GEOSILENCERS_TABLE);
            onCreate(db);
        }

        public List<Silencer> getSilencers()
        {
            List<Silencer> list = new ArrayList<>();
            silentDB = getWritableDatabase();
            Cursor cursor = silentDB.rawQuery("SELECT * FROM silencers" , null);
            while (cursor.moveToNext())
            {
                int indexName = cursor.getColumnIndex(SILENCER_NAME);
                int indexStartTime = cursor.getColumnIndex(SILENCER_START_TIME);
                int indexEndTime = cursor.getColumnIndex(SILENCER_END_TIME);
                int indexSunday = cursor.getColumnIndex(SILENCER_SUNDAY);
                int indexMonday = cursor.getColumnIndex(SILENCER_MONDAY);
                int indexTuesday = cursor.getColumnIndex(SILENCER_TUESDAY);
                int indexWednesday = cursor.getColumnIndex(SILENCER_WEDNESDAY);
                int indexThursday = cursor.getColumnIndex(SILENCER_THURSDAY);
                int indexFriday = cursor.getColumnIndex(SILENCER_FRIDAY);
                int indexSaturday = cursor.getColumnIndex(SILENCER_SATURDAY);
                int indexIsChecked = cursor.getColumnIndex(SILENCER_IS_CHECKED);

                String name = cursor.getString(indexName);
                String startTime = cursor.getString(indexStartTime);
                String endTime = cursor.getString(indexEndTime);
                boolean sunday = toBool(cursor.getInt(indexSunday));
                boolean monday = toBool(cursor.getInt(indexMonday));
                boolean tuesday = toBool(cursor.getInt(indexTuesday));
                boolean wednesday = toBool(cursor.getInt(indexWednesday));
                boolean thursday = toBool(cursor.getInt(indexThursday));
                boolean friday = toBool(cursor.getInt(indexFriday));
                boolean saturday = toBool(cursor.getInt(indexSaturday));
                boolean isChecked = toBool(cursor.getInt(indexIsChecked));

                Silencer silencer = new Silencer(name, startTime, endTime, sunday, monday,
                        tuesday, wednesday, thursday, friday, saturday, isChecked);

                list.add(silencer);
            }

            cursor.close();
            return list;
        }

        private boolean toBool(int i)
        {
            return !(i % 2 == 0);
        }
    }
}