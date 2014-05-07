
package com.bj4.u2bplayer.utilities;

import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.bj4.u2bplayer.R;
import com.bj4.u2bplayer.activity.U2bPlayerMainFragmentActivity;

public class NotificationBuilder {
    private static final String TAG = "NotificationBuilder";

    private static final boolean DEBUG = false;

    public static final int NOTIFICATION_ID = NotificationBuilder.class.hashCode();

    public static final int RECOMMAND_START_APP_NOTIFICATION_ID = NOTIFICATION_ID + 1;

    private NotificationBuilder() {
    }

//    public static Notification createSimpleNotification(final Context context,
//            final PlayListInfo info) {
//        Intent notifyIntent = new Intent(context, U2bPlayerMainFragmentActivity.class);
//        PendingIntent appIntent = PendingIntent.getActivity(context, 0, notifyIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        String title = info == null ? "U2B notification" : info.mMusicTitle;
//        String content = info == null ? "no music information" : info.mAlbumTitle + "  "
//                + info.mArtist;
//        try {
//            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//                Notification.Builder builder = new Notification.Builder(context);
//                builder.setContentTitle(title).setContentText(content)
//                        .setSmallIcon(R.drawable.ic_launcher).setContentIntent(appIntent)
//                        .setAutoCancel(false).setOngoing(true);
//                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
//                if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
//                    builder.setPriority(Notification.PRIORITY_LOW);
//                    return builder.build();
//                } else {
//                    return builder.getNotification();
//                }
//            } else {
//                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
//                builder.setContentTitle(title).setContentText(content)
//                        .setSmallIcon(R.drawable.ic_launcher).setContentIntent(appIntent)
//                        .setPriority(Notification.PRIORITY_LOW).setAutoCancel(false)
//                        .setOngoing(true);
//                return builder.build();
//            }
//        } catch (Exception e) {
//            if (DEBUG)
//                Log.w(TAG, "failed to create notification", e);
//        }
//        return null;
//    }

    
    public static void handleSimpleNotification(final Context context, final PlayListInfo info) {
        try {
            NotificationManager nm = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(null, NOTIFICATION_ID, createSimpleNotification(context, info));
        } catch (Exception e) {
            if (DEBUG)
                Log.w(TAG, "failed to create notification", e);
        }
    }

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
    
    
    public static Notification createSimpleNotification(final Context context, final PlayListInfo info) {
        try {
            String title = info == null ? "U2B notification" : info.mMusicTitle;
            String content = info == null ? "no music information" : info.mAlbumTitle + "  " + info.mArtist;
            Bitmap remote_picture = null;
      
            remote_picture = BitmapFactory.decodeResource(context.getResources(), R.drawable.kkbox);
            NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();
            notiStyle.setBigContentTitle(title);
            notiStyle.setSummaryText(content);
            Intent resultIntent = new Intent(context, U2bPlayerMainFragmentActivity.class);

            PendingIntent appIntent = PendingIntent.getActivity(context, 0, resultIntent, 0);
            RemoteViews expandedView = new RemoteViews(context.getPackageName(), R.layout.notification_view);
            expandedView.setTextViewText(R.id.text_view, title+"\n"+content);
            expandedView.setOnClickPendingIntent(R.id.play_info_pause, appIntent); 

            Notification notification = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setOngoing(true) 
                    .setLargeIcon(remote_picture)
                    .setContent(expandedView)
                    .setStyle(notiStyle)
                    .build();
            
            return notification;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
