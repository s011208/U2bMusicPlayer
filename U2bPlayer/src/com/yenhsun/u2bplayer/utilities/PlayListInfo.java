
package com.yenhsun.u2bplayer.utilities;

public class PlayListInfo {
    public String mArtist;
    public String mMusicTitle;
    public String mAlbumTitle;
    public String mRtspHighQuility;
    public String mRtspLowQuility;
    public String mHttpUri;
    public String mVideoId;
    public int mRank;

    public PlayListInfo(String artist, String albumTitle, String musicTitle, String rtspH,
            String rtspL,
            String httpUri, String videoId, int rank) {
        mArtist = artist;
        mMusicTitle = musicTitle;
        mRtspHighQuility = rtspH;
        mRtspLowQuility = rtspL;
        mHttpUri = httpUri;
        mVideoId = videoId;
        mAlbumTitle = albumTitle;
        mRank = rank;
    }
}
