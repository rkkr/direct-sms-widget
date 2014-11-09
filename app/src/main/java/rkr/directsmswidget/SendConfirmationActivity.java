package rkr.directsmswidget;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class SendConfirmationActivity extends Activity {

    WidgetSetting widgetSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_confirmation);

        int widgetId = Helpers.IntentToWidgetId(getIntent());
        widgetSetting = WidgetSettingsFactory.load(this.getApplicationContext(), widgetId);
        String message = "Send message \"" + widgetSetting.title + "\"?";

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
