package rkr.directsmswidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.provider.ContactsContract;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;



/**
 * The configuration screen for the {@link DirectSmsHomeWidget DirectSmsHomeWidget} AppWidget.
 */
public class DirectSmsHomeWidgetConfigureActivity extends Activity {

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private static final int CONTACT_PICKER_RESULT = 1;
    private static int widgetClickActionSelection = 0;

    public DirectSmsHomeWidgetConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        //Open view
        setContentView(R.layout.direct_sms_home_widget_configure);

        //Button handlers
        findViewById(R.id.btn_select).setOnClickListener(mOnSelectContactClickListener);
        findViewById(R.id.add_button).setOnClickListener(mOnAddClickListener);
        ((Spinner)findViewById(R.id.cboClickAction)).setOnItemSelectedListener(mOnSelectWidgetClickAction);

        mAppWidgetId = Helpers.IntentToWidgetId(getIntent());

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
    }

    OnClickListener mOnSelectContactClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent pickContactIntent = new Intent( Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI );
            pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(pickContactIntent, CONTACT_PICKER_RESULT);
        }
    };

    AdapterView.OnItemSelectedListener mOnSelectWidgetClickAction = new AdapterView.OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            widgetClickActionSelection = pos;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            //skip
        }
    };

    OnClickListener mOnAddClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Context context = DirectSmsHomeWidgetConfigureActivity.this;

            //Read all settings
            WidgetSetting setting = new WidgetSetting();
            setting.phoneNumber = ((TextView)findViewById(R.id.editPhone)).getText().toString();
            setting.title = ((TextView)findViewById(R.id.editTitle)).getText().toString();
            setting.message = ((TextView)findViewById(R.id.editMessage)).getText().toString();
            setting.clickAction = widgetClickActionSelection;

            //Stop if any are empty
            if (setting.phoneNumber.isEmpty()) {
                Toast.makeText(v.getContext(), "Phone number not entered", Toast.LENGTH_SHORT);
                return;
            }
            if (setting.title.isEmpty()) {
                Toast.makeText(v.getContext(), "Title not entered", Toast.LENGTH_SHORT);
                return;
            }
            if (setting.message.isEmpty()) {
                Toast.makeText(v.getContext(), "Message not entered", Toast.LENGTH_SHORT);
                return;
            }

            WidgetSettingsFactory.save(context, mAppWidgetId, setting);
            // It is the responsibility of the configuration activity to update the app widget
            //AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            //DirectSmsHomeWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTACT_PICKER_RESULT)
        {
            if(resultCode == RESULT_OK)
            {
                Uri contactURI = data.getData();

                Cursor cursor = getContentResolver().query(contactURI, null, null, null, null);
                Boolean numbersExist = cursor.moveToFirst();
                if (!numbersExist)
                    return;
                String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String displayName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                ((TextView)findViewById(R.id.editPhone)).setText(phoneNumber);
                ((TextView)findViewById(R.id.editTitle)).setText(displayName);
            }
        }
    }

}



