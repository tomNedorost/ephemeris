package rgbg.ss18.android.ephemeris;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.Menu;

import java.util.Calendar;

import rgbg.ss18.android.ephemeris.database.DiaEntryDatabase;
import rgbg.ss18.android.ephemeris.model.DiaEntry;

public class DetailActivity extends AppCompatActivity {

    // Key für das DiaEntry, das im Extra gespeichert ist
    public static final String TODO_KEY = "DIAENTRY";

    // Neue Instanz, diese wird in initDiaEntry() aus den Extras gezogen.
    private DiaEntry diaEntry;
    private DiaEntry dbDiaEntry;

    // Hier alle Variablen des activity layouts, diese werden in initLayout beschrieben
    private TextView title, timeStamp, entryText, location;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dia_entry_detail);

        initDiaEntry();
        initLayout();

    }

    private void initDiaEntry() {
        // erhält aus dem intent den diaentry, der nur aus einer ID und einem Namen besteht und sucht sich danach mittels der ID den passenden aus der DB.
        // Grund dafür: würden wir alle DiaEntries in der Liste anzeigen, und dort die Bilder auch mit reinladen, würde aufgrund der größe und menge die App abstürzen
        diaEntry = (DiaEntry) getIntent().getSerializableExtra(TODO_KEY);
        dbDiaEntry = DiaEntryDatabase.getInstance(this).getDiaEntry(diaEntry.getId());
    }

    // zuerst die oben erstellten Variablen beschreiben, dann den Text entsprechend anpassen
    // wenn sie nicht als Strings in der DB stehen (vgl. Model DiaEntry) entsprechend parsen
    // an die Inhalte gelangst du mit diaEntry.getWasDuWillst. Wenn dir ein getter fehlt, gib mir einfach Bescheid.
    private void initLayout() {
        title = findViewById(R.id.textView_title);
        timeStamp = findViewById(R.id.textView_timestamp);
        entryText = findViewById(R.id.textView_entryText);
        imageView = findViewById(R.id.imageView_entryImage);
        location = findViewById(R.id.textView_location);

        setUpAppBar();

        title.setText(dbDiaEntry.getName());

        // wenn der Entry keine Description hat, sollte auf Grund der Konstruktoren nicht möglich sein, aber sicher ist sicher :)
        if (dbDiaEntry.getDescription() != null) {
            Log.e("Description", dbDiaEntry.getDescription());
            entryText.setText(dbDiaEntry.getDescription());
        }
        // wenn der Entry nen Datum eingestellt hat, dann diesen beschreiben.
        // Darstellung eventuell anpassen.
        if (dbDiaEntry.getDate() != null) {
            timeStamp.setText(String.valueOf(dbDiaEntry.getDate().get(Calendar.YEAR)));
        }

        // lädt Bild, falls vorhanden in die imageview um, sonst platzhalterbild
        if (dbDiaEntry.getImage() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(dbDiaEntry.getImage(), 0, dbDiaEntry.getImage().length);
            imageView.setImageBitmap(bitmap);
        }

        // schreibt City, falls vorhanden in die location textview, sonst nichts
        // ToDo überlegen ob wir eine standard, oder fakestadt hinschreiben
        if (dbDiaEntry.getCity() != null) {
            location.setText(dbDiaEntry.getCity());
        }
    }

    // Setzt die AppBar auf mitsamt Toolbar.
    public void setUpAppBar (){
        // setzt die Toolbar auf
        Toolbar diaEntryDetailToolbar = findViewById(R.id.dia_entry_detail_toolbar);
        setSupportActionBar(diaEntryDetailToolbar);

        ActionBar searchActionBar = getSupportActionBar();

        if(searchActionBar != null){
            searchActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    // Inflates toolbar menu.
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.dia_entry_detail_menu, menu);

        return true;
    }

    // Handles toolbar selection.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Switch to CreateActivity
            case R.id.edit_entry:

                Intent editIntent = new Intent(this, CreateActivity.class);
//               ToDo: DiaEntry der bearbeitet werden soll an die CreateActivity übergeben.
                // editIntent.putExtra()

                startActivity(editIntent);

                return true;

            // Switch to SettingsActivity
            case R.id.settings:

                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);

                return true;

            default:

                return super.onOptionsItemSelected(item);

        }
    }
}
