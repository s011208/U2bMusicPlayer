
package com.bj4.u2bplayer.activity.fragments;

import com.bj4.u2bplayer.PlayList;
import com.bj4.u2bplayer.R;
import com.bj4.u2bplayer.activity.U2bPlayerMainFragmentActivity;
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

public class U2bPlayInfoFragment extends Fragment {
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onStart() {
        super.onStart();
        PlayListInfo info = mPlayList.getPlayList().get(mPlayList.getPointer());
        if (info != null) {
            mPlayInfo.setText("info\nartist: " + info.mArtist + "\nmusic: " + info.mMusicTitle);
        }
        initTheme();
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
                if (mActivity.isInitialized() == false) {
                    mActivity.play(mActivity.getCurrentViewIndex());
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
