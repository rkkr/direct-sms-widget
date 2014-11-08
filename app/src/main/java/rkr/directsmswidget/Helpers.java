package rkr.directsmswidget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class Helpers {
    static int IntentToWidgetId(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null)
        {
            Log.e("rkr.directsmswidget.helpers", "Widget id not in intent");
            return AppWidgetManager.INVALID_APPWIDGET_ID;
        }

        return extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }


}
