
package com.bj4.u2bplayer;

import java.util.ArrayList;

import com.bj4.u2bplayer.database.U2bDatabaseHelper;
import com.bj4.u2bplayer.utilities.PlayListInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class PlayList {

    private static final boolean DEBUG = true && PlayMusicApplication.OVERALL_DEBUG;

    private Context mContext;

    private final U2bDatabaseHelper mDatabaseHelper = PlayMusicApplication.getDataBaseHelper();

    private final ArrayList<PlayListInfo> mPlayList = new ArrayList<PlayListInfo>();

    private final ArrayList<PlayListLoaderCallback> mCallbacks = new ArrayList<PlayListLoaderCallback>();

    private static PlayList mSingleton;

    private SharedPreferences mPref;

    private long mPlayingAlbumId, mDisplayingAlbumId;

    private static final String SHARE_PREF_KEY = "play_list_config";

    private static final String SHARE_PREF_KEY_LAST_TIME_INDEX = "last_time_index";

    private PlayList(Context context) {
        mContext = context.getApplicationContext();
        mPref = mContext.getSharedPreferences(SHARE_PREF_KEY, Context.MODE_PRIVATE);
        retrieveAllPlayList();
    }

    public int getPointer() {
        int index = mPref.getInt(SHARE_PREF_KEY_LAST_TIME_INDEX, 0);
        if (index >= mPlayList.size() || index < 0) {
            index = 0;
            mPref.edit().putInt(SHARE_PREF_KEY_LAST_TIME_INDEX, index).apply();
        }
        return index;
    }

    public int getPreviousPointer() {
        int index = mPref.getInt(SHARE_PREF_KEY_LAST_TIME_INDEX, 0) - 1;
        if (index >= mPlayList.size()) {
            index = 0;
        } else if (index < 0) {
            index = mPlayList.size() - 1;
        }
        return index;
    }

    public int getNextPointer() {
        int index = mPref.getInt(SHARE_PREF_KEY_LAST_TIME_INDEX, 0) + 1;
        if (index >= mPlayList.size() || index < 0) {
            index = 0;
        }
        return index;
    }

    public PlayListInfo getCurrentPlayListInfo() {
        return mPlayList.get(getPointer());
    }

    public PlayListInfo getNextPlayListInfo() {
        return mPlayList.get(getNextPointer());
    }

    public void setPointer(final int pointer) {
        mPref.edit().putInt(SHARE_PREF_KEY_LAST_TIME_INDEX, pointer).apply();
    }

    public interface PlayListLoaderCallback {
        public void loadDone();
    }

    public void retrieveLocalPlayList() {
        resetPlayList();
        Cursor data = mDatabaseHelper.queryDataFromLocalData();
        U2bDatabaseHelper.convertFromLocalMusicDataCursorToPlayList(data, mPlayList);
    }

    public void retrieveAllPlayList() {
        resetPlayList();
        Cursor data = mDatabaseHelper.query(null, U2bDatabaseHelper.COLUMN_RTSP_H + "!=''");
        U2bDatabaseHelper.convertFromCursorToPlayList(data, mPlayList);
    }

    public void resetPlayList() {
        mPlayList.clear();
        setPointer(0);
    }

    public void setAlbumPlayList(String album) {
        resetPlayList();
        Cursor data = mDatabaseHelper.query(null, U2bDatabaseHelper.COLUMN_ALBUM + "='" + album
                + "'");
        U2bDatabaseHelper.convertFromCursorToPlayList(data, mPlayList);
        if (mPlayList.size() > 0) {
            mDisplayingAlbumId = mDatabaseHelper.getAlbumId(mPlayList.get(0).mAlbumTitle);
        } else {
            mDisplayingAlbumId = U2bDatabaseHelper.ALBUM_NOT_EXISTED;
        }
    }

    public void setPlayingAlbumId(String AlbumName){
        mPlayingAlbumId = mDatabaseHelper.getAlbumId(mPlayList.get(0).mAlbumTitle);
    }
    
    public long getPlayingAlbumId() {
        return mPlayingAlbumId;
    }

    public long getDisplayingAlbumId() {
        return mDisplayingAlbumId;
    }

    public void notifyScanDone() {
        for (PlayListLoaderCallback c : mCallbacks) {
            c.loadDone();
        }
    }

    public String getPlayListTitle() {
        if (mPlayList.isEmpty() == false) {
            PlayListInfo info = mPlayList.get(0);
            String rtn = "";
            if ("".equals(info.mAlbumTitle) == false) {
                rtn += info.mAlbumTitle;
            }
            if ("".equals(rtn) == false) {
                rtn += "  ";
            }
            rtn += info.mArtist;
            return rtn;
        }
        return "";
    }

    @SuppressWarnings("unchecked")
    public ArrayList<PlayListInfo> getPlayList() {
        return (ArrayList<PlayListInfo>)mPlayList.clone();
    }

    public static synchronized PlayList getInstance(Context context) {
        if (mSingleton == null) {
            mSingleton = new PlayList(context);
        }
        return mSingleton;
    }

    public void addCallback(PlayListLoaderCallback c) {
        mCallbacks.add(c);
    }

    public void removeCallback(PlayListLoaderCallback c) {
        mCallbacks.remove(c);
    }

    public static String getTimeString(long millis) {
        StringBuffer buf = new StringBuffer();

        int hours = (int)(millis / (1000 * 60 * 60));
        int minutes = (int)((millis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int)(((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

        buf.append(String.format("%02d", hours)).append(":").append(String.format("%02d", minutes))
                .append(":").append(String.format("%02d", seconds));

        return buf.toString();
    }
}
