
package com.bj4.u2bplayer.activity.fragments;

import com.bj4.u2bplayer.PlayList;
import com.bj4.u2bplayer.R;
import com.bj4.u2bplayer.activity.U2bPlayerMainFragmentActivity;
import com.bj4.u2bplayer.activity.U2bPlayerMainFragmentActivity.MainFragmentCallback;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class U2bPlayListFragment extends Fragment implements MainFragmentCallback {
    public static final String TAG = "U2bPlayListFragment";

    public static final boolean DEBUG = true;

    private View mContentView;

    private RelativeLayout mControllPanel;

    private PlayList mPlayList;

    private ListView mPlayListView;

    private PlayListAdapter mPlayListAdapter;

    private LayoutInflater mLayoutInflater;

    private U2bPlayerMainFragmentActivity mActivity;

    private ImageView mPlay, mPause, mPlayNext, mPlayPrevious;

    private ViewSwitcher mPlayOrPause;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initComponents() {
        if (mContentView != null) {
            mActivity = (U2bPlayerMainFragmentActivity)getActivity();
            mPlayList = PlayList.getInstance(mActivity);
            mLayoutInflater = LayoutInflater.from(mActivity);
            if (DEBUG) {
                Log.d(TAG, "" + mPlayList.getDisplayList().size());
            }
            mControllPanel = (RelativeLayout)mContentView
                    .findViewById(R.id.play_list_controll_panel);
            mPlayListView = (ListView)mContentView.findViewById(R.id.play_list_view);
            mPlayListAdapter = new PlayListAdapter();
            mPlayListView.setSelector(R.color.transparent);
            mPlayListView.setAdapter(mPlayListAdapter);
            mPlayListView.setSelection(mPlayList.getPointer());
            mPlayListView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                    v.setHapticFeedbackEnabled(true);
                    // mActivity.viewPlayInfo(mPlayList.getPlayList().get(position));
                    int index = mPlayList.getPointer();
                    if (DEBUG) {
                        Log.i(TAG, "getDisplayListAlbumId(): " + mPlayList.getDisplayListAlbumId()
                                + ", getPlayingAlbumId(): " + mActivity.getPlayingAlbumId());
                    }
                    if ((index != position || mPlayList.getDisplayListAlbumId() != mActivity
                            .getPlayingAlbumId()) || mActivity.isInitialized() == false) {
                        mActivity.play(position);
                    }
                }
            });
            mPlay = (ImageView)mContentView.findViewById(R.id.play_list_play);
            mPlay.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    v.setHapticFeedbackEnabled(true);
                    if (mActivity.isInitialized() == false) {
                        int index = mActivity.playFromLastTime();
                        if (index != -1) {
                            mPlayListView.smoothScrollToPosition(index);
                        }
                    } else {
                        mActivity.resumePlay();
                    }
                    mPlayOrPause.setDisplayedChild(1);
                }
            });
            mPlayNext = (ImageView)mContentView.findViewById(R.id.play_list_play_next);
            mPlayNext.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    v.setHapticFeedbackEnabled(true);
                    mActivity.playNext();
                }
            });
            mPlayPrevious = (ImageView)mContentView.findViewById(R.id.play_list_play_previous);
            mPlayPrevious.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    v.setHapticFeedbackEnabled(true);
                    mActivity.playPrevious();
                }
            });
            mPause = (ImageView)mContentView.findViewById(R.id.play_list_pause);
            mPause.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    v.setHapticFeedbackEnabled(true);
                    mActivity.pause();
                    mPlayOrPause.setDisplayedChild(0);
                }
            });
            mPlayOrPause = (ViewSwitcher)mContentView.findViewById(R.id.play_list_play_or_pause);
            mPlayOrPause.setDisplayedChild(mActivity.isPlaying() ? 1 : 0);
        }
    }

    public void onStart() {
        super.onStart();
        mActivity.addCallback(this);
        initTheme();
    }

    public void onStop() {
        super.onStop();
        mActivity.removeCallback(this);
    }

    public void setPlayOrPause(boolean isPlaying) {
        if (mPlayOrPause != null) {
            mPlayOrPause.setDisplayedChild(isPlaying ? 1 : 0);
        }
    }

    private void initTheme() {
        int theme = mActivity.getApplicationTheme();
        if (theme == U2bPlayerMainFragmentActivity.THEME_BLUE) {
            mControllPanel.setBackgroundResource(R.color.theme_blue_action_bar_bg);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.play_list_fragment, container, false);
        initComponents();
        return mContentView;
    }

    public void changePlayIndex() {
        if (mPlayListAdapter != null) {
            mPlayListAdapter.notifyDataSetChanged();
        }
    }

    public void updateListContent() {
        if (mPlayListAdapter != null) {
            mPlayListAdapter.notifyDataSetChanged();
            mPlayListView.smoothScrollToPosition(mPlayList.getPointer());
        }
    }

    private class PlayListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mPlayList.getDisplayList().size();
        }

        @Override
        public Object getItem(int position) {
            return mPlayList.getDisplayList().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View contentView, ViewGroup parent) {
            ViewHolder holder = null;
            if (contentView == null) {
                contentView = mLayoutInflater.inflate(R.layout.play_list_content, parent, false);
                holder = new ViewHolder();
                holder.mThumbnail = (ImageView)contentView.findViewById(R.id.play_list_thumbnail);
                holder.mArtist = (TextView)contentView.findViewById(R.id.play_list_artist);
                holder.mMusic = (TextView)contentView.findViewById(R.id.play_list_music);
                contentView.setTag(holder);
            } else {
                holder = (ViewHolder)contentView.getTag();
            }
            holder.mArtist.setText(mPlayList.getDisplayList().get(position).mArtist);
            holder.mMusic.setText(mPlayList.getDisplayList().get(position).mMusicTitle);
            initTheme(contentView, position);
            return contentView;
        }

        private void initTheme(final View contentView, final int position) {
            int theme = mActivity.getApplicationTheme();
            if (theme == U2bPlayerMainFragmentActivity.THEME_BLUE) {
                if (position == mPlayList.getPointer()) {
                    contentView
                            .setBackgroundResource(R.drawable.theme_blue_list_selected_item_oval_bg);
                } else {
                    contentView
                            .setBackgroundResource(position % 2 == 0 ? R.drawable.theme_blue_list_dark_oval_bg
                                    : R.drawable.theme_blue_list_light_oval_bg);
                }
            }
        }

        private class ViewHolder {
            ImageView mThumbnail;

            TextView mArtist;

            TextView mMusic;
        }
    }
}
