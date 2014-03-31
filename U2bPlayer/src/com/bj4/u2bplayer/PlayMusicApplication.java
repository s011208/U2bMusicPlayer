
package com.bj4.u2bplayer;

import com.bj4.u2bplayer.database.U2bDatabaseHelper;

import android.app.Application;
import android.util.Log;

public class PlayMusicApplication extends Application {
    public static final String DEVELOPER_KEY = "AIzaSyAI7xUOv1W_jKNR4XjDIGaqKK22nt74d50";

    private static U2bDatabaseHelper mDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        mDatabase = new U2bDatabaseHelper(this);
    }

    public static U2bDatabaseHelper getDataBaseHelper() {
        return mDatabase;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mDatabase.close();
    }
}
