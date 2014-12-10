package rkr.directsmswidget.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Map;

import rkr.directsmswidget.R;
import rkr.directsmswidget.settings.NotificationSetting;
import rkr.directsmswidget.settings.SettingsFactory;
import rkr.directsmswidget.settings.WidgetSetting;


public class NotificationScheduler extends BroadcastReceiver {

    public static void sync (Context context)
    {
        Map<Integer, NotificationSetting> settings = SettingsFactory.loadAll(NotificationSetting.class, context);
        for (Map.Entry<Integer, NotificationSetting> entry : settings.entrySet())
            sync(context, entry.getKey(), entry.getValue());

        //TODO: enable autoboot if needed
    }

    public static void sync (Context context, Integer widgetId, NotificationSetting setting)
    {
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationScheduler.class);
        intent.setAction("rkr.directsmswidget.NOTIFICATION_SCHEDULE");
        intent.getExtras().putInt("widgetId", widgetId);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, widgetId, intent, 0);

        //cancel all for this widget
        alarmMgr.cancel(alarmIntent);

        if (!setting.enabled)
            return;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, setting.hour);
        calendar.set(Calendar.MINUTE, setting.minute);

        alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    private void notify(Context context, int widgetId)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int dayOfWeek =  calendar.get(Calendar.DAY_OF_WEEK);

        NotificationSetting setting = SettingsFactory.load(NotificationSetting.class, context, widgetId);

        if (!setting.enabled) {
            Log.e("rkr.directsmswidget.notificationscheduler", "Alarm found for disabled notification");
            sync(context, widgetId, setting);
            return;
        }

        if (dayOfWeek == Calendar.MONDAY && setting.day1 ||
            dayOfWeek == Calendar.TUESDAY && setting.day2 ||
            dayOfWeek == Calendar.WEDNESDAY && setting.day3 ||
            dayOfWeek == Calendar.THURSDAY && setting.day4 ||
            dayOfWeek == Calendar.FRIDAY && setting.day5 ||
            dayOfWeek == Calendar.SATURDAY && setting.day6 ||
            dayOfWeek == Calendar.SUNDAY && setting.day7
            )
            doNotify(context, setting, widgetId);
    }

    private void doNotify(Context context, NotificationSetting setting, int widgetId)
    {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder notification = new Notification.Builder(context);
        //TODO: Update to white icon
        notification.setSmallIcon(R.drawable.ic_launcher);
        notification.setContentTitle("Direct SMS");
        notification.setContentText(setting.getWidgetTitle());

        Intent intent = new Intent(context, NotificationScheduler.class);
        intent.setAction("rkr.directsmswidget.NOTIFICATION_CLICK");
        intent.getExtras().putInt("widgetId", widgetId);
        PendingIntent clickIntent = PendingIntent.getBroadcast(context, widgetId, intent, 0);

        notification.setContentIntent(clickIntent);
        notification.setAutoCancel(true);

        if (setting.notificationSound)
        {
            Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notification.setSound(uri);
        }

        mNotificationManager.notify(widgetId, notification.build());

        doRegisterAutoDelete(context, setting, widgetId);
    }

    private void doRegisterAutoDelete(Context context, NotificationSetting setting, int widgetId)
    {
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationScheduler.class);
        intent.setAction("rkr.directsmswidget.NOTIFICATION_REMOVE");
        intent.getExtras().putInt("widgetId", widgetId);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, widgetId, intent, 0);

        //TODO: store the autodismiss time
        long timeStamp = System.currentTimeMillis() + 60*1000;
        alarmMgr.set(AlarmManager.ELAPSED_REALTIME, timeStamp, alarmIntent);
    }

    private void doSend(Context context, int widgetId)
    {
        WidgetSetting setting = SettingsFactory.load(WidgetSetting.class, context, widgetId);
        SmsFactory.Send(context, setting);
    }

    private void doRemove(Context context, int widgetId)
    {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(widgetId);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            sync(context);
        }
        if (intent.getAction().equals("rkr.directsmswidget.NOTIFICATION_SCHEDULE")) {
            int widgetId = intent.getExtras().getInt("widgetId");
            notify(context, widgetId);
        }
        if (intent.getAction().equals("rkr.directsmswidget.NOTIFICATION_CLICK")) {
            int widgetId = intent.getExtras().getInt("widgetId");
            doSend(context, widgetId);
        }
        if (intent.getAction().equals("rkr.directsmswidget.NOTIFICATION_REMOVE")) {
            int widgetId = intent.getExtras().getInt("widgetId");
            doRemove(context, widgetId);
        }
    }

}
