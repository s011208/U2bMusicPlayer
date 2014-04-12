
package com.bj4.u2bplayer.service;

import com.bj4.u2bplayer.PlayMusicApplication;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class SpiderService extends Service {

    private static final String TAG = "SpiderService";

    private static final boolean DEBUG = true;

    private static final HandlerThread sWorkerThread = new HandlerThread("SpiderService-scanner");
    static {
        sWorkerThread.start();
    }

    private static final Handler sWorker = new Handler(sWorkerThread.getLooper());

    private final ISpiderService.Stub mBinder = new ISpiderService.Stub() {

        @Override
        public void startToParse() throws RemoteException {
            if (DEBUG) {
                Log.i(TAG, "start to parse");
            }
            sWorker.post(new Runnable() {

                @Override
                public void run() {
                    PlayMusicApplication.getPlayScanner().scan();
                    Toast.makeText(getApplicationContext(), "start scan process", Toast.LENGTH_LONG)
                            .show();
                }
            });
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
