
package com.bj4.u2bplayer.activity;

import java.lang.reflect.Method;

import com.bj4.u2bplayer.PlayMusicApplication;
import com.bj4.u2bplayer.R;
import com.bj4.u2bplayer.dialogs.MainActivityOptionDialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bj4.u2bplayer.activity.fragments.*;

public class U2bPlayerMainFragmentActivity extends FragmentActivity {
    private static final String TAG = "QQQQ";

    private static final boolean DEBUG = true && PlayMusicApplication.OVERALL_DEBUG;

    public static final int FRAGMENT_TYPE_MAIN = 0;

    public static final int FRAGMENT_TYPE_PLAYLIST = 1;

    public static final int FRAGMENT_TYPE_MUSIC_DETAIL = 2;

    public static final int THEME_BLUE = 0; // default

    public static final String SHARE_PREF_KEY = "sharf";

    public static final String SHARE_PREF_KEY_THEME = "application_theme";

    private RelativeLayout mMainLayout, mActionBar;

    private ImageButton mOptionBtn;

    private Fragment mU2bPlayListFragment, mU2bMainFragment;

    private TextView mActionBarTitle;

    private SharedPreferences mPref;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.u2b_main_activity);
        initComponents();
        themeSwitcher();
        switchFragment(FRAGMENT_TYPE_MAIN);
        // switchFragment(FRAGMENT_TYPE_PLAYLIST);
    }

    public int getApplicationTheme() {
        return mPref.getInt(SHARE_PREF_KEY_THEME, THEME_BLUE);
    }

    private void themeSwitcher() {
        final int theme = getApplicationTheme();
        if (DEBUG) {
            Log.d(TAG, "THEME: " + theme);
        }
        if (theme == THEME_BLUE) {
            mMainLayout.setBackgroundResource(R.color.theme_blue_activity_bg);
            mActionBar.setBackgroundResource(R.color.theme_blue_action_bar_bg);
        }
    }

    /**
     * do not edit this method
     */
    private void initMainLayout() {
        boolean hasNavigationBar = true;
        try {
            Class<?> c = Class.forName("android.view.WindowManagerGlobal");
            Method m = c.getDeclaredMethod("getWindowManagerService", new Class<?>[] {});
            Object windowManagerService = m.invoke(null, new Object[] {});
            c = windowManagerService.getClass();
            m = c.getDeclaredMethod("hasNavigationBar", new Class<?>[] {});
            hasNavigationBar = (Boolean)m.invoke(windowManagerService, new Object[] {});
            if (DEBUG)
                Log.d(TAG, "hasNavigationBar: " + hasNavigationBar);
        } catch (Exception e) {
            if (DEBUG)
                Log.w(TAG, "failed to get windowManagerService", e);
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // TODO do something about transparent navigation bar
            int statusBarHeight = (int)getResources().getDimension(R.dimen.status_bar_height);
            int navigationBarHeight = hasNavigationBar ? (int)getResources().getDimension(
                    R.dimen.navigation_bar_height) : 0;
            mMainLayout.setPadding(mMainLayout.getPaddingLeft(), statusBarHeight,
                    mMainLayout.getPaddingRight(), navigationBarHeight);
        }
    }

    private void initComponents() {
        mPref = this.getSharedPreferences(SHARE_PREF_KEY, Context.MODE_PRIVATE);
        mMainLayout = (RelativeLayout)findViewById(R.id.u2b_main_activity_main_layout);
        mActionBar = (RelativeLayout)findViewById(R.id.action_bar_parent);
        mOptionBtn = (ImageButton)findViewById(R.id.menu);
        mActionBarTitle = (TextView)findViewById(R.id.action_bar_music_info);
        initMainLayout();
        initActionBarComponents();
    }

    private synchronized Fragment getMainFragment() {
        if (mU2bMainFragment == null) {
            mU2bMainFragment = new U2bMainFragment();
        }
        return mU2bMainFragment;
    }

    private synchronized Fragment getPlayListFragment() {
        if (mU2bPlayListFragment == null) {
            mU2bPlayListFragment = new U2bPlayListFragment();
        }
        return mU2bPlayListFragment;
    }

    public void initActionBarComponents() {

        mOptionBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                int[] location1 = {
                        0, 0
                };
                Rect r = new Rect();
                v.getLocationInWindow(location1);
                v.getLocalVisibleRect(r);
                display.getSize(size);
                int[] location = {
                        size.x - location1[0] - r.width(), 2 * location1[1]
                };
                new MainActivityOptionDialog(U2bPlayerMainFragmentActivity.this, mOptionCallback,
                        location).show(getFragmentManager(), "");
            }
        });
    }

    private MainActivityOptionDialog.MainActivityOptionDialogCallback mOptionCallback = new MainActivityOptionDialog.MainActivityOptionDialogCallback() {

        @Override
        public void onSelected(int option) {
            switch (option) {
                case MainActivityOptionDialog.ITEM_DOWNLOAD_DATA:
                    if (DEBUG) {
                        Log.d(TAG, "action bar -- sync pressed");
                    }
                    startToScan();
                    break;
            }
        }
    };

    private void startToScan() {
        // scan list
        PlayMusicApplication.getPlayScanner().scan();
    }

    public void switchFragment(final int type) {
        Fragment target = null;

        switch (type) {
            case FRAGMENT_TYPE_MAIN:
                target = getMainFragment();
                break;
            case FRAGMENT_TYPE_PLAYLIST:
                target = getPlayListFragment();
                break;
            case FRAGMENT_TYPE_MUSIC_DETAIL:
                break;
        }
        if (target != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_fragment_container, target);
            transaction.commitAllowingStateLoss();
        } else {
            if (DEBUG) {
                Log.w(TAG, "wrong fragment type: " + type);
            }
        }
    }

    public void setActionMusicInfo(String text) {
        if (mActionBarTitle != null) {
            mActionBarTitle.setText(text);
        }
    }
}
