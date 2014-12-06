package rkr.directsmswidget.utils;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.Activity;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import rkr.directsmswidget.activities.SendConfirmationActivity;
import rkr.directsmswidget.settings.SettingsFactory;
import rkr.directsmswidget.settings.WidgetSetting;

public class SmsFactory {

    static private boolean toastReceiverRegistered = false;

    public static void SendForWidget(final Context context, Intent intent){
        int widgetId = Helpers.IntentToWidgetId(intent);
        final WidgetSetting setting = SettingsFactory.load(WidgetSetting.class, context, widgetId);

        if (setting == null || setting.phoneNumber == null || setting.message == null){
            Log.e("rkr.directsmswidget.smsfactory", "Reading settings file failed");
            return;
        }

        switch (setting.clickAction)
        {
            case 0:
                Send(context, setting);
                break;
            case 1:
                Intent confirmationIntent = new Intent(context, SendConfirmationActivity.class);
                confirmationIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
                confirmationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(confirmationIntent);
                break;
            case 2:
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address", setting.phoneNumber);
                smsIntent.putExtra("sms_body",setting.message);
                smsIntent.setFlags(smsIntent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(smsIntent);
                break;
            default:
                Log.e("rkr.directsmswidget.smsfactory", "Unknown widget click action: " + setting.clickAction);
        }
    }

    public static void Send(Context context, WidgetSetting setting)
    {
        String phoneNumbers[] = setting.phoneNumber.split(";");
        for (int i=0; i<phoneNumbers.length; i++)
            Send(context, phoneNumbers[i], setting.message);
    }

    public static void Send(Context context, String phoneNumber, String message)
    {
        Send(context, phoneNumber, message, true);
    }

    public static void Send(Context context, String phoneNumber, String message, Boolean showToast)
    {
        Intent sentIntent = new Intent("message_sent");
        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, sentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (!toastReceiverRegistered) {
            context.getApplicationContext().registerReceiver(smsReceive, new IntentFilter("message_sent"));
            toastReceiverRegistered = true;
        }

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, sentPI, null);
    }

    static BroadcastReceiver smsReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (getResultCode() == Activity.RESULT_OK)
                Toast.makeText(context, "Message sent", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, "Message send failure", Toast.LENGTH_SHORT).show();
        }
    };
}
