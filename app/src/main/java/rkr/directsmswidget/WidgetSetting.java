package rkr.directsmswidget;

public class WidgetSetting {
    public String phoneNumber;
    public String contactName;
    public String title;
    public String message;
    public int clickAction;

    public String getWidgetTitle() {
        if (this.title != null && this.title != "")
            return this.title;

        return this.contactName;
    }
}
