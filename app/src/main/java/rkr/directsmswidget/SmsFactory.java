package rkr.directsmswidget;

import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

public class SmsFactory {

    public static void SendForWidget(Context context, Intent intent){
        int widgetId = Helpers.IntentToWidgetId(intent);
        WidgetSetting setting = WidgetSettingsFactory.load(context, widgetId);

        if (setting == null || setting.phoneNumber == null || setting.message == null){
            Log.e("rkr.directSmsWidget", "Reading settings file failed");
            return;
        }

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(setting.phoneNumber, null, setting.message, null, null);
    }
}
