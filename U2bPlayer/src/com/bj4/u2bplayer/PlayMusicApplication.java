
package com.bj4.u2bplayer;

import com.bj4.u2bplayer.database.U2bDatabaseHelper;
import com.bj4.u2bplayer.scanner.PlayScanner;
import com.bj4.u2bplayer.u2bParser.YoutubeDataParser;

import android.app.Application;

public class PlayMusicApplication extends Application {
    public static final boolean OVERALL_DEBUG = true;

    public static final String DEVELOPER_KEY = "AIzaSyAI7xUOv1W_jKNR4XjDIGaqKK22nt74d50";

    private static U2bDatabaseHelper sDatabase;

    private static YoutubeDataParser sU2bParser;

    private static PlayScanner sPlayScanner;

    @Override
    public void onCreate() {
        super.onCreate();
        sDatabase = new U2bDatabaseHelper(this);
        sU2bParser = new YoutubeDataParser();
        sDatabase.addCallback(sU2bParser);
    }

    public synchronized static PlayScanner getPlayScanner() {
        if (sPlayScanner == null) {
            sPlayScanner = new PlayScanner();
        }
        return sPlayScanner;
    }

    public static U2bDatabaseHelper getDataBaseHelper() {
        return sDatabase;
    }

    public static YoutubeDataParser getU2bParser() {
        return sU2bParser;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        sDatabase.close();
    }
}
