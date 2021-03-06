package rkr.directsmswidget.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;
import android.app.PendingIntent;
import android.content.Intent;

import java.util.Arrays;

import rkr.directsmswidget.R;
import rkr.directsmswidget.settings.SettingsFactory;
import rkr.directsmswidget.settings.WidgetSetting;
import rkr.directsmswidget.utils.SmsFactory;

public class HomeWidget extends AppWidgetProvider {

    public static String CLICK_ACTION = "rkr.directsmswidget.intent.action.Click";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d("Direct SMS Widget", "Received intent: " + intent.getAction());
        if (intent.getAction().startsWith(CLICK_ACTION)) {
            SmsFactory.Send(WidgetSetting.class, context, intent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        Log.d("Direct SMS Widget", "Update for widgets: " + Arrays.toString(appWidgetIds));
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
            SettingsFactory.delete(WidgetSetting.class, context, appWidgetIds[i]);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        SettingsFactory.deleteAll(WidgetSetting.class, context);
    }

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.home_widget);

        WidgetSetting setting = SettingsFactory.load(WidgetSetting.class, context, appWidgetId);
        if (setting == null) {
            Log.e("rkr.directsmswidget.smshomewidget", "Reading settings file failed");
        }
        else {
            String widgetText = setting.getWidgetTitle();
            widgetText = widgetText.replaceAll(";", "; ");

            views.setOnClickPendingIntent(R.id.appwidget_text, getPendingSelfIntent(context, CLICK_ACTION, appWidgetId));
            views.setOnClickPendingIntent(R.id.appwidget_frame, getPendingSelfIntent(context, CLICK_ACTION, appWidgetId));
            views.setOnClickPendingIntent(R.id.appwidget_header, getPendingSelfIntent(context, CLICK_ACTION, appWidgetId));

            views.setTextViewText(R.id.appwidget_text, widgetText);
            views.setTextColor(R.id.appwidget_text, setting.textColor);
            views.setInt(R.id.appwidget_text, "setBackgroundColor", setting.backgroundColor);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action, int appWidgetId) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action + "_" + appWidgetId);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
    }
}


