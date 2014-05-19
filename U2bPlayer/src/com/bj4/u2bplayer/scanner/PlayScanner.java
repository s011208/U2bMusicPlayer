
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
import com.bj4.u2bplayer.utilities.ProgressCallback;

public class PlayScanner {
    private static final boolean DEBUG = true && PlayMusicApplication.OVERALL_DEBUG;

    public static final String WEB_TYPE_KKBOX = "KKBOX";

    public static final String WEB_TYPE_HITFM = "HITFM";

    public static final String KKBOX_MUSIC_RYTHM_CHINESE_URL = "http://www.kkbox.com/tw/tc/charts/chinese-monthly-song-latest.html";
 
    public static final String KKBOX_MUSIC_RYTHM_WESTERN_URL = "http://www.kkbox.com/tw/tc/charts/western-daily-song-latest.html";

    public static final String KKBOX_MUSIC_RYTHM_JAPANESE_URL = "http://www.kkbox.com/tw/tc/charts/japanese-daily-song-latest.html";

    public static final String KKBOX_MUSIC_RYTHM_KOREAN_URL = "http://www.kkbox.com/tw/tc/charts/korean-daily-song-latest.html";

    public static final String KKBOX_MUSIC_RYTHM_HOKKIEN_URL = "http://www.kkbox.com/tw/tc/charts/hokkien-daily-song-latest.html";

    public static final String KKBOX_MUSIC_RYTHM_CANTONESE_URL = "http://www.kkbox.com/tw/tc/charts/cantonese-daily-song-latest.html";

    public static final String HITFM_MUSIC_RYTHM_CHINESE_URL_0 = "http://www.hitoradio.com/newweb/chart_2.php?ch_year=2013&pageNum_rsList=0";

    public static final String HITFM_MUSIC_RYTHM_CHINESE_URL_1 = "http://www.hitoradio.com/newweb/chart_2.php?ch_year=2013&pageNum_rsList=1";

    public static final String MUSIC_TYPE_CHINESE = "�ػy";

    public static final String MUSIC_TYPE_WESTERN = "��v";

    public static final String MUSIC_TYPE_JAPANESE = "��y";

    public static final String MUSIC_TYPE_KOREAN = "���y";

    public static final String MUSIC_TYPE_HOKKIEN = "�x�y";

    public static final String MUSIC_TYPE_CANTONESE = "�f�y";
    
    public static final String MUSIC_TYPE_CHOISE = "���";   

    private static final String TAG = "PlayScanner";

    private ArrayList<ContentValues> MasterlistSource;

    private ContentValues contentSouce;

    private static String[] sSongs = null;

    private static String[] sAlbum = null;

    private static String[] sArtist = null;

    private static String[] sMonth = null;

    private static String sStr = null;
    
    private static int sRank;

    public void scan() {
        new Thread(mRunnable).start();
    }

    public void scan(final String webType, final String musicType, final ProgressCallback mCallback) {
        new Thread(new ScannerRunnable(webType, musicType, mCallback)).start();
    }

    private class ScannerRunnable implements Runnable {

        private String mWebType;

        private String mMusicType;

        private ProgressCallback mCallback;

        public ScannerRunnable(String webType, String musicType, final ProgressCallback mCallback) {
            mWebType = webType;
            mMusicType = musicType;
        }

        @Override
        public void run() {
            if (WEB_TYPE_KKBOX.equals(mWebType)) {
                if (MUSIC_TYPE_CHINESE.equals(mMusicType)) {
                    conversion(KKBOX_MUSIC_RYTHM_CHINESE_URL, MUSIC_TYPE_CHINESE, WEB_TYPE_KKBOX,
                            false, mCallback);
                    insertU2bDB(MasterlistSource);
                } else if (MUSIC_TYPE_WESTERN.equals(mMusicType)) {
                    conversion(KKBOX_MUSIC_RYTHM_WESTERN_URL, MUSIC_TYPE_WESTERN, WEB_TYPE_KKBOX,
                            false, mCallback);
                    insertU2bDB(MasterlistSource);
                } else if (MUSIC_TYPE_JAPANESE.equals(mMusicType)) {
                    conversion(KKBOX_MUSIC_RYTHM_JAPANESE_URL, MUSIC_TYPE_JAPANESE, WEB_TYPE_KKBOX,
                            false, mCallback);
                    insertU2bDB(MasterlistSource);
                } else if (MUSIC_TYPE_KOREAN.equals(mMusicType)) {
                    conversion(KKBOX_MUSIC_RYTHM_KOREAN_URL, MUSIC_TYPE_KOREAN, WEB_TYPE_KKBOX,
                            false, mCallback);
                    insertU2bDB(MasterlistSource);
                } else if (MUSIC_TYPE_HOKKIEN.equals(mMusicType)) {
                    conversion(KKBOX_MUSIC_RYTHM_HOKKIEN_URL, MUSIC_TYPE_HOKKIEN, WEB_TYPE_KKBOX,
                            false, mCallback);
                    insertU2bDB(MasterlistSource);
                } else if (MUSIC_TYPE_CANTONESE.equals(mMusicType)) {
                    conversion(KKBOX_MUSIC_RYTHM_CANTONESE_URL, MUSIC_TYPE_CANTONESE,
                            WEB_TYPE_KKBOX, false, mCallback);
                    insertU2bDB(MasterlistSource);
                }
            } else if (WEB_TYPE_HITFM.equals(mWebType)) {
                if (MUSIC_TYPE_CHINESE.equals(mMusicType)) {
                    conversion(HITFM_MUSIC_RYTHM_CHINESE_URL_0, MUSIC_TYPE_CHINESE, WEB_TYPE_HITFM,
                            false, mCallback);
                    insertU2bDB(MasterlistSource);

                    conversion(HITFM_MUSIC_RYTHM_CHINESE_URL_1, MUSIC_TYPE_CHINESE, WEB_TYPE_HITFM,
                            true, mCallback);
                    insertU2bDB(MasterlistSource);
                }
            }
        }
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "start scan...");

            // �ػyTop100��]
            conversion(KKBOX_MUSIC_RYTHM_CHINESE_URL, MUSIC_TYPE_CHINESE, WEB_TYPE_KKBOX, false);
            insertU2bDB(MasterlistSource);

            // ��vTop100��]
            conversion(KKBOX_MUSIC_RYTHM_WESTERN_URL, MUSIC_TYPE_WESTERN, WEB_TYPE_KKBOX, false);
            insertU2bDB(MasterlistSource);

            // ��yTop100��]
            conversion(KKBOX_MUSIC_RYTHM_JAPANESE_URL, MUSIC_TYPE_JAPANESE, WEB_TYPE_KKBOX, false);
            insertU2bDB(MasterlistSource);

            // ���yTop100��]
            conversion(KKBOX_MUSIC_RYTHM_KOREAN_URL, MUSIC_TYPE_KOREAN, WEB_TYPE_KKBOX, false);
            insertU2bDB(MasterlistSource);

            // �x�yTop100��]
            conversion(KKBOX_MUSIC_RYTHM_HOKKIEN_URL, MUSIC_TYPE_HOKKIEN, WEB_TYPE_KKBOX, false);
            insertU2bDB(MasterlistSource);

            // �f�yTop100��]
            conversion(KKBOX_MUSIC_RYTHM_CANTONESE_URL, MUSIC_TYPE_CANTONESE, WEB_TYPE_KKBOX, false);
            insertU2bDB(MasterlistSource);

            // hitfm�~��top50+50
            conversion(HITFM_MUSIC_RYTHM_CHINESE_URL_0, MUSIC_TYPE_CHINESE, WEB_TYPE_HITFM, false);
            insertU2bDB(MasterlistSource);

            conversion(HITFM_MUSIC_RYTHM_CHINESE_URL_1, MUSIC_TYPE_CHINESE, WEB_TYPE_HITFM, true);
            insertU2bDB(MasterlistSource);
        }
    };

    /**
     * �s�W�ܸ�Ʈw �p�M�褤�e�����q�����P �Χ�s���
     * 
     * @param listSource
     */
    private void insertU2bDB(ArrayList<ContentValues> listSource) {
        U2bDatabaseHelper databaseHelper = PlayMusicApplication.getDataBaseHelper();
        Log.d(TAG, "databaseHelper!=null: " + String.valueOf(databaseHelper != null));
        if (databaseHelper != null) {
            Log.d(TAG, "!isDataExsit(listSource): " + String.valueOf(isDataExsit(listSource)));
            if (!isDataExsit(listSource)) {
                databaseHelper.insert(listSource, true);
            }
        }
    }

    /**
     * �P�_�M�褤�e�����q���O�_�ۦP
     * 
     * @param listSource
     */
    private boolean isDataExsit(ArrayList<ContentValues> listSource) {
        try {

            U2bDatabaseHelper databaseHelper = PlayMusicApplication.getDataBaseHelper();
            ContentValues contentSouce = new ContentValues();
            String COLUMN_ARTIST = "";
            String COLUMN_ALBUM = "";
            String COLUMN_MUSIC = "";
            String COLUMN_RANK = "";
            boolean isMusicExisted = false;

            Log.d(TAG, "listSource.size(): " + listSource.size());

            for (int i = 0; i < listSource.size(); i++) {
                if (i == 5) {
                    return true;
                }
                contentSouce = listSource.get(i);
                COLUMN_ARTIST = contentSouce.getAsString(U2bDatabaseHelper.COLUMN_ARTIST);
                COLUMN_ALBUM = contentSouce.getAsString(U2bDatabaseHelper.COLUMN_ALBUM);
                COLUMN_MUSIC = contentSouce.getAsString(U2bDatabaseHelper.COLUMN_MUSIC);
                COLUMN_RANK = contentSouce.getAsString(U2bDatabaseHelper.COLUMN_RANK);

                isMusicExisted = databaseHelper.isMusicExisted(COLUMN_MUSIC, COLUMN_RANK);

                if (DEBUG) {
                    Log.d(TAG, "isMusicExisted: " + isMusicExisted + "-" + COLUMN_ARTIST + ","
                            + COLUMN_ALBUM + "," + COLUMN_MUSIC + "," + COLUMN_RANK);
                }

                if (!isMusicExisted) {
                    databaseHelper.removeAlbum(COLUMN_ALBUM, true);
                    return isMusicExisted;
                }
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return true;
    }
    
//    private boolean isDataUpdate(ArrayList<ContentValues> listSource) {
//        try {
//            U2bDatabaseHelper databaseHelper = PlayMusicApplication.getDataBaseHelper();
//            ContentValues contentSouce = listSource.get(0);
//            String COLUMN_ALBUM = contentSouce.getAsString(U2bDatabaseHelper.COLUMN_ALBUM);
//            boolean isAlbumExisted = databaseHelper.isAlbumExisted(COLUMN_ALBUM);
//            if (DEBUG) {
//                Log.d(TAG, "isAlbumExisted: " + isAlbumExisted + ", COLUMN_ALBUM: " + COLUMN_ALBUM);
//            }
//            if (isAlbumExisted) {
//                return true;
//            } else {
//                databaseHelper.addNewAlbum(COLUMN_ALBUM);
//            }
//        } catch (Exception e) {
//            Log.e(TAG, e.getMessage());
//        }
//        return false;
//    }

//    private void dumpDbData() {
//        if (DEBUG) {
//            U2bDatabaseHelper databaseHelper = PlayMusicApplication.getDataBaseHelper();
//            Cursor c = databaseHelper.query(null, null);
//            // print
//            if (c != null) {
//                while (c.moveToNext()) {
//                    String artist = c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_ARTIST));
//                    String album = c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_ALBUM));
//                    String music = c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_MUSIC));
//                    String rank = c.getString(c.getColumnIndex(U2bDatabaseHelper.COLUMN_RANK));
//                    Log.d(TAG, "PRINT " + artist + ", " + album + ", " + music + ", " + rank);
//                }
//                c.close();
//            }
//            databaseHelper.clearTableContent();
//        }
//    }

    /**
     * �����ഫ
     * 
     * @param link
     * @param language
     * @param webType
     */
    private void conversion(String link, String language, String webType, boolean last) {
        conversion(link, language, webType, last, null);
    }

    private void conversion(String link, String language, String webType, boolean last,
            ProgressCallback mCallback) {
        if (MasterlistSource == null) {
            MasterlistSource = new ArrayList<ContentValues>();
        } else {
            MasterlistSource.clear();
        }
        sRank = 0;

        // HitFM Top100���⭶ �ҥH���⦸insert
        if (last)
            sRank = 50;

        try {
            // ���o�������e �ন��r��
            URL url_address = new URL(link);
            BufferedReader br = new BufferedReader(new InputStreamReader(url_address.openStream(),
                    "UTF-8"));

            // �v��P�_ ���o���
            while ((sStr = br.readLine()) != null) {
                // �P�_�����ӷ�
                if (WEB_TYPE_KKBOX.equals(webType)) {
                    searchKkboxData(language);

                } else if (WEB_TYPE_HITFM.equals(webType)) {
                    searchHitFMData(language);
                }
                if (mCallback != null) {
                    mCallback.setProgress(sRank);
                }
            }

            if (DEBUG)
                printTest();

        } catch (MalformedURLException e) {
            Log.w(TAG, e.getMessage());
        } catch (UnsupportedEncodingException e) {
            Log.w(TAG, e.getMessage());
        } catch (IOException e) {
            Log.w(TAG, e.getMessage());
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
            if (sStr.contains("<h4><a href=")) {
                sSongs = sStr.split("title=\"");
                sSongs = sSongs[1].split("\">");
                sSongs = convertString(sSongs, true);
            }

            // �q��
            if (sStr.contains("<h5 class=")) {
                sArtist = sStr.split("title=\"");
                sArtist = sArtist[1].split("\">");
                sArtist = convertString(sArtist, false);
            }

            // ���o����� �����Ʀ�]���
            if (sStr.contains("�̰���") && sMonth == null) {
                sMonth = sStr.split("class=\"date\">");
                sMonth = sMonth[1].split("</span>");
                sMonth = sMonth[0].split("-");
            }

            // �C���o�q���κq�� �K�[�Jlist�M��M��
            if (sArtist != null && sSongs != null && sMonth != null) {
                contentSouce = new ContentValues();
                contentSouce.put(U2bDatabaseHelper.COLUMN_ARTIST, sArtist[0].trim());
                contentSouce.put(U2bDatabaseHelper.COLUMN_ALBUM, language + MUSIC_TYPE_CHOISE);
                contentSouce.put(U2bDatabaseHelper.COLUMN_MUSIC, sSongs[0].trim());
                contentSouce.put(U2bDatabaseHelper.COLUMN_RANK, ++sRank);
                MasterlistSource.add(contentSouce);

                sSongs = null;
                sArtist = null;
            }
        } catch (Exception e) {
            Log.w(TAG, e.getMessage());
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
            if (sStr.contains("<td width=\"200\">") && sSongs == null) {
                sSongs = sStr.split("<td width=\"200\">");
                sSongs = sSongs[1].split("</td>");
            }

            // �M��
            if (sStr.contains("selected>") && sAlbum == null) {
                sAlbum = sStr.split("selected>");
                sAlbum = sAlbum[1].split("</option>");
            }

            // �q��
            if (sStr.contains("<td width=\"105\">")) {
                sArtist = sStr.split("<td width=\"105\">");
                sArtist = sArtist[1].split("</td>");
            }

            // �C���o�q���κq�� �K�[�Jlist�M��M��
            if (sSongs != null && sAlbum != null && sArtist != null) {
                contentSouce = new ContentValues();
                contentSouce.put(U2bDatabaseHelper.COLUMN_ARTIST, sArtist[0]);
                contentSouce.put(U2bDatabaseHelper.COLUMN_ALBUM, sAlbum[0] + "HitFM�~�׳榱");
                contentSouce.put(U2bDatabaseHelper.COLUMN_MUSIC, sSongs[0]);
                contentSouce.put(U2bDatabaseHelper.COLUMN_RANK, ++sRank);
//                MasterlistSource.add(contentSouce);

                sSongs = null;
                sArtist = null;
            }
        } catch (Exception e) {
            Log.w(TAG, e.getMessage());
        }
    }

    /**
     * �����q��κq���h�l���r��
     * @param source
     * @return
     */
    private String[] convertString(String[] source, boolean song) {

        if (source[0].contains("�]")) {
            source = source[0].split("�]");
        } else if (source[0].contains("(")) {
            source = source[0].split("\\(");
        } else if (source[0].contains("�i")) {
            source = source[0].split("\\�i");
        } else if (source[0].contains("�m")) {
            source = source[0].split("\\�m");
        } else if (source[0].contains("<")) {
            source = source[0].split("\\<");
        } else if (source[0].contains("-") && song) {
            source = source[0].split("\\-");
        } else if (source[0].contains("��") && song) {
            source = source[0].split("\\��");
        } else if (source[0].contains("Various Artists")) {
            source = new String[] {
                ""
            };
        }

        return source;
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

        Log.d(TAG, "list size:" + MasterlistSource.size());

        for (int i = 0; i < MasterlistSource.size(); i++) {
            content = MasterlistSource.get(i);
            strArtist = content.getAsString(U2bDatabaseHelper.COLUMN_ARTIST);
            strAlbum = content.getAsString(U2bDatabaseHelper.COLUMN_ALBUM);
            strMusic = content.getAsString(U2bDatabaseHelper.COLUMN_MUSIC);
            strRank = content.getAsString(U2bDatabaseHelper.COLUMN_RANK);
            Log.d(TAG, "@@printTest: " + strArtist + ", " + strAlbum + ", " + strMusic + ", "
                    + strRank);
        }
    }
}
