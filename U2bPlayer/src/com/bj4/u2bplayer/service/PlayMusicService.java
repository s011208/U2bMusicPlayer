
package com.bj4.u2bplayer.service;

import java.io.IOException;
import java.util.ArrayList;

import com.bj4.u2bplayer.PlayList;
import com.bj4.u2bplayer.PlayMusicApplication;
import com.bj4.u2bplayer.R;
import com.bj4.u2bplayer.dialogs.SleepModeDialog;
import com.bj4.u2bplayer.utilities.NotificationBuilder;
import com.bj4.u2bplayer.utilities.PlayListInfo;
import com.bj4.u2bplayer.widget.SimplePlayWidget;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class PlayMusicService extends Service implements PlayList.PlayListLoaderCallback {
    private static final boolean DEBUG = true;

    private static final String TAG = "PlayMusicService";

    public static final int PLAY_NEXT_INDEX = -1;

    public static final int PLAY_PREVIOUS_INDEX = -2;

    private static final int TRACK_WENT_TO_NEXT = 1;

    public static final String INTENT_PLAY_INDEX = "play_index";

    public static final String INTENT_ACTION = "actions";

    public static final String INTENT_ACTION_PLAY = "play";

    public static final String INTENT_SWITCH_FAVORITE = "favorite";

    public static final String INTENT_ACTION_PAUSE = "pause";

    public static final String INTENT_ACTION_EXIT = "exit";

    private MultiPlayer mPlayer;

    private PlayList mPlayList;

    private boolean mIsForeground = false;

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

    private static final HandlerThread sWorkerThread = new HandlerThread("PlayMusicService-player");
    static {
        sWorkerThread.start();
        sWorkerThread.setPriority(Thread.MAX_PRIORITY);
    }

    private static final Handler sWorker = new Handler(sWorkerThread.getLooper());

    private boolean headsetConnected = false;

    private int mPlayingAlbumId;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            NotificationManager nm = (NotificationManager)context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            String action = intent.getAction();
            if (Intent.ACTION_HEADSET_PLUG.equals(action) && mPlayer != null) {
                if (PlayMusicApplication.sShowNotificationWhenHeadsetOn == false)
                    return;
                if (intent.hasExtra("state")) {
                    if (headsetConnected && intent.getIntExtra("state", 0) == 0) {
                        headsetConnected = false;
                        if (mPlayer.isPlaying()) {
                            pauseMusic();
                        }
                        nm.cancel(NotificationBuilder.RECOMMAND_START_APP_NOTIFICATION_ID);
                    } else if (!headsetConnected && intent.getIntExtra("state", 0) == 1) {
                        headsetConnected = true;
                        if (!mIsForeground) {
                            nm.notify(null,
                                    NotificationBuilder.RECOMMAND_START_APP_NOTIFICATION_ID,
                                    NotificationBuilder.createHeadSetConnectedNotification(context));
                        } else {
                            nm.cancel(NotificationBuilder.RECOMMAND_START_APP_NOTIFICATION_ID);
                        }
                    }
                }
            } else if (Intent.ACTION_NEW_OUTGOING_CALL.equals(action)) {
                if (mIsForeground) {
                    if (mPlayer.isPlaying()) {
                        pauseMusic();
                    }
                }
            } else if (Intent.ACTION_CALL.equals(action)) {
                if (mIsForeground) {
                    if (mPlayer.isPlaying()) {
                        pauseMusic();
                    }
                }
            }
        }
    };

    private class MyPhoneStateListener extends PhoneStateListener {
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    if (mIsForeground) {
                        if (mPlayer.isPlaying() == false) {
                            resumeMusic();
                        }
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    break;
                default:
                    break;
            }
        }
    }

    private MyPhoneStateListener mPhoneListener = new MyPhoneStateListener();

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        filter.addAction(Intent.ACTION_CALL);
        registerReceiver(mReceiver, filter);
        TelephonyManager tmgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        tmgr.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    private void unRegisterBroadcastReceiver() {
        unregisterReceiver(mReceiver);
        TelephonyManager tmgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        tmgr.listen(mPhoneListener, PhoneStateListener.LISTEN_NONE);
    }

    public void onCreate() {
        super.onCreate();
        if (DEBUG)
            Log.d(TAG, "service oncreate");
        mPlayer = new MultiPlayer();
        mPlayer.setHandler(mHandler);
        mPlayList = PlayList.getInstance(this);
        mPlayList.addCallback(this);
        registerBroadcastReceiver();
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (DEBUG)
            Log.d(TAG, "onStartCommand");
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String action = extras.getString(INTENT_ACTION);
                if (action != null) {
                    if (INTENT_ACTION_PLAY.equals(action)) {
                        int index = extras.getInt(INTENT_PLAY_INDEX);
                        if (index >= 0) {
                            if (mPlayer.isInitialized()) {
                                mPlayer.start();
                            } else {
                                playMusic(index);
                            }
                        } else {
                            playMusic(index);
                        }
                    } else if (INTENT_SWITCH_FAVORITE.equals(action)) {
                        switchFavorite();
                    } else if (INTENT_ACTION_PAUSE.equals(action)) {
                        pauseMusic();
                    } else if (INTENT_ACTION_EXIT.equals(action)) {
                        stopPlayService();
                    }
                }
            }
        }
        return Service.START_STICKY;
    }

    private void switchFavorite() {
        if (mPlayList == null)
            return;
        PlayListInfo info = mPlayList.getCurrentPlayingListInfo();
        if (info == null)
            return;
        info.mIsFavorite = !info.mIsFavorite;
        if (!info.mIsFavorite) {
            Toast.makeText(this, info.mMusicTitle + getString(R.string.toast_remove_from_favorite),
                    Toast.LENGTH_SHORT).show();
            PlayMusicApplication.getDataBaseHelper().removeFromFavorite(info);
        } else {
            Toast.makeText(this, info.mMusicTitle + getString(R.string.toast_add_into_favorite),
                    Toast.LENGTH_SHORT).show();
            PlayMusicApplication.getDataBaseHelper().addIntoFavorite(info);
        }

        notifyFavoriteChange();
    }

    public void onDestroy() {
        super.onDestroy();
        if (DEBUG)
            Log.d(TAG, "onDestroy");
        mPlayList.removeCallback(this);
        mPlayer.release();
        unRegisterBroadcastReceiver();
    }

    private Runnable mTrackNextRunnable = new Runnable() {

        @Override
        public void run() {
            PlayListInfo nextInfo = mPlayList.getNextPlayingListInfo();
            if (nextInfo != null) {
                String dataSource = PlayMusicApplication.sUsingHighQuality ? nextInfo.mRtspHighQuility
                        : nextInfo.mRtspLowQuility;
                mPlayer.setNextDataSource(dataSource, nextInfo);
            }
        }
    };

    private void trackNext() {
        sWorker.removeCallbacks(mTrackNextRunnable);
        sWorker.post(mTrackNextRunnable);
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

    private void stopPlayService() {
        stopForeground(true);
        try {
            mPlayer.stop(true);
        } catch (Exception e) {
            if (DEBUG)
                Log.w(TAG, "exit app failed", e);
        }
        stopSelf();
    }

    private class PlayMusicRunnable implements Runnable {
        private int mIndex;

        public PlayMusicRunnable(int index) {
            mIndex = index;
        }

        @Override
        public void run() {
            try {
                ArrayList<PlayListInfo> playList = mPlayList.getPlayingList();
                int pointer = 0;
                if (DEBUG)
                    Log.d(TAG, "play: " + mIndex);
                if (playList.isEmpty())
                    return;
                if (mIndex == PLAY_NEXT_INDEX) {
                    pointer = mPlayList.getNextPointer();
                } else if (mIndex == PLAY_PREVIOUS_INDEX) {
                    pointer = mPlayList.getPreviousPointer();
                } else {
                    pointer = mIndex;
                    if (pointer >= playList.size() || pointer < 0) {
                        pointer = 0;
                    }
                }
                try {
                    PlayListInfo currentInfo = playList.get(pointer);
                    String dataSource = PlayMusicApplication.sUsingHighQuality ? currentInfo.mRtspHighQuility
                            : currentInfo.mRtspLowQuility;
                    mPlayer.setDataSource(dataSource, currentInfo);
                    PlayListInfo nextInfo = playList.get(pointer + 1);
                    if (nextInfo != null) {
                        dataSource = PlayMusicApplication.sUsingHighQuality ? nextInfo.mRtspHighQuility
                                : nextInfo.mRtspLowQuility;
                        mPlayer.setNextDataSource(dataSource, nextInfo);
                    }
                } catch (Exception e) {
                    if (DEBUG)
                        Log.w(TAG, "play failed", e);
                } finally {
                    mPlayingAlbumId = PlayMusicApplication.getDataBaseHelper().getAlbumId(
                            mPlayList.getCurrentPlayingListInfo().mAlbumTitle);
                    mPlayList.setPointer(pointer);
                    notifyChanged();
                }
            } catch (Exception e) {
                Log.w(TAG, "play failed", e);
            }
        }
    }

    private Runnable mPlayMusicRunnable;

    private void playMusic(final int index) {
        if (mPlayMusicRunnable != null) {
            sWorker.removeCallbacks(mPlayMusicRunnable);
        }
        mPlayMusicRunnable = new PlayMusicRunnable(index);
        sWorker.post(mPlayMusicRunnable);
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
            try {
                if (mCompatMode) {
                    mNextPlayer = next;
                } else {
                    super.setNextMediaPlayer(next);
                }
            } catch (Exception e) {
                Log.w(TAG, "failed to setNextMediaPlayer", e);
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
            try {
                return mCurrentMediaPlayer.isPlaying();
            } catch (Exception e) {
                Log.w(TAG, "failed", e);
                return false;
            }
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
                    player.setOnPreparedListener(new OnPreparedListener() {

                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mCurrentMediaPlayer.setNextMediaPlayer(mp);
                            if (DEBUG) {
                                Log.d(TAG, "setNextMediaPlayer onPrepared");
                            }
                        }
                    });
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
            try {
                mNextMediaPlayer.setAudioSessionId(getAudioSessionId());
            } catch (Exception e) {
                Log.w(TAG, "failed", e);
            }
            if (setDataSourceImpl(info, mNextMediaPlayer, path, false)) {
                // set next at setDataSourceImpl
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
            notifiWidgetsChanged(true);
            notifyNotificationChanged(true);
        }

        public void stop() {
            stop(false);
        }

        public void stop(boolean forceStopService) {
            mCurrentMediaPlayer.reset();
            mIsInitialized = false;
            notifyPlayStateChanged(false);
            notifiWidgetsChanged(false);
            notifyNotificationChanged(false, true);
        }

        public void release() {
            stop();
            mCurrentMediaPlayer.release();
            notifyPlayStateChanged(false);
            notifiWidgetsChanged(false);
            notifyNotificationChanged(false);
        }

        public void pause() {
            mCurrentMediaPlayer.pause();
            notifyPlayStateChanged(false);
            notifiWidgetsChanged(false);
            notifyNotificationChanged(false);
        }

        public void setHandler(Handler handler) {
            mHandler = handler;
        }

        MediaPlayer.OnCompletionListener listener = new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                Log.w(TAG,
                        "onCompletion, (mp == mCurrentMediaPlayer && mNextMediaPlayer != null): "
                                + (mp == mCurrentMediaPlayer && mNextMediaPlayer != null));
                if (mp == mCurrentMediaPlayer && mNextMediaPlayer != null) {
                    mCurrentMediaPlayer.release();
                    mCurrentMediaPlayer = mNextMediaPlayer;
                    mNextMediaPlayer = null;
                    mPlayList.setPointer(getNextPointer());
                    notifyChanged();
                    mHandler.sendEmptyMessage(TRACK_WENT_TO_NEXT);
                } else {
                    playMusic(getNextPointer());
                }
            }
        };

        MediaPlayer.OnErrorListener errorListener = new MediaPlayer.OnErrorListener() {
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.w(TAG, "Error: " + what + "," + extra);
                switch (what) {
                    case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                        mIsInitialized = false;
                        mCurrentMediaPlayer.release();
                        mCurrentMediaPlayer = new CompatMediaPlayer();
                        mCurrentMediaPlayer.setWakeMode(PlayMusicService.this,
                                PowerManager.PARTIAL_WAKE_LOCK);
                        return false;
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
            return mPlayList.getCurrentPlayingListInfo();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return (int)mPlayer.position();
        }

        @Override
        public long getDuration() throws RemoteException {
            return mPlayer.duration();
        }

        @Override
        public long getPlayingAlbumId() throws RemoteException {
            return mPlayingAlbumId;
        }

        @Override
        public void notifyFavoriteChanged() throws RemoteException {
            notifyFavoriteChange();
        }

        @Override
        public void sleepMode(int type) throws RemoteException {
            // TODO Auto-generated method stub
            mHandler.removeCallbacks(mSleepMode);
            int postTime = 0;
            switch (type) {
                case SleepModeDialog.DISABLE:
                    return;
                case SleepModeDialog.MINS_15:
                    postTime = 900000;
                    break;
                case SleepModeDialog.MINS_30:
                    postTime = 1800000;
                    break;
                case SleepModeDialog.HOUR_1:
                    postTime = 3600000;
                    break;
                case SleepModeDialog.HOUR_2:
                    postTime = 7200000;
                    break;
                case SleepModeDialog.HOUR_3:
                    postTime = 10800000;
                    break;
            }
            mHandler.postDelayed(mSleepMode, postTime);
        }

    };

    private Runnable mSleepMode = new Runnable() {

        @Override
        public void run() {
            stopPlayService();
        }
    };

    final RemoteCallbackList<IPlayMusicServiceCallback> mCallbacks = new RemoteCallbackList<IPlayMusicServiceCallback>();

    private synchronized void notifyChanged() {
        notifyIndexChanged();
        notifyPlayStateChanged(mPlayer.isPlaying());
        notifyPlayInfoChanged();
        notifyNotificationChanged(mPlayer.isPlaying());
        notifiWidgetsChanged(mPlayer.isPlaying());
    }

    private void notifyNotificationChanged(boolean isPlaying) {
        notifyNotificationChanged(isPlaying, false);
    }

    private void notifyNotificationChanged(boolean isPlaying, boolean forceStopService) {
        final PlayListInfo info = mPlayList.getCurrentPlayingListInfo();
        if (info == null)
            return;
        if (forceStopService) {
            mIsForeground = false;
        } else {
            startForeground(NotificationBuilder.NOTIFICATION_ID,
                    NotificationBuilder.createSimpleNotification(getApplicationContext(), info,
                            isPlaying));
            NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(NotificationBuilder.RECOMMAND_START_APP_NOTIFICATION_ID);
            mIsForeground = true;
        }
    }

    private void notifyFavoriteChange() {
        mPlayList.reloadCurrentPlayingList();
        notifiWidgetsChanged(mPlayer.isPlaying());
        final int N = mCallbacks.beginBroadcast();
        for (int i = 0; i < N; i++) {
            try {
                mCallbacks.getBroadcastItem(i).askToReloadDisplayList();
            } catch (RemoteException e) {
            }
        }
        mCallbacks.finishBroadcast();
    }

    private void notifiWidgetsChanged(boolean isPlaying) {
        final PlayListInfo info = mPlayList.getCurrentPlayingListInfo();
        if (info == null)
            return;
        SimplePlayWidget.performUpdate(this, info, isPlaying);
    }

    private void notifyPlayInfoChanged() {
        final int N = mCallbacks.beginBroadcast();
        for (int i = 0; i < N; i++) {
            try {
                mCallbacks.getBroadcastItem(i).notifyPlayInfoChanged(
                        mPlayList.getCurrentPlayingListInfo());
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
    }
}
