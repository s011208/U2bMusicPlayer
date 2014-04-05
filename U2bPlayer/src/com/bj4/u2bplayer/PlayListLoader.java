
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
        mPlayList.add(new PlayListInfo("五月天", "瘋狂世界", "瘋狂世界", "", "", "", "", 0));
        mPlayList.add(new PlayListInfo("五月天", "瘋狂世界", "擁抱", "", "", "", "", 1));
        mPlayList.add(new PlayListInfo("五月天", "瘋狂世界", "透露", "", "", "", "", 2));
        mPlayList.add(new PlayListInfo("五月天", "瘋狂世界", "生活", "", "", "", "", 3));
        mPlayList.add(new PlayListInfo("五月天", "瘋狂世界", "愛情的模樣", "", "", "", "", 4));
        mPlayList.add(new PlayListInfo("五月天", "瘋狂世界", "嘿我要走了", "", "", "", "", 5));
        mPlayList.add(new PlayListInfo("五月天", "瘋狂世界", "軋車", "", "", "", "", 6));
        mPlayList.add(new PlayListInfo("五月天", "瘋狂世界", "志明與春嬌", "", "", "", "", 7));
        mPlayList.add(new PlayListInfo("五月天", "瘋狂世界", "Hosee", "", "", "", "", 8));
        mPlayList.add(new PlayListInfo("五月天", "瘋狂世界", "黑白講", "", "", "", "", 9));
        mPlayList.add(new PlayListInfo("五月天", "瘋狂世界", "I Love You 無望", "", "", "", "", 10));
        mPlayList.add(new PlayListInfo("五月天", "瘋狂世界", "風若吹", "", "", "", "", 11));
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
