package rkr.directsmswidget.settings;

public class NotificationSetting {
    public static final String PREFS_NAME = "rkr.directsmswidget.NotificationPrefs";

    public String phoneNumber;
    public String contactName;
    public String title;
    public String message;
    public Integer clickAction;

    public String getWidgetTitle() {
        if (this.title != null && this.title.trim().length() > 0)
            return this.title;

        return this.contactName;
    }
}
