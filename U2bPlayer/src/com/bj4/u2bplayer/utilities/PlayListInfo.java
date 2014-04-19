
package com.bj4.u2bplayer.utilities;

import android.os.Parcel;
import android.os.Parcelable;

public class PlayListInfo implements Parcelable {
    public String mArtist;

    public String mMusicTitle;

    public String mAlbumTitle;

    public String mRtspHighQuility;

    public String mRtspLowQuility;

    public String mHttpUri;

    public String mVideoId;

    public int mRank;

    public PlayListInfo() {
    }

    public PlayListInfo(String artist, String albumTitle, String musicTitle, String rtspH,
            String rtspL, String httpUri, String videoId, int rank) {
        mArtist = artist;
        mMusicTitle = musicTitle;
        mRtspHighQuility = rtspH;
        mRtspLowQuility = rtspL;
        mHttpUri = httpUri;
        mVideoId = videoId;
        mAlbumTitle = albumTitle;
        mRank = rank;
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
