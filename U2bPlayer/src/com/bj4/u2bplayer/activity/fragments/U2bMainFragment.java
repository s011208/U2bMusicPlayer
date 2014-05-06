
package com.bj4.u2bplayer.activity.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
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
import com.bj4.u2bplayer.activity.ThemeReloader;
import com.bj4.u2bplayer.activity.U2bPlayerMainFragmentActivity;
import com.bj4.u2bplayer.database.U2bDatabaseHelper;
import com.bj4.u2bplayer.utilities.PlayListInfo;

public class U2bMainFragment extends Fragment implements ThemeReloader {
    private static final boolean DEBUG = true && PlayMusicApplication.OVERALL_DEBUG;

    public static final String TAG = "U2bMainFragment";

    private U2bPlayerMainFragmentActivity mActivity;

    private Fragment mU2bPlayListFragment = new U2bPlayListFragment();

    private PlayList mPlayList = PlayList.getInstance(mActivity);

    private final ArrayList<PlayListInfo> mPlayingList = new ArrayList<PlayListInfo>();

    private final U2bDatabaseHelper mDatabaseHelper = PlayMusicApplication.getDataBaseHelper();

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
        // 0503 �令�w�]kkbox�ػy�B�����ݭ��֡A�p���s�W�A�t�~�W�[
        // �����{���M��
        // queryDbData("");
        // // �s�W�M���Ƨ�
        // addAlbumButton("");

        // //�w�]kkbox�ػy
        addAlbum_kkboxChinese();
        // //��������
        localMusic();

    }

    public void localMusic() {
        localQueryDbData();

        localAddAlbumButton();
    }

    /**
     * �����{���M��
     */
    private void queryDbData(String type) {
        U2bDatabaseHelper databaseHelper = PlayMusicApplication.getDataBaseHelper();
        Cursor c;
        if ("local".equals(type)) {
            mAlbumList.clear();
            c = databaseHelper.queryDataFromLocalData();
        } else {
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
                // artist =
                // c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_ARTIST));
                album = c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_ALBUM));
                // music =
                // c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_MUSIC));
                // rank =
                // c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_RANK));
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
     * �s�W�M���Ƨ�
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
            Log.d(TAG, mStrAlbum + " �O�_����:" + String.valueOf(button == null));

            if (button == null) {
                mAlbumButton.setBackgroundColor(Color.BLACK);

                if (mStrAlbum.contains("KKbox")) {
                    if (mStrAlbum.contains("�ػy")) {
                        mAlbumButton.setBackgroundResource(R.drawable.kkbox);
                    } else if (mStrAlbum.contains("��v")) {
                        mAlbumButton.setBackgroundResource(R.drawable.kkboxw);
                    } else if (mStrAlbum.contains("��y")) {
                        mAlbumButton.setBackgroundResource(R.drawable.kkboxj);
                    } else if (mStrAlbum.contains("���y")) {
                        mAlbumButton.setBackgroundResource(R.drawable.kkboxk);
                    } else if (mStrAlbum.contains("�x�y")) {
                        mAlbumButton.setBackgroundResource(R.drawable.kkboxh);
                    } else if (mStrAlbum.contains("�f�y")) {
                        mAlbumButton.setBackgroundResource(R.drawable.kkboxc);
                    } else {
                        mAlbumButton.setBackgroundResource(R.drawable.kkbox);
                    }
                } else if (mStrAlbum.contains("HitFM")) {
                    mAlbumButton.setBackgroundResource(R.drawable.hitfm);
                } else {
                    mAlbumButton.setBackgroundResource(R.drawable.local);
                }

                mAlbumButton.setTag(mStrAlbum);
                mAlbumButton.getBackground().setAlpha(180);
                // mAlbumButton.setMinimumHeight(400);
                // mAlbumButton.setMinimumWidth(400);
                // mAlbumButton.setMaxHeight(400);
                // mAlbumButton.setMaxWidth(400);
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

    private void localQueryDbData() {
        U2bDatabaseHelper databaseHelper = PlayMusicApplication.getDataBaseHelper();
        Cursor c;
        mAlbumList.clear();
        c = databaseHelper.queryDataFromLocalData();

        String mStrAlbum = "";
        String artist = "";
        String album = "";
        String music = "";
        String rank = "";
        int intAlbumCount = 0;

        // print
        try {
            if (c != null) {
                while (c.moveToNext()) {
                    // artist =
                    // c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_ARTIST));
                    album = c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_ALBUM));
                    // music =
                    // c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_MUSIC));
                    // rank =
                    // c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_RANK));
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
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void localAddAlbumButton() {
        mAlbumButton = new Button(mActivity);
        mAlbumButton.setBackgroundColor(Color.WHITE);
        mAlbumButton.getBackground().setAlpha(125);
        mAlbumButton.setGravity(Gravity.BOTTOM);
        mAlbumButton.setMinimumHeight(10);
        mAlbumButton.setMinimumWidth(10);
        mAlbumButton.setMaxHeight(10);
        mAlbumButton.setMaxWidth(10);
        mVerLinearLayout.addView(mAlbumButton);

        mActivity = (U2bPlayerMainFragmentActivity)getActivity();
        mVerLinearLayout = (LinearLayout)mAlbumView.findViewById(R.id.player_main_container);
        mHouLinearLayout = new LinearLayout(mActivity);
        // mVerLinearLayout.removeAllViews();

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
            Log.d(TAG, mStrAlbum + " �O�_����:" + String.valueOf(button == null));

            // if (button == null) {
            mAlbumButton.setBackgroundColor(Color.BLACK);
            mAlbumButton.setBackgroundResource(R.drawable.local);
            mAlbumButton.setTag(mStrAlbum);
            // mAlbumButton.getBackground().setAlpha(180);
            mAlbumButton.setText(mStrAlbum);
            mAlbumButton.setWidth(10);
            mAlbumButton.setSingleLine(true);
            mAlbumButton.setEllipsize(TruncateAt.END) ;
            mAlbumButton.setTextSize(25);
            mAlbumButton.setTextColor(Color.WHITE);
            mAlbumButton.setGravity(Gravity.TOP);
            mAlbumButton.setOnClickListener(localclickHandler);
            mHouLinearLayout.addView(mAlbumButton);

            if (i % 2 == 0) {
                mVerLinearLayout.addView(mHouLinearLayout);
            }
            // }
        }
    }

    /**
     * �i�J�e���Y���ͱM��i�I��
     */
    private void addAlbum_kkboxChinese() {
        // TODO

        mActivity = (U2bPlayerMainFragmentActivity)getActivity();
        mVerLinearLayout = (LinearLayout)mAlbumView.findViewById(R.id.player_main_container);
        mHouLinearLayout = new LinearLayout(mActivity);
        mHouLinearLayout.setGravity(Gravity.CENTER);
        mAlbumButton = new Button(mActivity);

        // int[] screenValue = getScreenWidthAndSizeInPx(mActivity);
        // Log.d(TAG, screenValue[1] + "/" + screenValue[2] + " = " +
        // screenValue[1]/screenValue[2]);

        try {
            mAlbumButton.setBackgroundColor(Color.BLACK);
            mAlbumButton.setBackgroundResource(R.drawable.kkbox);
            mAlbumButton.setTag(mStrAlbum);
            mAlbumButton.getBackground().setAlpha(180);
            mAlbumButton.setText("�ػy03��KKbox Top100");
            mAlbumButton.setTextSize(25);
            mAlbumButton.setTextColor(Color.WHITE);
            mAlbumButton.setGravity(Gravity.TOP);
            mAlbumButton.setOnClickListener(default_clickHandler);
            mHouLinearLayout.addView(mAlbumButton);
            mVerLinearLayout.addView(mHouLinearLayout);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * �I�U�M��i�J�M��
     */
    private OnClickListener clickHandler = new OnClickListener() {
        public void onClick(View v) {
            Button button = (Button)v;
            toPlayList(String.valueOf(button.getText()));
        }
    };

    /**
     * �I�U�M��~�}�lparse�q���B�M��i�J�M��
     */
    private OnClickListener default_clickHandler = new OnClickListener() {
        // TODO
        public void onClick(View v) {
            Log.d(TAG, "kkbox");

            Button button = (Button)v;
            toPlayList(String.valueOf(button.getText()));
        }
    };

    /**
     * �����ݭ��֫��U�M��� �j�M�q��
     */
    private OnClickListener localclickHandler = new OnClickListener() {
        // TODO
        public void onClick(View v) {
            Log.d(TAG, "local");
            Button button = (Button)v;
            toPlayList(String.valueOf(button.getText()));

            Log.d(TAG, String.valueOf(button.getText()));

            // mPlayList.retrieveLocalPlayList();
            // getPlayListFragment().changePlayIndex();

            // mActivity = (U2bPlayerMainFragmentActivity)getActivity();
            // mActivity.setDisplayingAlbumName("�ػy03��KKbox Top100");
            // mActivity.switchFragment(U2bPlayerMainFragmentActivity.FRAGMENT_TYPE_PLAYLIST);

            // mPlayList.retrieveAllPlayList();

            // mPlayingList.clear();
            // Cursor data = mDatabaseHelper.queryDataFromLocalData();
            // U2bDatabaseHelper.convertFromLocalMusicDataCursorToPlayList(data,
            // mPlayingList);

            // mActivity.switchToLocalMusicData();

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

    /**
     * ���o�ù��e��
     * 
     * @param activity
     * @return int[0] �e�Aint[1]��
     */
    public int[] getScreenWidthAndSizeInPx(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int[] value = new int[4];
        value[0] = displayMetrics.heightPixels;
        value[1] = displayMetrics.widthPixels;
        value[2] = (int)displayMetrics.density;
        value[3] = displayMetrics.densityDpi;

        return value;
    }

    private synchronized U2bPlayListFragment getPlayListFragment() {
        if (mU2bPlayListFragment == null) {
            mU2bPlayListFragment = new U2bPlayListFragment();
        }
        return (U2bPlayListFragment)mU2bPlayListFragment;
    }
}
