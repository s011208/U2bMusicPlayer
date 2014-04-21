
package com.bj4.u2bplayer.utilities;

import android.os.Parcel;
import android.os.Parcelable;

public class PlayListInfo implements Parcelable {
    public static final int IS_LOCAL_INFO = 0;

    public static final int NOT_LOCAL_INFO = 1;

    public String mArtist;

    public String mMusicTitle;

    public String mAlbumTitle;

    public String mRtspHighQuility;

    public String mRtspLowQuility;

    public String mHttpUri;

    public String mVideoId;

    public int mRank;

    public int mIsLocal;

    public PlayListInfo() {
    }

    public PlayListInfo(String artist, String albumTitle, String musicTitle, String rtspH,
            String rtspL, String httpUri, String videoId, int rank) {
        this(artist, albumTitle, musicTitle, rtspH,
                rtspL, httpUri, videoId, rank, NOT_LOCAL_INFO);
    }

    public PlayListInfo(String artist, String albumTitle, String musicTitle, String rtspH,
            String rtspL, String httpUri, String videoId, int rank, int isLocal) {
        mArtist = artist;
        mMusicTitle = musicTitle;
        mRtspHighQuility = rtspH;
        mRtspLowQuility = rtspL;
        mHttpUri = httpUri;
        mVideoId = videoId;
        mAlbumTitle = albumTitle;
        mRank = rank;
        mIsLocal = isLocal;
    }

    public String toString() {
        return "Artist: " + mArtist + ", music: " + mMusicTitle + ", album: " + mAlbumTitle
                + ", rank: " + mRank + ", http: " + mHttpUri + ", videoId: " + mVideoId
                + ", rtspH: " + mRtspHighQuility + ", rtspL: " + mRtspLowQuility;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void readFromParcel(Parcel parcel) {
        mArtist = parcel.readString();
        mMusicTitle = parcel.readString();
        mRtspHighQuility = parcel.readString();
        mRtspLowQuility = parcel.readString();
        mHttpUri = parcel.readString();
        mVideoId = parcel.readString();
        mAlbumTitle = parcel.readString();
        mRank = parcel.readInt();
        mIsLocal = parcel.readInt();
    }

    public PlayListInfo(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel write, int flag) {
        write.writeString(mArtist);
        write.writeString(mMusicTitle);
        write.writeString(mRtspHighQuility);
        write.writeString(mRtspLowQuility);
        write.writeString(mHttpUri);
        write.writeString(mVideoId);
        write.writeString(mAlbumTitle);
        write.writeInt(mRank);
        write.writeInt(mIsLocal);
    }

    public static final Parcelable.Creator<PlayListInfo> CREATOR = new Parcelable.Creator<PlayListInfo>() {
        public PlayListInfo createFromParcel(Parcel in) {
            return new PlayListInfo(in);
        }

        public PlayListInfo[] newArray(int size) {
            return new PlayListInfo[size];
        }
    };
}
