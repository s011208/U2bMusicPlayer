
package com.bj4.u2bplayer.activity.fragments;

import com.bj4.u2bplayer.PlayList;
import com.bj4.u2bplayer.R;
import com.bj4.u2bplayer.activity.U2bPlayerMainFragmentActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class U2bPlayListFragment extends Fragment {
    public static final String TAG = "QQQQ";

    public static final boolean DEBUG = true;

    private View mContentView;

    private RelativeLayout mControllPanel;

    private PlayList mPlayList;

    private ListView mPlayListView;

    private PlayListAdapter mPlayListAdapter;

    private LayoutInflater mLayoutInflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initComponents() {
        if (mContentView != null) {
            mPlayList = PlayList.getInstance();
            mLayoutInflater = LayoutInflater.from(getActivity());
            if (DEBUG) {
                Log.d(TAG, "" + mPlayList.getPlayList().size());
            }
            mControllPanel = (RelativeLayout)mContentView
                    .findViewById(R.id.play_list_controll_panel);
            mPlayListView = (ListView)mContentView.findViewById(R.id.play_list_view);
            mPlayListAdapter = new PlayListAdapter();
            mPlayListView.setSelector(R.color.transparent);
            mPlayListView.setAdapter(mPlayListAdapter);
            initTheme();
        }
    }

    private void initTheme() {
        U2bPlayerMainFragmentActivity activity = (U2bPlayerMainFragmentActivity)getActivity();
        int theme = activity.getApplicationTheme();
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

    private class PlayListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mPlayList.getPlayList().size();
        }

        @Override
        public Object getItem(int position) {
            return mPlayList.getPlayList().get(position);
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
            holder.mArtist.setText(mPlayList.getPlayList().get(position).mArtist);
            holder.mMusic.setText(mPlayList.getPlayList().get(position).mMusicTitle);
            initTheme(contentView, position);
            return contentView;
        }

        private void initTheme(final View contentView, final int position) {
            U2bPlayerMainFragmentActivity activity = (U2bPlayerMainFragmentActivity)getActivity();
            int theme = activity.getApplicationTheme();
            if (theme == U2bPlayerMainFragmentActivity.THEME_BLUE) {
                contentView
                        .setBackgroundResource(position % 2 == 0 ? R.drawable.theme_blue_list_dark_oval_bg
                                : R.drawable.theme_blue_list_light_oval_bg);
            }
        }

        private class ViewHolder {
            ImageView mThumbnail;

            TextView mArtist;

            TextView mMusic;
        }
    }
}
