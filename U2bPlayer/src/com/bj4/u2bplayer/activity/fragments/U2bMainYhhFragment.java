
package com.bj4.u2bplayer.activity.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bj4.u2bplayer.PlayMusicApplication;
import com.bj4.u2bplayer.R;
import com.bj4.u2bplayer.activity.ThemeReloader;
import com.bj4.u2bplayer.activity.U2bPlayerMainFragmentActivity;
import com.bj4.u2bplayer.database.U2bDatabaseHelper;
import com.bj4.u2bplayer.scanner.PlayScanner;
import com.bj4.u2bplayer.service.PlayMusicService;

public class U2bMainYhhFragment extends Fragment implements ThemeReloader {
    private static final boolean DEBUG = false && PlayMusicApplication.OVERALL_DEBUG;

    public static final String TAG = "U2bMainFragment";

    public static final String MUSIC_TYPE_CHINESE = "華語";

    public static final String MUSIC_TYPE_WESTERN = "西洋";

    public static final String MUSIC_TYPE_JAPANESE = "日語";

    public static final String MUSIC_TYPE_KOREAN = "韓語";

    public static final String MUSIC_TYPE_HOKKIEN = "台語";

    public static final String MUSIC_TYPE_CANTONESE = "粵語";

    public static final String MUSIC_TYPE_CHOISE = "精選";

    public static String MUSIC_TYPE_MYFAVORITE;

    public static final String SHARE_PREF_KEY_SOURCE_LIST = "source_list";

    public static final String WEB_TYPE_KKBOX = "KKBOX";

    public static final String SOURCE_KKBOX = "0";

    public static final String SOURCE_MYFAVORITE = "1";

    private U2bPlayerMainFragmentActivity mActivity;

    private Context mContext;

    public static final String INTENT_ACTION_PAUSE = "pause";

    private View mAlbumView;

    private LinearLayout mVerLinearLayout, mHouLinearLayout;

    private ArrayList<Map<String, String>> mAlbumList = new ArrayList<Map<String, String>>();

    private ArrayList<HashMap<String, String>> mLocalAlbumList = new ArrayList<HashMap<String, String>>();

    private static final String LOCAL_ALBUM_LIST_ALBUM_NAME = "ALBUM";

    private static final String LOCAL_ALBUM_LIST_SERIALNUMBER = "SERIALNUMBER";

    private Map<String, String> albumMap = new HashMap<String, String>();

    private String mStrAlbum = "";

    private ViewPager mMainPager;

    private MainPagerAdapter mMainPagerAdapter;

    class MainPagerAdapter extends PagerAdapter {

        private final ArrayList<View> mContent = new ArrayList<View>();

        private LayoutInflater mInflater;

        // local
        private ListView mLocalListView;

        private LocalMusicListAdapter mLocalListAdapter;

        // web
        private GridView mWebGridView;

        private WebMusicListAdapter mWebMusicListAdapter;

        MainPagerAdapter() {
            mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            initLocalMusicList();
            initWebMusicList();
            initComponents();
        }

        private void initComponents() {
            mContent.add(mLocalListView);
            mContent.add(mWebGridView);
        }

        private void initWebMusicList() {
            mWebGridView = (GridView)mInflater.inflate(
                    R.layout.play_main_yhh_fragment_web_music_grid, null);
            mWebMusicListAdapter = new WebMusicListAdapter();
            mWebGridView.setAdapter(mWebMusicListAdapter);
            mWebGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
                        long id) {
                    final String albumName = mWebMusicListAdapter.getItem(position).mAlbumName;
                    final PlayScanner playScanner = new PlayScanner();

                    if (MUSIC_TYPE_MYFAVORITE.equals(albumName))
                        return false;

                    AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                    dialog.setTitle(R.string.play_main_update_dialog_update);
                    dialog.setMessage(mContext
                            .getString(R.string.play_main_update_dialog_update_text) + albumName);
                    dialog.setIcon(android.R.drawable.ic_dialog_alert);
                    dialog.setCancelable(false);
                    dialog.setPositiveButton(mContext.getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(
                                            mContext,
                                            mContext.getString(R.string.spider_service_taost_start_to_scan)
                                                    + " " + albumName, Toast.LENGTH_LONG).show();
                                    playScanner.scan(WEB_TYPE_KKBOX, albumName.substring(0, 2),
                                            null);
                                    dialog.dismiss();
                                }
                            });
                    dialog.setNegativeButton(mContext.getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    dialog.show();
                    return true;
                }
            });

            mWebGridView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String albumName = mWebMusicListAdapter.getItem(position).mAlbumName;
                    toPlayList(albumName);
                }
            });
        }

        class WebMusicListAdapter extends BaseAdapter {
            class WebData {
                String mAlbumName;

                int mAlbumThumbnail;

                public WebData(String albumName, int albumThumbnail) {
                    mAlbumName = albumName;
                    mAlbumThumbnail = albumThumbnail;
                }
            }

            private final ArrayList<WebData> mData = new ArrayList<WebData>();

            public WebMusicListAdapter() {
                refreshGridContent();
            }

            private void refreshGridContent() {
                SharedPreferences pref = PlayMusicApplication.getPref(mActivity);
                HashSet<String> sourceSet = (HashSet<String>)pref.getStringSet(
                        SHARE_PREF_KEY_SOURCE_LIST, new HashSet<String>());
                Iterator<String> sourceSetIter = sourceSet.iterator();
                while (sourceSetIter.hasNext()) {
                    String source = sourceSetIter.next();
                    if (SOURCE_MYFAVORITE.equals(source)) {
                        mData.add(new WebData(MUSIC_TYPE_MYFAVORITE, R.drawable.myfavorite));
                    } else if (SOURCE_KKBOX.equals(source)) {
                        mData.add(new WebData(MUSIC_TYPE_CHINESE + MUSIC_TYPE_CHOISE,
                                R.drawable.kkbox));
                        mData.add(new WebData(MUSIC_TYPE_WESTERN + MUSIC_TYPE_CHOISE,
                                R.drawable.kkboxw));
                        mData.add(new WebData(MUSIC_TYPE_JAPANESE + MUSIC_TYPE_CHOISE,
                                R.drawable.kkboxj));
                        mData.add(new WebData(MUSIC_TYPE_KOREAN + MUSIC_TYPE_CHOISE,
                                R.drawable.kkboxk));
                        mData.add(new WebData(MUSIC_TYPE_HOKKIEN + MUSIC_TYPE_CHOISE,
                                R.drawable.kkboxh));
                        mData.add(new WebData(MUSIC_TYPE_CANTONESE + MUSIC_TYPE_CHOISE,
                                R.drawable.kkboxc));
                    }
                }
            }

            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return mData.size();
            }

            @Override
            public WebData getItem(int arg0) {
                // TODO Auto-generated method stub
                return mData.get(arg0);
            }

            @Override
            public long getItemId(int arg0) {
                // TODO Auto-generated method stub
                return arg0;
            }

            @Override
            public View getView(int position, View container, ViewGroup parent) {
                ViewHolder holder;
                if (container == null) {
                    holder = new ViewHolder();
                    container = mInflater.inflate(
                            R.layout.play_main_yhh_fragment_web_music_grid_data, null);
                    holder.mAlbumThumbnail = (ImageView)container
                            .findViewById(R.id.play_main_yhh_grid_img);
                    holder.mAlbumTitle = (TextView)container
                            .findViewById(R.id.play_main_yhh_grid_album_title);
                    container.setTag(holder);
                } else {
                    holder = (ViewHolder)container.getTag();
                }
                WebData data = mData.get(position);
                holder.mAlbumThumbnail.setImageResource(data.mAlbumThumbnail);
                holder.mAlbumTitle.setText(data.mAlbumName);
                return container;
            }

            class ViewHolder {
                TextView mAlbumTitle;

                ImageView mAlbumThumbnail;
            }

            @Override
            public void notifyDataSetChanged() {
                refreshGridContent();
                super.notifyDataSetChanged();
            }
        }

        private void initLocalMusicList() {
            mLocalListView = (ListView)mInflater.inflate(
                    R.layout.play_main_yhh_fragment_local_music_list, null);
            mLocalListView.setDivider(new ColorDrawable(mContext.getResources().getColor(
                    R.color.play_main_list_view_divider_color)));
            mLocalListView.setDividerHeight(2);
            mLocalListAdapter = new LocalMusicListAdapter();
            mLocalListView.setAdapter(mLocalListAdapter);
            mLocalListView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String albumName = mLocalListAdapter.getItem(position).get(
                            LOCAL_ALBUM_LIST_ALBUM_NAME);
                    toPlayList(albumName);
                }
            });
        }

        class LocalMusicListAdapter extends BaseAdapter {

            public LocalMusicListAdapter() {
                localQueryDbData();
            }

            @Override
            public void notifyDataSetChanged() {
                localQueryDbData();
                super.notifyDataSetChanged();
            }

            @Override
            public int getCount() {
                return mLocalAlbumList.size();
            }

            @Override
            public HashMap<String, String> getItem(int position) {
                // TODO Auto-generated method stub
                return mLocalAlbumList.get(position);
            }

            @Override
            public long getItemId(int position) {
                // TODO Auto-generated method stub
                return position;
            }

            @Override
            public View getView(int position, View container, ViewGroup parent) {
                ViewHolder holder;
                if (container == null) {
                    container = mInflater.inflate(
                            R.layout.play_main_yhh_fragment_local_music_list_row, null);
                    holder = new ViewHolder();
                    holder.mAlbumName = (TextView)container.findViewById(R.id.album_title);
                    container.setTag(holder);
                } else {
                    holder = (ViewHolder)container.getTag();
                }
                HashMap<String, String> viewData = getItem(position);
                String albumName = viewData.get(LOCAL_ALBUM_LIST_ALBUM_NAME);
                holder.mAlbumName.setText(albumName);
                return container;
            }

            private class ViewHolder {
                TextView mAlbumName;
            }
        }

        @Override
        public void destroyItem(View v, int position, Object arg2) {
            ((ViewPager)v).removeView(mContent.get(position));
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public int getCount() {
            return mContent.size();
        }

        @Override
        public Object instantiateItem(View v, int position) {
            ((ViewPager)v).addView(mContent.get(position));
            return mContent.get(position);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }

        @Override
        public int getItemPosition(Object object) {
            return mContent.indexOf(object);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAlbumView = inflater.inflate(R.layout.play_main_yhh_fragment, container, false);
        initComponents();
        return mAlbumView;
    }

    private void initPager() {
        mMainPager = (ViewPager)mAlbumView.findViewById(R.id.play_main_fragment_viewpager);
        mMainPagerAdapter = new MainPagerAdapter();
        mMainPager.setAdapter(mMainPagerAdapter);
    }

    private void initComponents() {
        initPager();

        // mActivity = (U2bPlayerMainFragmentActivity)getActivity();
        // SharedPreferences mPref = PlayMusicApplication.getPref(mActivity);
        // HashSet<String> mSet =
        // (HashSet<String>)mPref.getStringSet(SHARE_PREF_KEY_SOURCE_LIST,
        // new HashSet<String>());
        // SourceListChanged(mSet);
        // getScreenWidthAndSizeInPx(mActivity);
    }

    public void SourceListChanged(HashSet<String> source) {
        mActivity = (U2bPlayerMainFragmentActivity)getActivity();
        mVerLinearLayout = (LinearLayout)mAlbumView.findViewById(R.id.player_main_container);
        mVerLinearLayout.removeAllViews();
        try {
            // 來源音樂
            String data = "";
            if (source != null) {
                Iterator<String> iterator = source.iterator();
                while (iterator.hasNext()) {
                    data = iterator.next();
                    addAlbumSource(data);
                }
            }

            // 本機音樂
            localQueryDbData();
            localAddAlbumButton();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根據來源 加入專輯
     * 
     * @param sourceType
     */
    public void addAlbumSource(String source) {
        mActivity = (U2bPlayerMainFragmentActivity)getActivity();
        mVerLinearLayout = (LinearLayout)mAlbumView.findViewById(R.id.player_main_container);

        mHouLinearLayout = new LinearLayout(mActivity);
        mHouLinearLayout.setGravity(Gravity.CENTER);
        try {
            if (SOURCE_MYFAVORITE.equals(source)) {
                addAlbum(MUSIC_TYPE_MYFAVORITE, R.drawable.myfavorite);
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

    /**
     * 搜尋本機音樂
     */
    private void localQueryDbData() {
        U2bDatabaseHelper databaseHelper = PlayMusicApplication.getDataBaseHelper();
        Cursor c;
        mLocalAlbumList.clear();
        c = databaseHelper.queryDataFromLocalData();

        String album = "";
        int intAlbumCount = 0;

        // print
        try {
            if (c != null) {
                HashMap<String, String> albumMap;
                String strAlbum = null;
                while (c.moveToNext()) {
                    album = c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_ALBUM));

                    if (!album.equals(strAlbum)) {
                        intAlbumCount++;
                        strAlbum = album;
                        albumMap = new HashMap<String, String>();
                        albumMap.put(LOCAL_ALBUM_LIST_SERIALNUMBER, String.valueOf(intAlbumCount));
                        albumMap.put(LOCAL_ALBUM_LIST_ALBUM_NAME, strAlbum);
                        mLocalAlbumList.add(albumMap);
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
     * 進入畫面即產生專輯可點選
     */
    private void addAlbum(String nameAlbum, int picAlbum) {
        mActivity = (U2bPlayerMainFragmentActivity)getActivity();
        if (mHouLinearLayout.getChildCount() % 2 == 0) {
            mHouLinearLayout = new LinearLayout(mActivity);
            mHouLinearLayout.setGravity(Gravity.CENTER);
        }

        FrameLayout frameLayout = new FrameLayout(mActivity);
        ImageView albumViewButton = new ImageView(mActivity);
        TextView textView = new TextView(mActivity);

        try {

            // 圖案
            albumViewButton.setBackgroundResource(picAlbum);
            albumViewButton.setTag(nameAlbum);
            albumViewButton.getBackground().setAlpha(255);
            albumViewButton.setOnClickListener(default_clickHandler);
            albumViewButton.setOnLongClickListener(default_longClickHandler);

            // 文字
            textView.setText(nameAlbum);
            textView.setTextSize(getScreenWidthAndSizeInPx(mActivity));
            textView.setTextColor(Color.WHITE);
            textView.setBackgroundColor(Color.BLACK);
            textView.setSingleLine(true);
            textView.setEllipsize(TruncateAt.END);
            textView.setShadowLayer(10f, // float radius
                    5f, // float dx
                    5f, // float dy
                    Color.BLACK // int color
            );
            textView.getBackground().setAlpha(0);
            textView.setGravity(1);
            textView.setMaxEms(1);

            // 圖文重疊
            frameLayout.addView(albumViewButton);
            frameLayout.addView(textView);

            // 新增到清單
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
            // 分割線
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
                mStrAlbum = String.valueOf(albumMap.get(LOCAL_ALBUM_LIST_ALBUM_NAME));

                // 圖案
                albumViewButton.setBackgroundResource(R.drawable.local);
                albumViewButton.setTag(mStrAlbum);
                albumViewButton.getBackground().setAlpha(255);
                albumViewButton.setOnClickListener(localclickHandler);

                // 文字
                textView.setText(mStrAlbum);
                textView.setTextSize(getScreenWidthAndSizeInPx(mActivity));
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.BLACK);
                textView.setSingleLine(true);
                textView.setEllipsize(TruncateAt.END);
                textView.setWidth(4);
                textView.setShadowLayer(10f, // float radius
                        5f, // float dx
                        5f, // float dy
                        Color.BLACK // int color
                );
                textView.getBackground().setAlpha(0);
                textView.setGravity(1);
                textView.setMaxEms(1);

                // 圖文重疊
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
            ImageView AlbumView = (ImageView)v;
            Log.d(TAG, AlbumView.getTag().toString());
            AlbumView.getBackground().setAlpha(180);
            toPlayList(String.valueOf(AlbumView.getTag()));
        }
    };

    /**
     * 長按即更新專輯
     */
    private OnLongClickListener default_longClickHandler = new OnLongClickListener() {
        public boolean onLongClick(View v) {
            mActivity = (U2bPlayerMainFragmentActivity)getActivity();
            final ImageView AlbumView = (ImageView)v;
            final PlayScanner playScanner = new PlayScanner();

            if (MUSIC_TYPE_MYFAVORITE.equals(AlbumView.getTag().toString()))
                return false;

            AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
            dialog.setTitle("更新");
            dialog.setMessage("是否要更新" + AlbumView.getTag().toString());
            dialog.setIcon(android.R.drawable.ic_dialog_alert);
            dialog.setCancelable(false);
            dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // 按下PositiveButton要做的事
                    Toast.makeText(mActivity,
                            "start scan process: " + AlbumView.getTag().toString(),
                            Toast.LENGTH_LONG).show();
                    playScanner.scan(WEB_TYPE_KKBOX, AlbumView.getTag().toString().substring(0, 2),
                            null);

                }
            });
            dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            dialog.show();

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
            AlbumView.getBackground().setAlpha(180);
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

        Log.d(TAG, "heightPixels: " + String.valueOf(heigh));
        Log.d(TAG, "widthPixels: " + String.valueOf(width));
        Log.d(TAG, "density: " + String.valueOf(density));
        Log.d(TAG, "densityDpi: " + String.valueOf(dpi));

        if (width / density > 700) {
            mTextSize = 50;
        } else if (width / density > 500) {
            mTextSize = 40;
        } else if (width / density > 300) {
            mTextSize = 30;
        } else {
            mTextSize = 20;
        }

        return mTextSize;
    }
}
