
package com.bj4.u2bplayer.activity;

import java.lang.reflect.Method;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.bj4.u2bplayer.PlayMusicApplication;
import com.bj4.u2bplayer.database.U2bDatabaseHelper;
import com.bj4.u2bplayer.dialogs.MainActivityOptionDialog;
import com.bj4.u2bplayer.scanner.PlayScanner;
import com.bj4.u2bplayer.R;

public class U2bPlayerActivity extends U2bPlayerBaseActivity {
    private static final String TAG = "QQQQ";

    private static final boolean DEBUG = true && PlayMusicApplication.OVERALL_DEBUG;

    private RelativeLayout mMainLayout;

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
        int statusBarHeight = (int)getResources().getDimension(R.dimen.status_bar_height);
        int navigationBarHeight = hasNavigationBar ? (int)getResources().getDimension(
                R.dimen.navigation_bar_height) : 0;
        mMainLayout = (RelativeLayout)findViewById(R.id.u2b_main_activity_main_layout);
        mMainLayout.setPadding(mMainLayout.getPaddingLeft(), statusBarHeight,
                mMainLayout.getPaddingRight(), navigationBarHeight);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // TODO do something about transparent navigation bar
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.u2b_main_activity);
        initMainLayout();
        initComponents();
    }

    public void onResume() {
        super.onResume();
    }

    private void initComponents() {
    }

}
