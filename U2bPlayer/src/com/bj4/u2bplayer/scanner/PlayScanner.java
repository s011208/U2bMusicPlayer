
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

            // �ػyTop100��]
            String chineseKkbox100 = "http://www.kkbox.com/tw/tc/charts/chinese-monthly-song-latest.html";
            conversion(chineseKkbox100, "�ػy", "KKBOX", false);
            insertU2bDB(MasterlistSource);
            
            // ��vTop100��]
            String westernKkbox100 = "http://www.kkbox.com/tw/tc/charts/western-daily-song-latest.html";
            conversion(westernKkbox100, "��v", "KKBOX", false);
            insertU2bDB(MasterlistSource);
            
            // ��yTop100��]
            String japaneseKkbox100 = "http://www.kkbox.com/tw/tc/charts/japanese-daily-song-latest.html";
            conversion(japaneseKkbox100, "��y", "KKBOX", false);
            insertU2bDB(MasterlistSource);
            
            // ���yTop100��]
            String koreanKkbox100 = "http://www.kkbox.com/tw/tc/charts/korean-daily-song-latest.html";
            conversion(koreanKkbox100, "���y", "KKBOX", false);
            insertU2bDB(MasterlistSource);
            
            // �x�yTop100��]
            String hokkienKkbox100 = "http://www.kkbox.com/tw/tc/charts/hokkien-daily-song-latest.html";
            conversion(hokkienKkbox100, "�x�y", "KKBOX", false);
            insertU2bDB(MasterlistSource);
            
            // �f�yTop100��]
            String cantoneseKkbox100 = "http://www.kkbox.com/tw/tc/charts/cantonese-daily-song-latest.html";
            conversion(cantoneseKkbox100, "�f�y", "KKBOX", false);
            insertU2bDB(MasterlistSource);
            
            
            

            // hitfm�~��top50+50
            String htifm50 = "http://www.hitoradio.com/newweb/chart_2.php?ch_year=2013&pageNum_rsList=0";
            conversion(htifm50, "�ػy", "HITFM", false);
            insertU2bDB(MasterlistSource);
            htifm50 = "http://www.hitoradio.com/newweb/chart_2.php?ch_year=2013&pageNum_rsList=1";
            conversion(htifm50, "�ػy", "HITFM", true);
            insertU2bDB(MasterlistSource);

        }
    };

    /**
     * �s�W�ܸ�Ʈw
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
     * ��ƬO�_�w�s�b
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
     * �����ഫ
     * 
     * @param link
     * @param language
     * @param webType
     */
    private void conversion(String link, String language, String webType, boolean last) {
        MasterlistSource = new ArrayList<ContentValues>();
        mRank = 0;
        
        //HitFM Top100���⭶ �ҥH���⦸insert
        if(last) mRank = 50;

        try {
            // ���o�������e �ন��r��
            URL url_address = new URL(link);
            BufferedReader br = new BufferedReader(new InputStreamReader(url_address.openStream(),
                    "UTF-8"));

            // �v��P�_ ���o���
            while ((mStr = br.readLine()) != null) {
                // �P�_�����ӷ�
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
     * ���oKKbox���
     * 
     * @param language
     */
    private void searchKkboxData(String language) {
        try {
            // �q��
            if (mStr.contains("<h4><a href=")) {
                mSongs = mStr.split("title=\"");
                mSongs = mSongs[1].split("\">");
            }

            // �q��
            if (mStr.contains("<h5 class=")) {
                mArtist = mStr.split("title=\"");
                mArtist = mArtist[1].split("\">");
            }

            // ���o����� �����Ʀ�]���
            if (mStr.contains("�̰���") && mMonth == null) {
                mMonth = mStr.split("class=\"date\">");
                mMonth = mMonth[1].split("</span>");
                mMonth = mMonth[0].split("-");
            }

            // �C���o�q���κq�� �K�[�Jlist�M��M��
            if (mArtist != null && mSongs != null && mMonth != null) {
                contentSouce = new ContentValues();
                contentSouce.put(U2bDatabaseHelper.COLUMN_ARTIST, mArtist[0]);
                contentSouce.put(U2bDatabaseHelper.COLUMN_ALBUM, language + mMonth[1] + "��KKbox Top100");
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
     * ���oHitFM �Ʀ�]
     * 
     * @param language
     */
    private void searchHitFMData(String language) {
        
            
        try {
            // �q��
            if (mStr.contains("<td width=\"200\">") && mSongs == null) {
                mSongs = mStr.split("<td width=\"200\">");
                mSongs = mSongs[1].split("</td>");
            }

            // �M��
            if (mStr.contains("selected>") && mAlbum == null) {
                mAlbum = mStr.split("selected>");
                mAlbum = mAlbum[1].split("</option>");
            }

            // �q��
            if (mStr.contains("<td width=\"105\">")) {
                mArtist = mStr.split("<td width=\"105\">");
                mArtist = mArtist[1].split("</td>");
            }

            // �C���o�q���κq�� �K�[�Jlist�M��M��
            if (mSongs != null && mAlbum != null && mArtist != null) {
                contentSouce = new ContentValues();
                contentSouce.put(U2bDatabaseHelper.COLUMN_ARTIST, mArtist[0]);
                contentSouce.put(U2bDatabaseHelper.COLUMN_ALBUM, mAlbum[0]+"HitFM�~�׳榱");
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
     * �C�L����
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
