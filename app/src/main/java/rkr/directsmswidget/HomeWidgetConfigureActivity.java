package rkr.directsmswidget;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.provider.ContactsContract;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class HomeWidgetConfigureActivity extends Activity {

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private static final int CONTACT_PICKER_RESULT = 1;
    private static int widgetClickActionSelection = 0;

    public HomeWidgetConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        //Open view
        setContentView(R.layout.home_widget_configure);

        //Button handlers
        findViewById(R.id.btn_select).setOnClickListener(mOnSelectContactClickListener);
        ((Spinner)findViewById(R.id.cboClickAction)).setOnItemSelectedListener(mOnSelectWidgetClickAction);

        mAppWidgetId = Helpers.IntentToWidgetId(getIntent());

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Log.e("rkr.directsmswidget.widgetconfigureactivity", "WidgetID not found in intent, canceling load");
            finish();
            return;
        }

        if (getIntent().getExtras().getBoolean("loadExisting", false))
        {
            WidgetSetting setting = WidgetSettingsFactory.load(this.getApplicationContext(), mAppWidgetId);
            fillSettingsWindow(setting);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save)
            mOnAddClickListener(this.getApplicationContext());
        else
            return super.onOptionsItemSelected(item);
        return true;
    }

    private void fillSettingsWindow(WidgetSetting setting)
    {
        ((TextView)findViewById(R.id.editPhone)).setText(setting.phoneNumber);
        ((TextView)findViewById(R.id.editTitle)).setHint(setting.contactName);
        ((TextView)findViewById(R.id.editTitle)).setText(setting.title);
        ((TextView)findViewById(R.id.editMessage)).setText(setting.message);
        ((Spinner)findViewById(R.id.cboClickAction)).setSelection(setting.clickAction);
        Log.i("rkr.directsmswidget.configureactivity", "Item " + setting.clickAction + " loaded");
        widgetClickActionSelection = setting.clickAction;
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
            Log.i("rkr.directsmswidget.configureactivity", "Item " + pos + " selected");
            widgetClickActionSelection = pos;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            //skip
        }
    };

    private void mOnAddClickListener(Context context) {
        //Read all settings
        WidgetSetting setting = new WidgetSetting();
        setting.phoneNumber = ((TextView)findViewById(R.id.editPhone)).getText().toString();
        CharSequence contactName = ((TextView)findViewById(R.id.editTitle)).getHint();
        setting.contactName = contactName == null ? "" : contactName.toString();
        setting.title = ((TextView)findViewById(R.id.editTitle)).getText().toString();
        setting.message = ((TextView)findViewById(R.id.editMessage)).getText().toString();
        setting.clickAction = widgetClickActionSelection;

        //Stop if any are empty
        if (setting.phoneNumber.isEmpty()) {
            Toast.makeText(context, "Phone number not entered", Toast.LENGTH_SHORT).show();
            return;
        }
        if (setting.message.isEmpty()) {
            Toast.makeText(context, "Message not entered", Toast.LENGTH_SHORT).show();
            return;
        }

        WidgetSettingsFactory.save(context, mAppWidgetId, setting);

        //Update the widget manually if it exists
        //if (getIntent().getExtras().getBoolean("loadExisting", false)) {
        //Widget update event needs to be called explicitly
        //Sending broadcast to update all widgets as it is created at this time
        Intent intent = new Intent(context, HomeWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] widgetIds = {mAppWidgetId};
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
        sendBroadcast(intent);
        //}

        if (getIntent().getExtras().getBoolean("loadExisting", false)) {
            Intent settingsUpdateIntent = new Intent();
            settingsUpdateIntent.setAction(AppSettingsActivity.refreshAction);
            settingsUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            sendBroadcast(settingsUpdateIntent);
        }

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

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
                ((TextView)findViewById(R.id.editTitle)).setHint(displayName);
            }
        }
    }

}



