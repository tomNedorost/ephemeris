package rgbg.ss18.android.ephemeris;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {
    public final static int NOTIFICATION_REQUEST_CODE = 200;
    public final static String SWITCH_IS_ENABLED = "SwitchEnabled";
    public final static String SHARED_PREFERENCES = "rgbg.ss18.android.ephemeris";

    Switch notificationEnabledSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initUi();
        loadPreferences();
    }

    // checks if switch Button is clicked, or not, by loading sharedpreferences
    private void loadPreferences() {
        SharedPreferences sharedPrefs = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        notificationEnabledSwitch.setChecked(sharedPrefs.getBoolean(SWITCH_IS_ENABLED, false));
    }

    // init Switchbutton and creates pending intent when switch is on, or cancels pending intent if switch is off. Also saves the state to sharedprefs
    // Because def Value of shared preferences is false, and this is a on change listener there will always be a pending Intent, that can be cancelled
    private void initUi() {

        notificationEnabledSwitch = findViewById(R.id.notificationEnabledSwitch);

        notificationEnabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
                PendingIntent pendingIntent;

                // switch on == true, switch off == false
                if (isChecked) {
                    SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE).edit();
                    editor.putBoolean(SWITCH_IS_ENABLED, true);
                    editor.commit();

                    Calendar calendar = Calendar.getInstance();

                    calendar.set(Calendar.HOUR_OF_DAY, 20);
                    calendar.set(Calendar.MINUTE, 0);

                    String notificationTime = new SimpleDateFormat("HH:mm").format(calendar.getTime());
                    Toast.makeText(getApplicationContext(), "Du wirst täglich um " + notificationTime + " erinnert.", Toast.LENGTH_SHORT).show();

                    pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), NOTIFICATION_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                } else {
                    PendingIntent.getBroadcast(getApplicationContext(), NOTIFICATION_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT).cancel();
                    Toast.makeText(getApplicationContext(), "Du wirst nicht mehr erinnert.", Toast.LENGTH_SHORT).show();

                    SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE).edit();
                    editor.putBoolean(SWITCH_IS_ENABLED, false);
                    editor.commit();
                }
            }
        });
    }
}
