
package com.yenhsun.u2bplayer;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
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

public class PlayListView extends RelativeLayout implements PlayListLoader.PlayListLoaderCallback {
    private Context mContext;

    private ListView mPlayListContent;
    private LayoutInflater mLayoutInflater;
    private PlayListContentAdapter mPlayListAdapter;
    private ArrayList<PlayListInfo> mPlayList = new ArrayList<PlayListInfo>();
    private TextView mTitle;
    private ImageView mPlay, mPlayNext, mPlayPrevious;
    private Handler mHandler = new Handler();
    private MediaPlayer mMediaPlayer;
    private IPlayMusicService mService;
    private PlayListLoader mLoader;

    public PlayListView(Context context) {
        this(context, null);
    }

    public PlayListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        mLoader = PlayListLoader.getInstance(mContext);
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

    public void setService(IPlayMusicService service) {
        mService = service;
        mPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mService.play(PlayMusicService.PLAY_NEXT_INDEX);
                } catch (RemoteException e) {
                }
            }
        });
        mPlayNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mService.next();
                } catch (RemoteException e) {
                }
            }
        });
        mPlayPrevious.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mService.previous();
                } catch (RemoteException e) {
                }
            }
        });
    }

    public void onFinishInflate() {
        super.onFinishInflate();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mTitle = (TextView) findViewById(R.id.playlist_title);
        mPlay = (ImageView) findViewById(R.id.playlist_play);
        mPlayNext = (ImageView) findViewById(R.id.playlist_play_next);

        mPlayPrevious = (ImageView) findViewById(R.id.playlist_play_previous);

        mLayoutInflater = LayoutInflater.from(mContext);
        mPlayListAdapter = new PlayListContentAdapter();
        mPlayListContent = (ListView) findViewById(R.id.playlist_content);
        mPlayListContent.setAdapter(mPlayListAdapter);
        mPlayListContent.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    mService.play(position);
                } catch (RemoteException e) {
                    Log.e("QQQQ", "fail", e);
                }
            }
        });
    }

    private void refreshAdapter() {
        // ask to refresh
        mHandler.post(new Runnable() {
            @Override
            public void run() {
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
                holder.mPlayTitle = (TextView) contentView
                        .findViewById(R.id.playlist_content_playtitle);
                contentView.setTag(holder);
            } else {
                holder = (ViewHolder) contentView.getTag();
            }
            holder.mPlayTitle.setText(mPlayList.get(position).mMusicTitle);
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
