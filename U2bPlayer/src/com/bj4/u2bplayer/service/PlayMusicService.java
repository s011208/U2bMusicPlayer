
package com.bj4.u2bplayer.service;

import java.util.ArrayList;

import com.bj4.u2bplayer.PlayList;
import com.bj4.u2bplayer.R;
import com.bj4.u2bplayer.activity.U2bPlayerMainFragmentActivity;
import com.bj4.u2bplayer.utilities.PlayListInfo;

import android.app.Notification;
import android.app.PendingIntent;
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

    private PlayList mPlayList;

    private ArrayList<PlayListInfo> mPlayListContent = new ArrayList<PlayListInfo>();

    public void onCreate() {
        super.onCreate();
        if (DEBUG)
            Log.d(TAG, "service oncreate");
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayList = PlayList.getInstance(this);
        mPlayList.addCallback(this);
        mPlayListContent = mPlayList.getPlayList();
        startForeground(PlayMusicService.class.hashCode(), getNotification());
    }

    private Notification getNotification() {
        Notification notification = new Notification();
        notification.icon = R.drawable.ic_launcher;
        notification.tickerText = "playing music";
        notification.defaults = Notification.DEFAULT_ALL;
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                U2bPlayerMainFragmentActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, "playing music", "playing music", contentIntent);
        return notification;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (DEBUG)
            Log.d(TAG, "onStartCommand");
        return Service.START_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();
        if (DEBUG)
            Log.d(TAG, "onDestroy");
        mPlayList.removeCallback(this);
        mMediaPlayer.release();
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
        int pointer = mPlayList.getPointer();
        if (DEBUG)
            Log.d(TAG, "play: " + index);
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.reset();
        if (mPlayListContent.isEmpty())
            return;
        if (index == PLAY_NEXT_INDEX) {
            if (pointer >= mPlayListContent.size()) {
                pointer = 0;
            } else {
                ++pointer;
            }
        } else if (index == PLAY_PREVIOUS_INDEX) {
            if (pointer < 0) {
                pointer = mPlayListContent.size() - 1;
            } else {
                --pointer;
            }
        } else {
            pointer = index;
            if (pointer >= mPlayListContent.size()) {
                pointer = 0;
            }
        }
        try {
            mMediaPlayer.setDataSource(mPlayListContent.get(pointer).mRtspHighQuility);
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
            mPlayList.setPointer(pointer);
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
            playMusic(mPlayList.getPointer());
            return mPlayList.getPointer();
        }
    };

    final RemoteCallbackList<IPlayMusicServiceCallback> mCallbacks = new RemoteCallbackList<IPlayMusicServiceCallback>();

    private void notifyIndexChanged() {
        final int N = mCallbacks.beginBroadcast();
        for (int i = 0; i < N; i++) {
            try {
                mCallbacks.getBroadcastItem(i).notiftPlayIndexChanged();
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
        mPlayListContent = mPlayList.getPlayList();
    }

}
