package rgbg.ss18.android.ephemeris;

import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_PREF_REMINDER = "reminder_notification";
    public static final String KEY_PREF_REMINDER_VIBRATE = "reminder_notification_vibrate";
    public static final String KEY_PREF_REMINDER_TIME = "reminder_notification_time";
    public SharedPreferences reminderSharedPref;
    public SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sets SettingsFragment as main content for SettingsActivity
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();

        // Sets SharedPrefs
        reminderSharedPref =  PreferenceManager.getDefaultSharedPreferences(this);

        // Sets SharedPrefsListener ToDo: ohne doppelte implemtierung von onSharedPreferenceChanged lösen ; ?
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                switch(key) {

                    // Activate or Deactivate the Notification with/without vibrate and chosen time
                    case KEY_PREF_REMINDER  :
                    case KEY_PREF_REMINDER_VIBRATE:
                    case KEY_PREF_REMINDER_TIME:
                        if (reminderSharedPref.getBoolean(KEY_PREF_REMINDER, true)) {
                            ReminderNotification.setUpReminder(SettingsActivity.this, NotificationReceiver.class, reminderSharedPref.getString(KEY_PREF_REMINDER_TIME,"12:00"));
                        } else ReminderNotification.cancelReminder(SettingsActivity.this, NotificationReceiver.class );

                    default:
                 }
              }
          };
        reminderSharedPref.registerOnSharedPreferenceChangeListener(listener);
        }





    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch(key) {

            // Activate or Deactivate the Notification with/without vibrate and chosen time
            case KEY_PREF_REMINDER  :
            case KEY_PREF_REMINDER_VIBRATE:
            case KEY_PREF_REMINDER_TIME:
                if (reminderSharedPref.getBoolean(KEY_PREF_REMINDER, true)) {
                    ReminderNotification.setUpReminder(SettingsActivity.this, NotificationReceiver.class, reminderSharedPref.getString(KEY_PREF_REMINDER_TIME,"12:00"));
                } else ReminderNotification.cancelReminder(SettingsActivity.this, NotificationReceiver.class );

            default:
        }
    }


    @Override
    protected void onResume(){
        super.onResume();
        reminderSharedPref.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause(){
        super.onPause();
        reminderSharedPref.unregisterOnSharedPreferenceChangeListener(this);
    }


}