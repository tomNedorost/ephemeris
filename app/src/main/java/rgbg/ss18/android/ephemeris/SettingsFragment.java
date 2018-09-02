package rgbg.ss18.android.ephemeris;


import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;



public class SettingsFragment extends PreferenceFragmentCompat {

    // Used to add to SettingsFragment to display preferences
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey){

        setPreferencesFromResource(R.xml.preferences, rootKey);

    }

}
