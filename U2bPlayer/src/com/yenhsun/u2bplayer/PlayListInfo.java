
package com.yenhsun.u2bplayer;

public class PlayListInfo {
    String mArthur;
    String mMusicTitle;
    String mCdTitle;
    String mRtspHighQuility;
    String mRtspLowQuility;
    String mHttpUri;
    String mVideoId;

    public PlayListInfo(String arthur, String cdTitle, String musicTitle, String rtspH,
            String rtspL,
            String httpUri, String videoId) {
        mArthur = arthur;
        mMusicTitle = musicTitle;
        mRtspHighQuility = rtspH;
        mRtspLowQuility = rtspL;
        mHttpUri = httpUri;
        mVideoId = videoId;
        mCdTitle = cdTitle;
    }
}
