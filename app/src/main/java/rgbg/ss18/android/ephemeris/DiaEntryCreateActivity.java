package rgbg.ss18.android.ephemeris;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import rgbg.ss18.android.ephemeris.database.DiaEntryDatabase;
import rgbg.ss18.android.ephemeris.model.DiaEntry;

public class DiaEntryCreateActivity extends AppCompatActivity {

    private EditText title, description;
    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dia_entry_create);

        initUi();
        initBtn();
    }

    private void initUi() {
        title = findViewById(R.id.createDiaEntryTitleEditText);
        description = findViewById(R.id.createDiaEntryDescEditText);
    }

    // Sobald der Eintrag erstellen Button aufgerufen wird
    private void initBtn() {
        // Button instanziieren
        save = findViewById(R.id.createDiaEntryBtn);

        // onClickListener setzen
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // neuen DiaEntry erstellen, den brauchen wir, da die createEntry Funktion ein DiaEntry Object verlangt. Wenn du noch weitere Konstruktoren brauchst, gib Bescheid
                DiaEntry newDiaEntry =  new DiaEntry(title.getText().toString(), description.getText().toString());

                // verbindung zur DB aufbauen
                DiaEntryDatabase db = DiaEntryDatabase.getInstance(DiaEntryCreateActivity.this);

                // diaEntry hinzufügen zur db
                db.createEntry(newDiaEntry);

                // verbindung zur DB schließen
                db.close();

                // Activity schließen, eventueller Toast für erfolgreiche speicherung
                finish();
            }
        });
    }
}
