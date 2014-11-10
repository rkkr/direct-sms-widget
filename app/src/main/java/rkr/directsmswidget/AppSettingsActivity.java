package rkr.directsmswidget;

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

public class AppSettingsActivity extends PreferenceActivity {

    public static String refreshAction = "rkr.directsmsmessage.appsettingsactivity.refreshwidgetlist";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings_main);

        IntentFilter filter = new IntentFilter(refreshAction);
        registerReceiver(broadcastReceiver, filter);

        CreateHomeWidgets();
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
            WidgetSetting setting = WidgetSettingsFactory.load(this.getApplicationContext(), widgetId);
            Preference pref = new Preference(this.getApplicationContext());
            pref.setTitle(setting.title);

            Intent intent = new Intent(this.getApplicationContext(), HomeWidgetConfigureActivity.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            intent.putExtra("loadExisting", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            pref.setIntent(intent);
            homeWidgetsSection.addPreference(pref);
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            CreateHomeWidgets();
        }
    };
}
