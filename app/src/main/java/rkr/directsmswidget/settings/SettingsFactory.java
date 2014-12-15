package rkr.directsmswidget.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SettingsFactory {
    //public static final String PREFS_NAME = "rkr.directsmswidget.WidgetPrefs";

    //static void save(Context context, int appWidgetId, WidgetSetting setting) {
    public static void save(Context context, int appWidgetId, Object setting) {
        SharedPreferences.Editor prefs = context.getSharedPreferences((String)get(setting, "PREFS_NAME"), Context.MODE_PRIVATE).edit();

        for (Field field : setting.getClass().getFields())
        {
            //don't save/load constants
            if (Modifier.isFinal(field.getModifiers()))
                continue;

            String key = appWidgetId + "_" + field.getName();
            try {
                if (field.getType().equals(Boolean.class))
                    prefs.putBoolean(key, (Boolean)field.get(setting));
                if (field.getType().equals(Integer.class))
                    prefs.putInt(key, (Integer)field.get(setting));
                if (field.getType().equals(String.class))
                    prefs.putString(key, (String)field.get(setting));
            }
            catch (Exception e){
                Log.e("rkr.directsmswidget.widgetsettingfactory.save", e.getMessage());
            }
        }
        prefs.commit();
    }

    public static <T> Map<Integer, T> loadAll(Class<T> cls, Context context) {
        Map<Integer, T> temp = new HashMap<Integer, T>();
        Set<Integer> ids = getIds(cls, context);
        for (Integer id : ids)
            temp.put(id, (load(cls, context, id)));
        return temp;
    }

    public static <T> Set<Integer> getIds (Class<T> cls, Context context) {
        T setting = createObject(cls);
        SharedPreferences prefs = context.getSharedPreferences((String)get(setting, "PREFS_NAME"), Context.MODE_PRIVATE);
        Map<String, ?> settings = prefs.getAll();
        Set<Integer> widgetIds = new HashSet<Integer>();

        for (String key : settings.keySet()) {
            widgetIds.add(Integer.parseInt(key.substring(0, key.indexOf("_"))));
        }
        return widgetIds;
    }


    public static <T> T load(Class<T> cls, Context context, int appWidgetId) {
        T setting = createObject(cls);
        SharedPreferences prefs = context.getSharedPreferences((String)get(setting, "PREFS_NAME"), Context.MODE_PRIVATE);
        boolean loaded = false;

        for (Field field : setting.getClass().getFields())
        {
            //don't save/load constants
            if (Modifier.isFinal(field.getModifiers()))
                continue;

            String key = appWidgetId + "_" + field.getName();
            if (!prefs.contains(key)) {
                Log.e("rkr.direct-sms-widget.widgetsettingfactory.load", "Setting " + key + " not found");
                continue;
                //return null;
            }
            loaded = true;
            //Log.d("rkr.direct-sms-widget.widgetsettingfactory.load", "Setting " + key + " loading");

            try {
                if (field.getType() == Boolean.class)
                    field.set(setting, prefs.getBoolean(key, false));
                if (field.getType() == Integer.class)
                    field.set(setting, prefs.getInt(key, 0));
                if (field.getType() == String.class)
                    field.set(setting, prefs.getString(key, null));
            }
            catch (Exception e){
                Log.e("rkr.direct-sms-widget.widgetsettingfactory.load", e.getMessage());
                continue;
                //return null;
            }
        }
        if (!loaded) {
            Log.e("rkr.direct-sms-widget.widgetsettingfactory.load", "Failed to load setting " + appWidgetId);
            return null;
        }

        return setting;
    }

    public static <T> void delete(Class<T> cls, Context context, int appWidgetId) {
        T setting = createObject(cls);
        SharedPreferences.Editor prefs = context.getSharedPreferences((String)get(setting, "PREFS_NAME"), Context.MODE_PRIVATE).edit();

        for (Map.Entry<String, ?> item : context.getSharedPreferences((String)get(setting, "PREFS_NAME"), Context.MODE_PRIVATE).getAll().entrySet())
            if (item.getKey().startsWith(appWidgetId + "_"))
                prefs.remove(item.getKey());

        prefs.commit();
    }

    public static <T> void deleteAll(Class<T> cls, Context context) {
        T setting = createObject(cls);
        SharedPreferences.Editor prefs = context.getSharedPreferences((String)get(setting, "PREFS_NAME"), Context.MODE_PRIVATE).edit();
        prefs.clear();
        prefs.commit();
    }

    private static Object get(Object obj, String key)
    {
        try {
            return obj.getClass().getField(key).get(obj);
        }
        catch (Exception e){
            return null;
        }
    }

    private static void set(Object obj, String key, Object value)
    {
        try {
            obj.getClass().getField(key).set(obj, value);
        }
        catch (Exception e){
        }
    }

    private static <T> T createObject(Class<T> cls)
    {
        T setting;
        try {
            setting = cls.newInstance();
        }
        catch (Exception e){
            Log.e("rkr.direct-sms-widget.widgetsettingfactory.load", e.getMessage());
            return null;
        }
        return setting;
    }
}
