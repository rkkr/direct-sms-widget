package rkr.directsmswidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;
import android.app.PendingIntent;
import android.content.Intent;

public class HomeWidget extends AppWidgetProvider {

    public static String CLICK_ACTION = "rkr.directsmswidget.intent.action.Click";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(CLICK_ACTION)) {
            SmsFactory.SendForWidget(context, intent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            WidgetSettingsFactory.delete(context, appWidgetIds[i]);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        WidgetSettingsFactory.deleteAll(context);
    }

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        WidgetSetting setting = WidgetSettingsFactory.load(context, appWidgetId);
        if (setting == null) {
            Log.e("rkr.directsmswidget.smshomewidget", "Reading settings file failed");
            return;
        }
        String widgetText = setting.title;

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.home_widget);

        views.setOnClickPendingIntent(R.id.appwidget_text, getPendingSelfIntent(context, CLICK_ACTION, appWidgetId));
        views.setOnClickPendingIntent(R.id.appwidget_frame, getPendingSelfIntent(context, CLICK_ACTION, appWidgetId));
        views.setOnClickPendingIntent(R.id.appwidget_header, getPendingSelfIntent(context, CLICK_ACTION, appWidgetId));

        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action, int appWidgetId) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
    }
}


