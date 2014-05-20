
package com.bj4.u2bplayer.activity.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils.TruncateAt;
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

    public static final String MUSIC_TYPE_CHINESE = "�ػy";

    public static final String MUSIC_TYPE_WESTERN = "��v";

    public static final String MUSIC_TYPE_JAPANESE = "��y";

    public static final String MUSIC_TYPE_KOREAN = "���y";

    public static final String MUSIC_TYPE_HOKKIEN = "�x�y";

    public static final String MUSIC_TYPE_CANTONESE = "�f�y";
    
    public static final String MUSIC_TYPE_CHOISE = "���";    
    
    public static final String SHARE_PREF_KEY_SOURCE_LIST = "source_list";
    
    public static final String WEB_TYPE_KKBOX = "KKBOX";
    
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
    }

    /**
     * �j�M��������
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
            if (c != null)
                c.close();
        }
    }
    
    /**
     * �ھڨӷ� �[�J�M��
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
    
    public void SourceListChanged(HashSet<String> source){
        mVerLinearLayout = (LinearLayout)mAlbumView.findViewById(R.id.player_main_container);
        mVerLinearLayout.removeAllViews();
        
        //�ӷ�����
        String data = "";
        if(source != null){
            Iterator<String> iterator = source.iterator();
            while(iterator.hasNext()){
                data = iterator.next();
                addAlbumSource(data);
            }
        }

        //��������
        localQueryDbData();
        localAddAlbumButton();
    }
    
    /**
     * �i�J�e���Y���ͱM��i�I��
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
            //����
            albumViewBG.setBackgroundColor(Color.WHITE);
            albumViewBG.getBackground().setAlpha(100);
            
            //�Ϯ�
            albumViewButton.setBackgroundResource(picAlbum);
            albumViewButton.setTag(nameAlbum);
            albumViewButton.getBackground().setAlpha(150);
            albumViewButton.setOnClickListener(default_clickHandler);
            albumViewButton.setOnLongClickListener(default_longClickHandler);
            albumViewButton.setOnTouchListener(default_touchHandler);
            
            //��r
            textView.setText(nameAlbum);
            textView.setTextSize(getResources().getDimension(R.dimen.action_bar_item_size));
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
            
            //�Ϥ孫�|
            frameLayout.addView(albumViewBG);
            frameLayout.addView(albumViewButton);
            frameLayout.addView(textView);
            
            //�s�W��M��
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
            //���νu
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
  
                //�Ϯ�
                albumViewButton.setBackgroundResource(R.drawable.local);
                albumViewButton.setTag(mStrAlbum);
                albumViewButton.getBackground().setAlpha(180);
                albumViewButton.setOnClickListener(localclickHandler);
                albumViewButton.setOnTouchListener(default_touchHandler);
                
                //��r
                textView.setText(mStrAlbum);
                textView.setTextSize(getResources().getDimension(R.dimen.action_bar_item_size));
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
                
                //�Ϥ孫�|
                frameLayout.addView(albumViewButton);
                frameLayout.addView(textView);
                
                
                mHouLinearLayout.addView(frameLayout);
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
     * �I�U�M��i�J�M��
     */
    private OnClickListener default_clickHandler = new OnClickListener() {
        public void onClick(View v) {
            ImageView AlbumView = (ImageView)v;
            Log.d(TAG, AlbumView.getTag().toString());
            toPlayList(String.valueOf(AlbumView.getTag()));
        }
    };
    
    /**
     * �����Y��s�M��
     */
    private OnLongClickListener default_longClickHandler = new OnLongClickListener() {
        public boolean onLongClick(View v) {
            mActivity = (U2bPlayerMainFragmentActivity)getActivity();
            PlayScanner playScanner = new PlayScanner();
            ImageView AlbumView = (ImageView)v;
            playScanner.scan(WEB_TYPE_KKBOX, AlbumView.getTag().toString().substring(0, 2), null);
            Toast.makeText(mActivity, "start scan process: " + AlbumView.getTag().toString(),
                    Toast.LENGTH_LONG).show();
            
            return false;
        }
    };
    
    private OnTouchListener default_touchHandler = new OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            ImageView AlbumView = (ImageView)v;
            
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                AlbumView.getBackground().setAlpha(255);
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {  
                AlbumView.getBackground().setAlpha(150);
                toPlayList(String.valueOf(AlbumView.getTag()));
            }
            return false;
        }
    };
    
    

    /**
     * ����-�I�U�M��i�J�M��
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
