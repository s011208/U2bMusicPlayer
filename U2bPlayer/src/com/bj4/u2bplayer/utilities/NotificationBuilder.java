
package com.bj4.u2bplayer.utilities;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.bj4.u2bplayer.R;
import com.bj4.u2bplayer.activity.U2bPlayerMainFragmentActivity;
import com.bj4.u2bplayer.service.PlayMusicService;
import com.bj4.u2bplayer.widget.SimplePlayWidget;

public class NotificationBuilder{
    private static final String TAG = "NotificationBuilder";

    private static final boolean DEBUG = false;
    
    private static final String PLAY = "play";
    
    private static final String PAUSE = "pause";
    
    private static final String NEXT = "next";
    
    private static final String EXIT = "exit";
    
    private static boolean sIsPlaying = false;

    public static final int NOTIFICATION_ID = NotificationBuilder.class.hashCode();

    public static final int RECOMMAND_START_APP_NOTIFICATION_ID = NOTIFICATION_ID + 1;

    private NotificationBuilder() {
    }

    public static Notification createSimpleNotification(final Context context, final PlayListInfo info, boolean isPlaying) {
        Intent notifyIntent = new Intent(context, U2bPlayerMainFragmentActivity.class);
        PendingIntent appIntent = PendingIntent.getActivity(context, 0, notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        String title = info == null ? "U2B notification" : info.mMusicTitle;
        String content = info == null ? "no music information" : info.mAlbumTitle + "  "
                + info.mArtist;
        
        sIsPlaying = isPlaying;
        RemoteViews expandedView = new RemoteViews(context.getPackageName(), R.layout.notification_view);
        expandedView.setTextViewText(R.id.text_view, title+"\n"+content);
        
        try {
            
            if(sIsPlaying){
                expandedView.setOnClickPendingIntent(R.id.paly_pause_music, getPendingSelfIntent(context, PAUSE));
                expandedView.setImageViewResource(R.id.paly_pause_music, R.drawable.ic_pause);
            }else{
                expandedView.setOnClickPendingIntent(R.id.paly_pause_music, getPendingSelfIntent(context, PLAY));
                expandedView.setImageViewResource(R.id.paly_pause_music, R.drawable.ic_play);
            }
            expandedView.setOnClickPendingIntent(R.id.next_music, getPendingSelfIntent(context, NEXT));
            
            expandedView.setOnClickPendingIntent(R.id.exit_app, getPendingSelfIntent(context, EXIT));
            
            
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                Notification.Builder builder = new Notification.Builder(context);
                builder.setContentTitle(title).setContentText(content)
                        .setSmallIcon(R.drawable.ic_launcher).setContentIntent(appIntent)
                        .setAutoCancel(false).setOngoing(true).setContent(expandedView);
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    builder.setPriority(Notification.PRIORITY_LOW).setContent(expandedView);
                    return builder.build();
                } else {
                    return builder.getNotification();
                }
            } else {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                builder.setContentTitle(title).setContentText(content)
                        .setSmallIcon(R.drawable.ic_launcher).setContentIntent(appIntent)
                        .setPriority(Notification.PRIORITY_LOW).setAutoCancel(false)
                        .setOngoing(true).setContent(expandedView);
                return builder.build();
            }
        } catch (Exception e) {
            if (DEBUG)
                Log.w(TAG, "failed to create notification", e);
        }
        return null;
    }

    private static PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, SimplePlayWidget.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent.putExtra(PlayMusicService.INTENT_ACTION, action), 0);
    }

    /* 
    public void handleSimpleNotification(final Context context, final PlayListInfo info) {
        try {
            NotificationManager nm = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(null, NOTIFICATION_ID, createSimpleNotification(context, info));
        } catch (Exception e) {
            if (DEBUG)
                Log.w(TAG, "failed to create notification", e);
        }
    }
    */

    public static Notification createHeadSetConnectedNotification(final Context context) {
        Intent notifyIntent = new Intent(context, U2bPlayerMainFragmentActivity.class);
        PendingIntent appIntent = PendingIntent.getActivity(context, 0, notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        String title = "Recommand music apps";
        String content = "Bj4 U2B music player";
        try {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                Notification.Builder builder = new Notification.Builder(context);
                builder.setContentTitle(title).setContentText(content)
                        .setSmallIcon(R.drawable.ic_launcher).setContentIntent(appIntent)
                        .setAutoCancel(true);
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    builder.setPriority(Notification.PRIORITY_LOW);
                    return builder.build();
                } else {
                    return builder.getNotification();
                }
            } else {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                builder.setContentTitle(title).setContentText(content)
                        .setSmallIcon(R.drawable.ic_launcher).setContentIntent(appIntent)
                        .setPriority(Notification.PRIORITY_LOW).setAutoCancel(true);
                return builder.build();
            }
        } catch (Exception e) {
            if (DEBUG)
                Log.w(TAG, "failed to create notification", e);
        }
        return null;
    }
}
