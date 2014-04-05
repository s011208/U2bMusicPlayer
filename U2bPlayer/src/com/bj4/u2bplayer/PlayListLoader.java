
package com.bj4.u2bplayer;

import java.util.ArrayList;

import com.bj4.u2bplayer.u2bParser.YoutubeDataParser;
import com.bj4.u2bplayer.utilities.PlayListInfo;

import android.content.Context;

public class PlayListLoader {

    private static final boolean DEBUG = true && PlayMusicApplication.OVERALL_DEBUG;

    private static final boolean DEBUG_PRELOAD_LIST = true;

    public interface PlayListLoaderCallback {
        public void loadDone();
    }

    private final ArrayList<PlayListInfo> mPlayList = new ArrayList<PlayListInfo>();

    private final ArrayList<PlayListLoaderCallback> mCallbacks = new ArrayList<PlayListLoaderCallback>();

    private static PlayListLoader mSingleton;

    private PlayListLoader() {
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

    private void initDebugData() {
        mPlayList.clear();
        mPlayList.add(new PlayListInfo("�����", "�ƨg�@��", "�ƨg�@��", "", "", "", "", 0));
        mPlayList.add(new PlayListInfo("�����", "�ƨg�@��", "�֩�", "", "", "", "", 1));
        mPlayList.add(new PlayListInfo("�����", "�ƨg�@��", "�z�S", "", "", "", "", 2));
        mPlayList.add(new PlayListInfo("�����", "�ƨg�@��", "�ͬ�", "", "", "", "", 3));
        mPlayList.add(new PlayListInfo("�����", "�ƨg�@��", "�R�����Ҽ�", "", "", "", "", 4));
        mPlayList.add(new PlayListInfo("�����", "�ƨg�@��", "�K�ڭn���F", "", "", "", "", 5));
        mPlayList.add(new PlayListInfo("�����", "�ƨg�@��", "�", "", "", "", "", 6));
        mPlayList.add(new PlayListInfo("�����", "�ƨg�@��", "�ө��P�K�b", "", "", "", "", 7));
        mPlayList.add(new PlayListInfo("�����", "�ƨg�@��", "Hosee", "", "", "", "", 8));
        mPlayList.add(new PlayListInfo("�����", "�ƨg�@��", "�¥���", "", "", "", "", 9));
        mPlayList.add(new PlayListInfo("�����", "�ƨg�@��", "I Love You �L��", "", "", "", "", 10));
        mPlayList.add(new PlayListInfo("�����", "�ƨg�@��", "���Y�j", "", "", "", "", 11));
    }

    @SuppressWarnings("unchecked")
    public ArrayList<PlayListInfo> getPlayList() {
        return (ArrayList<PlayListInfo>)mPlayList.clone();
    }

    public static synchronized PlayListLoader getInstance() {
        if (mSingleton == null) {
            mSingleton = new PlayListLoader();
        }
        return mSingleton;
    }

    public void addCallback(PlayListLoaderCallback c) {
        mCallbacks.add(c);
    }

    public void removeCallback(PlayListLoaderCallback c) {
        mCallbacks.remove(c);
    }

    public void initPlayListContent() {
        if (DEBUG_PRELOAD_LIST)
            initDebugData();
        YoutubeDataParser.parseYoutubeData(mPlayList,
                new YoutubeDataParser.YoutubeIdParserResultCallback() {

                    @Override
                    public void setResult(ArrayList<PlayListInfo> infoList) {
                        if (mCallbacks.isEmpty() == false) {
                            for (PlayListLoaderCallback c : mCallbacks) {
                                c.loadDone();
                            }
                        }
                    }
                });
    }
}
