package rkr.directsmswidget;

public class WidgetSetting {
    public String phoneNumber;
    public String contactName;
    public String title;
    public String message;
    public int clickAction;
    public int backgroundColor;
    public int textColor;

    public String getWidgetTitle() {
        if (this.title != null && this.title.trim().length() > 0)
            return this.title;

        return this.contactName;
    }
}
