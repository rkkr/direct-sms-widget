package rkr.directsmswidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.util.Log;


import java.util.List;
import java.util.Set;

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


        Set<Integer> widgetIds = WidgetSettingsFactory.getWidgetIds(this.getApplicationContext());
        for (Integer widgetId : widgetIds)
        {
            WidgetSetting setting = WidgetSettingsFactory.load(this.getApplicationContext(), widgetId);
            Preference pref = new Preference(this.getApplicationContext());
            pref.setTitle(setting.title);
            //pref

            Intent intent = new Intent(this.getApplicationContext(), DirectSmsHomeWidgetConfigureActivity.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            intent.putExtra("loadExisting", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            pref.setIntent(intent);
            //pref.setOnPreferenceClickListener(homeWidgetSettingClick);
            homeWidgetsSection.addPreference(pref);
        }
    }

    Preference.OnPreferenceClickListener homeWidgetSettingClick = new Preference.OnPreferenceClickListener(){
        @Override
        public boolean onPreferenceClick(Preference pref) {
            Intent intent = new Intent(pref.getContext(), DirectSmsHomeWidgetConfigureActivity.class);
            //intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

            startActivity(intent);
            return true;
        }
    };
}
