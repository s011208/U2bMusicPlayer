
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
        // mPlayList.add(new PlayListInfo("五月天", "瘋狂世界", "瘋狂世界", "", "", "", "",
        // 0));
        // mPlayList.add(new PlayListInfo("五月天", "瘋狂世界", "擁抱", "", "", "", "",
        // 1));
        // mPlayList.add(new PlayListInfo("五月天", "瘋狂世界", "透露", "", "", "", "",
        // 2));
        // mPlayList.add(new PlayListInfo("五月天", "瘋狂世界", "生活", "", "", "", "",
        // 3));
        // mPlayList.add(new PlayListInfo("五月天", "瘋狂世界", "愛情的模樣", "", "", "",
        // "", 4));
        // mPlayList.add(new PlayListInfo("五月天", "瘋狂世界", "嘿我要走了", "", "", "",
        // "", 5));
        // mPlayList.add(new PlayListInfo("五月天", "瘋狂世界", "軋車", "", "", "", "",
        // 6));
        // mPlayList.add(new PlayListInfo("五月天", "瘋狂世界", "志明與春嬌", "", "", "",
        // "", 7));
        // mPlayList.add(new PlayListInfo("五月天", "瘋狂世界", "Hosee", "", "", "",
        // "", 8));
        // mPlayList.add(new PlayListInfo("五月天", "瘋狂世界", "黑白講", "", "", "", "",
        // 9));
        // mPlayList.add(new PlayListInfo("五月天", "瘋狂世界", "I Love You 無望", "",
        // "", "", "", 10));
        // mPlayList.add(new PlayListInfo("五月天", "瘋狂世界", "風若吹", "", "", "", "",
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
                        "瘋狂世界", "擁抱", "透露", "生活", "愛情的模樣", "嘿我要走了", "軋車", "志明與春嬌", "Hosee", "黑白講",
                        "I Love You 無望", "風若吹"
                };
                for (String music : cdList) {
                    YoutubeDataParser.showYoutubeResult(
                            "五月天", "瘋狂世界", music
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
