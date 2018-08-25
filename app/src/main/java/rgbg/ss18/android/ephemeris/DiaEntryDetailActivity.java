package rgbg.ss18.android.ephemeris;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Calendar;

import rgbg.ss18.android.ephemeris.model.DiaEntry;

public class DiaEntryDetailActivity extends AppCompatActivity {

    // Key für das DiaEntry, das im Extra gespeichert ist
    public static final String TODO_KEY = "DIAENTRY";

    // Neue Instanz, diese wird in initDiaEntry() aus den Extras gezogen.
    private DiaEntry diaEntry;

    // Hier alle Variablen des activity layouts, diese werden in initLayout beschrieben
    private TextView title, timeStamp, entryText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dia_entry_detail);

        initDiaEntry();
        initLayout();
        Log.e("ID ", String.valueOf(diaEntry.getId()));
        Log.e("Name ", diaEntry.getName());
        Log.e("Mood ", String.valueOf(diaEntry.getMood()));
        Log.e("Description", diaEntry.getDescription());
    }

    private void initDiaEntry() {
        diaEntry = (DiaEntry) getIntent().getSerializableExtra(TODO_KEY);
    }

    // zuerst die oben erstellten Variablen beschreiben, dann den Text entsprechend anpassen
    // wenn sie nicht als Strings in der DB stehen (vgl. Model DiaEntry) entsprechend parsen
    // an die Inhalte gelangst du mit diaEntry.getWasDuWillst. Wenn dir ein getter fehlt, gib mir einfach Bescheid.
    private void initLayout() {
        title = findViewById(R.id.textView_title);
        timeStamp = findViewById(R.id.textView_timestamp);
        entryText = findViewById(R.id.textView_entryText);

        title.setText(diaEntry.getName());

        if (diaEntry.getDescription() != null) {
            entryText.setText(diaEntry.getDescription());
        }
        // wenn der Entry nen Datum eingestellt hat, dann diesen beschreiben.
        // Darstellung eventuell anpassen.
        if (diaEntry.getDate() != null) {
            timeStamp.setText(String.valueOf(diaEntry.getDate().getTime()));
        }

    }
}
