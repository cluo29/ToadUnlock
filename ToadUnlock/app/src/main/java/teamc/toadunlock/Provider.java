package teamc.toadunlock;

/**
 * Created by CLU on 10/22/2015.
 */
import java.util.HashMap;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Environment;
import android.provider.BaseColumns;
import android.util.Log;

import com.aware.Aware;
import com.aware.utils.DatabaseHelper;

public class Provider extends ContentProvider {
    public static final int DATABASE_VERSION = 6;
    public static String AUTHORITY = "teamc.ToadUnlock.provider.ToadUnlock";
    private static final int TOADUNLOCK = 1;
    private static final int TOADUNLOCK_ID = 2;
    private static final int TOADUNLOCK2 = 3;
    private static final int TOADUNLOCK2_ID = 4;
    public static final String DATABASE_NAME = Environment.getExternalStorageDirectory()+"/Download/ToadUnlock.db";

    public static final String[] DATABASE_TABLES = {
            "ToadUnlock", "ToadUnlock2"
    };
    public static final class Toad_Unlock_Data2 implements BaseColumns {
        private Toad_Unlock_Data2(){};

        public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/ToadUnlock2");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.ToadUnlock2";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.ToadUnlock2";

        public static final String _ID = "_id";
        public static final String TIMESTAMP = "timestamp";
        public static final String DEVICE_ID = "device_id";

        //wrong
        public static final String WRONG = "double_wrong";
        //right
        public static final String RIGHT = "double_right";

    }
    public static final class Toad_Unlock_Data implements BaseColumns {
        private Toad_Unlock_Data(){};

        public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/ToadUnlock");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.ToadUnlock";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.ToadUnlock";

        public static final String _ID = "_id";
        public static final String TIMESTAMP = "timestamp";
        public static final String DEVICE_ID = "device_id";

        //accelerometer
        public static final String A_VALUES_0 = "double_A_values_0";   //x axis of accelerometer: m/s^2
        public static final String A_VALUES_1 = "double_A_values_1";    //y
        public static final String A_VALUES_2 = "double_A_values_2";    //z
        //Barometer
        //public static final String B_VALUES_0 = "double_B_values_0";   //Barometer
        //Battery level
        public static final String BL_VALUES_0 = "double_BL_values_0";   //Battery level
        //battery_status
        public static final String BS_VALUES_0 = "double_BS_values_0";   //Battery status

        //Light

        public static final String LUX = "double_Lux";    //Light sensor
        //GPS
        public static final String LATITUDE = "double_Latitude";    //the location’s latitude, in degrees
        public static final String LONGITUDE = "double_Longitude";    //the location’s longitude, in degrees
        public static final String BEARING = "double_Bearing";    //the location’s bearing, in degrees
        public static final String SPEED = "double_Speed";    //speed
        public static final String ALTITUDE = "double_Altitude";    //altitude

        //time
        public static final String HOUR = "double_Hour";    //hour in day

        public static final String DAY = "double_Day";    //day in week

        //this means package name of app
        public static final String LABEL = "Label";
    }
    public static final String[] TABLES_FIELDS = {
            Toad_Unlock_Data._ID + " integer primary key autoincrement," +
                    Toad_Unlock_Data.TIMESTAMP + " real default 0," +
                    Toad_Unlock_Data.DEVICE_ID + " text default ''," +
                    Toad_Unlock_Data.A_VALUES_0 + " real default 0," +
                    Toad_Unlock_Data.A_VALUES_1 + " real default 0," +
                    Toad_Unlock_Data.A_VALUES_2 + " real default 0," +
                    //Toad_Unlock_Data.B_VALUES_0 + " real default 0," +
                    Toad_Unlock_Data.BL_VALUES_0 + " real default 0," +
                    Toad_Unlock_Data.BS_VALUES_0 + " real default 0," +
                    Toad_Unlock_Data.LUX + " real default 0," +
                    Toad_Unlock_Data.LATITUDE + " real default 0," +
                    Toad_Unlock_Data.LONGITUDE + " real default 0," +
                    Toad_Unlock_Data.BEARING + " real default 0," +
                    Toad_Unlock_Data.SPEED + " real default 0," +
                    Toad_Unlock_Data.ALTITUDE + " real default 0," +
                    Toad_Unlock_Data.HOUR + " real default 0," +
                    Toad_Unlock_Data.DAY + " real default 0," +
                    Toad_Unlock_Data.LABEL + " text default ''," +
                    "UNIQUE("+ Toad_Unlock_Data.TIMESTAMP+","+ Toad_Unlock_Data.DEVICE_ID+")",

            Toad_Unlock_Data2._ID + " integer primary key autoincrement," +
                    Toad_Unlock_Data2.TIMESTAMP + " real default 0," +
                    Toad_Unlock_Data2.DEVICE_ID + " text default ''," +
                    Toad_Unlock_Data2.WRONG + " real default 0," +
                    Toad_Unlock_Data2.RIGHT + " real default 0," +
                    "UNIQUE("+ Toad_Unlock_Data2.TIMESTAMP+","+ Toad_Unlock_Data2.DEVICE_ID+")"

    };
    private static UriMatcher URIMatcher;
    private static HashMap<String, String> databaseMap;
    private static HashMap<String, String> databaseMap2;
    private static DatabaseHelper databaseHelper;
    private static SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        Log.d("DATABASE NAME", "DEFAULT AUTHORITY: " + AUTHORITY);
        AUTHORITY = getContext().getPackageName() + ".provider.ToadUnlock";
        Log.d("DATABASE", "Authority set up after onCreate " + AUTHORITY);
        URIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        URIMatcher.addURI(AUTHORITY, DATABASE_TABLES[0], TOADUNLOCK);
        URIMatcher.addURI(AUTHORITY, DATABASE_TABLES[0] + "/#", TOADUNLOCK_ID);
        URIMatcher.addURI(AUTHORITY, DATABASE_TABLES[1], TOADUNLOCK2);
        URIMatcher.addURI(AUTHORITY, DATABASE_TABLES[1] + "/#", TOADUNLOCK2_ID);
        databaseMap = new HashMap<String, String>();
        databaseMap.put(Toad_Unlock_Data._ID, Toad_Unlock_Data._ID);
        databaseMap.put(Toad_Unlock_Data.TIMESTAMP, Toad_Unlock_Data.TIMESTAMP);
        databaseMap.put(Toad_Unlock_Data.DEVICE_ID, Toad_Unlock_Data.DEVICE_ID);
        databaseMap.put(Toad_Unlock_Data.A_VALUES_0, Toad_Unlock_Data.A_VALUES_0);
        databaseMap.put(Toad_Unlock_Data.A_VALUES_1, Toad_Unlock_Data.A_VALUES_1);
        databaseMap.put(Toad_Unlock_Data.A_VALUES_2, Toad_Unlock_Data.A_VALUES_2);
        //databaseMap.put(Toad_Unlock_Data.B_VALUES_0, Toad_Unlock_Data.B_VALUES_0);
        databaseMap.put(Toad_Unlock_Data.BL_VALUES_0, Toad_Unlock_Data.BL_VALUES_0);
        databaseMap.put(Toad_Unlock_Data.BS_VALUES_0, Toad_Unlock_Data.BS_VALUES_0);

        databaseMap.put(Toad_Unlock_Data.LUX, Toad_Unlock_Data.LUX);
        databaseMap.put(Toad_Unlock_Data.LATITUDE, Toad_Unlock_Data.LATITUDE);
        databaseMap.put(Toad_Unlock_Data.LONGITUDE, Toad_Unlock_Data.LONGITUDE);
        databaseMap.put(Toad_Unlock_Data.BEARING, Toad_Unlock_Data.BEARING);
        databaseMap.put(Toad_Unlock_Data.SPEED, Toad_Unlock_Data.SPEED);
        databaseMap.put(Toad_Unlock_Data.ALTITUDE, Toad_Unlock_Data.ALTITUDE);
        databaseMap.put(Toad_Unlock_Data.HOUR, Toad_Unlock_Data.HOUR);
        databaseMap.put(Toad_Unlock_Data.DAY, Toad_Unlock_Data.DAY);



        databaseMap.put(Toad_Unlock_Data.LABEL, Toad_Unlock_Data.LABEL);


        databaseMap2= new HashMap<String, String>();
        databaseMap2.put(Toad_Unlock_Data2._ID, Toad_Unlock_Data2._ID);
        databaseMap2.put(Toad_Unlock_Data2.TIMESTAMP, Toad_Unlock_Data2.TIMESTAMP);
        databaseMap2.put(Toad_Unlock_Data2.DEVICE_ID, Toad_Unlock_Data2.DEVICE_ID);
        databaseMap2.put(Toad_Unlock_Data2.WRONG, Toad_Unlock_Data2.WRONG);
        databaseMap2.put(Toad_Unlock_Data2.RIGHT, Toad_Unlock_Data2.RIGHT);
        //


        return true;
    }

    private boolean initializeDB() {

        if (databaseHelper == null) {
            Log.d("DATABASE","DatabaseHelper is null");
            databaseHelper = new DatabaseHelper( getContext(), DATABASE_NAME, null, DATABASE_VERSION, DATABASE_TABLES, TABLES_FIELDS );


        }
        if( databaseHelper != null && ( database == null || ! database.isOpen() )) {
            database = databaseHelper.getWritableDatabase();

        }
        return( database != null && databaseHelper != null);
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if( ! initializeDB() ) {
            Log.w(AUTHORITY,"Database unavailable...");
            return 0;
        }

        int count = 0;
        switch (URIMatcher.match(uri)) {
            case TOADUNLOCK:
                count = database.delete(DATABASE_TABLES[0], selection, selectionArgs);
                break;
            case TOADUNLOCK2:
                count = database.delete(DATABASE_TABLES[1], selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (URIMatcher.match(uri)) {
            case TOADUNLOCK:
                return Toad_Unlock_Data.CONTENT_TYPE;
            case TOADUNLOCK_ID:
                return Toad_Unlock_Data.CONTENT_ITEM_TYPE;
            case TOADUNLOCK2:
                return Toad_Unlock_Data2.CONTENT_TYPE;
            case TOADUNLOCK2_ID:
                return Toad_Unlock_Data2.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }
    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        if (!initializeDB()) {
            Log.w(AUTHORITY, "Database unavailable...");
            return null;
        }

        ContentValues values = (initialValues != null) ? new ContentValues(
                initialValues) : new ContentValues();

        switch (URIMatcher.match(uri)) {
            case TOADUNLOCK:
                Log.d("DATABASE",database.getPath());
                long weather_id = database.insert(DATABASE_TABLES[0], Toad_Unlock_Data.DEVICE_ID, values);

                if (weather_id > 0) {
                    Uri new_uri = ContentUris.withAppendedId(
                            Toad_Unlock_Data.CONTENT_URI,
                            weather_id);
                    getContext().getContentResolver().notifyChange(new_uri,
                            null);
                    return new_uri;
                }
                throw new SQLException("Failed to insert row into " + uri);
            case TOADUNLOCK2:
                Log.d("DATABASE",database.getPath());
                long weather_id2 = database.insert(DATABASE_TABLES[1], Toad_Unlock_Data2.DEVICE_ID, values);

                if (weather_id2 > 0) {
                    Uri new_uri = ContentUris.withAppendedId(
                            Toad_Unlock_Data2.CONTENT_URI,
                            weather_id2);
                    getContext().getContentResolver().notifyChange(new_uri,
                            null);
                    return new_uri;
                }
                throw new SQLException("Failed to insert row into " + uri);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        if( ! initializeDB() ) {
            Log.w(AUTHORITY,"Database unavailable...");
            return null;
        }

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (URIMatcher.match(uri)) {
            case TOADUNLOCK:
                qb.setTables(DATABASE_TABLES[0]);
                qb.setProjectionMap(databaseMap);
                break;
            case TOADUNLOCK2:
                qb.setTables(DATABASE_TABLES[1]);
                qb.setProjectionMap(databaseMap2);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        try {
            Cursor c = qb.query(database, projection, selection, selectionArgs,
                    null, null, sortOrder);
            c.setNotificationUri(getContext().getContentResolver(), uri);
            return c;
        } catch (IllegalStateException e) {
            if (Aware.DEBUG)
                Log.e(Aware.TAG, e.getMessage());
            return null;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        if( ! initializeDB() ) {
            Log.w(AUTHORITY,"Database unavailable...");
            return 0;
        }

        int count = 0;
        switch (URIMatcher.match(uri)) {
            case TOADUNLOCK:
                count = database.update(DATABASE_TABLES[0], values, selection,
                        selectionArgs);
                break;
            case TOADUNLOCK2:
                count = database.update(DATABASE_TABLES[1], values, selection,
                        selectionArgs);
                break;
            default:

                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}