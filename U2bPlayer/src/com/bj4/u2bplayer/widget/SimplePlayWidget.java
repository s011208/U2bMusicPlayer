
package com.bj4.u2bplayer.widget;

import com.bj4.u2bplayer.R;
import com.bj4.u2bplayer.utilities.PlayListInfo;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class SimplePlayWidget extends AppWidgetProvider {
    private static final String PLAY = "play";

    private static final String PAUSE = "pause";

    private static final String NEXT = "next";

    private static final String PREVIOUS = "previous";

    private static final String FAVORITE = "favorite";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        performUpdate(context, null);
    }

    public static void performUpdate(Context context, PlayListInfo info) {
        AppWidgetManager awm = AppWidgetManager.getInstance(context);
        performUpdate(context, awm, info);
    }

    public static void performUpdate(Context context, AppWidgetManager awm, PlayListInfo info) {
        int widgetId[] = awm.getAppWidgetIds(new ComponentName(context, SimplePlayWidget.class));
        for (int appWidgetId : widgetId) {
            RemoteViews updateViews = new RemoteViews(context.getPackageName(),
                    R.layout.simple_play_widget);
            String infoString = context.getString(R.string.simple_play_widget_none_playinfo);
            if (info != null) {
                infoString = info.mArtist + ": " + info.mMusicTitle;
            }
            updateViews.setTextViewText(R.id.simple_play_widget_info, infoString);
            updateViews.setOnClickPendingIntent(R.id.simple_play_widget_favorite,
                    getPendingSelfIntent(context, FAVORITE));
            updateViews.setOnClickPendingIntent(R.id.simple_play_widget_previous,
                    getPendingSelfIntent(context, PREVIOUS));
            updateViews.setOnClickPendingIntent(R.id.simple_play_widget_play,
                    getPendingSelfIntent(context, PLAY));
            updateViews.setOnClickPendingIntent(R.id.simple_play_widget_next,
                    getPendingSelfIntent(context, NEXT));
            awm.updateAppWidget(appWidgetId, updateViews);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        if (PLAY.equals(action)) {
        } else if (FAVORITE.equals(action)) {
        } else if (NEXT.equals(action)) {
        } else if (NEXT.equals(action)) {
        }
    }

    protected static PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, SimplePlayWidget.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}
