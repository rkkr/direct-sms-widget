package rkr.directsmswidget.activities;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import rkr.directsmswidget.AppSettingsActivity;
import rkr.directsmswidget.R;
import rkr.directsmswidget.settings.NotificationSetting;
import rkr.directsmswidget.settings.SettingsFactory;
import rkr.directsmswidget.utils.Helpers;

public class NotificationConfigureActivity extends HomeWidgetConfigureActivity {

    @Override
    public void onCreate(Bundle icicle) {
        super.passOnCreate(icicle);

        //Open view
        setContentView(R.layout.notification_widget_configure);

        //Button handlers
        findViewById(R.id.button_select_contact).setOnClickListener(mOnSelectContactClickListener);
        findViewById(R.id.button_add_new_contact).setOnClickListener(mAddContactClickListener);
        findViewById(R.id.button_delete_contact).setOnClickListener(mDeleteContactClickListener);
        findViewById(R.id.button_select_time).setOnClickListener(mSelectTimeClickListener);
        ((Spinner)findViewById(R.id.cboClickAction)).setOnItemSelectedListener(mOnSelectWidgetClickAction);
        contactRows.add(new ContactRow(findViewById(R.id.layout_contact_row_1)));


        if (getIntent().getExtras().getBoolean("loadExisting", false))
        {
            NotificationSetting setting = SettingsFactory.load(NotificationSetting.class, this.getApplicationContext(), mAppWidgetId);
            fillSettingsWindow(setting);
        }
        else
            mAppWidgetId = Helpers.getRandInt();
    }

    View.OnClickListener mSelectTimeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TimePickerDialog mTimePicker = new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {}
            }, 8, 0, true);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        }
    };

    private void fillSettingsWindow(NotificationSetting setting)
    {
        String[] phoneNumbers = setting.phoneNumber.split(";");
        String[] contactsNames = setting.contactName.split(";");
        for (int i=1; i<phoneNumbers.length; i++)
            mAddContactClickListener.onClick(null);
        for (int i=0; i<phoneNumbers.length; i++) {
            ContactRow row = contactRows.get(i);
            ((TextView) row.view.findViewById(R.id.editPhone)).setText(phoneNumbers[i]);
            row.contact = contactsNames[i];
            contactRows.set(i, row);
        }

        ((TextView)findViewById(R.id.editTitle)).setText(setting.title);
        ((TextView)findViewById(R.id.editMessage)).setText(setting.message);
        ((Spinner)findViewById(R.id.cboClickAction)).setSelection(setting.clickAction);
        widgetClickActionSelection = setting.clickAction;

        updateTitleHint();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getIntent().getExtras().getBoolean("loadExisting", false)) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.widget_delete_action, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete)
            mOnRemoveClickListener(this.getApplicationContext());
        else
            return super.onOptionsItemSelected(item);
        return true;
    }

    public void mOnRemoveClickListener(final Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setIcon(android.R.drawable.ic_dialog_alert);
        alert.setMessage("Delete notification?");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SettingsFactory.delete(NotificationSetting.class, context, mAppWidgetId);
                Intent settingsUpdateIntent = new Intent();
                settingsUpdateIntent.setAction(AppSettingsActivity.refreshAction);
                settingsUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                sendBroadcast(settingsUpdateIntent);
                finish();
                }
        });
        alert.setNegativeButton("Cancel", null);
        alert.show();
    }


    public void mOnAddClickListener(Context context) {
        //Read all settings
        NotificationSetting setting = new NotificationSetting();
        setting.phoneNumber = "";
        setting.contactName = "";
        for (ContactRow contactRow : contactRows)
        {
            setting.phoneNumber += ";" + ((TextView)contactRow.view.findViewById(R.id.editPhone)).getText().toString();
            setting.contactName += ";" + contactRow.contact;
        }
        setting.phoneNumber = setting.phoneNumber.substring(1);
        setting.contactName = setting.contactName.substring(1);


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

        SettingsFactory.save(context, mAppWidgetId, setting);

        if (getIntent().getExtras().getBoolean("loadExisting", false)) {
            Intent settingsUpdateIntent = new Intent();
            settingsUpdateIntent.setAction(AppSettingsActivity.refreshAction);
            settingsUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            sendBroadcast(settingsUpdateIntent);
        }
        finish();
    }
}
