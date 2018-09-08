package rgbg.ss18.android.ephemeris;



import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import rgbg.ss18.android.ephemeris.TimePickerPreference.TimePreference;
import rgbg.ss18.android.ephemeris.TimePickerPreference.TimePreferenceDialogFragmentCompat;


public class SettingsFragment extends PreferenceFragmentCompat {

    // Used to add to SettingsFragment to display preferences
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey){

        setPreferencesFromResource(R.xml.preferences, rootKey);

    }


    // Used for TimePreference. Source https://stackoverflow.com/questions/5533078/timepicker-in-preferencescreen by Dalija Prasnikar
    @Override
    public void onDisplayPreferenceDialog(Preference preference)
    {
        DialogFragment dialogFragment = null;
        if (preference instanceof TimePreference)
        {
            dialogFragment = new TimePreferenceDialogFragmentCompat();
            Bundle bundle = new Bundle(1);
            bundle.putString("key", preference.getKey());
            dialogFragment.setArguments(bundle);
        }

        if (dialogFragment != null)
        {
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(this.getFragmentManager(), "android.support.v7.preference.PreferenceFragment.DIALOG");
        }
        else
        {
            super.onDisplayPreferenceDialog(preference);
        }
    }

}
