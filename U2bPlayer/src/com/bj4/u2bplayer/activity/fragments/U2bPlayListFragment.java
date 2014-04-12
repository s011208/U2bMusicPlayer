
package com.bj4.u2bplayer.activity.fragments;

import com.bj4.u2bplayer.PlayList;
import com.bj4.u2bplayer.R;
import com.bj4.u2bplayer.activity.U2bPlayerMainFragmentActivity;

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

public class U2bPlayListFragment extends Fragment {
    public static final String TAG = "QQQQ";

    public static final boolean DEBUG = true;

    private View mContentView;

    private RelativeLayout mControllPanel;

    private PlayList mPlayList;

    private ListView mPlayListView;

    private PlayListAdapter mPlayListAdapter;

    private LayoutInflater mLayoutInflater;

    private U2bPlayerMainFragmentActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initComponents() {
        if (mContentView != null) {
            mActivity = (U2bPlayerMainFragmentActivity)getActivity();
            mPlayList = PlayList.getInstance();
            mLayoutInflater = LayoutInflater.from(mActivity);
            if (DEBUG) {
                Log.d(TAG, "" + mPlayList.getPlayList().size());
            }
            mControllPanel = (RelativeLayout)mContentView
                    .findViewById(R.id.play_list_controll_panel);
            mPlayListView = (ListView)mContentView.findViewById(R.id.play_list_view);
            mPlayListAdapter = new PlayListAdapter();
            mPlayListView.setSelector(R.color.transparent);
            mPlayListView.setAdapter(mPlayListAdapter);
            mPlayListView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                    v.setHapticFeedbackEnabled(true);
                    mActivity.play(position);
                }
            });
            initTheme();
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
            int theme = mActivity.getApplicationTheme();
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
