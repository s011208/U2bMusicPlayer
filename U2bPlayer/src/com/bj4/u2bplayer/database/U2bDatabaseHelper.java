
package com.bj4.u2bplayer.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class U2bDatabaseHelper extends SQLiteOpenHelper {

    private static final boolean DEBUG = true;

    private static final String TAG = "QQQQ";

    private static final String DATABASE_NAME = "u2b_data.db";

    private static final int VERSION = 1;

    public static final String TABLE_MAIN_INFO = "main_info";

    public static final String COLUMN_ARTIST = "artist";

    public static final String COLUMN_ALBUM = "album";

    public static final String COLUMN_MUSIC = "music";

    public static final String COLUMN_VIDEO_PATH = "video_path";

    public static final String COLUMN_RTSP_H = "rtsp_h";

    public static final String COLUMN_RTSP_L = "rtsp_l";

    public static final String COLUMN_RANK = "rank";

    private Context mContext;

    private SQLiteDatabase mDb;

    public U2bDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        mContext = context;
        getDb().enableWriteAheadLogging();
        getDb().execSQL("PRAGMA synchronous=1");
        createTables();
    }

    private SQLiteDatabase getDb() {
        if (mDb == null) {
            mDb = this.getWritableDatabase();
        } else if (mDb.isOpen() == false) {
            mDb = this.getWritableDatabase();
        }
        return mDb;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    public void clearTableContent() {
        getDb().delete(TABLE_MAIN_INFO, null, null);
    }

    public void insert(String rawCmd) {
        getDb().execSQL(rawCmd);
    }

    public Cursor query(String rawCmd) {
        return getDb().rawQuery(rawCmd, null);
    }

    public Cursor query(String[] columns, String selection) {
        return getDb().query(TABLE_MAIN_INFO, columns, selection, null, null, null, null);
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs,
            String groupBy, String having, String orderBy) {
        return getDb().query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public long insert(ContentValues cv) {
        return getDb().insert(TABLE_MAIN_INFO, null, cv);
    }

    public int insert(ArrayList<ContentValues> cvs) {
        int records = 0;
        try {
            getDb().beginTransaction();
            for (ContentValues cv : cvs) {
                getDb().insert(TABLE_MAIN_INFO, null, cv);
                ++records;
            }
            getDb().setTransactionSuccessful();
        } catch (SQLException e) {
        } finally {
            getDb().endTransaction();
        }
        return records;
    }

    private void createTables() {
        getDb().execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE_MAIN_INFO + "(" + COLUMN_ARTIST
                        + " TEXT NOT NULL," + COLUMN_ALBUM + " TEXT NOT NULL," + COLUMN_MUSIC
                        + " TEXT NOT NULL," + COLUMN_VIDEO_PATH + " TEXT," + COLUMN_RTSP_H
                        + " TEXT," + COLUMN_RTSP_L + " TEXT," + COLUMN_RANK
                        + " INTEGER,PRIMARY KEY(" + COLUMN_ARTIST + ", " + COLUMN_ALBUM + ", "
                        + COLUMN_MUSIC + "))");
        if (DEBUG) {
            Log.d(TAG, "table created!");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
