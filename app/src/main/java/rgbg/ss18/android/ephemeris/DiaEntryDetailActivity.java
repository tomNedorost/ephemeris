package rgbg.ss18.android.ephemeris;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import rgbg.ss18.android.ephemeris.model.DiaEntry;

public class DiaEntryDetailActivity extends AppCompatActivity {

    public static final String TODO_KEY = "DIAENTRY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dia_entry_detail);

        DiaEntry diaEntry = (DiaEntry) getIntent().getSerializableExtra(TODO_KEY);

        Log.e("ID ", String.valueOf(diaEntry.getId()));
        Log.e("Name ", diaEntry.getName());
        Log.e("Mood ",

                String.valueOf(diaEntry.getMood()));
    }
}
