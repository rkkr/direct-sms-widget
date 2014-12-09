package rkr.directsmswidget.settings;

public class NotificationSetting {
    public static final String PREFS_NAME = "rkr.directsmswidget.NotificationPrefs";

    public String phoneNumber;
    public String contactName;
    public String title;
    public String message;
    public Integer clickAction;
    public Integer hour;
    public Integer minute;
    public Boolean day1;
    public Boolean day2;
    public Boolean day3;
    public Boolean day4;
    public Boolean day5;
    public Boolean day6;
    public Boolean day7;
    public Boolean enabled;
    public Boolean notificationSound;

    public String getWidgetTitle() {
        if (this.title != null && this.title.trim().length() > 0)
            return this.title;

        return this.contactName;
    }

    public String[] phoneNumbers(){
        return this.phoneNumber.split(";", -1);
    }

    public String[] contactNames(){
        return this.contactName.split(";", -1);
    }
}
