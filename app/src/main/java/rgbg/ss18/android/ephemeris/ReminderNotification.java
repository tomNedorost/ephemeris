package rgbg.ss18.android.ephemeris;

/**
 * Based on Source : https://github.com/jaisonfdo/RemindMe
 */

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;



public class ReminderNotification {

    private static final int NOTIFICATION_ID = 0;
    private static final String CHANNEL_ID = "REMINDER";
    public static final int DAILY_REMINDER_CODE=100;



    public static void setUpReminder(Context context, Class cls, String time){

        Calendar calendar = Calendar.getInstance();

        Calendar alarmCalendar = Calendar.getInstance();
        alarmCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0,1)));
        alarmCalendar.set(Calendar.MINUTE, Integer.parseInt(time.substring(3,4)));
        alarmCalendar.set(Calendar.SECOND, 0);

        // cancel already scheduled reminders
        cancelReminder(context,cls);

        if(alarmCalendar.before(calendar))
            alarmCalendar.add(Calendar.DATE,1);

        // Sets a new receiver
        ComponentName cReceiver = new ComponentName(context, cls);
        PackageManager packageManager = context.getPackageManager();

        packageManager.setComponentEnabledSetting(cReceiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        // sets the Alarm
        Intent reminderAlarmIntent = new Intent(context, cls);
        PendingIntent reminderAlarmPendingIntent = PendingIntent.getBroadcast(context, DAILY_REMINDER_CODE, reminderAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager reminderAlarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        reminderAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, reminderAlarmPendingIntent);

    }

    // cancels the reminder
    public static void cancelReminder(Context context, Class cls){

        // Disable a receiver

        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        // cancels Alarm
        Intent reminderCancelAlarmIntent = new Intent(context, cls);
        PendingIntent reminderAlarmPendingIntent = PendingIntent.getBroadcast(context, DAILY_REMINDER_CODE, reminderCancelAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager reminderAlarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        reminderAlarmManager.cancel(reminderAlarmPendingIntent);
       reminderAlarmPendingIntent.cancel();
    }


    // Sets up the reminder notification
    public static void setUpReminderNotification (Context context, Class cls){

        // Sets up a Pending intent for the notification that lead to the create Activity.
        Intent notificationIntent = new Intent(context, CreateActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity
                (context, NOTIFICATION_ID, notificationIntent, 0);

        // Sets task stack

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        // Sets up the Notification icon , text, ect.
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_reminder)
                .setContentTitle(context.getString(R.string.reminder_notification_title))
                .setContentText(context.getString(R.string.reminder_notification_text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true);

        // Sets Vibrate if selected
        SharedPreferences reminderPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        if(reminderPrefs.getBoolean(SettingsActivity.KEY_PREF_REMINDER_VIBRATE, true)){
            mBuilder.setVibrate(new long []{0, 1000, 1000, 1000, 0});
        }

        // Shows the Notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    /* ToDo: Wird benötigt . Funktioniert derzeit nicht.

    // Notification Channel for Android 8.0+
    public static void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.reminder_notification_channel_name);
            String description = getString(R.string.reminder_notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
        */
}

