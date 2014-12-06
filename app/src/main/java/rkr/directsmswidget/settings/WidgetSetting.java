package rkr.directsmswidget.settings;

public class WidgetSetting {
    public static final String PREFS_NAME = "rkr.directsmswidget.WidgetPrefs";

    public String phoneNumber;
    public String contactName;
    public String title;
    public String message;
    public Integer clickAction;
    public Integer backgroundColor;
    public Integer textColor;

    public String getWidgetTitle() {
        if (this.title != null && this.title.trim().length() > 0)
            return this.title;

        return this.contactName;
    }

    public String[] phoneNumbers(){return this.phoneNumber.split(";", -1);}

    public String[] contactNames(){return this.contactName.split(";", -1);}
}
