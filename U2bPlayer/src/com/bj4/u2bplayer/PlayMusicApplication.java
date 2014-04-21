
package com.bj4.u2bplayer;

import java.lang.Thread.UncaughtExceptionHandler;

import com.bj4.u2bplayer.activity.UnCaughtExceptionHandlerActivity;
import com.bj4.u2bplayer.database.U2bDatabaseHelper;
import com.bj4.u2bplayer.scanner.PlayScanner;
import com.bj4.u2bplayer.u2bParser.YoutubeDataParser;

import android.app.Application;
import android.content.Intent;

public class PlayMusicApplication extends Application {
    public static final boolean ENABLE_UNCAUGHT_EXCEPTION = true;

    public static final boolean OVERALL_DEBUG = true;

    public static final String DEVELOPER_KEY = "AIzaSyAI7xUOv1W_jKNR4XjDIGaqKK22nt74d50";

    public static final String INTENT_CRASH_LOG = "crash_log";

    private static U2bDatabaseHelper sDatabase;

    private static YoutubeDataParser sU2bParser;

    private static PlayScanner sPlayScanner;

    @Override
    public void onCreate() {
        super.onCreate();
        setUncaughtExceptionHandlerIfAllowed();
        sDatabase = new U2bDatabaseHelper(this);
        sU2bParser = new YoutubeDataParser(this);
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

    private void setUncaughtExceptionHandlerIfAllowed() {
        if (PlayMusicApplication.ENABLE_UNCAUGHT_EXCEPTION) {
            Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable ex) {
                    try {
                        StringBuilder sb = new StringBuilder();
                        for (StackTraceElement s : ex.getStackTrace()) {
                            sb.append(s.toString() + "\n");
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
