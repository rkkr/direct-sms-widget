package rkr.directsmswidget;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

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

    private static Map<String, ?> loadAllSettings(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        //Map<String, String> settings = (Map<String, String>) prefs.getAll();
        Map<String, ?> settings = prefs.getAll();
        return settings;
    }

    static Set<Integer> getWidgetIds (Context context) {
        Map<String, ?> settings = loadAllSettings(context);
        Set<Integer> widgetIds = new HashSet<Integer>();

        for (String key : settings.keySet()) {
            //Log.i("Setting keys", key);
            widgetIds.add(Integer.parseInt(key.substring(0, key.indexOf("_"))));
        }
        return widgetIds;
    }


    static WidgetSetting load(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        WidgetSetting setting = new WidgetSetting();
        setting.phoneNumber = prefs.getString(appWidgetId + "_phoneNumber", null);
        setting.title = prefs.getString(appWidgetId + "_title", null);
        setting.message = prefs.getString(appWidgetId + "_message", null);
        setting.clickAction = prefs.getInt(appWidgetId + "_clickAction", 0);

        //if (setting.phoneNumber == null || setting.title == null)
        //    return null;

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
