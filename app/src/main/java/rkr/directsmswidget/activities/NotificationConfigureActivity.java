package rkr.directsmswidget.activities;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import rkr.directsmswidget.AppSettingsActivity;
import rkr.directsmswidget.R;
import rkr.directsmswidget.settings.NotificationSetting;
import rkr.directsmswidget.settings.SettingsFactory;
import rkr.directsmswidget.utils.Helpers;

public class NotificationConfigureActivity extends HomeWidgetConfigureActivity {

    private int selectedTimeHour;
    private int selectedTimeMinute;

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

        findViewById(R.id.button_select_week_mon).setOnClickListener(mSelectWeekToggleClickListener);
        findViewById(R.id.button_select_week_tue).setOnClickListener(mSelectWeekToggleClickListener);
        findViewById(R.id.button_select_week_wed).setOnClickListener(mSelectWeekToggleClickListener);
        findViewById(R.id.button_select_week_thu).setOnClickListener(mSelectWeekToggleClickListener);
        findViewById(R.id.button_select_week_fri).setOnClickListener(mSelectWeekToggleClickListener);
        findViewById(R.id.button_select_week_sat).setOnClickListener(mSelectWeekToggleClickListener);
        findViewById(R.id.button_select_week_sun).setOnClickListener(mSelectWeekToggleClickListener);

        ((Spinner)findViewById(R.id.cboClickAction)).setOnItemSelectedListener(mOnSelectWidgetClickAction);
        contactRows.add(new ContactRow(findViewById(R.id.layout_contact_row_1)));

        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("loadExisting", false))
        {
            mAppWidgetId = Helpers.IntentToWidgetId(getIntent());
            NotificationSetting setting = SettingsFactory.load(NotificationSetting.class, this.getApplicationContext(), mAppWidgetId);
            fillSettingsWindow(setting);
        }
        else
        {
            mAppWidgetId = Helpers.getRandInt();
            updateTimeView(8, 0);
        }
    }

    private void updateTimeView(int hour, int minute)
    {
        selectedTimeMinute = minute;
        selectedTimeHour = hour;
        String text = String.format("%d:%02d", hour, minute);

        ((TextView)findViewById(R.id.button_select_time)).setText(text);
    }

    View.OnClickListener mSelectWeekToggleClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ToggleButton toggle = (ToggleButton)v;
            if (toggle.isChecked()) {
                toggle.setTextColor(0xff33b5e5);
                toggle.setTypeface(null, Typeface.BOLD);
                toggle.setPaintFlags(toggle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            }
            else {
                toggle.setTextColor(0xffffffff);
                toggle.setTypeface(null, Typeface.NORMAL);
                toggle.setPaintFlags(toggle.getPaintFlags() ^ Paint.UNDERLINE_TEXT_FLAG);
            }

        }
    };

    View.OnClickListener mSelectTimeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String currentTime = (String)((TextView)findViewById(R.id.button_select_time)).getText();

            TimePickerDialog mTimePicker = new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    updateTimeView(selectedHour, selectedMinute);
                }
            }, selectedTimeHour, selectedTimeMinute, true);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        }
    };

    private void fillSettingsWindow(NotificationSetting setting)
    {
        String[] phoneNumbers = setting.phoneNumbers();
        String[] contactsNames = setting.contactNames();
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

        updateTimeView(setting.hour, setting.minute);
        ((ToggleButton)findViewById(R.id.button_select_week_mon)).setChecked(setting.day1);
        ((ToggleButton)findViewById(R.id.button_select_week_tue)).setChecked(setting.day2);
        ((ToggleButton)findViewById(R.id.button_select_week_wed)).setChecked(setting.day3);
        ((ToggleButton)findViewById(R.id.button_select_week_thu)).setChecked(setting.day4);
        ((ToggleButton)findViewById(R.id.button_select_week_fri)).setChecked(setting.day5);
        ((ToggleButton)findViewById(R.id.button_select_week_sat)).setChecked(setting.day6);
        ((ToggleButton)findViewById(R.id.button_select_week_sun)).setChecked(setting.day7);

        mSelectWeekToggleClickListener.onClick(findViewById(R.id.button_select_week_mon));
        mSelectWeekToggleClickListener.onClick(findViewById(R.id.button_select_week_tue));
        mSelectWeekToggleClickListener.onClick(findViewById(R.id.button_select_week_wed));
        mSelectWeekToggleClickListener.onClick(findViewById(R.id.button_select_week_thu));
        mSelectWeekToggleClickListener.onClick(findViewById(R.id.button_select_week_fri));
        mSelectWeekToggleClickListener.onClick(findViewById(R.id.button_select_week_sat));
        mSelectWeekToggleClickListener.onClick(findViewById(R.id.button_select_week_sun));

        ((Switch)findViewById(R.id.switch_notification_enabled)).setChecked(setting.enabled);
        ((Switch)findViewById(R.id.switch_notification_sound_enabled)).setChecked(setting.notificationSound);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("loadExisting", false)) {
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
        setting.hour = selectedTimeHour;
        setting.minute = selectedTimeMinute;
        setting.day1 = ((ToggleButton)findViewById(R.id.button_select_week_mon)).isChecked();
        setting.day2 = ((ToggleButton)findViewById(R.id.button_select_week_tue)).isChecked();
        setting.day3 = ((ToggleButton)findViewById(R.id.button_select_week_wed)).isChecked();
        setting.day4 = ((ToggleButton)findViewById(R.id.button_select_week_thu)).isChecked();
        setting.day5 = ((ToggleButton)findViewById(R.id.button_select_week_fri)).isChecked();
        setting.day6 = ((ToggleButton)findViewById(R.id.button_select_week_sat)).isChecked();
        setting.day7 = ((ToggleButton)findViewById(R.id.button_select_week_sun)).isChecked();
        setting.enabled = ((Switch)findViewById(R.id.switch_notification_enabled)).isChecked();
        setting.notificationSound = ((Switch)findViewById(R.id.switch_notification_sound_enabled)).isChecked();

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

        if ((setting.day1 || setting.day2 || setting.day3 || setting.day4 || setting.day5 ||
            setting.day6 || setting.day7) == false)
        {
            Toast.makeText(context, "No days selected - notification will be shown only once", Toast.LENGTH_SHORT).show();
        }

        Intent settingsUpdateIntent = new Intent();
        settingsUpdateIntent.setAction(AppSettingsActivity.refreshAction);
        settingsUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        sendBroadcast(settingsUpdateIntent);
        finish();
    }
}
