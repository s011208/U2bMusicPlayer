
package com.bj4.u2bplayer.service;

import java.util.ArrayList;

import com.bj4.u2bplayer.PlayList;
import com.bj4.u2bplayer.utilities.PlayListInfo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

public class PlayMusicService extends Service implements PlayList.PlayListLoaderCallback {
    private static final boolean DEBUG = true;

    private static final String TAG = "PlayMusicService";

    public static final int PLAY_NEXT_INDEX = -1;

    public static final int PLAY_PREVIOUS_INDEX = -2;

    private MediaPlayer mMediaPlayer;

    private PlayList mLoader;

    private ArrayList<PlayListInfo> mPlayList = new ArrayList<PlayListInfo>();

    private int mPlayPointer = 0;

    private SharedPreferences mPref;

    private static final String SHARE_PREF_KEY = "play_music_service_config";

    private static final String SHARE_PREF_KEY_LAST_TIME_INDEX = "last_time_index";

    public void onCreate() {
        super.onCreate();
        mPref = this.getSharedPreferences(SHARE_PREF_KEY, Context.MODE_PRIVATE);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mLoader = PlayList.getInstance(this);
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
            notifyIndexChanged();
            mPref.edit().putInt(SHARE_PREF_KEY_LAST_TIME_INDEX, mPlayPointer).apply();
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

        @Override
        public void registerCallback(IPlayMusicServiceCallback cb) throws RemoteException {
            mCallbacks.register(cb);
        }

        @Override
        public void unRegisterCallback(IPlayMusicServiceCallback cb) throws RemoteException {
            mCallbacks.unregister(cb);
        }

        @Override
        public int playFromLastTime() throws RemoteException {
            int index = mPref.getInt(SHARE_PREF_KEY_LAST_TIME_INDEX, 0);
            playMusic(index);
            return index;
        }
    };

    final RemoteCallbackList<IPlayMusicServiceCallback> mCallbacks = new RemoteCallbackList<IPlayMusicServiceCallback>();

    private void notifyIndexChanged() {
        final int N = mCallbacks.beginBroadcast();
        for (int i = 0; i < N; i++) {
            try {
                mCallbacks.getBroadcastItem(i).notiftPlayIndexChanged(mPlayPointer);
            } catch (RemoteException e) {
            }
        }
        mCallbacks.finishBroadcast();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void loadDone() {
        mPlayList = mLoader.getPlayList();
    }

}
