package rgbg.ss18.android.ephemeris.TimePickerPreference;

/**
 * Help class for a TimePreference.
 * Based on this Source: https://stackoverflow.com/questions/5533078/timepicker-in-preferencescreen by Dalija Prasnikar
 */

import android.content.Context;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TimePicker;

import rgbg.ss18.android.ephemeris.R;

public class TimePreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat implements DialogPreference.TargetFragment
{
    TimePicker timePicker = null;

    @Override
    protected View onCreateDialogView(Context context)
    {
        timePicker = new TimePicker(new ContextThemeWrapper(context, R.style.TimePickerTheme));
        return (timePicker);
    }

    @Override
    protected void onBindDialogView(View v)
    {
        super.onBindDialogView(v);
        timePicker.setIs24HourView(true);
        TimePreference pref = (TimePreference) getPreference();
        timePicker.setCurrentHour(pref.hour);
        timePicker.setCurrentMinute(pref.minute);
    }

    @Override
    public void onDialogClosed(boolean positiveResult)
    {
        if (positiveResult)
        {
            TimePreference pref = (TimePreference) getPreference();
            pref.hour = timePicker.getCurrentHour();
            pref.minute = timePicker.getCurrentMinute();

            String value = TimePreference.timeToString(pref.hour, pref.minute);
            pref.setSummary(value);
            if (pref.callChangeListener(value)) pref.persistStringValue(value);
        }

    }

    @Override
    public Preference findPreference(CharSequence charSequence)
    {
        return getPreference();
    }
}