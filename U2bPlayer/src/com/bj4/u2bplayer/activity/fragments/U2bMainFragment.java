
package com.bj4.u2bplayer.activity.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bj4.u2bplayer.PlayMusicApplication;
import com.bj4.u2bplayer.R;
import com.bj4.u2bplayer.activity.ThemeReloader;
import com.bj4.u2bplayer.activity.U2bPlayerMainFragmentActivity;
import com.bj4.u2bplayer.database.U2bDatabaseHelper;
import com.bj4.u2bplayer.scanner.PlayScanner;

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
    
    public static final String MUSIC_TYPE_MYFAVORITE = "我的最愛";
    
    public static final String SHARE_PREF_KEY_SOURCE_LIST = "source_list";
    
    public static final String WEB_TYPE_KKBOX = "KKBOX";
    
    public static final String SOURCE_KKBOX = "0";
    
    public static final String SOURCE_MYFAVORITE = "1";
    
    private U2bPlayerMainFragmentActivity mActivity;

    private View mAlbumView;

    private LinearLayout mVerLinearLayout, mHouLinearLayout;
    
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
        mActivity = (U2bPlayerMainFragmentActivity)getActivity();
        SharedPreferences mPref = PlayMusicApplication.getPref(mActivity);
        HashSet<String> mSet = (HashSet<String>)mPref.getStringSet(SHARE_PREF_KEY_SOURCE_LIST, new HashSet<String>());
        SourceListChanged(mSet);
        getScreenWidthAndSizeInPx(mActivity);
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
            e.printStackTrace();
            if (c != null)
                c.close();
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
            if (SOURCE_MYFAVORITE.equals(source)) {
                addAlbum(MUSIC_TYPE_MYFAVORITE, R.drawable.myfavorite);// TODO
            }
            if (SOURCE_KKBOX.equals(source)) {
                addAlbum(MUSIC_TYPE_CHINESE + MUSIC_TYPE_CHOISE, R.drawable.kkbox);
                addAlbum(MUSIC_TYPE_WESTERN + MUSIC_TYPE_CHOISE, R.drawable.kkboxw);
                addAlbum(MUSIC_TYPE_JAPANESE + MUSIC_TYPE_CHOISE, R.drawable.kkboxj);
                addAlbum(MUSIC_TYPE_KOREAN + MUSIC_TYPE_CHOISE, R.drawable.kkboxk);
                addAlbum(MUSIC_TYPE_HOKKIEN + MUSIC_TYPE_CHOISE, R.drawable.kkboxh);
                addAlbum(MUSIC_TYPE_CANTONESE + MUSIC_TYPE_CHOISE, R.drawable.kkboxc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void SourceListChanged(HashSet<String> source){
        mVerLinearLayout = (LinearLayout)mAlbumView.findViewById(R.id.player_main_container);
        mVerLinearLayout.removeAllViews();
        
        //來源音樂
        String data = "";
        if(source != null){
            Iterator<String> iterator = source.iterator();
            while(iterator.hasNext()){
                data = iterator.next();
                addAlbumSource(data);
            }
        }

        //本機音樂
        localQueryDbData();
        localAddAlbumButton();
    }
    
    /**
     * 進入畫面即產生專輯可點選
     */
    private void addAlbum(String nameAlbum, int picAlbum) {
        mActivity = (U2bPlayerMainFragmentActivity)getActivity();
        if (mHouLinearLayout.getChildCount() % 2 == 0) {
            mHouLinearLayout = new LinearLayout(mActivity);
            mHouLinearLayout.setGravity(Gravity.CENTER);
        }
        
        FrameLayout frameLayout = new FrameLayout(mActivity);  
        ImageView albumViewBG = new ImageView(mActivity);
        ImageView albumViewButton = new ImageView(mActivity);
        TextView textView = new TextView(mActivity);
        
        try {
            //底圖
            //albumViewBG.setBackgroundColor(Color.BLACK);
            
            //圖案
            albumViewButton.setBackgroundResource(picAlbum);
            albumViewButton.setTag(nameAlbum);
            albumViewButton.setOnClickListener(default_clickHandler);
            albumViewButton.setOnLongClickListener(default_longClickHandler);
            albumViewButton.setOnTouchListener(default_touchHandler);
            
            //文字
            textView.setText(nameAlbum);
            //textView.setTextSize(getResources().getDimension(R.dimen.play_list_view_artist_textsize));
            textView.setTextSize(getScreenWidthAndSizeInPx(mActivity));
            textView.setTextColor(Color.WHITE);
            textView.setBackgroundColor(Color.BLACK);
            textView.setSingleLine(true);
            textView.setEllipsize(TruncateAt.END);
            //textView.setWidth(10);
            textView.setShadowLayer(10f,   //float radius
                                        5f,  //float dx
                                        5f,  //float dy 
                                        Color.BLACK //int color
                                        );
            textView.getBackground().setAlpha(0);
            textView.setGravity(1);
            textView.setMaxEms(1);
            
            //圖文重疊
            frameLayout.addView(albumViewBG);
            frameLayout.addView(albumViewButton);
            frameLayout.addView(textView);
            
            //新增到清單
            mHouLinearLayout.addView(frameLayout);
            mVerLinearLayout.addView(mHouLinearLayout);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void localAddAlbumButton() {
        FrameLayout frameLayout = new FrameLayout(mActivity);  
        ImageView albumViewButton = new ImageView(mActivity);
        TextView textView = new TextView(mActivity);
        
        try {
            //分割線
            textView = new TextView(mActivity);
            textView.setBackgroundColor(Color.WHITE);
            textView.getBackground().setAlpha(190);
            textView.setGravity(Gravity.BOTTOM);
            mVerLinearLayout.addView(textView);

            mActivity = (U2bPlayerMainFragmentActivity)getActivity();
            mVerLinearLayout = (LinearLayout)mAlbumView.findViewById(R.id.player_main_container);
            mHouLinearLayout = new LinearLayout(mActivity);

            for (int i = 0; i < mAlbumList.size(); i++) {
                frameLayout = new FrameLayout(mActivity);
                albumViewButton = new ImageView(mActivity);
                textView = new TextView(mActivity);
                
                albumMap = new HashMap<String, String>();

                if (i % 2 == 0) {
                    mHouLinearLayout = new LinearLayout(mActivity);
                    mHouLinearLayout.setGravity(Gravity.CENTER);
                }

                albumMap = mAlbumList.get(i);
                mStrAlbum = String.valueOf(albumMap.get("ALBUM"));
  
                //圖案
                albumViewButton.setBackgroundResource(R.drawable.local);
                albumViewButton.setTag(mStrAlbum);
                albumViewButton.getBackground().setAlpha(180);
                albumViewButton.setOnClickListener(localclickHandler);
                albumViewButton.setOnTouchListener(default_touchHandler);
                
                //文字
                textView.setText(mStrAlbum);
                textView.setTextSize(getScreenWidthAndSizeInPx(mActivity));
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.BLACK);
                textView.setSingleLine(true);
                textView.setEllipsize(TruncateAt.END);
                textView.setWidth(4);
                textView.setShadowLayer(10f,   //float radius
                                            5f,  //float dx
                                            5f,  //float dy 
                                            Color.BLACK //int color
                                            );
                textView.getBackground().setAlpha(0);
                textView.setGravity(1);
                textView.setMaxEms(1);
                
                //圖文重疊
                frameLayout.addView(albumViewButton);
                frameLayout.addView(textView);
                
                
                mHouLinearLayout.addView(frameLayout);
                if (i % 2 == 0) {
                    mVerLinearLayout.addView(mHouLinearLayout);
                }
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 點下專輯進入清單
     */
    private OnClickListener default_clickHandler = new OnClickListener() {
        public void onClick(View v) {
            /* 由OnTouchListener實做即可，過method拿掉會當掉
            ImageView AlbumView = (ImageView)v;
            Log.d(TAG, AlbumView.getTag().toString());
            if(MUSIC_TYPE_MYFAVORITE.equals(AlbumView.getTag())){
                Log.d(TAG, "進入我的最愛 click");                
            }else{
                toPlayList(String.valueOf(AlbumView.getTag()));
            }
            */
        }
    };
    
    /**
     * 長按即更新專輯
     */
    private OnLongClickListener default_longClickHandler = new OnLongClickListener() {
        public boolean onLongClick(View v) {
            mActivity = (U2bPlayerMainFragmentActivity)getActivity();
            PlayScanner playScanner = new PlayScanner();
            ImageView AlbumView = (ImageView)v;

            if (MUSIC_TYPE_MYFAVORITE.equals(AlbumView.getTag().toString()))
                return false;

            playScanner.scan(WEB_TYPE_KKBOX, AlbumView.getTag().toString().substring(0, 2), null);
            Toast.makeText(mActivity, "start scan process: " + AlbumView.getTag().toString(),
                    Toast.LENGTH_LONG).show();

            return false;
        }
    };
    
    /**
     * 點擊專輯回饋
     */
    private OnTouchListener default_touchHandler = new OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            ImageView AlbumView = (ImageView)v;
            
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                AlbumView.getBackground().setAlpha(150);
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {  
                AlbumView.getBackground().setAlpha(255);
                toPlayList(String.valueOf(AlbumView.getTag()));
            }
            return false;
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
    
    public int getScreenWidthAndSizeInPx(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int mTextSize = 0;
        int heigh = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        int density = (int)displayMetrics.density;
        int dpi = displayMetrics.densityDpi;

        Log.d(TAG, "heightPixels: "+String.valueOf(heigh));
        Log.d(TAG, "widthPixels: "+String.valueOf(width));
        Log.d(TAG, "density: "+String.valueOf(density));
        Log.d(TAG, "densityDpi: "+String.valueOf(dpi));
        
        if (width/density > 700) {
            mTextSize = 50;
        } else if (width/density > 500) {
            mTextSize = 40;
        } else if (width/density > 300) {
            mTextSize = 30;
        } else {
            mTextSize = 20;
        }
        
        return mTextSize;
    }

}
