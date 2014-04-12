
package com.bj4.u2bplayer.activity;

import java.util.ArrayList;

import com.bj4.u2bplayer.PlayList;
import com.bj4.u2bplayer.PlayMusicApplication;
import com.bj4.u2bplayer.service.IPlayMusicService;
import com.bj4.u2bplayer.service.PlayMusicService;
import com.bj4.u2bplayer.utilities.PlayListInfo;
import com.bj4.u2bplayer.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class PlayListView extends RelativeLayout implements PlayList.PlayListLoaderCallback {

    private static final boolean DEBUG = true && PlayMusicApplication.OVERALL_DEBUG;
    private static final String TAG = "QQQQ";
    private static final int VIEW_SWITCHER_PLAY = 0;
    private static final int VIEW_SWITCHER_PAUSE = 1;

    private Context mContext;
    private ListView mPlayListContent;
    private LayoutInflater mLayoutInflater;
    private PlayListContentAdapter mPlayListAdapter;
    private ArrayList<PlayListInfo> mPlayList = new ArrayList<PlayListInfo>();
    private TextView mTitle;
    private ViewSwitcher mPlayOrPause;
    private ImageView mPlay, mPlayNext, mPlayPrevious, mPause;
    private Handler mHandler = new Handler();
    private MediaPlayer mMediaPlayer;
    private IPlayMusicService mService;
    private PlayList mLoader;

    public PlayListView(Context context) {
        this(context, null);
    }

    public PlayListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        mLoader = PlayList.getInstance();
        mPlayList = mLoader.getPlayList();
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mLoader.addCallback(this);
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mLoader.removeCallback(this);
    }

    private void switchPlayAndPause(int index) {
        if (mPlayOrPause != null) {
            if (mPlayOrPause.getDisplayedChild() == index) {
                return;
            } else {
                mPlayOrPause.setDisplayedChild(index);
            }
        }
    }

    public void setService(IPlayMusicService service) {
        mService = service;
        mPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mPlayOrPause != null) {
                        if (mPlayOrPause.getDisplayedChild() == VIEW_SWITCHER_PLAY) {
                            mService.resume();
                        } else {
                            mService.play(PlayMusicService.PLAY_NEXT_INDEX);
                        }
                        switchPlayAndPause(VIEW_SWITCHER_PAUSE);
                    }
                } catch (RemoteException e) {
                }
            }
        });
        mPlayNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mService.next();
                    switchPlayAndPause(VIEW_SWITCHER_PAUSE);
                } catch (RemoteException e) {
                }
            }
        });
        mPlayPrevious.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mService.previous();
                    switchPlayAndPause(VIEW_SWITCHER_PAUSE);
                } catch (RemoteException e) {
                }
            }
        });
        mPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mService.pause();
                    switchPlayAndPause(VIEW_SWITCHER_PLAY);
                } catch (RemoteException e) {
                }
            }
        });
    }

    private void setTitleIfNeeded() {
        if (mTitle != null && mLoader != null && mTitle.getText().equals("")) {
            mTitle.setText(mLoader.getPlayListTitle());
        }
    }

    public void onFinishInflate() {
        super.onFinishInflate();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mTitle = (TextView) findViewById(R.id.playlist_title);
        setTitleIfNeeded();
        mPlay = (ImageView) findViewById(R.id.playlist_play);
        mPlayNext = (ImageView) findViewById(R.id.playlist_play_next);
        mPlayPrevious = (ImageView) findViewById(R.id.playlist_play_previous);
        mPause = (ImageView) findViewById(R.id.playlist_pause);
        mPlayOrPause = (ViewSwitcher) findViewById(R.id.playlist_play_or_pause);
        mPlayOrPause.setOutAnimation(mContext, R.anim.view_switch_anim_out);
        mPlayOrPause.setInAnimation(mContext, R.anim.view_switch_anim_in);

        mLayoutInflater = LayoutInflater.from(mContext);
        mPlayListAdapter = new PlayListContentAdapter();
        mPlayListContent = (ListView) findViewById(R.id.playlist_content);
        mPlayListContent.setSelector(R.drawable.playlist_item_bg);
        mPlayListContent.setAdapter(mPlayListAdapter);
        mPlayListContent.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                try {
                    mService.play(position);
                    switchPlayAndPause(VIEW_SWITCHER_PAUSE);
                } catch (RemoteException e) {
                    if (DEBUG)
                        Log.e(TAG, "fail", e);
                }
            }
        });
    }

    private void refreshAdapter() {
        // ask to refresh
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                setTitleIfNeeded();
                mPlayListAdapter.notifyDataSetChanged();
            }
        });
    }

    class PlayListContentAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mPlayList.size();
        }

        @Override
        public PlayListInfo getItem(int position) {
            return mPlayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View contentView, ViewGroup parent) {
            ViewHolder holder = null;
            if (contentView == null) {
                contentView = mLayoutInflater.inflate(R.layout.play_list_content, parent, false);
                holder = new ViewHolder();
//                holder.mPlayTitle = (TextView) contentView
//                        .findViewById(R.id.playlist_content_playtitle);
                contentView.setTag(holder);
            } else {
                holder = (ViewHolder) contentView.getTag();
            }
//            holder.mPlayTitle.setText(mPlayList.get(position).mMusicTitle);
            return contentView;
        }

        private class ViewHolder {
            TextView mPlayTitle;
        }
    }

    @Override
    public void loadDone() {
        mPlayList = mLoader.getPlayList();
        refreshAdapter();
    }

}
