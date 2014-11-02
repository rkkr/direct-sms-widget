package rkr.directsmswidget;

import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

public class SmsFactory {

    public static void SendForWidget(Context context, Intent intent){
        String phoneNumber = "+37064603228";
        String message = "Hello World!";

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }
}
