
package com.bj4.u2bplayer.activity;

import java.lang.reflect.Method;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.bj4.u2bplayer.PlayMusicApplication;
import com.bj4.u2bplayer.database.U2bDatabaseHelper;
import com.bj4.u2bplayer.scanner.PlayScanner;
import com.yenhsun.u2bplayer.R;

public class U2bPlayerActivity extends Activity {
    private static final String TAG = "QQQQ";

    private static final boolean DEBUG = true && PlayMusicApplication.OVERALL_DEBUG;

    private Button mActionBarSync;

    private RelativeLayout mMainLayout;
    
    private PlayScanner mPlayScanner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.u2b_main_activity);
        initMainLayout();
        initComponents();
    }

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

    private void initComponents() {
        mActionBarSync = (Button)findViewById(R.id.action_bar_sync);
        mPlayScanner = new PlayScanner(); 
        mActionBarSync.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO start to parse?
                if (DEBUG) {
                    Log.d(TAG, "action bar -- sync pressed");
                }
                // DbDemo();

                // scan list
                mPlayScanner.scan();
            }
        });
    }

    private void DbDemo() {
        Log.i(TAG, "------start to demo------");
        U2bDatabaseHelper mDatabaseHelper = PlayMusicApplication.getDataBaseHelper();
        if (mDatabaseHelper != null) {
            // XXX example below
            ContentValues cv;
            Cursor c;
//            cv = new ContentValues();
//            cv.put(U2bDatabaseHelper.COLUMN_ARTIST, "五月天");
//            cv.put(U2bDatabaseHelper.COLUMN_ALBUM, "瘋狂世界");
//            cv.put(U2bDatabaseHelper.COLUMN_MUSIC, "瘋狂世界");
//            cv.put(U2bDatabaseHelper.COLUMN_RANK, 1);
//            mDatabaseHelper.insert(cv, true);
//            c = mDatabaseHelper.query(null, null);
//            Log.i(TAG, "demo 1 start");
//            if (c != null) {
//                while (c.moveToNext()) {
//                    String artist = c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_ARTIST));
//                    String album = c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_ALBUM));
//                    String music = c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_MUSIC));
//                    String rank = c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_RANK));
//                    Log.e(TAG, "demo1 " + artist + ", " + album + ", " + music + ", " + rank);
//
//                }
//                c.close();
//            }
//            mDatabaseHelper.clearTableContent();

            Log.i(TAG, "demo 2 start");

            ArrayList<ContentValues> cvs = new ArrayList<ContentValues>();
            cv = new ContentValues();
            cv.put(U2bDatabaseHelper.COLUMN_ARTIST, "五月天");
            cv.put(U2bDatabaseHelper.COLUMN_ALBUM, "瘋狂世界");
            cv.put(U2bDatabaseHelper.COLUMN_MUSIC, "瘋狂世界");
            cv.put(U2bDatabaseHelper.COLUMN_RANK, 1);
            cvs.add(cv);
            cv = new ContentValues();
            cv.put(U2bDatabaseHelper.COLUMN_ARTIST, "五月天");
            cv.put(U2bDatabaseHelper.COLUMN_ALBUM, "瘋狂世界");
            cv.put(U2bDatabaseHelper.COLUMN_MUSIC, "擁抱");
            cv.put(U2bDatabaseHelper.COLUMN_RANK, 2);
            cvs.add(cv);
            cv = new ContentValues();
            cv.put(U2bDatabaseHelper.COLUMN_ARTIST, "五月天");
            cv.put(U2bDatabaseHelper.COLUMN_ALBUM, "瘋狂世界");
            cv.put(U2bDatabaseHelper.COLUMN_MUSIC, "透露");
            cv.put(U2bDatabaseHelper.COLUMN_RANK, 3);
            cvs.add(cv);
            cv = new ContentValues();
            cv.put(U2bDatabaseHelper.COLUMN_ARTIST, "五月天");
            cv.put(U2bDatabaseHelper.COLUMN_ALBUM, "瘋狂世界");
            cv.put(U2bDatabaseHelper.COLUMN_MUSIC, "生活");
            cv.put(U2bDatabaseHelper.COLUMN_RANK, 4);
            cvs.add(cv);
            cv = new ContentValues();
            cv.put(U2bDatabaseHelper.COLUMN_ARTIST, "五月天");
            cv.put(U2bDatabaseHelper.COLUMN_ALBUM, "瘋狂世界");
            cv.put(U2bDatabaseHelper.COLUMN_MUSIC, "愛情的模樣");
            cv.put(U2bDatabaseHelper.COLUMN_RANK, 5);
            cvs.add(cv);
            // using bulk insert
            mDatabaseHelper.insert(cvs, true);
            cvs.add(cv);
            c = mDatabaseHelper.query(null, null);
            if (c != null) {
                while (c.moveToNext()) {
                    String artist = c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_ARTIST));
                    String album = c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_ALBUM));
                    String music = c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_MUSIC));
                    String rank = c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_RANK));
                    Log.e(TAG, "demo2 " + artist + ", " + album + ", " + music + ", " + rank);
                }
                c.close();
            }
            // mDatabaseHelper.clearTableContent();
        }
    }
}
