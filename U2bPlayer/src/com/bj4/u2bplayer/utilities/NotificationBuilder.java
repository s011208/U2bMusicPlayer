
package com.bj4.u2bplayer.utilities;

import com.bj4.u2bplayer.R;
import com.bj4.u2bplayer.activity.U2bPlayerMainFragmentActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotificationBuilder {
    private static final String TAG = "NotificationBuilder";

    private static final boolean DEBUG = false;

    public static final int NOTIFICATION_ID = NotificationBuilder.class.hashCode();

    private NotificationBuilder() {
    }

    public static Notification createSimpleNotification(final Context context,
            final PlayListInfo info) {
        Intent notifyIntent = new Intent(context, U2bPlayerMainFragmentActivity.class);
        PendingIntent appIntent = PendingIntent.getActivity(context, 0, notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        String title = info == null ? "U2B notification" : info.mMusicTitle;
        String content = info == null ? "no music information" : info.mAlbumTitle + "  "
                + info.mArtist;
        try {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                Notification.Builder builder = new Notification.Builder(context);
                builder.setContentTitle(title).setContentText(content)
                        .setSmallIcon(R.drawable.ic_launcher).setContentIntent(appIntent)
                        .setAutoCancel(false).setOngoing(true);
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
                        .setPriority(Notification.PRIORITY_LOW).setAutoCancel(false)
                        .setOngoing(true);
                return builder.build();
            }
        } catch (Exception e) {
            if (DEBUG)
                Log.w(TAG, "failed to create notification", e);
        }
        return null;
    }

    public static void handleSimpleNotification(final Context context, final PlayListInfo info) {
        try {
            NotificationManager nm = (NotificationManager)context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(null, NOTIFICATION_ID, createSimpleNotification(context, info));
        } catch (Exception e) {
            if (DEBUG)
                Log.w(TAG, "failed to create notification", e);
        }
    }
}
