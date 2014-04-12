
package com.bj4.u2bplayer.service;

import com.bj4.u2bplayer.PlayMusicApplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

public class SpiderService extends Service {

    private static final String TAG = "QQQQ";

    private static final boolean DEBUG = true;

    private final ISpiderService.Stub mBinder = new ISpiderService.Stub() {

        @Override
        public void startToParse() throws RemoteException {
            if (DEBUG) {
                Log.i(TAG, "start to parse");
            }
            PlayMusicApplication.getPlayScanner().scan();
        }

        @Override
        public int getProgress() throws RemoteException {
            return 0;
        }
    };

    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

}
