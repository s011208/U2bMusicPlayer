
package com.bj4.u2bplayer.activity.fragments;

import java.util.ArrayList;

import com.bj4.u2bplayer.PlayList;
import com.bj4.u2bplayer.PlayMusicApplication;
import com.bj4.u2bplayer.R;
import com.bj4.u2bplayer.activity.ThemeReloader;
import com.bj4.u2bplayer.activity.U2bPlayerMainFragmentActivity;
import com.bj4.u2bplayer.activity.U2bPlayerMainFragmentActivity.MainFragmentCallback;
import com.bj4.u2bplayer.utilities.PlayListInfo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class U2bPlayListFragment extends Fragment implements MainFragmentCallback, ThemeReloader {
    public static final String TAG = "U2bPlayListFragment";

    public static final boolean DEBUG = true;

    private static final String TABLE_FAVORITE = "favorite";

    private static final String TABLE_ALBUM_INFO = "album_info";

    private static final int HOLO_LIGHT_BLUE = 0xff3366ff;

    private View mContentView;

    private RelativeLayout mControllPanel;

    private PlayList mPlayList;

    private ListView mPlayListView;

    private PlayListAdapter mPlayListAdapter;

    private LayoutInflater mLayoutInflater;

    private U2bPlayerMainFragmentActivity mActivity;

    private ImageView mPlay, mPause, mPlayNext, mPlayPrevious;

    private ViewSwitcher mPlayOrPause;

    private static String sDisplayAlbumName;

    private static int sDisplayAlbumId;

    private ArrayList<PlayListInfo> mDisplayList = new ArrayList<PlayListInfo>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void reloadDisplayList() {
        if (mActivity == null)
            return;
        final String albumName = mActivity.getDisplayingAlbumName();
        if (albumName != null && !albumName.isEmpty()) {
            // keep previous status
            sDisplayAlbumName = albumName;
            sDisplayAlbumId = PlayMusicApplication.getDataBaseHelper()
                    .getAlbumId(sDisplayAlbumName);
            mDisplayList.clear();
            mDisplayList.addAll(PlayMusicApplication.getDataBaseHelper().getPlayList(
                    sDisplayAlbumName, true));
        }
        if (mPlayListAdapter != null) {
            mPlayListAdapter.notifyDataSetChanged();
        }
    }

    private void initComponents() {
        if (mContentView != null) {
            mActivity = (U2bPlayerMainFragmentActivity)getActivity();
            reloadDisplayList();
            mPlayList = PlayList.getInstance(mActivity);
            mLayoutInflater = LayoutInflater.from(mActivity);
            mControllPanel = (RelativeLayout)mContentView
                    .findViewById(R.id.play_list_controll_panel);
            mPlayListView = (ListView)mContentView.findViewById(R.id.play_list_view);
            View blankView = mLayoutInflater.inflate(R.layout.play_list_content_blank_view, null);
            mPlayListView.addFooterView(blankView);
            mPlayListAdapter = new PlayListAdapter();
            mPlayListView.setSelector(R.color.transparent);
            mPlayListView.setAdapter(mPlayListAdapter);
            if (isPlayingList()) {
                mPlayListView.setSelection(mPlayList.getPointer());
            }
            mPlayListView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                    mActivity.countInterstitial();
                    v.setHapticFeedbackEnabled(true);
                    mPlayList.setAlbumPlayingList(sDisplayAlbumName);
                    // mActivity.viewPlayInfo(mPlayList.getPlayList().get(position));
                    int index = mPlayList.getPointer();
                    if (DEBUG) {
                        Log.i(TAG, "getDisplayListAlbumId(): " + mPlayList.getPlayingListAlbumId()
                                + ", getPlayingAlbumId(): " + mActivity.getPlayingAlbumId());
                    }
                    if ((index != position || mPlayList.getPlayingListAlbumId() != mActivity
                            .getPlayingAlbumId()) || mActivity.isInitialized() == false) {
                        mActivity.play(position);
                    }
                }
            });
            mPlayListView.setOnItemLongClickListener(new OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
                        long id) {
                    PlayListInfo info = mDisplayList.get(position);
                    if (info != null) {
                        if (info.mIsFavorite) {
                            Toast.makeText(
                                    mActivity,
                                    info.mMusicTitle
                                            + mActivity
                                                    .getString(R.string.toast_remove_from_favorite),
                                    Toast.LENGTH_SHORT).show();
                            info.mIsFavorite = !info.mIsFavorite;
                            PlayMusicApplication.getDataBaseHelper().removeFromFavorite(info);
                        } else {
                            Toast.makeText(
                                    mActivity,
                                    info.mMusicTitle
                                            + mActivity.getString(R.string.toast_add_into_favorite),
                                    Toast.LENGTH_SHORT).show();
                            info.mIsFavorite = !info.mIsFavorite;
                            PlayMusicApplication.getDataBaseHelper().addIntoFavorite(info);
                        }
                        reloadDisplayList();
                    }
                    return true;
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
        } else if (theme == U2bPlayerMainFragmentActivity.THEME_WHITE) {
            mControllPanel.setBackgroundResource(R.color.theme_white_action_bar_bg);
        } else if (theme == U2bPlayerMainFragmentActivity.THEME_BLACK) {
            mControllPanel.setBackgroundResource(R.color.theme_black_action_bar_bg);
        } else if (theme == U2bPlayerMainFragmentActivity.THEME_ORANGE) {
            mControllPanel.setBackgroundResource(R.color.theme_orange_action_bar_bg);
        } else if (theme == U2bPlayerMainFragmentActivity.THEME_YELLOW) {
            mControllPanel.setBackgroundResource(R.color.theme_yellow_action_bar_bg);
        } else if (theme == U2bPlayerMainFragmentActivity.THEME_GRAY) {
            mControllPanel.setBackgroundResource(R.color.theme_gray_action_bar_bg);
        } else if (theme == U2bPlayerMainFragmentActivity.THEME_NAVY) {
            mControllPanel.setBackgroundResource(R.color.theme_navy_action_bar_bg);
        } else if (theme == U2bPlayerMainFragmentActivity.THEME_PURPLE) {
            mControllPanel.setBackgroundResource(R.color.theme_purple_action_bar_bg);
        } else if (theme == U2bPlayerMainFragmentActivity.THEME_SIMPLE_WHITE) {
            mControllPanel.setBackgroundResource(R.color.theme_simple_white_action_bar_bg);
        } else if (theme == U2bPlayerMainFragmentActivity.THEME_RED) {
            mControllPanel.setBackgroundResource(R.color.theme_red_action_bar_bg);
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
            reloadDisplayList();
            mPlayListAdapter.notifyDataSetChanged();
            mPlayListView.smoothScrollToPosition(mPlayList.getPointer());
        }
    }

    private class PlayListAdapter extends BaseAdapter {

        private int mSelectedBackground = 0, mLightBackground, mDarkBackground;

        private int mTextColor, mSelectTextColor;

        @Override
        public int getCount() {
            return mDisplayList.size();
        }

        @Override
        public PlayListInfo getItem(int position) {
            return mDisplayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View contentView, ViewGroup parent) {
            ViewHolder holder = null;
            final PlayListInfo info = getItem(position);
            if (contentView == null) {
                if (mSelectedBackground == 0) {
                    initColor();
                }
                contentView = mLayoutInflater.inflate(R.layout.play_list_content, parent, false);
                holder = new ViewHolder();
                holder.mThumbnail = (ImageView)contentView.findViewById(R.id.play_list_thumbnail);
                holder.mArtist = (TextView)contentView.findViewById(R.id.play_list_artist);
                holder.mMusic = (TextView)contentView.findViewById(R.id.play_list_music);
                contentView.setTag(holder);
            } else {
                holder = (ViewHolder)contentView.getTag();
            }

            holder.mArtist.setText(info.mArtist);
            holder.mArtist.setTextColor(mTextColor);
            holder.mMusic.setText(info.mMusicTitle);
            holder.mMusic.setTextColor(mTextColor);
            if (mDisplayList.get(position).mIsFavorite) {
                holder.mThumbnail.setImageResource(R.drawable.widget_favorite_true);
            } else {
                holder.mThumbnail.setImageResource(R.drawable.widget_favorite);
            }
            initTheme(contentView, position);
            return contentView;
        }

        public void notifyDataSetChanged() {
            initColor();
            super.notifyDataSetChanged();
        }

        private void initColor() {
            int theme = mActivity.getApplicationTheme();
            if (theme == U2bPlayerMainFragmentActivity.THEME_BLUE) {
                mSelectedBackground = R.drawable.theme_blue_list_selected_item_oval_bg;
                mLightBackground = R.drawable.theme_blue_list_light_oval_bg;
                mDarkBackground = R.drawable.theme_blue_list_dark_oval_bg;
                mTextColor = Color.WHITE;
                mSelectTextColor = HOLO_LIGHT_BLUE;
            } else if (theme == U2bPlayerMainFragmentActivity.THEME_WHITE) {
                mSelectedBackground = R.drawable.theme_white_list_selected_item_oval_bg;
                mLightBackground = R.drawable.theme_white_list_light_oval_bg;
                mDarkBackground = R.drawable.theme_white_list_dark_oval_bg;
                mTextColor = Color.BLACK;
                mSelectTextColor = HOLO_LIGHT_BLUE;
            } else if (theme == U2bPlayerMainFragmentActivity.THEME_BLACK) {
                mSelectedBackground = R.drawable.theme_black_list_selected_item_oval_bg;
                mLightBackground = R.drawable.theme_black_list_light_oval_bg;
                mDarkBackground = R.drawable.theme_black_list_dark_oval_bg;
                mTextColor = Color.WHITE;
                mSelectTextColor = HOLO_LIGHT_BLUE;
            } else if (theme == U2bPlayerMainFragmentActivity.THEME_ORANGE) {
                mSelectedBackground = R.drawable.theme_orange_list_selected_item_oval_bg;
                mLightBackground = R.drawable.theme_orange_list_light_oval_bg;
                mDarkBackground = R.drawable.theme_orange_list_dark_oval_bg;
                mTextColor = Color.WHITE;
                mSelectTextColor = HOLO_LIGHT_BLUE;
            } else if (theme == U2bPlayerMainFragmentActivity.THEME_YELLOW) {
                mSelectedBackground = R.drawable.theme_yellow_list_selected_item_oval_bg;
                mLightBackground = R.drawable.theme_yellow_list_light_oval_bg;
                mDarkBackground = R.drawable.theme_yellow_list_dark_oval_bg;
                mTextColor = Color.WHITE;
                mSelectTextColor = HOLO_LIGHT_BLUE;
            } else if (theme == U2bPlayerMainFragmentActivity.THEME_GRAY) {
                mSelectedBackground = R.drawable.theme_gray_list_selected_item_oval_bg;
                mLightBackground = R.drawable.theme_gray_list_light_oval_bg;
                mDarkBackground = R.drawable.theme_gray_list_dark_oval_bg;
                mTextColor = Color.WHITE;
                mSelectTextColor = Color.MAGENTA;
            } else if (theme == U2bPlayerMainFragmentActivity.THEME_NAVY) {
                mSelectedBackground = R.drawable.theme_navy_list_selected_item_oval_bg;
                mLightBackground = R.drawable.theme_navy_list_light_oval_bg;
                mDarkBackground = R.drawable.theme_navy_list_dark_oval_bg;
                mTextColor = Color.WHITE;
                mSelectTextColor = Color.MAGENTA;
            } else if (theme == U2bPlayerMainFragmentActivity.THEME_PURPLE) {
                mSelectedBackground = R.drawable.theme_purple_list_selected_item_oval_bg;
                mLightBackground = R.drawable.theme_purple_list_light_oval_bg;
                mDarkBackground = R.drawable.theme_purple_list_dark_oval_bg;
                mTextColor = Color.WHITE;
                mSelectTextColor = Color.BLACK;
            } else if (theme == U2bPlayerMainFragmentActivity.THEME_SIMPLE_WHITE) {
                mSelectedBackground = R.drawable.theme_simple_white_list_selected_item_oval_bg;
                mLightBackground = R.drawable.theme_simple_white_list_light_oval_bg;
                mDarkBackground = R.drawable.theme_simple_white_list_dark_oval_bg;
                mTextColor = Color.BLACK;
                mSelectTextColor = HOLO_LIGHT_BLUE;
            } else if (theme == U2bPlayerMainFragmentActivity.THEME_RED) {
                mSelectedBackground = R.drawable.theme_red_list_selected_item_oval_bg;
                mLightBackground = R.drawable.theme_red_list_light_oval_bg;
                mDarkBackground = R.drawable.theme_red_list_dark_oval_bg;
                mTextColor = Color.WHITE;
                mSelectTextColor = HOLO_LIGHT_BLUE;
            }
        }

        private void initTheme(final View contentView, final int position) {
            ViewHolder holder = (ViewHolder)contentView.getTag();
            if (position == mPlayList.getPointer() && isPlayingList()) {
                contentView.setBackgroundResource(mSelectedBackground);
                holder.mArtist.setTextColor(mSelectTextColor);
                holder.mMusic.setTextColor(mSelectTextColor);
            } else {
                contentView.setBackgroundResource(position % 2 == 0 ? mDarkBackground
                        : mLightBackground);
            }
        }

        private class ViewHolder {
            ImageView mThumbnail;

            TextView mArtist;

            TextView mMusic;
        }
    }

    private boolean isPlayingList() {
        if (mPlayList == null)
            return false;
        return sDisplayAlbumId == mPlayList.getPlayingListAlbumId();
    }

    @Override
    public void reloadTheme() {
        initTheme();
        if (mPlayListAdapter != null) {
            mPlayListAdapter.notifyDataSetChanged();
        }
    }
}
