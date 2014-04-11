
package com.bj4.u2bplayer.activity;

import java.lang.reflect.Method;

import com.bj4.u2bplayer.PlayMusicApplication;
import com.bj4.u2bplayer.R;
import com.bj4.u2bplayer.dialogs.MainActivityOptionDialog;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class U2bPlayerMainFragmentActivity extends FragmentActivity {
    private static final String TAG = "QQQQ";

    private static final boolean DEBUG = true && PlayMusicApplication.OVERALL_DEBUG;

    private RelativeLayout mMainLayout;

    private ImageButton mOptionBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.u2b_main_activity);
        initMainLayout();
        initComponents();
        initActionBarComponents();
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
            mMainLayout = (RelativeLayout)findViewById(R.id.u2b_main_activity_main_layout);
            mMainLayout.setPadding(mMainLayout.getPaddingLeft(), statusBarHeight,
                    mMainLayout.getPaddingRight(), navigationBarHeight);
        }
    }

    private void initComponents() {
    }

    public void initActionBarComponents() {
        if (mOptionBtn == null) {
            mOptionBtn = (ImageButton)findViewById(R.id.menu);
            if (mOptionBtn == null) {
                if (DEBUG)
                    Log.w(TAG, "cannot find option button");
                return;
            }
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
                    new MainActivityOptionDialog(U2bPlayerMainFragmentActivity.this,
                            mOptionCallback, location).show(getFragmentManager(), "");
                }
            });
        }
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
}
