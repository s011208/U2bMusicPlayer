
package com.bj4.u2bplayer.scanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.bj4.u2bplayer.PlayMusicApplication;
import com.bj4.u2bplayer.database.U2bDatabaseHelper;

public class PlayScanner extends Activity {
    private ArrayList<ContentValues> MasterlistSource = new ArrayList<ContentValues>();

    private ContentValues contentSouce = new ContentValues();

    private static final String TAG = "GG";

    private static String[] Songs = null;

    private static String[] Artist = null;

    private static String[] Month = null;

    private static String str = null;

    private static int rank = 0;

    public PlayScanner() {
        new Thread(runnable).start();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.e(TAG, "start scan...");

            // �ػyTop100��]
            String chinese_kkbox_100 = "http://www.kkbox.com/tw/tc/charts/chinese-monthly-song-latest.html";
            conversion(chinese_kkbox_100, "�ػy");
            insertU2bDB(MasterlistSource);
        }
    };

    /**
     * �s�W�ܸ�Ʈw
     * 
     * @param listSource
     */
    private void insertU2bDB(ArrayList<ContentValues> listSource) {
        U2bDatabaseHelper mDatabaseHelper = PlayMusicApplication.getDataBaseHelper();
        if (mDatabaseHelper != null) {
            Cursor c = mDatabaseHelper.query(null, null);
            mDatabaseHelper.clearTableContent();

            // using bulk insert
            mDatabaseHelper.insert(listSource);

            // print
            c = mDatabaseHelper.query(null, null);
            if (c != null) {
                while (c.moveToNext()) {
                    String artist = c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_ARTIST));
                    String album = c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_ALBUM));
                    String music = c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_MUSIC));
                    String rank = c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_RANK));
                    Log.e(TAG, "PRINT " + artist + ", " + album + ", " + music + ", " + rank);
                }
                c.close();
            }
            mDatabaseHelper.clearTableContent();
        }
    }

    /**
     * �ഫ
     * 
     * @param link
     * @param language
     * @return
     */
    private void conversion(String link, String language) {
        try {
            // ���o�������e �ন��r��
            URL url_address = new URL(link);
            BufferedReader br = new BufferedReader(new InputStreamReader(url_address.openStream(), "UTF-8"));

            // �v��P�_ ���o���
            while ((str = br.readLine()) != null) {
                searchData(language);
            }

        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * ���o���
     * 
     * @param language
     */
    private void searchData(String language) {
        try {
            // �q��
            if (str.contains("<h4><a href=")) {
                Songs = str.split("title=\"");
                Songs = Songs[1].split("\">");
            }

            // �q��
            if (str.contains("<h5 class=")) {
                Artist = str.split("title=\"");
                Artist = Artist[1].split("\">");
            }

            // ���o����� �����Ʀ�]���
            if (str.contains("�̰���") && Month == null) {
                Month = str.split("class=\"date\">");
                Month = Month[1].split("</span>");
                Month = Month[0].split("-");
            }

            // �C���o�q���κq�� �K�[�Jlist�M��M��
            if (Artist != null && Songs != null && Month != null) {
                contentSouce = new ContentValues();
                contentSouce.put(U2bDatabaseHelper.COLUMN_ARTIST, Artist[0]);
                contentSouce.put(U2bDatabaseHelper.COLUMN_ALBUM, language + Month[1] + "��Top100");
                contentSouce.put(U2bDatabaseHelper.COLUMN_MUSIC, Songs[0]);
                contentSouce.put(U2bDatabaseHelper.COLUMN_RANK, ++rank);
                MasterlistSource.add(contentSouce);
                Songs = null;
                Artist = null;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
