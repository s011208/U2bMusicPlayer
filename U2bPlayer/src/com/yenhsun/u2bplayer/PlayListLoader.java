
package com.yenhsun.u2bplayer;

import java.util.ArrayList;

import com.yenhsun.u2bplayer.utilities.PlayListInfo;
import com.yenhsun.u2bplayer.utilities.YoutubeDataParser;

import android.content.Context;

public class PlayListLoader {

    public interface PlayListLoaderCallback {
        public void loadDone();
    }

    private Context mContext;
    private final ArrayList<PlayListInfo> mPlayList = new ArrayList<PlayListInfo>();
    private final ArrayList<PlayListLoaderCallback> mCallbacks = new ArrayList<PlayListLoaderCallback>();
    private static PlayListLoader mSingleton;

    private PlayListLoader(Context context) {
        mContext = context;
    }

    public String getPlayListTitle() {
        if (mPlayList.isEmpty() == false) {
            PlayListInfo info = mPlayList.get(0);
            String rtn = "";
            if ("".equals(info.mCdTitle) == false) {
                rtn += info.mCdTitle;
            }
            if ("".equals(rtn) == false) {
                rtn += "  ";
            }
            rtn += info.mArthur;
            return rtn;
        }
        return "";
    }

    @SuppressWarnings("unchecked")
    public ArrayList<PlayListInfo> getPlayList() {
        // +++using debug
        // mPlayList.clear();
        // mPlayList.add(new PlayListInfo("�����", "�ƨg�@��", "�ƨg�@��", "", "", "", "",
        // 0));
        // mPlayList.add(new PlayListInfo("�����", "�ƨg�@��", "�֩�", "", "", "", "",
        // 1));
        // mPlayList.add(new PlayListInfo("�����", "�ƨg�@��", "�z�S", "", "", "", "",
        // 2));
        // mPlayList.add(new PlayListInfo("�����", "�ƨg�@��", "�ͬ�", "", "", "", "",
        // 3));
        // mPlayList.add(new PlayListInfo("�����", "�ƨg�@��", "�R�����Ҽ�", "", "", "",
        // "", 4));
        // mPlayList.add(new PlayListInfo("�����", "�ƨg�@��", "�K�ڭn���F", "", "", "",
        // "", 5));
        // mPlayList.add(new PlayListInfo("�����", "�ƨg�@��", "�", "", "", "", "",
        // 6));
        // mPlayList.add(new PlayListInfo("�����", "�ƨg�@��", "�ө��P�K�b", "", "", "",
        // "", 7));
        // mPlayList.add(new PlayListInfo("�����", "�ƨg�@��", "Hosee", "", "", "",
        // "", 8));
        // mPlayList.add(new PlayListInfo("�����", "�ƨg�@��", "�¥���", "", "", "", "",
        // 9));
        // mPlayList.add(new PlayListInfo("�����", "�ƨg�@��", "I Love You �L��", "",
        // "", "", "", 10));
        // mPlayList.add(new PlayListInfo("�����", "�ƨg�@��", "���Y�j", "", "", "", "",
        // 11));
        // ---using debug
        return (ArrayList<PlayListInfo>) mPlayList.clone();
    }

    public static synchronized PlayListLoader getInstance(Context context) {
        if (mSingleton == null) {
            mSingleton = new PlayListLoader(context);
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
        // if (true)
        // return;
        new Thread(new Runnable() {

            @Override
            public void run() {
                // get list and put into mPlayList
                String[] cdList = new String[] {
                        "�ƨg�@��", "�֩�", "�z�S", "�ͬ�", "�R�����Ҽ�", "�K�ڭn���F", "�", "�ө��P�K�b", "Hosee", "�¥���",
                        "I Love You �L��", "���Y�j"
                };
                for (String music : cdList) {
                    YoutubeDataParser.showYoutubeResult(
                            "�����", "�ƨg�@��", music
                            , new YoutubeDataParser.YoutubeIdParserResultCallback() {

                                @Override
                                public void setResult(ArrayList<PlayListInfo> infoList) {
                                    mPlayList.addAll(infoList);
                                    if (mCallbacks.isEmpty() == false) {
                                        for (PlayListLoaderCallback c : mCallbacks) {
                                            c.loadDone();
                                        }
                                    }
                                }
                            });
                }
            }
        }).start();
    }
}
