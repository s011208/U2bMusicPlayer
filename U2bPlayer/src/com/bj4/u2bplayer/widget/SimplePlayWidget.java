
package com.bj4.u2bplayer.widget;

import com.bj4.u2bplayer.PlayList;
import com.bj4.u2bplayer.R;
import com.bj4.u2bplayer.service.PlayMusicService;
import com.bj4.u2bplayer.utilities.PlayListInfo;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class SimplePlayWidget extends AppWidgetProvider {
    private static final String PLAY = "play";

    private static final String PAUSE = "pause";

    private static final String NEXT = "next";

    private static final String PREVIOUS = "previous";

    private static final String FAVORITE = "favorite";
    
    private static final String EXIT = "exit";

    private static boolean sIsPlaying = false;

    private static PlayListInfo sCurrentInfo;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        performUpdate(context, sCurrentInfo, false);
    }

    public static void performUpdate(Context context, PlayListInfo info, boolean isPlaying) {
        AppWidgetManager awm = AppWidgetManager.getInstance(context);
        performUpdate(context, awm, info, isPlaying);
    }

    public static void performUpdate(Context context, AppWidgetManager awm, PlayListInfo info,
            boolean isPlaying) {
        sCurrentInfo = info;
        sIsPlaying = isPlaying;
        int widgetId[] = awm.getAppWidgetIds(new ComponentName(context, SimplePlayWidget.class));
        for (int appWidgetId : widgetId) {
            // basic
            RemoteViews updateViews = new RemoteViews(context.getPackageName(),
                    R.layout.simple_play_widget);
            String infoString = context.getString(R.string.simple_play_widget_none_playinfo);
            boolean isFavorite = false;
            if (sCurrentInfo != null) {
                infoString = sCurrentInfo.mArtist + ": " + sCurrentInfo.mMusicTitle;
                isFavorite = sCurrentInfo.mIsFavorite;
            }
            updateViews.setTextViewText(R.id.simple_play_widget_info, infoString);
            updateViews.setOnClickPendingIntent(R.id.simple_play_widget_favorite,
                    getPendingSelfIntent(context, FAVORITE));
            updateViews.setOnClickPendingIntent(R.id.simple_play_widget_previous,
                    getPendingSelfIntent(context, PREVIOUS));
            updateViews.setOnClickPendingIntent(R.id.simple_play_widget_play,
                    getPendingSelfIntent(context, PLAY));
            updateViews.setOnClickPendingIntent(R.id.simple_play_widget_pause,
                    getPendingSelfIntent(context, PAUSE));
            updateViews.setOnClickPendingIntent(R.id.simple_play_widget_next,
                    getPendingSelfIntent(context, NEXT));

            // change
            updateViews.setViewVisibility(sIsPlaying ? R.id.simple_play_widget_play
                    : R.id.simple_play_widget_pause, View.GONE);
            updateViews.setViewVisibility(sIsPlaying ? R.id.simple_play_widget_pause
                    : R.id.simple_play_widget_play, View.VISIBLE);
            updateViews.setImageViewResource(R.id.simple_play_widget_favorite,
                    isFavorite ? R.drawable.widget_favorite_true : R.drawable.widget_favorite);

            awm.updateAppWidget(appWidgetId, updateViews);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        if (PLAY.equals(action)) {
            sendActions(context, PlayMusicService.INTENT_ACTION_PLAY, PlayList.getInstance(context)
                    .getPointer());
        } else if (FAVORITE.equals(action)) {
            sendActions(context, PlayMusicService.INTENT_SWITCH_FAVORITE);
        } else if (NEXT.equals(action)) {
            sendActions(context, PlayMusicService.INTENT_ACTION_PLAY,
                    PlayMusicService.PLAY_NEXT_INDEX);
        } else if (PREVIOUS.equals(action)) {
            sendActions(context, PlayMusicService.INTENT_ACTION_PLAY,
                    PlayMusicService.PLAY_PREVIOUS_INDEX);
        } else if (PAUSE.equals(action)) {
            sendActions(context, PlayMusicService.INTENT_ACTION_PAUSE);
        } else if (EXIT.equals(action)) {
            sendActions(context, PlayMusicService.INTENT_ACTION_EXIT);
        }
        performUpdate(context, sCurrentInfo, sIsPlaying);
    }

    private void sendActions(Context context, String action) {
        context.startService(new Intent(context, PlayMusicService.class).putExtra(
                PlayMusicService.INTENT_ACTION, action));
    }

    private void sendActions(Context context, String action, int index) {
        context.startService(new Intent(context, PlayMusicService.class).putExtra(
                PlayMusicService.INTENT_PLAY_INDEX, index).putExtra(PlayMusicService.INTENT_ACTION,
                action));
    }

    private static PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, SimplePlayWidget.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}
