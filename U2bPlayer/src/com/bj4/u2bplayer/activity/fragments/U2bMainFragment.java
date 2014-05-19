
package com.bj4.u2bplayer.activity.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bj4.u2bplayer.PlayList;
import com.bj4.u2bplayer.PlayMusicApplication;
import com.bj4.u2bplayer.R;
import com.bj4.u2bplayer.activity.ThemeReloader;
import com.bj4.u2bplayer.activity.U2bPlayerMainFragmentActivity;
import com.bj4.u2bplayer.database.U2bDatabaseHelper;
import com.bj4.u2bplayer.dialogs.DataSourceDialog;

public class U2bMainFragment extends Fragment implements ThemeReloader {
    private static final boolean DEBUG = true && PlayMusicApplication.OVERALL_DEBUG;

    public static final String TAG = "U2bMainFragment";

    public static final String MUSIC_TYPE_CHINESE = "華語";

    public static final String MUSIC_TYPE_WESTERN = "西洋";

    public static final String MUSIC_TYPE_JAPANESE = "日語";

    public static final String MUSIC_TYPE_KOREAN = "韓語";

    public static final String MUSIC_TYPE_HOKKIEN = "台語";

    public static final String MUSIC_TYPE_CANTONESE = "粵語";
    
    public static final String MUSIC_TYPE_CHOISE = "精選";    
    
    private U2bPlayerMainFragmentActivity mActivity;

    private PlayList mPlayList;

    private View mAlbumView;

    private LinearLayout mVerLinearLayout, mHouLinearLayout;

    private FrameLayout mFrameLayout;
    
    private ImageView mAlbumViewButton;

    private TextView mTextView;
    
//    private LayoutInflater mLayoutInflater;
    
    private ArrayList<Map<String, String>> mAlbumList = new ArrayList<Map<String, String>>();

    private Map<String, String> albumMap = new HashMap<String, String>();

    private String mStrAlbum = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlayList = PlayList.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAlbumView = inflater.inflate(R.layout.play_main_fragment, container, false);
        initComponents();
        return mAlbumView;
    }

    private void initComponents() {

        //根據所選取的來源 加入專輯
        addAlbumSource("0");//TODO 暫定來源0為kkbox
        // //本機音樂
        localQueryDbData();
        localAddAlbumButton();

    }

    /**
     * 搜尋本機音樂
     */
    private void localQueryDbData() {
        U2bDatabaseHelper databaseHelper = PlayMusicApplication.getDataBaseHelper();
        Cursor c;
        mAlbumList.clear();
        c = databaseHelper.queryDataFromLocalData();

        String album = "";
        int intAlbumCount = 0;

        // print
        try {
            if (c != null) {
                while (c.moveToNext()) {
                    album = c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_ALBUM));

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
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * 根據來源 加入專輯
     * @param sourceType
     */
    public void addAlbumSource(String source){
        mActivity = (U2bPlayerMainFragmentActivity)getActivity();
        mVerLinearLayout = (LinearLayout)mAlbumView.findViewById(R.id.player_main_container);
        
        mHouLinearLayout = new LinearLayout(mActivity);
        mHouLinearLayout.setGravity(Gravity.CENTER);
        try {
            if("0".equals(source)){
                addAlbum(MUSIC_TYPE_CHINESE + MUSIC_TYPE_CHOISE, R.drawable.kkbox);
                addAlbum(MUSIC_TYPE_WESTERN + MUSIC_TYPE_CHOISE, R.drawable.kkboxw);
                addAlbum(MUSIC_TYPE_JAPANESE + MUSIC_TYPE_CHOISE, R.drawable.kkboxj);
                addAlbum(MUSIC_TYPE_KOREAN + MUSIC_TYPE_CHOISE, R.drawable.kkboxk);
                addAlbum(MUSIC_TYPE_HOKKIEN + MUSIC_TYPE_CHOISE, R.drawable.kkboxh);
                addAlbum(MUSIC_TYPE_CANTONESE + MUSIC_TYPE_CHOISE, R.drawable.kkboxc);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
//    public void removeAlbumSource(ViewGroup parent){
//        Log.d(TAG, "REMOVE ALL");
//
//        mVerLinearLayout = (LinearLayout)mAlbumView.findViewById(R.id.player_main_container);
//        mVerLinearLayout.removeAllViews();
////        //本機音樂
////        localQueryDbData();
////        localAddAlbumButton();
//    }
    
    /**
     * 進入畫面即產生專輯可點選
     */
    private void addAlbum(String nameAlbum, int picAlbum) {
        mActivity = (U2bPlayerMainFragmentActivity)getActivity();
        if (mHouLinearLayout.getChildCount() % 2 == 0) {
            mHouLinearLayout = new LinearLayout(mActivity);
            mHouLinearLayout.setGravity(Gravity.CENTER);
        }
        mFrameLayout = new FrameLayout(mActivity);
        mAlbumViewButton = new ImageView(mActivity);
        mTextView = new TextView(mActivity);

        try {
            //圖案
            mAlbumViewButton.setBackgroundColor(Color.BLACK);
            mAlbumViewButton.setBackgroundResource(picAlbum);
            mAlbumViewButton.setTag(nameAlbum);
            mAlbumViewButton.getBackground().setAlpha(180);
            mAlbumViewButton.setOnClickListener(default_clickHandler);
            
            //文字
            mTextView.setText(nameAlbum);
            mTextView.setTextSize(getResources().getDimension(R.dimen.action_bar_item_size));
            mTextView.setTextColor(Color.WHITE);
            mTextView.setBackgroundColor(Color.BLACK);
            mTextView.setSingleLine(true);
            mTextView.setEllipsize(TruncateAt.END);
            mTextView.setMinimumHeight(4);
            mTextView.setMinimumWidth(4);
            mTextView.setMaxHeight(8);
            mTextView.setMaxWidth(8);
            mTextView.setShadowLayer(10f,   //float radius
                                        5f,  //float dx
                                        5f,  //float dy 
                                        Color.BLACK //int color
                                        );
            mTextView.getBackground().setAlpha(0);
            mTextView.setGravity(1);
            mTextView.setMaxEms(1);
            
            //圖文重疊
            mFrameLayout.addView(mAlbumViewButton);
            mFrameLayout.addView(mTextView);
            
            //新增到清單
            mHouLinearLayout.addView(mFrameLayout);
            mVerLinearLayout.addView(mHouLinearLayout);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void localAddAlbumButton() {
        try {
            //分割線
            mTextView = new TextView(mActivity);
            mTextView.setBackgroundColor(Color.WHITE);
            mTextView.getBackground().setAlpha(190);
            mTextView.setGravity(Gravity.BOTTOM);
            mTextView.setMinimumHeight(5);
            mTextView.setMinimumWidth(5);
            mTextView.setMaxHeight(8);
            mTextView.setMaxWidth(8);
            mVerLinearLayout.addView(mTextView);

            mActivity = (U2bPlayerMainFragmentActivity)getActivity();
            mVerLinearLayout = (LinearLayout)mAlbumView.findViewById(R.id.player_main_container);
            mHouLinearLayout = new LinearLayout(mActivity);

            Button button = new Button(mActivity);
            Log.d(TAG, String.valueOf(mAlbumList.size()));

            for (int i = 0; i < mAlbumList.size(); i++) {
                mFrameLayout = new FrameLayout(mActivity);
                mAlbumViewButton = new ImageView(mActivity);
                mTextView = new TextView(mActivity);
                
                albumMap = new HashMap<String, String>();

                if (i % 2 == 0) {
                    mHouLinearLayout = new LinearLayout(mActivity);
                    mHouLinearLayout.setGravity(Gravity.CENTER);
                }

                albumMap = mAlbumList.get(i);

                mStrAlbum = String.valueOf(albumMap.get("ALBUM"));
                button = (Button)mVerLinearLayout.findViewWithTag(mStrAlbum);
                Log.d(TAG, mStrAlbum + " 是否為空:" + String.valueOf(button == null));

              //圖案
                mAlbumViewButton.setBackgroundColor(Color.BLACK);
                mAlbumViewButton.setBackgroundResource(R.drawable.local);
                mAlbumViewButton.setTag(mStrAlbum);
                mAlbumViewButton.getBackground().setAlpha(180);
                mAlbumViewButton.setOnClickListener(localclickHandler);
                            
                //文字
                mTextView.setText(mStrAlbum);
                mTextView.setTextSize(getResources().getDimension(R.dimen.action_bar_item_size));
                mTextView.setTextColor(Color.WHITE);
                mTextView.setBackgroundColor(Color.BLACK);
                mTextView.setSingleLine(true);
                mTextView.setEllipsize(TruncateAt.END);
                mTextView.setWidth(10);
                mTextView.setShadowLayer(10f,   //float radius
                                            5f,  //float dx
                                            5f,  //float dy 
                                            Color.BLACK //int color
                                            );
                mTextView.getBackground().setAlpha(0);
                
                //圖文重疊
                mFrameLayout.addView(mAlbumViewButton);
                mFrameLayout.addView(mTextView);
                
                
                mHouLinearLayout.addView(mFrameLayout);
                if (i % 2 == 0) {
                    mVerLinearLayout.addView(mHouLinearLayout);
                }
            }
        } catch (NotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * 點下專輯進入清單
     */
    private OnClickListener default_clickHandler = new OnClickListener() {
        public void onClick(View v) {
            ImageView AlbumView = (ImageView)v;
            Log.d(TAG, AlbumView.getTag().toString());
            toPlayList(String.valueOf(AlbumView.getTag()));
        }
    };

    /**
     * 本機-點下專輯進入清單
     */
    private OnClickListener localclickHandler = new OnClickListener() {
        public void onClick(View v) {
            ImageView AlbumView = (ImageView)v;
            Log.d(TAG, AlbumView.getTag().toString());
            toPlayList(String.valueOf(AlbumView.getTag()));
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
