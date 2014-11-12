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
        prefs.putString(appWidgetId + "_contactName", setting.contactName);
        prefs.putString(appWidgetId + "_phoneNumber", setting.phoneNumber);
        prefs.putInt(appWidgetId + "_clickAction", setting.clickAction);
        prefs.putInt(appWidgetId + "_backgroundColor", setting.backgroundColor);
        prefs.putInt(appWidgetId + "_textColor", setting.textColor);
        prefs.commit();
    }

    private static Map<String, ?> loadAllSettings(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Map<String, ?> settings = prefs.getAll();
        return settings;
    }

    static Set<Integer> getWidgetIds (Context context) {
        Map<String, ?> settings = loadAllSettings(context);
        Set<Integer> widgetIds = new HashSet<Integer>();

        for (String key : settings.keySet()) {
            widgetIds.add(Integer.parseInt(key.substring(0, key.indexOf("_"))));
        }
        return widgetIds;
    }


    static WidgetSetting load(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        WidgetSetting setting = new WidgetSetting();
        setting.phoneNumber = prefs.getString(appWidgetId + "_phoneNumber", null);
        if (setting.phoneNumber == null)
            return null;

        setting.title = prefs.getString(appWidgetId + "_title", null);
        setting.message = prefs.getString(appWidgetId + "_message", null);
        setting.contactName = prefs.getString(appWidgetId + "_contactName", null);
        setting.clickAction = prefs.getInt(appWidgetId + "_clickAction", 0);
        setting.backgroundColor = prefs.getInt(appWidgetId + "_backgroundColor", 0xff33b5e5);
        setting.textColor = prefs.getInt(appWidgetId + "_textColor", 0xffffffff);

        return setting;
    }

    static void delete(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        prefs.remove(appWidgetId + "_title");
        prefs.remove(appWidgetId + "_phoneNumber");
        prefs.remove(appWidgetId + "_contactName");
        prefs.remove(appWidgetId + "_clickAction");
        prefs.remove(appWidgetId + "_message");
        prefs.remove(appWidgetId + "_backgroundColor");
        prefs.remove(appWidgetId + "_textColor");
        prefs.commit();
    }

    static void deleteAll(Context context) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        prefs.clear();
        prefs.commit();
    }
}
