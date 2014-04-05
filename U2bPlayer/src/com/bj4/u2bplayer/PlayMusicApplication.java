
package com.bj4.u2bplayer;

import com.bj4.u2bplayer.database.U2bDatabaseHelper;
import com.bj4.u2bplayer.u2bParser.YoutubeDataParser;

import android.app.Application;
public class PlayMusicApplication extends Application {
    public static final boolean OVERALL_DEBUG = true;
    public static final String DEVELOPER_KEY = "AIzaSyAI7xUOv1W_jKNR4XjDIGaqKK22nt74d50";

    private static U2bDatabaseHelper mDatabase;

    private static YoutubeDataParser mU2bParser;

    @Override
    public void onCreate() {
        super.onCreate();
        mDatabase = new U2bDatabaseHelper(this);
        mU2bParser = new YoutubeDataParser();
        mDatabase.addCallback(mU2bParser);
    }

    public static U2bDatabaseHelper getDataBaseHelper() {
        return mDatabase;
    }

    public static YoutubeDataParser getU2bParser() {
        return mU2bParser;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mDatabase.close();
    }
}
