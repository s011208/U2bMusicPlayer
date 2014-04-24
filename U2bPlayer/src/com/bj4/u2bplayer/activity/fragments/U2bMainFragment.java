
package com.bj4.u2bplayer.activity.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.bj4.u2bplayer.R;
import com.bj4.u2bplayer.activity.U2bPlayerMainFragmentActivity;
import com.bj4.u2bplayer.database.U2bDatabaseHelper;

public class U2bMainFragment extends Fragment {
    private static final boolean DEBUG = true && PlayMusicApplication.OVERALL_DEBUG;

    public static final String TAG = "U2bMainFragment";

    private U2bPlayerMainFragmentActivity mActivity;
    
    private PlayList mPlayList;
    
    private Fragment mU2bPlayListFragment;
    
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
        mAlbumView = inflater.inflate(R.layout.u2b_main_activity, container, false);
        initComponents();
        return mAlbumView;
    }
    
    private void initComponents() {
        //分類現有清單
        queryDbData();
        //新增專輯資料夾
        addAlbumButton();
    }
    
    /**
     * 分類現有清單
     */
    private void queryDbData() {
        U2bDatabaseHelper databaseHelper = PlayMusicApplication.getDataBaseHelper();
        Cursor c = databaseHelper.query(null, null);
        String mStrAlbum = "";
        String artist = "";
        String album = "";
        String music = "";
        String rank = "";
        int intAlbumCount = 0;
        
        // print
        if (c != null) {
            while (c.moveToNext()) {
                artist = c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_ARTIST));
                album = c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_ALBUM));
                music = c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_MUSIC));
                rank = c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_RANK));
                if (DEBUG) {
                    Log.d(TAG, "PRINT " + artist + ", " + album + ", " + music + ", " + rank);
                }
                
                if(!album.equals(mStrAlbum)){
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
    private void addAlbumButton(){
        mActivity = (U2bPlayerMainFragmentActivity)getActivity();
        mVerLinearLayout = (LinearLayout)mAlbumView.findViewById(R.id.main_fragment_container);
        mHouLinearLayout = new LinearLayout(mActivity);
        
        if(mVerLinearLayout.getChildCount() == 0 && mHouLinearLayout.getChildCount() == 0){
            
            for(int i=0; i<mAlbumList.size(); i++){
                mAlbumButton = new Button(mActivity);
                albumMap = new HashMap<String, String>();
                
                if(i%2 == 0){
                    mHouLinearLayout = new LinearLayout(mActivity);
                }
    
                albumMap = mAlbumList.get(i);
                mStrAlbum = String.valueOf(albumMap.get("ALBUM"));
                mAlbumButton.setBackgroundColor(Color.BLACK);
                if(mStrAlbum.contains("KKbox")){
                    mAlbumButton.setBackgroundResource(R.drawable.kkbox);
                }else if(mStrAlbum.contains("HitFM")){
                    mAlbumButton.setBackgroundResource(R.drawable.hitfm);
                }
                
                mAlbumButton.getBackground().setAlpha(180);
                mAlbumButton.setMinimumHeight(400);
                mAlbumButton.setMinimumWidth(400);
                mAlbumButton.setMaxHeight(400);
                mAlbumButton.setMaxWidth(400);
                mAlbumButton.setText(mStrAlbum);
                mAlbumButton.setTextSize(20);
                mAlbumButton.setTextColor(Color.WHITE);
                mAlbumButton.setGravity(Gravity.BOTTOM);
                mAlbumButton.setOnClickListener(clickHandler);
                mHouLinearLayout.addView(mAlbumButton);
                
                if(i%2 == 0){
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
    
    
    private void toPlayList(String album){
        mActivity = (U2bPlayerMainFragmentActivity)getActivity();
        mPlayList = PlayList.getInstance(mActivity);
        mPlayList.setAlbumPlayList(album);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_container, getPlayListFragment());
        transaction.commitAllowingStateLoss();
    }
    
    private synchronized U2bPlayListFragment getPlayListFragment() {
        if (mU2bPlayListFragment == null) {
            mU2bPlayListFragment = new U2bPlayListFragment();
        }
        return (U2bPlayListFragment) mU2bPlayListFragment;
    }
}
