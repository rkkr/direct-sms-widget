package rkr.directsmswidget;

import android.content.Context;
import android.content.SharedPreferences;

public class WidgetSettingsFactory {
    private static final String PREFS_NAME = "rkr.directsmswidget.WidgetPrefs";

    static void save(Context context, int appWidgetId, WidgetSetting setting) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        prefs.putString(appWidgetId + "_title", setting.title);
        prefs.putString(appWidgetId + "_message", setting.message);
        prefs.putString(appWidgetId + "_phoneNumber", setting.phoneNumber);
        prefs.putInt(appWidgetId + "_clickAction", setting.clickAction);
        prefs.commit();
    }

    static WidgetSetting load(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        WidgetSetting setting = new WidgetSetting();
        setting.phoneNumber = prefs.getString(appWidgetId + "_phoneNumber", null);
        setting.title = prefs.getString(appWidgetId + "_title", null);
        setting.message = prefs.getString(appWidgetId + "_message", null);
        setting.clickAction = prefs.getInt(appWidgetId + "clickAction", 0);

        if (setting.phoneNumber == null || setting.title == null)
            return null;

        return setting;
    }

    static void delete(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        prefs.remove(appWidgetId + "_title");
        prefs.remove(appWidgetId + "_phoneNumber");
        prefs.remove(appWidgetId + "_clickAction");
        prefs.remove(appWidgetId + "_message");
        prefs.commit();
    }
}
