package rkr.directsmswidget;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Map;

import rkr.directsmswidget.activities.HomeWidgetConfigureActivity;
import rkr.directsmswidget.activities.NotificationConfigureActivity;
import rkr.directsmswidget.settings.NotificationSetting;
import rkr.directsmswidget.utils.NotificationScheduler;
import rkr.directsmswidget.widgets.HomeWidget;
import rkr.directsmswidget.R;
import rkr.directsmswidget.settings.SettingsFactory;
import rkr.directsmswidget.settings.WidgetSetting;

public class AppSettingsActivity extends PreferenceActivity {

    public static String refreshAction = "rkr.directsmsmessage.refreshwidgetlist";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings_main);

        IntentFilter filter = new IntentFilter(refreshAction);
        registerReceiver(broadcastReceiver, filter);

        CreateHomeWidgets();
        CreateNotifications();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    public void CreateHomeWidgets() {
        PreferenceGroup homeWidgetsSection = (PreferenceGroup)getPreferenceManager().findPreference("HomeScreenSettings");
        int[] widgetIds = AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this, HomeWidget.class));

        //Keep the help text if no widgets are found
        if (widgetIds.length > 0)
            homeWidgetsSection.removeAll();

        for (Integer widgetId : widgetIds)
        {
            WidgetSetting setting = SettingsFactory.load(WidgetSetting.class, this.getApplicationContext(), widgetId);
            if (setting == null)
            {
                //Cleanup all zombie widgets
                AppWidgetHost host = new AppWidgetHost(this.getApplicationContext(), 1);
                host.deleteAppWidgetId(widgetId);
                continue;
            }

            Preference pref = new Preference(this.getApplicationContext());
            pref.setTitle(setting.getWidgetTitle());

            Intent intent = new Intent(this.getApplicationContext(), HomeWidgetConfigureActivity.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            intent.putExtra("loadExisting", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            pref.setIntent(intent);
            homeWidgetsSection.addPreference(pref);
        }
    }

    public void CreateNotifications()
    {
        PreferenceGroup notificationSection = (PreferenceGroup)getPreferenceManager().findPreference("NotificationSettings");
        while(notificationSection.getPreferenceCount() > 1)
            notificationSection.removePreference(notificationSection.getPreference(0));

        //Add new button
        Intent intent = new Intent(this.getApplicationContext(), NotificationConfigureActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationSection.findPreference("NotificationSettingsNew").setIntent(intent);

        //Load current notifications
        Map<Integer ,NotificationSetting> notifications = SettingsFactory.loadAll(NotificationSetting.class, this);
        for (Map.Entry<Integer, NotificationSetting> pair : notifications.entrySet())
        {
            Preference pref = new Preference(this.getApplicationContext());
            pref.setOrder(1);
            pref.setTitle(pair.getValue().getWidgetTitle());

            intent = new Intent(this.getApplicationContext(), NotificationConfigureActivity.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, pair.getKey());
            intent.putExtra("loadExisting", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            pref.setIntent(intent);
            notificationSection.addPreference(pref);
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            CreateHomeWidgets();
            CreateNotifications();
        }
    };
}
