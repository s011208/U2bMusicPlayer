
package com.bj4.u2bplayer.activity.fragments;

import com.bj4.u2bplayer.PlayList;
import com.bj4.u2bplayer.R;
import com.bj4.u2bplayer.activity.U2bPlayerMainFragmentActivity;
import com.bj4.u2bplayer.utilities.PlayListInfo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class U2bPlayInfoFragment extends Fragment {
    public static final String TAG = "U2bPlayInfoFragment";

    public static final boolean DEBUG = true;

    private View mContentView;

    private RelativeLayout mBottomPanel;

    private TextView mPlayInfo;

    private LayoutInflater mLayoutInflater;

    private PlayList mPlayList;

    private U2bPlayerMainFragmentActivity mActivity;

    private ViewSwitcher mMainContainer;

    private RotatedControlPanel mControlPanel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onStart() {
        super.onStart();
        PlayListInfo info = mPlayList.getPlayList().get(mPlayList.getPointer());
        if (info != null) {
            mPlayInfo.setText("info artist: " + info.mArtist + "\nalbum: " + info.mAlbumTitle
                    + "\nmusic: " + info.mMusicTitle);
        }
        initTheme();
    }

    private void initComponents() {
        mActivity = (U2bPlayerMainFragmentActivity)getActivity();
        mPlayList = PlayList.getInstance(mActivity);
        mLayoutInflater = LayoutInflater.from(mActivity);
        mBottomPanel = (RelativeLayout)mContentView.findViewById(R.id.play_info_bottom_panel);
        mPlayInfo = (TextView)mContentView.findViewById(R.id.play_info_playing_info);
        mMainContainer = (ViewSwitcher)mContentView.findViewById(R.id.play_info_main_container);
        mControlPanel = (RotatedControlPanel)mContentView
                .findViewById(R.id.play_info_control_panel);
    }

    private void initTheme() {
        int theme = mActivity.getApplicationTheme();
        if (theme == U2bPlayerMainFragmentActivity.THEME_BLUE) {
            mMainContainer.setBackgroundResource(R.color.theme_blue_play_info_main_container_bg);
        }
        mControlPanel.setTheme(theme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.play_info_fragment, container, false);
        initComponents();
        return mContentView;
    }
}
