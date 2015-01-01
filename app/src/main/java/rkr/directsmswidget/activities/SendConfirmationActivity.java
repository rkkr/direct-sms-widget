package rkr.directsmswidget.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.Serializable;

import rkr.directsmswidget.R;
import rkr.directsmswidget.settings.MessageSetting;
import rkr.directsmswidget.utils.Helpers;
import rkr.directsmswidget.utils.SmsFactory;


public class SendConfirmationActivity extends Activity {

    MessageSetting setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_confirmation);

        setting = Helpers.IntentToMessageSetting(getIntent());

        String message = "Send message \"" + setting.getWidgetTitle() + "\"?";

        ((TextView)findViewById(R.id.text_message)).setText(message);
        findViewById(R.id.button_send).setOnClickListener(mOnSendClickListener);
        findViewById(R.id.button_cancel).setOnClickListener(mOnCancelClickListener);
    }

    View.OnClickListener mOnSendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SmsFactory.DoSend(v.getContext(), setting);
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
