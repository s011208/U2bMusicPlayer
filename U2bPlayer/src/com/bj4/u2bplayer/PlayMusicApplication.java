
package com.bj4.u2bplayer;

import java.lang.Thread.UncaughtExceptionHandler;

import com.bj4.u2bplayer.activity.UnCaughtExceptionHandlerActivity;
import com.bj4.u2bplayer.database.U2bDatabaseHelper;
import com.bj4.u2bplayer.scanner.PlayScanner;
import com.bj4.u2bplayer.u2bParser.YoutubeDataParser;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PlayMusicApplication extends Application {
    public static final boolean ENABLE_UNCAUGHT_EXCEPTION = true;

    private static final String TAG = "PlayMusicApplication";

    public static final boolean OVERALL_DEBUG = true;

    public static final String DEVELOPER_KEY = "AIzaSyAI7xUOv1W_jKNR4XjDIGaqKK22nt74d50";

    public static final String INTENT_CRASH_LOG = "crash_log";

    private static final String GLOABAL_PREF_KEY = "global_pref_key";

    private static final String PREF_MUSIC_QUALITY = "pref_music_quality";

    private static U2bDatabaseHelper sDatabase;

    private static YoutubeDataParser sU2bParser;

    private static PlayScanner sPlayScanner;

    public static boolean sUsingHighQuality = true;

    @Override
    public void onCreate() {
        super.onCreate();
        setUncaughtExceptionHandlerIfAllowed();
        sDatabase = new U2bDatabaseHelper(this);
        sU2bParser = new YoutubeDataParser(this);
        sDatabase.addCallback(sU2bParser);
        sUsingHighQuality = getSharedPreferences(GLOABAL_PREF_KEY, Context.MODE_PRIVATE)
                .getBoolean(PREF_MUSIC_QUALITY, true);
    }

    public static void setMusicQuality(Context context, boolean usingHighQuality) {
        sUsingHighQuality = usingHighQuality;
        context.getSharedPreferences(GLOABAL_PREF_KEY, Context.MODE_PRIVATE).edit()
                .putBoolean(PREF_MUSIC_QUALITY, sUsingHighQuality);
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

    private void setUncaughtExceptionHandlerIfAllowed() {
        if (PlayMusicApplication.ENABLE_UNCAUGHT_EXCEPTION) {
            Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable ex) {
                    try {
                        Log.getStackTraceString(new Exception());
                        Log.e(TAG, "failed", ex);
                        StringBuilder sb = new StringBuilder();
                        sb.append(Log.getStackTraceString(ex) + "\n\n");
                        for (StackTraceElement s : ex.getStackTrace()) {
                            sb.append(s.toString() + "\n\n");
                        }
                        Intent crashedIntent = new Intent(PlayMusicApplication.this,
                                UnCaughtExceptionHandlerActivity.class);
                        crashedIntent.putExtra(INTENT_CRASH_LOG, sb.toString());
                        crashedIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        crashedIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        crashedIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(crashedIntent);
                        System.exit(0);
                    } catch (Exception e) {
                        System.exit(0);
                    }
                }
            });
        }
    }
}
