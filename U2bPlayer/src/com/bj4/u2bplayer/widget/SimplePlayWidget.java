
package com.bj4.u2bplayer.widget;

import com.bj4.u2bplayer.R;
import com.bj4.u2bplayer.utilities.PlayListInfo;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class SimplePlayWidget extends AppWidgetProvider {
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
            awm.updateAppWidget(appWidgetId, updateViews);
        }
    }
}
