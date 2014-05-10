
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

    private final ArrayList<PlayListInfo> mPlayingList = new ArrayList<PlayListInfo>();

    private final ArrayList<PlayListLoaderCallback> mCallbacks = new ArrayList<PlayListLoaderCallback>();

    private static PlayList mSingleton;

    private SharedPreferences mPref;

    private int mPlayingAlbumId;

    private String mPlayingAlbumName;

    private static final String SHARE_PREF_KEY = "play_list_config";

    private static final String SHARE_PREF_KEY_LAST_TIME_INDEX = "last_time_index";

    private static final String SHARE_PREF_KEY_LAST_TIME_ALBUM = "last_tiem_album";

    private PlayList(Context context) {
        mContext = context.getApplicationContext();
        mPref = mContext.getSharedPreferences(SHARE_PREF_KEY, Context.MODE_PRIVATE);
        initPlayList();
    }

    private void initPlayList() {
        String album = mPref.getString(SHARE_PREF_KEY_LAST_TIME_ALBUM, null);
        setAlbumPlayingList(album);
    }

    public int getPointer() {
        int index = mPref.getInt(SHARE_PREF_KEY_LAST_TIME_INDEX, 0);
        if (index >= mPlayingList.size() || index < 0) {
            index = 0;
            mPref.edit().putInt(SHARE_PREF_KEY_LAST_TIME_INDEX, index).apply();
        }
        return index;
    }

    public int getPreviousPointer() {
        int index = mPref.getInt(SHARE_PREF_KEY_LAST_TIME_INDEX, 0) - 1;
        if (index >= mPlayingList.size()) {
            index = 0;
        } else if (index < 0) {
            index = mPlayingList.size() - 1;
        }
        return index;
    }

    public int getNextPointer() {
        int index = mPref.getInt(SHARE_PREF_KEY_LAST_TIME_INDEX, 0) + 1;
        if (index >= mPlayingList.size() || index < 0) {
            index = 0;
        }
        return index;
    }

    public PlayListInfo getCurrentPlayingListInfo() {
        if (mPlayingList.isEmpty())
            return null;
        return mPlayingList.get(getPointer());
    }

    public PlayListInfo getNextPlayingListInfo() {
        return mPlayingList.get(getNextPointer());
    }

    public void setPointer(final int pointer) {
        mPref.edit().putInt(SHARE_PREF_KEY_LAST_TIME_INDEX, pointer).apply();
    }

    public interface PlayListLoaderCallback {
        public void loadDone();
    }

    public void resetPlayingList() {
        mPlayingList.clear();
    }

    public void reloadCurrentPlayingList() {
        setAlbumPlayingList(mPlayingAlbumName);
    }

    public void setAlbumPlayingList(String album) {
        if (album == null)
            return;
        resetPlayingList();
        mPlayingList.addAll(mDatabaseHelper.getPlayList(album, true));
        mPlayingAlbumId = mDatabaseHelper.getAlbumId(album);
        mPlayingAlbumName = album;
        mPref.edit().putString(SHARE_PREF_KEY_LAST_TIME_ALBUM, album).apply();
    }

    public long getPlayingListAlbumId() {
        return mPlayingAlbumId;
    }

    public String getPlayingListAlbumName() {
        return mPlayingAlbumName;
    }

    public void notifyScanDone() {
        for (PlayListLoaderCallback c : mCallbacks) {
            c.loadDone();
        }
    }

    @SuppressWarnings("unchecked")
    public ArrayList<PlayListInfo> getPlayingList() {
        return (ArrayList<PlayListInfo>)mPlayingList.clone();
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
