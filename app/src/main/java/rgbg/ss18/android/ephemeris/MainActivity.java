package rgbg.ss18.android.ephemeris;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;
import java.util.Random;

import rgbg.ss18.android.ephemeris.adapter.DiaEntryOverviewListAdapter;
import rgbg.ss18.android.ephemeris.database.DiaEntryDatabase;
import rgbg.ss18.android.ephemeris.model.DiaEntry;

public class MainActivity extends AppCompatActivity {

    private Button createBtn, clearAllBtn, clearFirstBtn, updateFirstBtn;
    private ListView diaListView;
    private List<DiaEntry> dataSource;
    private DiaEntryOverviewListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Sets up the toolbar.
        Toolbar mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        initListView();
        initButtons();
        setDefaultPreferences();
    }

    // Inflates toolbar menu.
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    // Handles Toolbar Selection.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Switch to SearchActivity
            case R.id.search:

                Intent searchIntent = new Intent(this, SearchActivity.class);
                startActivity(searchIntent);

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

//    Sets the default preferences for Settings
    private void setDefaultPreferences (){
        PreferenceManager.setDefaultValues(this,R.xml.preferences, false);
    }


    // refresht die ListView immer wenn diese Activity angezeigt wird
    @Override
    protected void onResume() {
        super.onResume();
        refreshListView();
    }

    private void initButtons() {
        createBtn = findViewById(R.id.createBtn);
        clearAllBtn = findViewById(R.id.clearAllBtn);
        clearFirstBtn = findViewById(R.id.clearFirstBtn);
        updateFirstBtn = findViewById(R.id.updateFirstBtn);

        setOnClickListener();
    }

    private void setOnClickListener() {
        if (createBtn != null) {
            createBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, DiaEntryCreateActivity.class);
                    startActivity(intent);

                    refreshListView();
                }
            });
        }

        if (clearAllBtn != null) {
            clearAllBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DiaEntryDatabase db = DiaEntryDatabase.getInstance(MainActivity.this);
                    db.deleteAllDiaEntries();

                    refreshListView();
                }
            });
        }

        if (clearFirstBtn != null) {
            clearFirstBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dataSource.size() > 0 ) {
                        DiaEntryDatabase db = DiaEntryDatabase.getInstance(MainActivity.this);
                        db.deleteDiaEntry(dataSource.get(0));
                        refreshListView();
                    }
                }
            });
        }

        if (updateFirstBtn != null) {
            updateFirstBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (dataSource.size() > 0) {
                        DiaEntryDatabase db = DiaEntryDatabase.getInstance(MainActivity.this);
                        Random r = new Random();
                        dataSource.get(0).setName(String.valueOf(r.nextInt()));
                        db.updateDiaEntry(dataSource.get(0));

                        refreshListView();
                    }
                }
            });
        }
    }

    private void initListView() {
        this.diaListView = findViewById(R.id.diaListView);
        this.dataSource = DiaEntryDatabase.getInstance(this).readAllDiaEntries();
        this.adapter = new DiaEntryOverviewListAdapter(this, dataSource);
        diaListView.setAdapter(adapter);

        // ToDo: zur edit activity weiterleiten, dies funtkioniert ähnlich wie die createEntry Methode, nur mit der updateEntry Methode
        this.diaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object element = parent.getAdapter().getItem(position);

                if (element instanceof DiaEntry) {
                    DiaEntry diaEntry = (DiaEntry) element;

                    Intent intent = new Intent(MainActivity.this, DiaEntryDetailActivity.class);
                    intent.putExtra(DiaEntryDetailActivity.TODO_KEY, diaEntry);

                    startActivity(intent);
                }
            }
        });
    }


    public void refreshListView() {
        dataSource.clear();
        dataSource.addAll(DiaEntryDatabase.getInstance(this).readAllDiaEntries());
        adapter.notifyDataSetChanged();
    }
}