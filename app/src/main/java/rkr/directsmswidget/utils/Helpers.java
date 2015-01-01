package rkr.directsmswidget.utils;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Random;

import rkr.directsmswidget.settings.MessageSetting;

public class Helpers {
    private static Random rd = new Random();

    public static int IntentToWidgetId(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null)
        {
            Log.e("rkr.directsmswidget.helpers", "Widget id not in intent");
            return AppWidgetManager.INVALID_APPWIDGET_ID;
        }

        return extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    public static MessageSetting IntentToMessageSetting(Intent intent) {
        Bundle extras = intent.getExtras();
        return (MessageSetting)extras.getSerializable("message");
    }

    public static int getRandInt(){
        return Math.abs(rd.nextInt());
    }

}
