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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.provider.ContactsContract;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.colorpicker.ColorPickerDialog;
import com.android.colorpicker.ColorPickerSwatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeWidgetConfigureActivity extends Activity {

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private static int widgetClickActionSelection = 0;
    private static int mSelectedColorBackground = 0xff33b5e5;
    private static int mSelectedColorText = 0xffffffff;

    private List<ContactRow> contactRows = new ArrayList<ContactRow>();
    private static Random rand = new Random();

    final static int[] mColors = new int[] {
            0xff33b5e5, 0xffaa66cc, 0xff99cc00,
            0xffffbb33, 0xffff4444, 0xff0099cc,
            0xff9933cc, 0xff669900, 0xffff8800,
            0xffcc0000, 0xffeeeeee, 0xffffffff
    };

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
        findViewById(R.id.button_select_contact).setOnClickListener(mOnSelectContactClickListener);
        findViewById(R.id.button_select_color_text).setOnClickListener(mOnSelectWidgetTextColorClickListener);
        findViewById(R.id.button_select_color_background).setOnClickListener(mOnSelectWidgetBackgroundColorClickListener);
        findViewById(R.id.button_add_new_contact).setOnClickListener(mAddContactClickListener);
        findViewById(R.id.button_delete_contact).setOnClickListener(mDeleteContactClickListener);
        ((Spinner)findViewById(R.id.cboClickAction)).setOnItemSelectedListener(mOnSelectWidgetClickAction);
        contactRows.add(new ContactRow(findViewById(R.id.layout_contact_row_1)));

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
        ((TextView)findViewById(R.id.text_sample_widget)).setTextColor(setting.textColor);
        findViewById(R.id.text_sample_widget).setBackgroundColor(setting.backgroundColor);

        mSelectedColorBackground = setting.backgroundColor;
        mSelectedColorText = setting.textColor;
        widgetClickActionSelection = setting.clickAction;

        updateTitleHint();
    }

    OnClickListener mOnSelectWidgetBackgroundColorClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            ColorPickerDialog colorCalendar = ColorPickerDialog.newInstance(
                    R.string.color_picker_default_title,
                    mColors,
                    mSelectedColorBackground,
                    4,
                    ColorPickerDialog.SIZE_SMALL);

            colorCalendar.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener(){
                @Override
                public void onColorSelected(int color) {
                    mSelectedColorBackground=color;
                    findViewById(R.id.text_sample_widget).setBackgroundColor(mSelectedColorBackground);
                }

            });

            colorCalendar.show(getFragmentManager(), "cal");
        }
    };

    OnClickListener mOnSelectWidgetTextColorClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            ColorPickerDialog colorCalendar = ColorPickerDialog.newInstance(
                    R.string.color_picker_default_title,
                    mColors,
                    mSelectedColorText,
                    4,
                    ColorPickerDialog.SIZE_SMALL);

            colorCalendar.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener(){
                @Override
                public void onColorSelected(int color) {
                    mSelectedColorText = color;
                    ((TextView)findViewById(R.id.text_sample_widget)).setTextColor(mSelectedColorText);
                }

            });

            colorCalendar.show(getFragmentManager(), "cal");
        }
    };

    OnClickListener mAddContactClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View addView = vi.inflate(R.layout.home_widget_configure_contact_row, null);

            addView.findViewById(R.id.button_select_contact).setOnClickListener(mOnSelectContactClickListener);
            addView.findViewById(R.id.button_delete_contact).setOnClickListener(mDeleteContactClickListener);
            addView.setId(Math.abs(rand.nextInt()));

            contactRows.add(new ContactRow(addView));

            View insertPoint = findViewById(R.id.layout_contacts_list);
            ((ViewGroup) insertPoint).addView(addView);
        }
    };

    OnClickListener mDeleteContactClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (contactRows.size() < 2)
                return;

            LinearLayout contactsList = (LinearLayout) v.getParent().getParent();
            int viewId = ((View) v.getParent()).getId();
            for (ContactRow contactRow : contactRows)
                if (contactRow.view.getId() == viewId) {
                    contactsList.removeView(contactRow.view);
                    contactRows.remove(contactRow);
                    updateTitleHint();
                    return;
                }
            Log.e("rkr.directsmswidget.deletecontactlistener", "View with id " + viewId + " not found");
        }
    };

    OnClickListener mOnSelectContactClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent pickContactIntent = new Intent( Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI );
            pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            View contactRow = (View)v.getParent();

            startActivityForResult(pickContactIntent, contactRow.getId());
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

    private void mOnAddClickListener(Context context) {
        //Read all settings
        WidgetSetting setting = new WidgetSetting();
        setting.phoneNumber = "";
        setting.contactName = "";
        for (ContactRow contactRow : contactRows)
        {
            setting.phoneNumber += ";" + ((TextView)contactRow.view.findViewById(R.id.editPhone)).getText().toString();
            setting.contactName += ";" + contactRow.contact;
        }
        setting.phoneNumber = setting.phoneNumber.substring(1);
        setting.contactName = setting.contactName.substring(1);

        //setting.phoneNumber = ((TextView)findViewById(R.id.editPhone)).getText().toString();
        //CharSequence contactName = ((TextView)findViewById(R.id.editTitle)).getHint();
        //setting.contactName = contactName == null ? "" : contactName.toString();
        setting.title = ((TextView)findViewById(R.id.editTitle)).getText().toString();
        setting.message = ((TextView)findViewById(R.id.editMessage)).getText().toString();
        setting.backgroundColor = mSelectedColorBackground;
        setting.textColor = mSelectedColorText;
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
        if(resultCode == RESULT_OK)
        {
            Uri contactURI = data.getData();

            Cursor cursor = getContentResolver().query(contactURI, null, null, null, null);
            Boolean numbersExist = cursor.moveToFirst();
            if (!numbersExist)
                return;
            String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String displayName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            //((TextView)findViewById(R.id.editPhone)).setText(phoneNumber);
            View contactView = findViewById(requestCode);
            ((TextView)contactView.findViewById(R.id.editPhone)).setText(phoneNumber);
            for (ContactRow contactRow : contactRows)
                if (contactRow.view.getId() == requestCode)
                    contactRow.contact = displayName;
            //((TextView)findViewById(R.id.editTitle)).setHint(displayName);
            updateTitleHint();
        }
    }

    private void updateTitleHint()
    {
        TextView titleText = ((TextView)findViewById(R.id.editTitle));
        int added = 0;
        titleText.setHint("");
        for (ContactRow contactRow : contactRows)
            if (contactRow.contact.trim().length() > 0)
            {
                if (added > 0) {
                    titleText.setHint(titleText.getHint().toString() + "; ...");
                    return;
                }

                titleText.setHint(contactRow.contact);
                added++;
            }
    }
}

class ContactRow
{
    public ContactRow(){};

    public ContactRow(View view){
        this.view = view;
    }

    public ContactRow(View view, String contact){
        this.view = view;
        this.contact = contact;
    }

    public View view;
    public String contact = "";
}



