
package com.bj4.u2bplayer.activity.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.bj4.u2bplayer.R;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bj4.u2bplayer.PlayList;
import com.bj4.u2bplayer.PlayMusicApplication;
import com.bj4.u2bplayer.activity.ThemeReloader;
import com.bj4.u2bplayer.activity.U2bPlayerMainFragmentActivity;
import com.bj4.u2bplayer.database.U2bDatabaseHelper;

public class U2bMainFragment extends Fragment implements ThemeReloader {
    private static final boolean DEBUG = true && PlayMusicApplication.OVERALL_DEBUG;

    public static final String TAG = "U2bMainFragment";

    private U2bPlayerMainFragmentActivity mActivity;

    private PlayList mPlayList;

    private View mAlbumView;

    private LinearLayout mVerLinearLayout, mHouLinearLayout;

    private Button mAlbumButton;

    private ArrayList<Map<String, String>> mAlbumList = new ArrayList<Map<String, String>>();

    private Map<String, String> albumMap = new HashMap<String, String>();

    private String mStrAlbum = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAlbumView = inflater.inflate(R.layout.play_main_fragment, container, false);
        initComponents();
        return mAlbumView;
    }

    private void initComponents() {
        // 分類現有清單
        queryDbData("");
        // 新增專輯資料夾
        addAlbumButton("");
    }

    public void localMusic(String type){
        queryDbData(type);
        
        testaddAlbumButton(type);
    }
    
    /**
     * 分類現有清單
     */
    private void queryDbData(String type) {
        U2bDatabaseHelper databaseHelper = PlayMusicApplication.getDataBaseHelper();
        Cursor c ;
        if("local".equals(type)){
            mAlbumList.clear();
            c = databaseHelper.queryDataFromLocalData();
        }else{
            mAlbumList.clear();
            c = databaseHelper.query(null, null);
        }
        
        String mStrAlbum = "";
        String artist = "";
        String album = "";
        String music = "";
        String rank = "";
        int intAlbumCount = 0;

        // print
        if (c != null) {
            while (c.moveToNext()) {
//                artist = c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_ARTIST));
                album = c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_ALBUM));
//                music = c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_MUSIC));
//                rank = c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_RANK));
                if (DEBUG) {
                    Log.d(TAG, "PRINT " + artist + ", " + album + ", " + music + ", " + rank);
                }

                if (!album.equals(mStrAlbum)) {
                    intAlbumCount++;
                    mStrAlbum = album;
                    albumMap = new HashMap<String, String>();
                    albumMap.put("SERIALNUMBER", String.valueOf(intAlbumCount));
                    albumMap.put("ALBUM", mStrAlbum);
                    mAlbumList.add(albumMap);
                }
            }
            c.close();
        }
    }

    /**
     * 新增專輯資料夾
     */
    private void addAlbumButton(String type) {
        mActivity = (U2bPlayerMainFragmentActivity)getActivity();
        mVerLinearLayout = (LinearLayout)mAlbumView.findViewById(R.id.player_main_container);
        mHouLinearLayout = new LinearLayout(mActivity);

        Button button = new Button(mActivity);
        Log.d(TAG, String.valueOf(mAlbumList.size()));
        
        for (int i = 0; i < mAlbumList.size(); i++) {

            mAlbumButton = new Button(mActivity);
            albumMap = new HashMap<String, String>();

            if (i % 2 == 0) {
                mHouLinearLayout = new LinearLayout(mActivity);
                mHouLinearLayout.setGravity(Gravity.CENTER);
            }

            albumMap = mAlbumList.get(i);
            
            mStrAlbum = String.valueOf(albumMap.get("ALBUM"));
            button = (Button)mVerLinearLayout.findViewWithTag(mStrAlbum);
            Log.d(TAG, mStrAlbum + " 是否為空:" + String.valueOf(button == null));

            if (button == null) {
                mAlbumButton.setBackgroundColor(Color.BLACK);

                if (mStrAlbum.contains("KKbox")) {
                    if (mStrAlbum.contains("華語")) {
                        mAlbumButton.setBackgroundResource(R.drawable.kkbox);
                    } else if (mStrAlbum.contains("西洋")) {
                        mAlbumButton.setBackgroundResource(R.drawable.kkboxw);
                    } else if (mStrAlbum.contains("日語")) {
                        mAlbumButton.setBackgroundResource(R.drawable.kkboxj);
                    } else if (mStrAlbum.contains("韓語")) {
                        mAlbumButton.setBackgroundResource(R.drawable.kkboxk);
                    } else if (mStrAlbum.contains("台語")) {
                        mAlbumButton.setBackgroundResource(R.drawable.kkboxh);
                    } else if (mStrAlbum.contains("粵語")) {
                        mAlbumButton.setBackgroundResource(R.drawable.kkboxc);
                    } else {
                        mAlbumButton.setBackgroundResource(R.drawable.kkbox);
                    }
                } else if (mStrAlbum.contains("HitFM")) {
                    mAlbumButton.setBackgroundResource(R.drawable.hitfm);
                } else {
                    mAlbumButton.setBackgroundResource(R.drawable.ico_folder_blue);
                }

                mAlbumButton.setTag(mStrAlbum);
                mAlbumButton.getBackground().setAlpha(180);
                mAlbumButton.setMinimumHeight(400);
                mAlbumButton.setMinimumWidth(400);
                mAlbumButton.setMaxHeight(400);
                mAlbumButton.setMaxWidth(400);
                mAlbumButton.setText(mStrAlbum);
                mAlbumButton.setTextSize(25);
                mAlbumButton.setTextColor(Color.WHITE);
                mAlbumButton.setGravity(Gravity.TOP);
                mAlbumButton.setOnClickListener(clickHandler);
                mHouLinearLayout.addView(mAlbumButton);

                if (i % 2 == 0) {
                    mVerLinearLayout.addView(mHouLinearLayout);
                }
            }
        }
    }
    
    
    private void testaddAlbumButton(String type) {
        mActivity = (U2bPlayerMainFragmentActivity)getActivity();
        mVerLinearLayout = (LinearLayout)mAlbumView.findViewById(R.id.player_main_container);
        mHouLinearLayout = new LinearLayout(mActivity);
        mVerLinearLayout.removeAllViews();
        
        
        Button button = new Button(mActivity);
        Log.d(TAG, String.valueOf(mAlbumList.size()));
        
        for (int i = 0; i < mAlbumList.size(); i++) {

            mAlbumButton = new Button(mActivity);
            albumMap = new HashMap<String, String>();

            if (i % 2 == 0) {
                mHouLinearLayout = new LinearLayout(mActivity);
                mHouLinearLayout.setGravity(Gravity.CENTER);
            }

            albumMap = mAlbumList.get(i);
            
            mStrAlbum = String.valueOf(albumMap.get("ALBUM"));
            button = (Button)mVerLinearLayout.findViewWithTag(mStrAlbum);
            Log.d(TAG, mStrAlbum + " 是否為空:" + String.valueOf(button == null));

            if (button == null) {
                mAlbumButton.setBackgroundColor(Color.BLACK);

                if (mStrAlbum.contains("KKbox")) {
                    if (mStrAlbum.contains("華語")) {
                        mAlbumButton.setBackgroundResource(R.drawable.kkbox);
                    } else if (mStrAlbum.contains("西洋")) {
                        mAlbumButton.setBackgroundResource(R.drawable.kkboxw);
                    } else if (mStrAlbum.contains("日語")) {
                        mAlbumButton.setBackgroundResource(R.drawable.kkboxj);
                    } else if (mStrAlbum.contains("韓語")) {
                        mAlbumButton.setBackgroundResource(R.drawable.kkboxk);
                    } else if (mStrAlbum.contains("台語")) {
                        mAlbumButton.setBackgroundResource(R.drawable.kkboxh);
                    } else if (mStrAlbum.contains("粵語")) {
                        mAlbumButton.setBackgroundResource(R.drawable.kkboxc);
                    } else {
                        mAlbumButton.setBackgroundResource(R.drawable.kkbox);
                    }
                } else if (mStrAlbum.contains("HitFM")) {
                    mAlbumButton.setBackgroundResource(R.drawable.hitfm);
                } else {
                    mAlbumButton.setBackgroundResource(R.drawable.ico_folder_blue);
                }

                mAlbumButton.setTag(mStrAlbum);
                mAlbumButton.getBackground().setAlpha(180);
                mAlbumButton.setMinimumHeight(400);
                mAlbumButton.setMinimumWidth(400);
                mAlbumButton.setMaxHeight(400);
                mAlbumButton.setMaxWidth(400);
                mAlbumButton.setText(mStrAlbum);
                mAlbumButton.setTextSize(25);
                mAlbumButton.setTextColor(Color.WHITE);
                mAlbumButton.setGravity(Gravity.TOP);
                mAlbumButton.setOnClickListener(clickHandler);
                mHouLinearLayout.addView(mAlbumButton);

                if (i % 2 == 0) {
                    mVerLinearLayout.addView(mHouLinearLayout);
                }
            }
        }
    }

    private OnClickListener clickHandler = new OnClickListener() {
        public void onClick(View v) {
            Button button = (Button)v;
            toPlayList(String.valueOf(button.getText()));
        }
    };

    private void toPlayList(String album) {
        mActivity = (U2bPlayerMainFragmentActivity)getActivity();
        mActivity.setDisplayingAlbumName(album);
        mActivity.switchFragment(U2bPlayerMainFragmentActivity.FRAGMENT_TYPE_PLAYLIST);
    }

    @Override
    public void reloadTheme() {
        // TODO Auto-generated method stub
    }
}
