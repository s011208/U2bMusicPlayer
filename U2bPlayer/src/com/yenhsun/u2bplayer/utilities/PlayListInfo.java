
package com.yenhsun.u2bplayer.utilities;

public class PlayListInfo {
    public String mArthur;
    public String mMusicTitle;
    public String mCdTitle;
    public String mRtspHighQuility;
    public String mRtspLowQuility;
    public String mHttpUri;
    public String mVideoId;
    public int mRank;

    public PlayListInfo(String arthur, String cdTitle, String musicTitle, String rtspH,
            String rtspL,
            String httpUri, String videoId, int rank) {
        mArthur = arthur;
        mMusicTitle = musicTitle;
        mRtspHighQuility = rtspH;
        mRtspLowQuility = rtspL;
        mHttpUri = httpUri;
        mVideoId = videoId;
        mCdTitle = cdTitle;
        mRank = rank;
    }
}
