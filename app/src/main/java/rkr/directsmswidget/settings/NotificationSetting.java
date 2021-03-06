package rkr.directsmswidget.settings;

import java.util.Calendar;

public class NotificationSetting extends MessageSetting {
    public static final String PREFS_NAME = "rkr.directsmswidget.NotificationPrefs";

    public Integer hour = 8;
    public Integer minute = 0;
    public Boolean day1 = false;
    public Boolean day2 = false;
    public Boolean day3 = false;
    public Boolean day4 = false;
    public Boolean day5 = false;
    public Boolean day6 = false;
    public Boolean day7 = false;
    public Boolean enabled = true;
    public Boolean notificationSound = true;
    public Integer autoDismiss = 0;

    public long getAutoDismissMilis() {
        switch (autoDismiss) {
            case 0:
                return 10 * 60 * 1000;
            case 1:
                return 30 * 60 * 1000;
            case 2:
                return 60 * 60 * 1000;
            case 3:
                return 2 * 60 * 60 * 1000;
            case 4:
                return 6 * 60 * 60 * 1000;
            case 5:
                return 12 * 60 * 60 * 1000;
            case 6:
                return 0;
        }
        return -1;
    }

    public Boolean weekdayEnabled (int weekday)
    {
        switch (weekday) {
            case Calendar.MONDAY:
                return day1;
            case Calendar.TUESDAY:
                return day2;
            case Calendar.WEDNESDAY:
                return day3;
            case Calendar.THURSDAY:
                return day4;
            case Calendar.FRIDAY:
                return day5;
            case Calendar.SATURDAY:
                return day6;
            case Calendar.SUNDAY:
                return day7;
        }
        return null;
    }

    public int daysToNextEnabledWeekDay (int weekday)
    {
        for (int i=1; i<=7; i++) {
            int _weekday = (weekday + i - 1) % 7 + 1;
            if (weekdayEnabled(_weekday))
                return i;
        }
        return -1;
    }
}
