package rgbg.ss18.android.ephemeris;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class NotificationReceiver extends BroadcastReceiver{

    @Override
    public void onReceive (Context context, Intent intent){
        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {

                SharedPreferences reminderSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                ReminderNotification.setUpReminder(context, NotificationReceiver.class, reminderSharedPreferences.getString(SettingsActivity.KEY_PREF_REMINDER_TIME, "12:00"));
                return;
            }
        }
        ReminderNotification.setUpReminderNotification(context, MainActivity.class);
    }
}

