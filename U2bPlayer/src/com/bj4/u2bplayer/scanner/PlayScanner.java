
package com.bj4.u2bplayer.scanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.bj4.u2bplayer.PlayMusicApplication;
import com.bj4.u2bplayer.database.U2bDatabaseHelper;

public class PlayScanner {
    private static final boolean DEBUG = true && PlayMusicApplication.OVERALL_DEBUG;

    private ArrayList<ContentValues> MasterlistSource;

    private ContentValues contentSouce;

    private static final String TAG = "GG";

    private static String[] mSongs = null;

    private static String[] mAlbum = null;

    private static String[] mArtist = null;

    private static String[] mMonth = null;

    private static String mStr = null;

    private static int mRank;

    public void scan() {
        new Thread(runnable).start();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.e(TAG, "start scan...");

            // 華語Top100月榜
            String chineseKkbox100 = "http://www.kkbox.com/tw/tc/charts/chinese-monthly-song-latest.html";
            conversion(chineseKkbox100, "華語", "KKBOX", false);
            insertU2bDB(MasterlistSource);
            
            // 西洋Top100月榜
            String westernKkbox100 = "http://www.kkbox.com/tw/tc/charts/western-daily-song-latest.html";
            conversion(westernKkbox100, "西洋", "KKBOX", false);
            insertU2bDB(MasterlistSource);
            
            // 日語Top100月榜
            String japaneseKkbox100 = "http://www.kkbox.com/tw/tc/charts/japanese-daily-song-latest.html";
            conversion(japaneseKkbox100, "日語", "KKBOX", false);
            insertU2bDB(MasterlistSource);
            
            // 韓語Top100月榜
            String koreanKkbox100 = "http://www.kkbox.com/tw/tc/charts/korean-daily-song-latest.html";
            conversion(koreanKkbox100, "韓語", "KKBOX", false);
            insertU2bDB(MasterlistSource);
            
            // 台語Top100月榜
            String hokkienKkbox100 = "http://www.kkbox.com/tw/tc/charts/hokkien-daily-song-latest.html";
            conversion(hokkienKkbox100, "台語", "KKBOX", false);
            insertU2bDB(MasterlistSource);
            
            // 粵語Top100月榜
            String cantoneseKkbox100 = "http://www.kkbox.com/tw/tc/charts/cantonese-daily-song-latest.html";
            conversion(cantoneseKkbox100, "粵語", "KKBOX", false);
            insertU2bDB(MasterlistSource);
            
            
            

            // hitfm年度top50+50
            String htifm50 = "http://www.hitoradio.com/newweb/chart_2.php?ch_year=2013&pageNum_rsList=0";
            conversion(htifm50, "華語", "HITFM", false);
            insertU2bDB(MasterlistSource);
            htifm50 = "http://www.hitoradio.com/newweb/chart_2.php?ch_year=2013&pageNum_rsList=1";
            conversion(htifm50, "華語", "HITFM", true);
            insertU2bDB(MasterlistSource);

        }
    };

    /**
     * 新增至資料庫
     * 
     * @param listSource
     */
    private void insertU2bDB(ArrayList<ContentValues> listSource) {
        U2bDatabaseHelper databaseHelper = PlayMusicApplication.getDataBaseHelper();
        if (databaseHelper != null) {
            if(!isDataExsit(listSource)){
             // using bulk insert
                databaseHelper.insert(listSource, true);
                dumpDbData();
            }
        }
    }
    
    /**
     * 資料是否已存在
     * @param listSource
     */
    private boolean isDataExsit(ArrayList<ContentValues> listSource){
        try {
            U2bDatabaseHelper databaseHelper = PlayMusicApplication.getDataBaseHelper();
            ContentValues contentSouce = listSource.get(0);
            String COLUMN_ALBUM = contentSouce.getAsString(U2bDatabaseHelper.COLUMN_ALBUM);
            String querySQL = "select *"
                             + " from " + U2bDatabaseHelper.TABLE_MAIN_INFO 
                            + " where " + U2bDatabaseHelper.COLUMN_ALBUM + " = '" + COLUMN_ALBUM + "'";
            Cursor c = databaseHelper.query(querySQL);
           
            if(DEBUG){
                Log.e(TAG, "querySQL:" + querySQL);
                Log.e(TAG, "data count:"+String.valueOf(c.getCount()));
            }
            
            if(c.getCount()>0) 
                return true;
            
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        
        return false;
    }
    
    private void dumpDbData() {
        if (DEBUG) {
            U2bDatabaseHelper databaseHelper = PlayMusicApplication.getDataBaseHelper();
            Cursor c = databaseHelper.query(null, null);
            // print
            if (c != null) {
                while (c.moveToNext()) {
                    String artist = c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_ARTIST));
                    String album = c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_ALBUM));
                    String music = c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_MUSIC));
                    String rank = c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_RANK));
                    Log.d(TAG, "PRINT " + artist + ", " + album + ", " + music + ", " + rank);
                }
                c.close();
            }
            databaseHelper.clearTableContent();
        }
    }

    /**
     * 網頁轉換
     * 
     * @param link
     * @param language
     * @param webType
     */
    private void conversion(String link, String language, String webType, boolean last) {
        MasterlistSource = new ArrayList<ContentValues>();
        mRank = 0;
        
        //HitFM Top100分兩頁 所以分兩次insert
        if(last) mRank = 50;

        try {
            // 取得網頁內容 轉成文字檔
            URL url_address = new URL(link);
            BufferedReader br = new BufferedReader(new InputStreamReader(url_address.openStream(),
                    "UTF-8"));

            // 逐行判斷 取得資料
            while ((mStr = br.readLine()) != null) {
                // 判斷網站來源
                if ("KKBOX".equals(webType)) {
                    searchKkboxData(language);

                } else if ("HITFM".equals(webType)) {
                    searchHitFMData(language);
                }
            }

            if (DEBUG)
                printTest();

        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * 取得KKbox資料
     * 
     * @param language
     */
    private void searchKkboxData(String language) {
        try {
            // 歌曲
            if (mStr.contains("<h4><a href=")) {
                mSongs = mStr.split("title=\"");
                mSongs = mSongs[1].split("\">");
            }

            // 歌手
            if (mStr.contains("<h5 class=")) {
                mArtist = mStr.split("title=\"");
                mArtist = mArtist[1].split("\">");
            }

            // 取得的月份 為當月排行榜月份
            if (mStr.contains("最高第") && mMonth == null) {
                mMonth = mStr.split("class=\"date\">");
                mMonth = mMonth[1].split("</span>");
                mMonth = mMonth[0].split("-");
            }

            // 每取得歌曲及歌手 便加入list然後清空
            if (mArtist != null && mSongs != null && mMonth != null) {
                contentSouce = new ContentValues();
                contentSouce.put(U2bDatabaseHelper.COLUMN_ARTIST, mArtist[0]);
                contentSouce.put(U2bDatabaseHelper.COLUMN_ALBUM, language + mMonth[1] + "月KKbox Top100");
                contentSouce.put(U2bDatabaseHelper.COLUMN_MUSIC, mSongs[0]);
                contentSouce.put(U2bDatabaseHelper.COLUMN_RANK, ++mRank);
                MasterlistSource.add(contentSouce);

                mSongs = null;
                mArtist = null;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * 取得HitFM 排行榜
     * 
     * @param language
     */
    private void searchHitFMData(String language) {
        
            
        try {
            // 歌曲
            if (mStr.contains("<td width=\"200\">") && mSongs == null) {
                mSongs = mStr.split("<td width=\"200\">");
                mSongs = mSongs[1].split("</td>");
            }

            // 專輯
            if (mStr.contains("selected>") && mAlbum == null) {
                mAlbum = mStr.split("selected>");
                mAlbum = mAlbum[1].split("</option>");
            }

            // 歌手
            if (mStr.contains("<td width=\"105\">")) {
                mArtist = mStr.split("<td width=\"105\">");
                mArtist = mArtist[1].split("</td>");
            }

            // 每取得歌曲及歌手 便加入list然後清空
            if (mSongs != null && mAlbum != null && mArtist != null) {
                contentSouce = new ContentValues();
                contentSouce.put(U2bDatabaseHelper.COLUMN_ARTIST, mArtist[0]);
                contentSouce.put(U2bDatabaseHelper.COLUMN_ALBUM, mAlbum[0]+"HitFM年度單曲");
                contentSouce.put(U2bDatabaseHelper.COLUMN_MUSIC, mSongs[0]);
                contentSouce.put(U2bDatabaseHelper.COLUMN_RANK, ++mRank);
                MasterlistSource.add(contentSouce);

                mSongs = null;
                mArtist = null;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * 列印測試
     */
    private void printTest() {
        ContentValues content = new ContentValues();
        String strArtist = "";
        String strAlbum = "";
        String strMusic = "";
        String strRank = "";

        Log.e(TAG, "list size:" + MasterlistSource.size());

        for (int i = 0; i < MasterlistSource.size(); i++) {
            content = MasterlistSource.get(i);
            strArtist = content.getAsString(U2bDatabaseHelper.COLUMN_ARTIST);
            strAlbum = content.getAsString(U2bDatabaseHelper.COLUMN_ALBUM);
            strMusic = content.getAsString(U2bDatabaseHelper.COLUMN_MUSIC);
            strRank = content.getAsString(U2bDatabaseHelper.COLUMN_RANK);
            Log.e(TAG, "printTest: " + strArtist + ", " + strAlbum + ", " + strMusic + ", "
                    + strRank);
        }
    }
}
