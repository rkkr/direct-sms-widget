package rkr.directsmswidget.settings;

import java.io.Serializable;

public class MessageSetting implements Serializable {
    public String phoneNumber = "";
    public String contactName = "";
    public String title = "";
    public String message = "";
    public Integer clickAction = 0;

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
