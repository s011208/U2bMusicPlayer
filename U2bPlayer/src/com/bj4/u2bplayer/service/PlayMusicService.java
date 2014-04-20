
package com.bj4.u2bplayer.service;

import java.io.IOException;
import java.util.ArrayList;

import com.bj4.u2bplayer.PlayList;
import com.bj4.u2bplayer.utilities.NotificationBuilder;
import com.bj4.u2bplayer.utilities.PlayListInfo;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
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
        PlayListInfo nextInfo = mPlayList.getNextPlayListInfo();
        if (nextInfo != null) {
            mPlayer.setNextDataSource(nextInfo.mRtspHighQuility, nextInfo);
        }
    }

    private int getNextPointer() {
        return mPlayList.getNextPointer();
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
            PlayListInfo currentInfo = mPlayListContent.get(pointer);
            mPlayer.setDataSource(currentInfo.mRtspHighQuility, currentInfo);
            PlayListInfo nextInfo = mPlayListContent.get(pointer + 1);
            if (nextInfo != null) {
                mPlayer.setNextDataSource(nextInfo.mRtspHighQuility, nextInfo);
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

        private PlayListInfo mPlayListInfo;

        public CompatMediaPlayer() {
            try {
                MediaPlayer.class.getMethod("setNextMediaPlayer", MediaPlayer.class);
                mCompatMode = false;
            } catch (NoSuchMethodException e) {
                mCompatMode = true;
                super.setOnCompletionListener(this);
            }
        }

        public void setPlayListInfo(PlayListInfo info) {
            mPlayListInfo = info;
        }

        public PlayListInfo getPlayListInfo() {
            return mPlayListInfo;
        }

        public void start() {
            super.start();
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

        public void setDataSource(String path, PlayListInfo info) {
            mIsInitialized = setDataSourceImpl(info, mCurrentMediaPlayer, path, true);
            if (mIsInitialized) {
                setNextDataSource(null, null);
            }
        }

        private boolean setDataSourceImpl(PlayListInfo info, MediaPlayer player, String path,
                boolean playAfterSync) {
            try {
                player.reset();
                if (info != null && player instanceof CompatMediaPlayer) {
                    ((CompatMediaPlayer)player).setPlayListInfo(info);
                }
                if (path.startsWith("content://")) {
                    player.setDataSource(PlayMusicService.this, Uri.parse(path));
                } else {
                    player.setDataSource(path);
                }
                player.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

                    @Override
                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
                        if (DEBUG) {
                            String debug = "";
                            if (mp instanceof CompatMediaPlayer) {
                                PlayListInfo info = ((CompatMediaPlayer)mp).getPlayListInfo();
                                if (info != null) {
                                    debug += "title: " + info.mMusicTitle + ", ";
                                }
                                if (percent == 100) {
                                    notifyPlayInfoChanged();
                                    notifyPlayTimeUpdated(mp.getDuration());
                                } else {
                                    notifyBufferingStatus(info, percent);
                                }
                            }
                            Log.d(TAG,
                                    debug + "percent: " + percent + ", duration: "
                                            + PlayList.getTimeString(mp.getDuration()));
                        }
                    }
                });
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                if (playAfterSync) {
                    player.setOnPreparedListener(new OnPreparedListener() {

                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            if (DEBUG) {
                                Log.i(TAG, "onPrepared");
                            }
                            mp.start();
                            notifyChanged();
                        }
                    });
                    player.prepareAsync();
                } else {
                    player.setOnPreparedListener(null);
                    player.prepareAsync();
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

        public void setNextDataSource(String path, PlayListInfo info) {
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
            if (setDataSourceImpl(info, mNextMediaPlayer, path, false)) {
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
            return mPlayList.getCurrentPlayListInfo();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return (int)mPlayer.position();
        }

        @Override
        public long getDuration() throws RemoteException {
            return mPlayer.duration();
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
                mPlayList.getCurrentPlayListInfo());
    }

    private void notifyPlayInfoChanged() {
        final int N = mCallbacks.beginBroadcast();
        for (int i = 0; i < N; i++) {
            try {
                mCallbacks.getBroadcastItem(i).notifyPlayInfoChanged(
                        mPlayList.getCurrentPlayListInfo());
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

    private void notifyBufferingStatus(PlayListInfo info, int percentage) {
        final int N = mCallbacks.beginBroadcast();
        for (int i = 0; i < N; i++) {
            try {
                mCallbacks.getBroadcastItem(i).updateBufferingPercentage(info, percentage);
            } catch (RemoteException e) {
            }
        }
        mCallbacks.finishBroadcast();
    }

    private void notifyPlayTimeUpdated(int time) {
        final int N = mCallbacks.beginBroadcast();
        for (int i = 0; i < N; i++) {
            try {
                mCallbacks.getBroadcastItem(i).updatePlayingTime(time);
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
