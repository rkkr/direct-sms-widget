package rkr.directsmswidget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;

public class Helpers {
    static int IntentToWidgetId(Intent intent){
        Bundle extras = intent.getExtras();
        if (extras == null)
            return AppWidgetManager.INVALID_APPWIDGET_ID;

        return extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }


}
