
package com.bj4.u2bplayer.database;

import java.util.ArrayList;

import com.bj4.u2bplayer.PlayMusicApplication;
import com.bj4.u2bplayer.utilities.PlayListInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.MediaStore;
import android.util.Log;

public class U2bDatabaseHelper extends SQLiteOpenHelper {

    private static final boolean DEBUG = true && PlayMusicApplication.OVERALL_DEBUG;

    private static final String TAG = "U2bDatabaseHelper";

    private static final String DATABASE_NAME = "u2b_data.db";

    private static final int VERSION = 1;

    public static final String TABLE_MAIN_INFO = "main_info";

    public static final String COLUMN_ARTIST = "artist";

    public static final String COLUMN_ALBUM = "album";

    public static final String COLUMN_MUSIC = "music";

    public static final String COLUMN_VIDEO_PATH = "video_path";

    public static final String COLUMN_RTSP_H = "rtsp_h";

    public static final String COLUMN_RTSP_L = "rtsp_l";

    public static final String COLUMN_VIDEO_ID = "video_id";

    public static final String COLUMN_RANK = "rank";

    public static final String TABLE_ALBUM_INFO = "album_info";

    public static final int ALBUM_NOT_EXISTED = -1;

    private Context mContext;

    private SQLiteDatabase mDb;

    public interface DatabaseHelperCallback {

        public void notifyDataSetChanged();
    }

    private ArrayList<DatabaseHelperCallback> mCallbacks = new ArrayList<DatabaseHelperCallback>();

    public void addCallback(DatabaseHelperCallback cb) {
        mCallbacks.add(cb);
    }

    public void removeCallback(DatabaseHelperCallback cb) {
        mCallbacks.remove(cb);
    }

    private void notifyDataSetChanged() {
        for (DatabaseHelperCallback cb : mCallbacks) {
            cb.notifyDataSetChanged();
        }
    }

    public U2bDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        mContext = context;
        getDb().enableWriteAheadLogging();
        getDb().execSQL("PRAGMA synchronous=0");
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
        notifyDataSetChanged();
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

    public Cursor queryDataForU2bParser() {
        return query("select * from " + TABLE_MAIN_INFO + " where " + COLUMN_VIDEO_PATH
                + " is NULL or " + COLUMN_RTSP_H + " is NULL or " + COLUMN_RTSP_L + " is NULL");
    }

    public Cursor queryDataFromLocalData() {
        return queryDataFromLocalData(null, null, null, null);
    }

    public Cursor queryDataFromLocalData(String[] projections, String selection,
            String[] selectionArgs, String sortOrder) {
        if (selection == null) {
            selection = MediaStore.Audio.Media.IS_MUSIC + "=1";
        } else {
            selection += " and " + MediaStore.Audio.Media.IS_MUSIC + "=1";
        }
        return mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projections, selection, selectionArgs, sortOrder);
    }

    public long insert(ContentValues cv, boolean notify) {
        long rtn = getDb().insert(TABLE_MAIN_INFO, null, cv);
        if (notify)
            notifyDataSetChanged();
        return rtn;
    }

    public int insert(ArrayList<PlayListInfo> infoList) {
        ArrayList<ContentValues> cvs = new ArrayList<ContentValues>();
        for (PlayListInfo info : infoList) {
            ContentValues cv = new ContentValues();
            cv.put(U2bDatabaseHelper.COLUMN_ARTIST, info.mArtist);
            cv.put(U2bDatabaseHelper.COLUMN_ALBUM, info.mAlbumTitle);
            cv.put(U2bDatabaseHelper.COLUMN_MUSIC, info.mMusicTitle);
            cv.put(U2bDatabaseHelper.COLUMN_RANK, info.mRank);
            cv.put(U2bDatabaseHelper.COLUMN_RTSP_H, info.mRtspHighQuility);
            cv.put(U2bDatabaseHelper.COLUMN_RTSP_L, info.mRtspLowQuility);
            cv.put(U2bDatabaseHelper.COLUMN_VIDEO_PATH, info.mHttpUri);
            cv.put(U2bDatabaseHelper.COLUMN_VIDEO_ID, info.mVideoId);
            cvs.add(cv);
            if (DEBUG)
                Log.i(TAG, info.toString());
        }
        return insert(cvs, false);
    }

    public int insert(ArrayList<ContentValues> cvs, boolean notify) {
        int records = 0;
        try {
            getDb().beginTransaction();
            for (ContentValues cv : cvs) {
                getDb().replaceOrThrow(TABLE_MAIN_INFO, null, cv);
                ++records;
            }
            getDb().setTransactionSuccessful();
        } catch (SQLException e) {
        } finally {
            getDb().endTransaction();
        }
        if (notify) {
            notifyDataSetChanged();
        }
        return records;
    }

    private void createTables() {
        getDb().execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE_MAIN_INFO + "(" + COLUMN_ARTIST
                        + " TEXT NOT NULL," + COLUMN_ALBUM + " TEXT NOT NULL," + COLUMN_MUSIC
                        + " TEXT NOT NULL," + COLUMN_VIDEO_PATH + " TEXT," + COLUMN_RTSP_H
                        + " TEXT," + COLUMN_RTSP_L + " TEXT," + COLUMN_VIDEO_ID + " TEXT, "
                        + COLUMN_RANK + " INTEGER,PRIMARY KEY(" + COLUMN_ARTIST + ", "
                        + COLUMN_ALBUM + ", " + COLUMN_MUSIC + "))");
        getDb().execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE_ALBUM_INFO
                        + "(_id integer primary key autoincrement, " + COLUMN_ALBUM
                        + " TEXT NOT NULL)");
        if (DEBUG) {
            Log.d(TAG, "table created!");
        }
    }

    public ArrayList<PlayListInfo> getPlayList(String albumName) {
        return getPlayList(albumName, false);
    }

    public ArrayList<PlayListInfo> getPlayList(String albumName, boolean fromLocalIfNotFound) {
        Cursor rtn = getDb().query(TABLE_MAIN_INFO, null, COLUMN_ALBUM + "='" + albumName + "'",
                null, null, null, COLUMN_RANK);
        ArrayList<PlayListInfo> playList = new ArrayList<PlayListInfo>();
        if (rtn != null && rtn.getCount() > 0) {
            convertFromCursorToPlayList(rtn, playList);
        } else {
            if (rtn != null && rtn.isClosed() == false) {
                rtn.close();
            }
            if (fromLocalIfNotFound) {
                rtn = queryDataFromLocalData(null,
                        MediaStore.Audio.Media.ALBUM + "='" + albumName
                                + "'", null, null);
                convertFromLocalMusicDataCursorToPlayList(rtn, playList);
            }
        }
        return playList;
    }

    public ArrayList<PlayListInfo> getLocalPlayList(String albumName) {
        Cursor rtn = queryDataFromLocalData();
        ArrayList<PlayListInfo> playList = new ArrayList<PlayListInfo>();
        convertFromLocalMusicDataCursorToPlayList(rtn, playList);
        return playList;
    }

    public int removeAlbum(String albumName) {
        return getDb().delete(TABLE_ALBUM_INFO, COLUMN_ALBUM + "='" + albumName + "'", null);
    }

    public boolean isAlbumExisted(String albumName) {
        Cursor result = query("select _id from " + TABLE_ALBUM_INFO + " where " + COLUMN_ALBUM
                + "='" + albumName + "'");
        if (result != null && result.getCount() > 0) {
            result.close();
            return true;
        }
        return false;
    }

    public void addNewAlbum(String albumName) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ALBUM, albumName);
        getDb().insert(TABLE_ALBUM_INFO, null, cv);
    }

    public int getAlbumId(String albumName) {
        Cursor result = query("select _id from " + TABLE_ALBUM_INFO + " where " + COLUMN_ALBUM
                + "='" + albumName + "'");
        if (result != null && result.getCount() > 0) {
            result.moveToNext();
            int rtn = result.getInt(0);
            result.close();
            return rtn;
        } else {
            if (result != null && result.isClosed() == false) {
                result.close();
            }
            result = queryDataFromLocalData(new String[] {
                    MediaStore.Audio.Media._ID
            },
                    MediaStore.Audio.Media.ALBUM + "='" + albumName
                            + "'", null, null);
            if (result != null && result.getCount() > 0) {
                result.moveToNext();
                int rtn = result.getInt(0);
                result.close();
                return rtn;
            }
        }
        return ALBUM_NOT_EXISTED;
    }

    public String getAlbumName(long id) {
        Cursor result = query("select " + COLUMN_ALBUM + " from " + TABLE_ALBUM_INFO
                + " where _id=" + id + "");
        if (result != null && result.getCount() > 0) {
            result.moveToNext();
            String rtn = result.getString(0);
            result.close();
            return rtn;
        } else {
            if (result != null && result.isClosed() == false) {
                result.close();
            }
            result = queryDataFromLocalData(new String[] {
                    MediaStore.Audio.Media.ALBUM
            },
                    MediaStore.Audio.Media._ID + "=" + id
                    , null, null);
            if (result != null && result.getCount() > 0) {
                result.moveToNext();
                String rtn = result.getString(0);
                result.close();
                return rtn;
            }
        }
        return null;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public static void convertFromLocalMusicDataCursorToPlayList(Cursor c,
            ArrayList<PlayListInfo> playList) {
        if (c != null && playList != null) {
            int iArtist = c.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int iAlbum = c.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int iMusic = c.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int iRank = c.getColumnIndex(MediaStore.Audio.Media.TRACK);
            int iRh = c.getColumnIndex(MediaStore.Audio.Media.DATA);
            int iRl = c.getColumnIndex(MediaStore.Audio.Media.DATA);
            int iVp = c.getColumnIndex(MediaStore.Audio.Media.DATA);
            int iVi = c.getColumnIndex(MediaStore.Audio.Media.DATA);
            while (c.moveToNext()) {
                String artist = c.getString(iArtist);
                String album = c.getString(iAlbum);
                String music = c.getString(iMusic);
                int rank = c.getInt(iRank);
                String rh = c.getString(iRh);
                String rl = c.getString(iRl);
                String vp = c.getString(iVp);
                String vi = c.getString(iVi);
                playList.add(new PlayListInfo(artist, album, music, rh, rl, vp, vi, rank,
                        PlayListInfo.IS_LOCAL_INFO));
            }
            c.close();
        }
        if (DEBUG) {
            Log.i(TAG, "convertFromCursorToPlayList");
            for (PlayListInfo info : playList) {
                Log.e(TAG, info.toString());
            }
        }
    }

    public static void convertFromCursorToPlayList(Cursor c, ArrayList<PlayListInfo> playList) {
        if (c != null && playList != null) {
            int iArtist = c.getColumnIndex(U2bDatabaseHelper.COLUMN_ARTIST);
            int iAlbum = c.getColumnIndex(U2bDatabaseHelper.COLUMN_ALBUM);
            int iMusic = c.getColumnIndex(U2bDatabaseHelper.COLUMN_MUSIC);
            int iRank = c.getColumnIndex(U2bDatabaseHelper.COLUMN_RANK);
            int iRh = c.getColumnIndex(U2bDatabaseHelper.COLUMN_RTSP_H);
            int iRl = c.getColumnIndex(U2bDatabaseHelper.COLUMN_RTSP_L);
            int iVp = c.getColumnIndex(U2bDatabaseHelper.COLUMN_VIDEO_PATH);
            int iVi = c.getColumnIndex(U2bDatabaseHelper.COLUMN_VIDEO_ID);
            while (c.moveToNext()) {
                String artist = c.getString(iArtist);
                String album = c.getString(iAlbum);
                String music = c.getString(iMusic);
                int rank = c.getInt(iRank);
                String rh = c.getString(iRh);
                String rl = c.getString(iRl);
                String vp = c.getString(iVp);
                String vi = c.getString(iVi);
                playList.add(new PlayListInfo(artist, album, music, rh, rl, vp, vi, rank));
            }
            c.close();
        }
        if (DEBUG) {
            Log.i(TAG, "convertFromCursorToPlayList");
            for (PlayListInfo info : playList) {
                Log.e(TAG, info.toString());
            }
        }
    }

    public ArrayList<PlayListInfo> getPlayListByMusic(final String music) {
        ArrayList<PlayListInfo> playList = new ArrayList<PlayListInfo>();
        Cursor data = getDb().rawQuery(
                "select * from " + TABLE_MAIN_INFO + " where " + COLUMN_MUSIC + "='" + music
                        + "' order by " + COLUMN_RANK, null);
        convertFromCursorToPlayList(data, playList);
        return playList;
    }

    public ArrayList<PlayListInfo> getPlayListByArtist(final String artist) {
        ArrayList<PlayListInfo> playList = new ArrayList<PlayListInfo>();
        Cursor data = getDb().rawQuery(
                "select * from " + TABLE_MAIN_INFO + " where " + COLUMN_ARTIST + "='" + artist
                        + "' order by " + COLUMN_RANK, null);
        convertFromCursorToPlayList(data, playList);
        return playList;
    }

    public ArrayList<PlayListInfo> getPlayListByAlbum(final String album) {
        ArrayList<PlayListInfo> playList = new ArrayList<PlayListInfo>();
        Cursor data = getDb().rawQuery(
                "select * from " + TABLE_MAIN_INFO + " where " + COLUMN_ALBUM + "='" + album
                        + "' order by " + COLUMN_RANK, null);
        convertFromCursorToPlayList(data, playList);
        return playList;
    }
}
