package rkr.directsmswidget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;

public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings_main);

        CreateHomeWidgets();
    }

    private void CreateHomeWidgets() {
        PreferenceGroup homeWidgetsSection = (PreferenceGroup)getPreferenceManager().findPreference("HomeScreenSettings");

        //Set<Integer> widgetIds = WidgetSettingsFactory.getWidgetIds(this.getApplicationContext());
        int[] widgetIds = AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this, HomeWidget.class));

        if (widgetIds.length == 0)
        {
            String helpText = "Widgets you add will appear here";
            Preference pref = new Preference(this.getApplicationContext());
            pref.setTitle(helpText);
            homeWidgetsSection.addPreference(pref);
        }

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
}
