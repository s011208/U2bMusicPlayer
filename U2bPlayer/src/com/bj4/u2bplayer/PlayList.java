
package com.bj4.u2bplayer;

import java.util.ArrayList;

import com.bj4.u2bplayer.database.U2bDatabaseHelper;
import com.bj4.u2bplayer.u2bParser.YoutubeDataParser;
import com.bj4.u2bplayer.utilities.PlayListInfo;

import android.content.Context;
import android.database.Cursor;

public class PlayList {

    private static final boolean DEBUG = true && PlayMusicApplication.OVERALL_DEBUG;

    private final U2bDatabaseHelper mDatabaseHelper = PlayMusicApplication.getDataBaseHelper();

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

    private PlayList() {
        retrieveAllPlayList();
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

    public static synchronized PlayList getInstance() {
        if (mSingleton == null) {
            mSingleton = new PlayList();
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
