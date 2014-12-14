package rkr.directsmswidget.settings;

public class WidgetSetting {
    public static final String PREFS_NAME = "rkr.directsmswidget.WidgetPrefs";

    public String phoneNumber = "";
    public String contactName = "";
    public String title = "";
    public String message = "";
    public Integer clickAction = 0;
    public Integer backgroundColor = 0xff33b5e5;
    public Integer textColor = 0xffffffff;

    public String getWidgetTitle() {
        if (this.title != null && this.title.trim().length() > 0)
            return this.title;

        return this.contactName;
    }

    public String[] phoneNumbers(){return this.phoneNumber.split(";", -1);}

    public String[] contactNames(){return this.contactName.split(";", -1);}
}
