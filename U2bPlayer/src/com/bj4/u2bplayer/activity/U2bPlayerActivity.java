
package com.bj4.u2bplayer.activity;

import java.lang.reflect.Method;

import com.yenhsun.u2bplayer.R;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

public class U2bPlayerActivity extends Activity {
    private static final String TAG = "QQQQ";

    private static final boolean DEBUG = true;

    private Button mActionBarSync;
    
    private RelativeLayout mMainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.u2b_main_activity);
        initMainLayout();
        initComponents();
    }

    private void initMainLayout(){
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
    
    private void initComponents() {
        mActionBarSync = (Button)findViewById(R.id.action_bar_sync);
        mActionBarSync.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO start to parse?
                if (DEBUG) {
                    Log.d(TAG, "action bar -- sync pressed");
                }
            }
        });
    }
}
