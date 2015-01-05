package rkr.directsmswidget.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;

import java.util.Calendar;
import java.util.Map;

import rkr.directsmswidget.R;
import rkr.directsmswidget.settings.NotificationSetting;
import rkr.directsmswidget.settings.SettingsFactory;


public class NotificationScheduler extends BroadcastReceiver {

    public static void sync (Context context)
    {
        Map<Integer, NotificationSetting> settings = SettingsFactory.loadAll(NotificationSetting.class, context);
        for (Map.Entry<Integer, NotificationSetting> entry : settings.entrySet())
            sync(context, entry.getKey(), entry.getValue());

        for (Map.Entry<Integer, NotificationSetting> entry : settings.entrySet())
            if (entry.getValue().enabled) {
                setAutoBoot(context, true);
                return;
            }

        setAutoBoot(context, false);
    }

    public static void sync (Context context, Integer widgetId, NotificationSetting setting)
    {
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationScheduler.class);
        intent.setAction("rkr.directsmswidget.NOTIFICATION_SCHEDULE");
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, widgetId, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        //cancel all for this widget
        alarmMgr.cancel(alarmIntent);

        if (!setting.enabled)
            return;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, setting.hour);
        calendar.set(Calendar.MINUTE, setting.minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        //Next schedule is tomorrow
        if (calendar.getTimeInMillis() <= System.currentTimeMillis())
            calendar.add(Calendar.HOUR, 24);

        if (android.os.Build.VERSION.SDK_INT >= 19){
            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
        } else{
            alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
        }

        //Log.d("rkr.directsmswidget.notificationscheduler", "Next notification in: " + (calendar.getTimeInMillis() - System.currentTimeMillis()) / 1000);

        setAutoBoot(context, true);
    }

    private static void setAutoBoot(Context context, boolean value)
    {
        ComponentName receiver = new ComponentName(context, NotificationScheduler.class);
        PackageManager pm = context.getPackageManager();

        if (value)
            pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        else
            pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
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
        {
            doNotify(context, setting, widgetId);
            //Add next schedule event
            sync(context, widgetId, setting);
            return;
        }

        if ((setting.day1 || setting.day2 || setting.day3 || setting.day4 || setting.day5 ||
                setting.day6 || setting.day7) == false)
        {
            doNotify(context, setting, widgetId);
            //Disable single use event
            setting.enabled = false;
            SettingsFactory.save(context, widgetId, setting);
        }
    }

    private void doNotify(Context context, NotificationSetting setting, int widgetId)
    {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder notification = new Notification.Builder(context);
        notification.setSmallIcon(R.drawable.ic_launcher);
        notification.setContentTitle("Direct SMS");
        notification.setContentText(setting.getWidgetTitle());

        Intent intent = new Intent(context, NotificationScheduler.class);
        intent.setAction("rkr.directsmswidget.NOTIFICATION_CLICK");
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        PendingIntent clickIntent = PendingIntent.getBroadcast(context, widgetId, intent, 0);

        notification.setContentIntent(clickIntent);
        notification.setAutoCancel(true);

        if (setting.notificationSound)
        {
            Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notification.setSound(uri);
        }

        //API Level 16 only
        //mNotificationManager.notify(widgetId, notification.build());
        mNotificationManager.notify(widgetId, notification.getNotification());

        doRegisterAutoDelete(context, setting, widgetId);
    }

    private void doRegisterAutoDelete(Context context, NotificationSetting setting, int widgetId)
    {
        if (setting.getAutoDismissMilis() <= 0)
            return;

        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationScheduler.class);
        intent.setAction("rkr.directsmswidget.NOTIFICATION_REMOVE");
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, widgetId, intent, 0);

        long timeStamp = SystemClock.elapsedRealtime() + setting.getAutoDismissMilis();
        alarmMgr.set(AlarmManager.ELAPSED_REALTIME, timeStamp, alarmIntent);
    }

    private void doRemove(Context context, int widgetId)
    {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(widgetId);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.d("rkr.directsmswidget.notificationscheduler", "Notification received: " + intent.getAction());

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED") ||
            intent.getAction().equals("android.intent.action.TIME_SET") ||
            intent.getAction().equals("android.intent.action.TIMEZONE_CHANGED")) {
            sync(context);
        }
        if (intent.getAction().equals("rkr.directsmswidget.NOTIFICATION_SCHEDULE")) {
            int widgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
            notify(context, widgetId);
        }
        if (intent.getAction().equals("rkr.directsmswidget.NOTIFICATION_CLICK")) {
            SmsFactory.Send(NotificationSetting.class, context, intent);
        }
        if (intent.getAction().equals("rkr.directsmswidget.NOTIFICATION_REMOVE")) {
            int widgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
            doRemove(context, widgetId);
        }
    }

}
