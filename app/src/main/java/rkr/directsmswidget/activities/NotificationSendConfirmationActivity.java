package rkr.directsmswidget.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import rkr.directsmswidget.R;
import rkr.directsmswidget.settings.NotificationSetting;
import rkr.directsmswidget.settings.SettingsFactory;
import rkr.directsmswidget.settings.WidgetSetting;
import rkr.directsmswidget.utils.Helpers;
import rkr.directsmswidget.utils.SmsFactory;


public class NotificationSendConfirmationActivity extends Activity {

    NotificationSetting widgetSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_confirmation);

        int widgetId = Helpers.IntentToWidgetId(getIntent());
        widgetSetting = SettingsFactory.load(NotificationSetting.class, this.getApplicationContext(), widgetId);
        String message = "Send message \"" + widgetSetting.getWidgetTitle() + "\"?";

        ((TextView)findViewById(R.id.text_message)).setText(message);
        findViewById(R.id.button_send).setOnClickListener(mOnSendClickListener);
        findViewById(R.id.button_cancel).setOnClickListener(mOnCancelClickListener);
    }

    View.OnClickListener mOnSendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SmsFactory.Send(v.getContext(), widgetSetting);
            finish();
        }
    };

    View.OnClickListener mOnCancelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };
}
