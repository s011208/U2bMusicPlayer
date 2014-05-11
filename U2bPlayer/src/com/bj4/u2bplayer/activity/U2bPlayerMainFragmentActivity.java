
package com.bj4.u2bplayer.activity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.bj4.u2bplayer.PlayList;
import com.bj4.u2bplayer.PlayMusicApplication;
import com.bj4.u2bplayer.R;
import com.bj4.u2bplayer.dialogs.MainActivityOptionDialog;
import com.bj4.u2bplayer.service.IPlayMusicService;
import com.bj4.u2bplayer.service.IPlayMusicServiceCallback;
import com.bj4.u2bplayer.service.ISpiderService;
import com.bj4.u2bplayer.service.PlayMusicService;
import com.bj4.u2bplayer.service.SpiderService;
import com.bj4.u2bplayer.utilities.PlayListInfo;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.bj4.u2bplayer.activity.fragments.*;
import com.google.android.gms.ads.*;
import com.vpadn.ads.VpadnAd;
import com.vpadn.ads.VpadnAdListener;
import com.vpadn.ads.VpadnAdRequest;
import com.vpadn.ads.VpadnAdRequest.VpadnErrorCode;
import com.vpadn.ads.VpadnAdSize;
import com.vpadn.ads.VpadnBanner;
import com.vpadn.ads.VpadnInterstitialAd;

public class U2bPlayerMainFragmentActivity extends FragmentActivity {
    private static final String TAG = "U2bPlayerMainFragmentActivity";

    private static final boolean DEBUG = false && PlayMusicApplication.OVERALL_DEBUG;

    protected static final boolean DEBUG_STRICT_MODE = false;

    public static final int FRAGMENT_TYPE_MAIN = 0;

    public static final int FRAGMENT_TYPE_PLAYLIST = 1;

    public static final int FRAGMENT_TYPE_MUSIC_INFO = 2;

    public static final String THEME_CHANGED_INTENT = "com.bj4.u2bplayer.activity.U2bPlayerMainFragmentActivity.themeChanged";

    public static final String DATA_SOURCE_LIST_CHANGED_INTENT = "com.bj4.u2bplayer.activity.U2bPlayerMainFragmentActivity.dataSourceChanged";

    public static final String THEME_CHANGED_INTENT_EXTRA_THEME = "new_theme";

    public static final int THEME_BLUE = 0; // default

    public static final int THEME_WHITE = 1;

    public static final int THEME_BLACK = 2;

    public static final int THEME_ORANGE = 3;

    public static final int THEME_YELLOW = 4;

    public static final int THEME_GRAY = 5;

    public static final int THEME_NAVY = 6;

    public static final int THEME_PURPLE = 7;

    public static final int THEME_SIMPLE_WHITE = 8;

    public static final int THEME_RED = 9;

    public static final String SHARE_PREF_KEY_THEME = "application_theme";

    public static final String SHARE_PREF_KEY_SOURCE_LIST = "source_list";

    private RelativeLayout mMainLayout, mActionBar, mStatusBar;

    private LinearLayout mMainContentFragment;

    private ImageButton mOptionBtn;

    private Fragment mU2bPlayListFragment, mU2bMainFragment, mU2bPlayInfoFragment;

    private TextView mActionBarTitle, mStatusBarInfo;

    private SharedPreferences mPref;

    private static int sCurrentFragment = 0;

    private IPlayMusicService mPlayMusicService;

    private ArrayList<MainFragmentCallback> mFragmentCallbacks = new ArrayList<MainFragmentCallback>();

    private ISpiderService mSpiderService;

    private PlayList mPlayList;

    private Handler mHandler = new Handler();

    private String mDisplayingAlbumName;

    private AdView mAdView;

    private Button mCloseAdViewBtn, mCloseVponAdViewBtn;

    private FrameLayout mAdViewParent, mVponAdViewParent;

    private InterstitialAd mInterstitial;

    private boolean mCanLoadNextAd = false;

    private VpadnBanner mVponBanner;

    private VpadnInterstitialAd mVponInterstitialAd;

    private boolean mCanShowVponInterstitialAd = false;

    private PlayList.PlayListLoaderCallback mPlayListCallback = new PlayList.PlayListLoaderCallback() {

        @Override
        public void loadDone() {
            if (DEBUG) {
                Log.i(TAG, "load done");
            }
        }
    };

    public interface MainFragmentCallback {
        public void changePlayIndex();

        public void setPlayOrPause(boolean isPlaying);
    }

    private IPlayMusicServiceCallback mPlayMusicServiceCallback = new IPlayMusicServiceCallback.Stub() {

        @Override
        public void notifyPlayIndexChanged() throws RemoteException {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    for (MainFragmentCallback cb : mFragmentCallbacks) {
                        cb.changePlayIndex();
                    }
                }
            });
        }

        @Override
        public void notifyPlayStateChanged(final boolean isPlaying) throws RemoteException {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    for (MainFragmentCallback cb : mFragmentCallbacks) {
                        cb.setPlayOrPause(isPlaying);
                    }
                }
            });
        }

        @Override
        public void notifyPlayInfoChanged(final PlayListInfo info) throws RemoteException {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    if (info != null) {
                        setActionMusicInfo(info);
                    }
                }
            });
        }

        @Override
        public void updateBufferingPercentage(final PlayListInfo info, final int percentage)
                throws RemoteException {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    setActionMusicInfo(info);
                    setBufferStatus(percentage);
                }
            });
        }

        @Override
        public void updatePlayingTime(final int time) throws RemoteException {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    U2bPlayInfoFragment fragment = getPlayInfoFragment();
                    if (fragment != null) {
                        fragment.setDuration(time);
                    }
                }
            });
        }

        @Override
        public void askToReloadDisplayList() throws RemoteException {
            getPlayListFragment().reloadDisplayList();
        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
            } else if (THEME_CHANGED_INTENT.equals(action)) {
                int newTheme = intent.getIntExtra(THEME_CHANGED_INTENT_EXTRA_THEME, THEME_BLUE);
                setApplicationTheme(newTheme);
            } else if (DATA_SOURCE_LIST_CHANGED_INTENT.equals(action)) {
                notifySourceListChanged();
            } else if (PlayMusicApplication.INTENT_STATUS_BAR_VISIBLITY_CHANGED.equals(action)) {
                notifyStatusBarVisibilityChanged();
            }
        }
    };

    private void notifyStatusBarVisibilityChanged() {
        if (mMainLayout == null || mMainContentFragment == null || mStatusBar == null)
            return;
        boolean isVisible = PlayMusicApplication.sShowStatus;
        if (isVisible) {
            mStatusBar.setVisibility(View.VISIBLE);
        } else {
            mStatusBar.setVisibility(View.GONE);
        }
    }

    private void notifySourceListChanged() {
        // TODO source list changed implements here
    }

    private void unRegisterBroadcastReceiver() {
        unregisterReceiver(mReceiver);
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(THEME_CHANGED_INTENT);
        filter.addAction(DATA_SOURCE_LIST_CHANGED_INTENT);
        filter.addAction(PlayMusicApplication.INTENT_STATUS_BAR_VISIBLITY_CHANGED);
        registerReceiver(mReceiver, filter);
    }

    public void addCallback(MainFragmentCallback cb) {
        mFragmentCallbacks.add(cb);
    }

    public void removeCallback(MainFragmentCallback cb) {
        mFragmentCallbacks.remove(cb);
    }

    protected void runStrictModeIfNeeded() {
        if (DEBUG_STRICT_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads()
                    .detectDiskWrites().detectNetwork().penaltyLog().detectAll().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects().penaltyLog().penaltyDeath()
                    .detectActivityLeaks().detectAll().build());
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        runStrictModeIfNeeded();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.u2b_main_activity);
        initComponents();
        themeSwitcher();
        switchFragment(sCurrentFragment);
        startService(new Intent(this, PlayMusicService.class));
        bindService(new Intent(this, PlayMusicService.class), mMusicPlayServiceConnection,
                Context.BIND_AUTO_CREATE);
        bindService(new Intent(this, SpiderService.class), mSpiderServiceConnection,
                Context.BIND_AUTO_CREATE);
        registerBroadcastReceiver();
    }

    public void setApplicationTheme(int theme) {
        mPref.edit().putInt(SHARE_PREF_KEY_THEME, theme).commit();
        themeSwitcher();
        reloadTheme();
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
            mStatusBar.setBackgroundResource(R.color.theme_blue_action_bar_bg);
            mActionBarTitle.setTextColor(Color.WHITE);
            mStatusBarInfo.setTextColor(Color.WHITE);
        } else if (theme == THEME_WHITE) {
            mMainLayout.setBackgroundResource(R.color.theme_white_activity_bg);
            mActionBar.setBackgroundResource(R.color.theme_white_action_bar_bg);
            mStatusBar.setBackgroundResource(R.color.theme_white_action_bar_bg);
            mActionBarTitle.setTextColor(Color.BLACK);
            mStatusBarInfo.setTextColor(Color.BLACK);
        } else if (theme == THEME_BLACK) {
            mMainLayout.setBackgroundResource(R.color.theme_black_activity_bg);
            mActionBar.setBackgroundResource(R.color.theme_black_action_bar_bg);
            mStatusBar.setBackgroundResource(R.color.theme_black_action_bar_bg);
            mActionBarTitle.setTextColor(Color.WHITE);
            mStatusBarInfo.setTextColor(Color.WHITE);
        } else if (theme == THEME_ORANGE) {
            mMainLayout.setBackgroundResource(R.color.theme_orange_activity_bg);
            mActionBar.setBackgroundResource(R.color.theme_orange_action_bar_bg);
            mStatusBar.setBackgroundResource(R.color.theme_orange_action_bar_bg);
            mActionBarTitle.setTextColor(Color.WHITE);
            mStatusBarInfo.setTextColor(Color.WHITE);
        } else if (theme == THEME_YELLOW) {
            mMainLayout.setBackgroundResource(R.color.theme_yellow_activity_bg);
            mActionBar.setBackgroundResource(R.color.theme_yellow_action_bar_bg);
            mStatusBar.setBackgroundResource(R.color.theme_yellow_action_bar_bg);
            mActionBarTitle.setTextColor(Color.WHITE);
            mStatusBarInfo.setTextColor(Color.WHITE);
        } else if (theme == THEME_GRAY) {
            mMainLayout.setBackgroundResource(R.color.theme_gray_activity_bg);
            mActionBar.setBackgroundResource(R.color.theme_gray_action_bar_bg);
            mStatusBar.setBackgroundResource(R.color.theme_gray_action_bar_bg);
            mActionBarTitle.setTextColor(Color.WHITE);
            mStatusBarInfo.setTextColor(Color.WHITE);
        } else if (theme == THEME_NAVY) {
            mMainLayout.setBackgroundResource(R.color.theme_navy_activity_bg);
            mActionBar.setBackgroundResource(R.color.theme_navy_action_bar_bg);
            mStatusBar.setBackgroundResource(R.color.theme_navy_action_bar_bg);
            mActionBarTitle.setTextColor(Color.WHITE);
            mStatusBarInfo.setTextColor(Color.WHITE);
        } else if (theme == THEME_PURPLE) {
            mMainLayout.setBackgroundResource(R.color.theme_purple_activity_bg);
            mActionBar.setBackgroundResource(R.color.theme_purple_action_bar_bg);
            mStatusBar.setBackgroundResource(R.color.theme_purple_action_bar_bg);
            mActionBarTitle.setTextColor(Color.WHITE);
            mStatusBarInfo.setTextColor(Color.WHITE);
        } else if (theme == THEME_SIMPLE_WHITE) {
            mMainLayout.setBackgroundResource(R.color.theme_simple_white_activity_bg);
            mActionBar.setBackgroundResource(R.color.theme_simple_white_action_bar_bg);
            mStatusBar.setBackgroundResource(R.color.theme_simple_white_action_bar_bg);
            mActionBarTitle.setTextColor(Color.BLACK);
            mStatusBarInfo.setTextColor(Color.BLACK);
        } else if (theme == THEME_RED) {
            mMainLayout.setBackgroundResource(R.color.theme_red_activity_bg);
            mActionBar.setBackgroundResource(R.color.theme_red_action_bar_bg);
            mStatusBar.setBackgroundResource(R.color.theme_red_action_bar_bg);
            mActionBarTitle.setTextColor(Color.WHITE);
            mStatusBarInfo.setTextColor(Color.WHITE);
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
            hasNavigationBar = (Boolean) m.invoke(windowManagerService, new Object[] {});
            if (DEBUG)
                Log.d(TAG, "hasNavigationBar: " + hasNavigationBar);
        } catch (Exception e) {
            if (DEBUG)
                Log.w(TAG, "failed to get windowManagerService", e);
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // TODO do something about transparent navigation bar
            int statusBarHeight = (int) getResources().getDimension(R.dimen.status_bar_height);
            int navigationBarHeight = hasNavigationBar ? (int) getResources().getDimension(
                    R.dimen.navigation_bar_height) : 0;
            // mMainLayout.setPadding(mMainLayout.getPaddingLeft(),
            // statusBarHeight,
            // mMainLayout.getPaddingRight(), navigationBarHeight);
        }
    }

    private void checkSourceListInAdvance() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    HashSet<String> set = (HashSet<String>) mPref.getStringSet(
                            SHARE_PREF_KEY_SOURCE_LIST, new HashSet<String>());
                    if (set.size() == 0) {
                        // put default
                        set.add("1");
                    }
                    mPref.edit().putStringSet(SHARE_PREF_KEY_SOURCE_LIST, set).commit();
                } catch (Exception e) {
                    // ignore
                }
            }
        }).start();

    }

    private void initComponents() {
        mPref = PlayMusicApplication.getPref(getApplicationContext());
        checkSourceListInAdvance();
        mPlayList = PlayList.getInstance(this);
        mPlayList.addCallback(mPlayListCallback);
        mMainLayout = (RelativeLayout) findViewById(R.id.u2b_main_activity_main_layout);
        mActionBar = (RelativeLayout) findViewById(R.id.action_bar_parent);
        mOptionBtn = (ImageButton) findViewById(R.id.menu);
        mActionBarTitle = (TextView) findViewById(R.id.action_bar_music_info);
        mMainContentFragment = (LinearLayout) findViewById(R.id.main_fragment_container);
        mStatusBar = (RelativeLayout) findViewById(R.id.main_status_bar);
        mStatusBarInfo = (TextView) findViewById(R.id.main_status_bar_info);

        notifyStatusBarVisibilityChanged();
        initMainLayout();
        initActionBarComponents();

        // main admob
        mAdViewParent = (FrameLayout) findViewById(R.id.ad_view_parent);
        mAdView = (AdView) findViewById(R.id.adView);

        mAdView.loadAd(new AdRequest.Builder().build());
        mCloseAdViewBtn = (Button) findViewById(R.id.close_adview_btn);
        mCloseAdViewBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mAdViewParent.setVisibility(View.GONE);
            }
        });
        mAdView.setAdListener(new AdListener() {
            public void onAdLoaded() {
                mCloseAdViewBtn.setVisibility(View.VISIBLE);
            }
        });
        mInterstitial = new InterstitialAd(this);
        mInterstitial.setAdUnitId("ca-app-pub-6081210604737939/5840186808");
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitial.loadAd(adRequest);

        // vpon
        mVponBanner = (VpadnBanner) findViewById(R.id.vpadnBannerXML);
        mCloseVponAdViewBtn = (Button) findViewById(R.id.close_vpon_adview_btn);
        mVponAdViewParent = (FrameLayout) findViewById(R.id.vpon_ad_view_parent);
        mCloseVponAdViewBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mVponAdViewParent.setVisibility(View.GONE);
            }
        });
        mVponBanner.setAdListener(new VpadnAdListener() {

            @Override
            public void onVpadnDismissScreen(VpadnAd arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onVpadnFailedToReceiveAd(VpadnAd arg0, VpadnErrorCode arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onVpadnLeaveApplication(VpadnAd arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onVpadnPresentScreen(VpadnAd arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onVpadnReceiveAd(VpadnAd arg0) {
                // TODO Auto-generated method stub
                mCloseVponAdViewBtn.setVisibility(View.VISIBLE);

            }
        });
        mVponInterstitialAd = new VpadnInterstitialAd(this, "8a80818245da428c0145e72982a9070e",
                "TW");
        VpadnAdRequest interstitialAdRequest = new VpadnAdRequest();
        mVponInterstitialAd.setAdListener(new VpadnAdListener() {

            @Override
            public void onVpadnDismissScreen(VpadnAd arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onVpadnFailedToReceiveAd(VpadnAd arg0, VpadnErrorCode arg1) {
                mCanShowVponInterstitialAd = false;
            }

            @Override
            public void onVpadnLeaveApplication(VpadnAd arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onVpadnPresentScreen(VpadnAd arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onVpadnReceiveAd(VpadnAd arg0) {
                mCanShowVponInterstitialAd = true;
            }
        });
        mVponInterstitialAd.loadAd(interstitialAdRequest);
    }

    private Runnable mShowAdViewRunnable = new Runnable() {

        @Override
        public void run() {
            if (mAdViewParent != null) {
                mAdViewParent.setVisibility(View.VISIBLE);
            }
        }
    };

    private synchronized U2bMainFragment getMainFragment() {
        if (mU2bMainFragment == null) {
            mU2bMainFragment = new U2bMainFragment();
        }
        return (U2bMainFragment) mU2bMainFragment;
    }

    private synchronized U2bPlayListFragment getPlayListFragment() {
        if (mU2bPlayListFragment == null) {
            mU2bPlayListFragment = new U2bPlayListFragment();
        }
        return (U2bPlayListFragment) mU2bPlayListFragment;
    }

    private synchronized U2bPlayInfoFragment getPlayInfoFragment() {
        if (mU2bPlayInfoFragment == null) {
            mU2bPlayInfoFragment = new U2bPlayInfoFragment();
        }
        return (U2bPlayInfoFragment) mU2bPlayInfoFragment;
    }

    public void initActionBarComponents() {
        mActionBarTitle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // getPlayInfoFragment().resetInfo();
                // switchFragment(FRAGMENT_TYPE_MUSIC_INFO);
                if (mPlayList != null) {
                    String albumName = PlayMusicApplication.getDataBaseHelper().getAlbumName(
                            getPlayingAlbumId());
                    if (albumName == null) {
                        albumName = mPlayList.getPlayingListAlbumName();
                    }
                    if (albumName != null) {
                        setDisplayingAlbumName(albumName);
                        mPlayList.setAlbumPlayingList(albumName);
                    }
                }
                switchFragment(FRAGMENT_TYPE_PLAYLIST);
            }
        });
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
                    checkWifiStatusAndScan();
                    break;
            }
        }
    };

    private void checkWifiStatusAndScan() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final boolean isWifiConnected = mWifi.isConnected();
        final boolean is3GAllowed = PlayMusicApplication.sAllow3GUpdate;
        if (isWifiConnected) {
            // parse directly
            startToScan();
        } else {
            if (is3GAllowed) {
                // parse directly
                startToScan();
            } else {
                // show alarm dialog
                AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(this,
                        android.R.style.Theme_Holo_Light_Dialog));
                dialog.setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                dialog.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startToScan();
                                dialog.dismiss();
                            }
                        });
                dialog.setTitle(R.string.dialog_alarm_3g_update);
                dialog.show();
            }
        }
    }

    private void startToScan() {
        // scan list
        try {
            mSpiderService.startToParse();
        } catch (RemoteException e) {
            if (DEBUG) {
                Log.w(TAG, "scan failed", e);
            }
        }
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
            // case FRAGMENT_TYPE_MUSIC_INFO:
            // target = getPlayInfoFragment();
            // break;
            default:
                target = getMainFragment();
                break;
        }
        if (target != null) {
            int previouseFragment = sCurrentFragment;
            sCurrentFragment = type;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_fragment_container, target);
            transaction.commitAllowingStateLoss();
            if (previouseFragment == type && previouseFragment == FRAGMENT_TYPE_PLAYLIST) {
                getPlayListFragment().updateListContent();
            }
        } else {
            if (DEBUG) {
                Log.w(TAG, "wrong fragment type: " + type);
            }
        }
    }

    public void viewPlayInfo(PlayListInfo info) {
        // switchFragment(U2bPlayerMainFragmentActivity.FRAGMENT_TYPE_MUSIC_INFO);
        // getPlayInfoFragment().setContentInfo(info);
    }

    public void setActionMusicInfo(PlayListInfo info) {
        if (mActionBarTitle != null) {
            mActionBarTitle.setText(info.mMusicTitle + "  " + info.mArtist);
        }
    }

    public void setBufferStatus(int percentage) {
        mStatusBarInfo.setText("loading progress: " + percentage);
    }

    public void setActionMusicInfo(String text) {
        if (mActionBarTitle != null) {
            mActionBarTitle.setText(text);
        }
    }

    public void onResume() {
        super.onResume();
        mAdView.resume();
        if (sCurrentFragment == FRAGMENT_TYPE_PLAYLIST) {
            getPlayListFragment().changePlayIndex();
        }
        mHandler.removeCallbacks(mShowAdViewRunnable);
        mHandler.post(mShowAdViewRunnable);
        countInterstitial();
    }

    public void countInterstitial() {
        ++PlayMusicApplication.sAdCount;
        if (PlayMusicApplication.sAdCount % PlayMusicApplication.AD_TIME == 0) {
            displayInterstitial();
            PlayMusicApplication.sAdCount = 1;
        } else if (PlayMusicApplication.sAdCount % PlayMusicApplication.AD_TIME == 3) {
            if (mCanLoadNextAd) {
                AdRequest adRequest = new AdRequest.Builder().build();
                mInterstitial.loadAd(adRequest);
                mCanLoadNextAd = false;
            }
        }
    }

    public void displayInterstitial() {
        if (mInterstitial.isLoaded()) {
            mInterstitial.show();
            mCanLoadNextAd = true;
        } else {
            mInterstitial.setAdListener(new AdListener() {
                public void onAdLoaded() {
                    mInterstitial.show();
                }

                public void onAdFailedToLoad(int errorCode) {
                    mCanLoadNextAd = true;
                }

                public void onAdLeftApplication() {
                }

                public void onAdClosed() {
                    mCanLoadNextAd = true;
                }
            });
        }
    }

    public void onPause() {
        mAdView.pause();
        super.onPause();
    }

    public void onDestroy() {
        mAdView.destroy();
        mVponBanner.destroy();
        mVponInterstitialAd.destroy();
        super.onDestroy();
        unbindService(mMusicPlayServiceConnection);
        unbindService(mSpiderServiceConnection);
        mPlayList.removeCallback(mPlayListCallback);
        unRegisterBroadcastReceiver();
    }

    private ServiceConnection mSpiderServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mSpiderService = ISpiderService.Stub.asInterface(service);
        }

        public void onServiceDisconnected(ComponentName className) {
            mSpiderService = null;
        }
    };

    private ServiceConnection mMusicPlayServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mPlayMusicService = IPlayMusicService.Stub.asInterface(service);
            try {
                mPlayMusicService.registerCallback(mPlayMusicServiceCallback);
                for (MainFragmentCallback cb : mFragmentCallbacks) {
                    cb.setPlayOrPause(mPlayMusicService.isPlaying());
                }
                try {
                    PlayListInfo info = mPlayMusicService.getCurrentPlayInfo();
                    if (info != null) {
                        setActionMusicInfo(info);
                    }
                } catch (Exception e) {
                }
            } catch (RemoteException e) {
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            mPlayMusicService = null;
            try {
                mPlayMusicService.unRegisterCallback(mPlayMusicServiceCallback);
            } catch (RemoteException e) {
            }
        }
    };

    public boolean isPlaying() {
        if (mPlayMusicService != null) {
            try {
                return mPlayMusicService.isPlaying();
            } catch (RemoteException e) {
                if (DEBUG) {
                    Log.w(TAG, "failed to play", e);
                }
            }
        }
        return false;
    }

    public int playFromLastTime() {
        if (mPlayMusicService != null) {
            try {
                return mPlayMusicService.playFromLastTime();
            } catch (RemoteException e) {
                if (DEBUG) {
                    Log.w(TAG, "failed to play", e);
                }
            }
        }
        return -1;
    }

    public void resumePlay() {
        if (mPlayMusicService != null) {
            try {
                mPlayMusicService.resume();
            } catch (RemoteException e) {
                if (DEBUG) {
                    Log.w(TAG, "failed to play", e);
                }
            }
        }
    }

    public void playNext() {
        if (mPlayMusicService != null) {
            try {
                mPlayMusicService.next();
            } catch (RemoteException e) {
                if (DEBUG) {
                    Log.w(TAG, "failed to play", e);
                }
            }
        }
    }

    public void pause() {
        if (mPlayMusicService != null) {
            try {
                mPlayMusicService.pause();
            } catch (RemoteException e) {
                if (DEBUG) {
                    Log.w(TAG, "failed to play", e);
                }
            }
        }
    }

    public void playPrevious() {
        if (mPlayMusicService != null) {
            try {
                mPlayMusicService.previous();
            } catch (RemoteException e) {
                if (DEBUG) {
                    Log.w(TAG, "failed to play", e);
                }
            }
        }
    }

    public void play(int index) {
        if (mPlayMusicService != null) {
            try {
                mPlayMusicService.play(index);
            } catch (RemoteException e) {
                if (DEBUG) {
                    Log.w(TAG, "failed to play", e);
                }
            }
        }
    }

    public boolean isInitialized() {
        if (mPlayMusicService != null) {
            try {
                return mPlayMusicService.isInitialized();
            } catch (RemoteException e) {
                if (DEBUG) {
                    Log.w(TAG, "failed to play", e);
                }
            }
        }
        return false;
    }

    public int getCurrentPosition() {
        if (mPlayMusicService != null) {
            try {
                return mPlayMusicService.getCurrentPosition();
            } catch (RemoteException e) {
                if (DEBUG) {
                    Log.w(TAG, "failed to play", e);
                }
            }
        }
        return 0;
    }

    public long getDuration() {
        if (mPlayMusicService != null) {
            try {
                return mPlayMusicService.getDuration();
            } catch (RemoteException e) {
                if (DEBUG) {
                    Log.w(TAG, "failed to play", e);
                }
            }
        }
        return 0;
    }

    public long getPlayingAlbumId() {
        if (mPlayMusicService != null) {
            try {
                return mPlayMusicService.getPlayingAlbumId();
            } catch (RemoteException e) {
                if (DEBUG) {
                    Log.w(TAG, "failed to play", e);
                }
            }
        }
        return 0;
    }

    public void notifyFavoriteChanged() {
        if (mPlayMusicService != null) {
            try {
                mPlayMusicService.notifyFavoriteChanged();
            } catch (RemoteException e) {
                if (DEBUG) {
                    Log.w(TAG, "failed to play", e);
                }
            }
        }
    }

    public void onBackPressed() {
        switch (sCurrentFragment) {
            case FRAGMENT_TYPE_MAIN:
                sCurrentFragment = -1;
                super.onBackPressed();
                break;
            case FRAGMENT_TYPE_PLAYLIST:
                switchFragment(FRAGMENT_TYPE_MAIN);
                break;
            case FRAGMENT_TYPE_MUSIC_INFO:
                switchFragment(FRAGMENT_TYPE_PLAYLIST);
                break;
            default:
                sCurrentFragment = -1;
                showVponAdIfNeeded();
                super.onBackPressed();
        }
    }

    private void showVponAdIfNeeded() {
        if (mCanShowVponInterstitialAd) {
            ++PlayMusicApplication.sAdCount;
            if (PlayMusicApplication.sAdCount % PlayMusicApplication.AD_TIME == 0) {
                mVponInterstitialAd.show();
            }
        }
    }

    public Fragment getCurrentFragment() {
        Fragment target = null;

        switch (sCurrentFragment) {
            case FRAGMENT_TYPE_MAIN:
                target = getMainFragment();
                break;
            case FRAGMENT_TYPE_PLAYLIST:
                target = getPlayListFragment();
                break;
        // case FRAGMENT_TYPE_MUSIC_INFO:
        // target = getPlayInfoFragment();
        // break;
        }
        return target;
    }

    private void reloadTheme() {
        Fragment fag = getCurrentFragment();
        if (fag != null && fag instanceof ThemeReloader) {
            ((ThemeReloader) fag).reloadTheme();
        }
    }

    public void setDisplayingAlbumName(String name) {
        mDisplayingAlbumName = name;
    }

    public String getDisplayingAlbumName() {
        return mDisplayingAlbumName;
    }
}
