
package com.bj4.u2bplayer.service;

import java.io.IOException;
import java.util.ArrayList;

import com.bj4.u2bplayer.PlayList;
import com.bj4.u2bplayer.R;
import com.bj4.u2bplayer.activity.U2bPlayerMainFragmentActivity;
import com.bj4.u2bplayer.utilities.NotificationBuilder;
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
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

public class PlayMusicService extends Service implements PlayList.PlayListLoaderCallback {
    private static final boolean DEBUG = true;

    private static final String TAG = "PlayMusicService";

    public static final int PLAY_NEXT_INDEX = -1;

    public static final int PLAY_PREVIOUS_INDEX = -2;

    private static final int TRACK_WENT_TO_NEXT = 1;

    private MultiPlayer mPlayer;

    private PlayList mPlayList;

    private ArrayList<PlayListInfo> mPlayListContent = new ArrayList<PlayListInfo>();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TRACK_WENT_TO_NEXT:
                    trackNext();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    public void onCreate() {
        super.onCreate();
        if (DEBUG)
            Log.d(TAG, "service oncreate");
        mPlayer = new MultiPlayer();
        mPlayer.setHandler(mHandler);
        mPlayList = PlayList.getInstance(this);
        mPlayList.addCallback(this);
        mPlayListContent = mPlayList.getPlayList();
        startForeground(NotificationBuilder.NOTIFICATION_ID,
                NotificationBuilder.createSimpleNotification(getApplicationContext(), null));
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
        mPlayer.release();
    }

    private void trackNext() {
        PlayListInfo nextInfo = mPlayListContent.get(getNextPointer());
        if (nextInfo == null) {
            nextInfo = mPlayListContent.get(0);
        }
        if (nextInfo != null) {
            mPlayer.setNextDataSource(nextInfo.mRtspHighQuility);
        }
    }

    private int getNextPointer() {
        int pointer = mPlayList.getPointer();
        if (pointer >= mPlayListContent.size()) {
            pointer = 0;
        } else {
            ++pointer;
        }
        return pointer;
    }

    private void pauseMusic() {
        mPlayer.pause();
    }

    private void resumeMusic() {
        mPlayer.start();
    }

    private void playMusic(int index) {
        int pointer = mPlayList.getPointer();
        if (DEBUG)
            Log.d(TAG, "play: " + index);
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
            mPlayer.setDataSource(mPlayListContent.get(pointer).mRtspHighQuility);
            PlayListInfo nextInfo = mPlayListContent.get(pointer + 1);
            if (nextInfo != null) {
                mPlayer.setNextDataSource(nextInfo.mRtspHighQuility);
            }
            mPlayList.setPointer(pointer);
            notifyChanged();
        } catch (Exception e) {
            if (DEBUG)
                Log.w(TAG, "play failed", e);
        }
    }

    private class CompatMediaPlayer extends MediaPlayer implements OnCompletionListener {

        private boolean mCompatMode = true;

        private MediaPlayer mNextPlayer;

        private OnCompletionListener mCompletion;

        public CompatMediaPlayer() {
            try {
                MediaPlayer.class.getMethod("setNextMediaPlayer", MediaPlayer.class);
                mCompatMode = false;
            } catch (NoSuchMethodException e) {
                mCompatMode = true;
                super.setOnCompletionListener(this);
            }
        }

        public void setNextMediaPlayer(MediaPlayer next) {
            if (mCompatMode) {
                mNextPlayer = next;
            } else {
                super.setNextMediaPlayer(next);
            }
        }

        @Override
        public void setOnCompletionListener(OnCompletionListener listener) {
            if (mCompatMode) {
                mCompletion = listener;
            } else {
                super.setOnCompletionListener(listener);
            }
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            if (mNextPlayer != null) {
                SystemClock.sleep(50);
                mNextPlayer.start();
            }
            mCompletion.onCompletion(this);
        }
    }

    private class MultiPlayer {
        private CompatMediaPlayer mCurrentMediaPlayer = new CompatMediaPlayer();

        private CompatMediaPlayer mNextMediaPlayer;

        private Handler mHandler;

        private boolean mIsInitialized = false;

        public MultiPlayer() {
            mCurrentMediaPlayer.setWakeMode(PlayMusicService.this, PowerManager.PARTIAL_WAKE_LOCK);
        }

        public boolean isPlaying() {
            return mCurrentMediaPlayer.isPlaying();
        }

        public void setDataSource(String path) {
            mIsInitialized = setDataSourceImpl(mCurrentMediaPlayer, path, true);
            if (mIsInitialized) {
                setNextDataSource(null);
            }
        }

        private boolean setDataSourceImpl(MediaPlayer player, String path, boolean playAfterSync) {
            try {
                player.reset();
                if (path.startsWith("content://")) {
                    player.setDataSource(PlayMusicService.this, Uri.parse(path));
                } else {
                    player.setDataSource(path);
                }
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                if (playAfterSync) {
                    player.setOnPreparedListener(new OnPreparedListener() {

                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                            notifyChanged();
                        }
                    });
                    player.prepareAsync();
                } else {
                    player.setOnPreparedListener(null);
                    player.prepare();
                }
            } catch (IOException ex) {
                return false;
            } catch (IllegalArgumentException ex) {
                return false;
            }
            player.setOnCompletionListener(listener);
            player.setOnErrorListener(errorListener);
            return true;
        }

        public void setNextDataSource(String path) {
            mCurrentMediaPlayer.setNextMediaPlayer(null);
            if (mNextMediaPlayer != null) {
                mNextMediaPlayer.release();
                mNextMediaPlayer = null;
            }
            if (path == null) {
                return;
            }
            mNextMediaPlayer = new CompatMediaPlayer();
            mNextMediaPlayer.setWakeMode(PlayMusicService.this, PowerManager.PARTIAL_WAKE_LOCK);
            mNextMediaPlayer.setAudioSessionId(getAudioSessionId());
            if (setDataSourceImpl(mNextMediaPlayer, path, false)) {
                mCurrentMediaPlayer.setNextMediaPlayer(mNextMediaPlayer);
            } else {
                mNextMediaPlayer.release();
                mNextMediaPlayer = null;
            }
        }

        public boolean isInitialized() {
            return mIsInitialized;
        }

        public void start() {
            mCurrentMediaPlayer.start();
            notifyPlayStateChanged(true);
        }

        public void stop() {
            mCurrentMediaPlayer.reset();
            mIsInitialized = false;
            notifyPlayStateChanged(false);
        }

        public void release() {
            stop();
            mCurrentMediaPlayer.release();
            notifyPlayStateChanged(false);
        }

        public void pause() {
            mCurrentMediaPlayer.pause();
            notifyPlayStateChanged(false);
        }

        public void setHandler(Handler handler) {
            mHandler = handler;
        }

        MediaPlayer.OnCompletionListener listener = new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                if (mp == mCurrentMediaPlayer && mNextMediaPlayer != null) {
                    mCurrentMediaPlayer.release();
                    mCurrentMediaPlayer = mNextMediaPlayer;
                    mNextMediaPlayer = null;
                    mPlayList.setPointer(getNextPointer());
                    notifyChanged();
                    mHandler.sendEmptyMessage(TRACK_WENT_TO_NEXT);
                }
            }
        };

        MediaPlayer.OnErrorListener errorListener = new MediaPlayer.OnErrorListener() {
            public boolean onError(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                        mIsInitialized = false;
                        mCurrentMediaPlayer.release();
                        mCurrentMediaPlayer = new CompatMediaPlayer();
                        mCurrentMediaPlayer.setWakeMode(PlayMusicService.this,
                                PowerManager.PARTIAL_WAKE_LOCK);
                        return true;
                    default:
                        Log.d(TAG, "Error: " + what + "," + extra);
                        break;
                }
                return false;
            }
        };

        public long duration() {
            return mCurrentMediaPlayer.getDuration();
        }

        public long position() {
            return mCurrentMediaPlayer.getCurrentPosition();
        }

        public long seek(long whereto) {
            mCurrentMediaPlayer.seekTo((int)whereto);
            return whereto;
        }

        public void setVolume(float vol) {
            mCurrentMediaPlayer.setVolume(vol, vol);
        }

        public void setAudioSessionId(int sessionId) {
            mCurrentMediaPlayer.setAudioSessionId(sessionId);
        }

        public int getAudioSessionId() {
            return mCurrentMediaPlayer.getAudioSessionId();
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
            return mPlayer.isPlaying();
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

        @Override
        public boolean isInitialized() throws RemoteException {
            return mPlayer.isInitialized();
        }

        @Override
        public PlayListInfo getCurrentPlayInfo() throws RemoteException {
            return getCurrentPlayingInfo();
        }
    };

    final RemoteCallbackList<IPlayMusicServiceCallback> mCallbacks = new RemoteCallbackList<IPlayMusicServiceCallback>();

    private void notifyChanged() {
        notifyIndexChanged();
        notifyPlayStateChanged(mPlayer.isPlaying());
        notifyPlayInfoChanged();
        notifyNotificationChanged();
    }

    private void notifyNotificationChanged() {
        NotificationBuilder.handleSimpleNotification(getApplicationContext(),
                getCurrentPlayingInfo());
    }

    private void notifyPlayInfoChanged() {
        final int N = mCallbacks.beginBroadcast();
        for (int i = 0; i < N; i++) {
            try {
                mCallbacks.getBroadcastItem(i).notifyPlayInfoChanged(getCurrentPlayingInfo());
            } catch (RemoteException e) {
            }
        }
        mCallbacks.finishBroadcast();
    }

    private void notifyPlayStateChanged(boolean isPlaying) {
        final int N = mCallbacks.beginBroadcast();
        for (int i = 0; i < N; i++) {
            try {
                mCallbacks.getBroadcastItem(i).notifyPlayStateChanged(isPlaying);
            } catch (RemoteException e) {
            }
        }
        mCallbacks.finishBroadcast();
    }

    private void notifyIndexChanged() {
        final int N = mCallbacks.beginBroadcast();
        for (int i = 0; i < N; i++) {
            try {
                mCallbacks.getBroadcastItem(i).notifyPlayIndexChanged();
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

    private PlayListInfo getCurrentPlayingInfo() {
        return mPlayList.getPlayList().get(mPlayList.getPointer());
    }

}
