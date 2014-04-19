
package com.bj4.u2bplayer.activity.fragments;

import com.bj4.u2bplayer.PlayList;
import com.bj4.u2bplayer.R;
import com.bj4.u2bplayer.activity.U2bPlayerMainFragmentActivity;
import com.bj4.u2bplayer.activity.U2bPlayerMainFragmentActivity.MainFragmentCallback;
import com.bj4.u2bplayer.utilities.PlayListInfo;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class U2bPlayInfoFragment extends Fragment implements MainFragmentCallback {
    public static final String TAG = "U2bPlayInfoFragment";

    public static final boolean DEBUG = true;

    private static final int CONTROL_PANEL_AUTO_HIDE_TIME = 2000;

    private View mContentView;

    private RelativeLayout mBottomPanel;

    private TextView mPlayInfo;

    private LayoutInflater mLayoutInflater;

    private PlayList mPlayList;

    private U2bPlayerMainFragmentActivity mActivity;

    private ViewSwitcher mMainContainer;

    private RotatedControlPanel mControlPanel;

    private Handler mHandler = new Handler();

    private ImageView mPlay, mPause, mPlayNext, mPlayPrevious;

    private ViewSwitcher mPlayOrPause;

    private PlayListInfo mInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setContentInfo(PlayListInfo info) {
        mInfo = info;
        if (info != null && mPlayInfo != null) {
            mPlayInfo.setText("info\nartist: " + mInfo.mArtist + "\nmusic: " + mInfo.mMusicTitle);
        }
    }

    public void onStart() {
        super.onStart();
        mActivity.addCallback(this);
        if (mInfo != null) {
            if (mPlayInfo != null) {
                mPlayInfo.setText("info\nartist: " + mInfo.mArtist + "\nmusic: "
                        + mInfo.mMusicTitle);
            }
        }
        initTheme();
    }

    public void onStop() {
        super.onStop();
        mActivity.removeCallback(this);
    }

    private Runnable mHideControlPanelRunnable = new Runnable() {

        @Override
        public void run() {
            ObjectAnimator oa = ObjectAnimator.ofFloat(mControlPanel, View.ALPHA, 1, 0);
            oa.setDuration(1000);
            oa.addListener(new AnimatorListener() {

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mControlPanel.setVisibility(View.INVISIBLE);
                    mControlPanel.setAlpha(1);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }

                @Override
                public void onAnimationStart(Animator animation) {
                }
            });
            oa.start();
        }
    };

    public void removeHideControlPanelFromHandler() {
        mHandler.removeCallbacks(mHideControlPanelRunnable);
    }

    public void postDelayHideControlPanel() {
        mHandler.removeCallbacks(mHideControlPanelRunnable);
        mHandler.postDelayed(mHideControlPanelRunnable, CONTROL_PANEL_AUTO_HIDE_TIME);
    }

    private void initComponents() {
        mActivity = (U2bPlayerMainFragmentActivity)getActivity();
        mPlayList = PlayList.getInstance(mActivity);
        mLayoutInflater = LayoutInflater.from(mActivity);
        mBottomPanel = (RelativeLayout)mContentView.findViewById(R.id.play_info_bottom_panel);
        mBottomPanel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mControlPanel != null) {
                    if (mControlPanel.getVisibility() == View.VISIBLE) {
                        mControlPanel.setVisibility(View.INVISIBLE);
                    } else {
                        mControlPanel.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        mPlayInfo = (TextView)mContentView.findViewById(R.id.play_info_playing_info);
        mMainContainer = (ViewSwitcher)mContentView.findViewById(R.id.play_info_main_container);
        mControlPanel = (RotatedControlPanel)mContentView
                .findViewById(R.id.play_info_control_panel);
        mControlPanel.setParent(this);
        mPlay = (ImageView)mContentView.findViewById(R.id.play_info_play);
        mPlay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                v.setHapticFeedbackEnabled(true);
                int index = mPlayList.getPlayList().indexOf(mInfo);
                if (mActivity.isInitialized() == false || mPlayList.getPointer() != index) {
                    mActivity.play(index);
                } else {
                    mActivity.resumePlay();
                }
                mPlayOrPause.setDisplayedChild(1);
            }
        });
        mPlayNext = (ImageView)mContentView.findViewById(R.id.play_info_play_next);
        mPlayNext.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                v.setHapticFeedbackEnabled(true);
                mActivity.playNext();
            }
        });
        mPlayPrevious = (ImageView)mContentView.findViewById(R.id.play_info_play_previous);
        mPlayPrevious.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                v.setHapticFeedbackEnabled(true);
                mActivity.playPrevious();
            }
        });
        mPause = (ImageView)mContentView.findViewById(R.id.play_info_pause);
        mPause.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                v.setHapticFeedbackEnabled(true);
                mActivity.pause();
                mPlayOrPause.setDisplayedChild(0);
            }
        });
        mPlayOrPause = (ViewSwitcher)mContentView.findViewById(R.id.play_info_play_or_pause);
        mPlayOrPause.setDisplayedChild(mActivity.isPlaying() ? 1 : 0);
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

    public void changePlayIndex() {
    }

    public void setPlayOrPause(boolean isPlaying) {
        if (mPlayOrPause != null) {
            mPlayOrPause.setDisplayedChild(isPlaying ? 1 : 0);
        }
    }
}
