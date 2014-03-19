
package com.yenhsun.u2bplayer.playservice;

import java.util.ArrayList;

import com.yenhsun.u2bplayer.playservice.IPlayMusicService;
import com.yenhsun.u2bplayer.PlayListLoader;
import com.yenhsun.u2bplayer.utilities.PlayListInfo;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class PlayMusicService extends Service implements PlayListLoader.PlayListLoaderCallback {
    private static final boolean DEBUG = true;
    private static final String TAG = "QQQQ";
    private MediaPlayer mMediaPlayer;
    private PlayListLoader mLoader;
    private ArrayList<PlayListInfo> mPlayList = new ArrayList<PlayListInfo>();
    private int mPlayPointer = 0;
    public static final int PLAY_NEXT_INDEX = -1;
    public static final int PLAY_PREVIOUS_INDEX = -2;

    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mLoader = PlayListLoader.getInstance();
        mLoader.initPlayListContent();
        mLoader.addCallback(this);
        mPlayList = mLoader.getPlayList();
    }

    public void onDestroy() {
        super.onDestroy();
        mLoader.removeCallback(this);
    }

    private void pauseMusic() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            if (DEBUG)
                Log.d(TAG, "pause");
        }
    }

    private void resumeMusic() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
            if (DEBUG)
                Log.d(TAG, "resume");
        }
    }

    private void playMusic(int index) {
        if (DEBUG)
            Log.d(TAG, "play: " + index);
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.reset();
        if (mPlayList.isEmpty())
            return;
        if (index == PLAY_NEXT_INDEX) {
            if (mPlayPointer >= mPlayList.size()) {
                mPlayPointer = 0;
            } else {
                ++mPlayPointer;
            }
        } else if (index == PLAY_PREVIOUS_INDEX) {
            if (mPlayPointer < 0) {
                mPlayPointer = mPlayList.size() - 1;
            } else {
                --mPlayPointer;
            }
        } else {
            mPlayPointer = index;
            if (mPlayPointer >= mPlayList.size()) {
                mPlayPointer = 0;
            }
        }
        try {
            mMediaPlayer.setDataSource(mPlayList.get(mPlayPointer).mRtspHighQuility);
            mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer player) {
                    if (DEBUG)
                        Log.i(TAG, "play");
                    player.start();
                }
            });
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer player) {
                    if (DEBUG)
                        Log.i(TAG, "complete");
                    playMusic(PLAY_NEXT_INDEX);
                }
            });
        } catch (Exception e) {
            if (DEBUG)
                Log.w(TAG, "play failed", e);
        }
    }

    private final IPlayMusicService.Stub mBinder = new IPlayMusicService.Stub() {

        @Override
        public void play(int index) throws RemoteException {
            playMusic(index);
        }

        @Override
        public void next() throws RemoteException {
            playMusic(PLAY_NEXT_INDEX);
        }

        @Override
        public void previous() throws RemoteException {
            playMusic(PLAY_PREVIOUS_INDEX);
        }

        @Override
        public void pause() throws RemoteException {
            pauseMusic();
        }

        @Override
        public void resume() throws RemoteException {
            resumeMusic();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return mMediaPlayer != null && mMediaPlayer.isPlaying();
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void loadDone() {
        mPlayList = mLoader.getPlayList();
    }

}
