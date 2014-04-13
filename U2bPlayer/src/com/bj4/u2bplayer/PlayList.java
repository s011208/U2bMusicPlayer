
package com.bj4.u2bplayer;

import java.util.ArrayList;

import com.bj4.u2bplayer.database.U2bDatabaseHelper;
import com.bj4.u2bplayer.u2bParser.YoutubeDataParser;
import com.bj4.u2bplayer.utilities.PlayListInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Looper;
import android.widget.Toast;

public class PlayList {

    private static final boolean DEBUG = true && PlayMusicApplication.OVERALL_DEBUG;

    private Context mContext;

    private final U2bDatabaseHelper mDatabaseHelper = PlayMusicApplication.getDataBaseHelper();

    private int mPointer = 0;

    public int getPointer() {
        return mPointer;
    }

    public void setPointer(final int pointer) {
        mPointer = pointer;
        mPref.edit().putInt(SHARE_PREF_KEY_LAST_TIME_INDEX, pointer).apply();
    }

    public interface PlayListLoaderCallback {
        public void loadDone();
    }

    public void retrieveAllPlayList() {
        mPlayList.clear();
        Cursor data = mDatabaseHelper.query(null, null);
        U2bDatabaseHelper.convertFromCursorToPlayList(data, mPlayList);
    }

    private final ArrayList<PlayListInfo> mPlayList = new ArrayList<PlayListInfo>();

    private final ArrayList<PlayListLoaderCallback> mCallbacks = new ArrayList<PlayListLoaderCallback>();

    private static PlayList mSingleton;

    private SharedPreferences mPref;

    private static final String SHARE_PREF_KEY = "play_list_config";

    private static final String SHARE_PREF_KEY_LAST_TIME_INDEX = "last_time_index";

    private PlayList(Context context) {
        mContext = context.getApplicationContext();
        mPref = mContext.getSharedPreferences(SHARE_PREF_KEY, Context.MODE_PRIVATE);
        mPointer = mPref.getInt(SHARE_PREF_KEY_LAST_TIME_INDEX, 0);
        retrieveAllPlayList();
    }

    public void notifyScanDone() {
        for (PlayListLoaderCallback c : mCallbacks) {
            c.loadDone();
        }
        Looper.prepare();
        Toast.makeText(mContext, "parse finish", Toast.LENGTH_LONG).show();
        Looper.loop();
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
}
