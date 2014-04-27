
package com.bj4.u2bplayer;

import com.bj4.u2bplayer.service.PlayMusicService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, PlayMusicService.class));
    }

}
