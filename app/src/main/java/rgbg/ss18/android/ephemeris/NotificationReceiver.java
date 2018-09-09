package rgbg.ss18.android.ephemeris;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import static rgbg.ss18.android.ephemeris.MainActivity.CHANNEL_ID;

public class NotificationReceiver extends BroadcastReceiver{

    // creates notification and builds it
    @Override
    public void onReceive (Context context, Intent intent){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intentCreate = new Intent(context, CreateActivity.class);
        intentCreate.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 200, intentCreate, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_add_black_18dp)
                .setContentTitle(Resources.getSystem().getString(R.string.reminder_notification_title))
                .setContentText(Resources.getSystem().getString(R.string.reminder_notification_text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        if (notificationManager != null) {
            notificationManager.notify(200,mBuilder.build());
        }
    }
}

