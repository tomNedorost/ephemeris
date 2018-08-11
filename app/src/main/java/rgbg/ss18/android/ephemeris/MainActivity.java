package rgbg.ss18.android.ephemeris;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.Calendar;
import java.util.Random;

import rgbg.ss18.android.ephemeris.adapter.DiaEntryOverviewListAdapter;
import rgbg.ss18.android.ephemeris.database.DiaEntryDatabase;
import rgbg.ss18.android.ephemeris.model.DiaEntry;

public class MainActivity extends AppCompatActivity {

    private Button createBtn, clearAllBtn, clearFirstBtn,updateFirstBtn;
    private ListView diaListView;
    private DiaEntryOverviewListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initListView();
        initButtons();
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
                    DiaEntryDatabase db = DiaEntryDatabase.getInstance(MainActivity.this);

                    // Testing
                    db.createEntry(new DiaEntry("test"));
                    db.createEntry(new DiaEntry("test2", Calendar.getInstance()));

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
                    DiaEntryDatabase db = DiaEntryDatabase.getInstance(MainActivity.this);
                    DiaEntry first = db.getFirstDiaEntry();

                    if (first != null) {
                        db.deleteDiaEntry(first);

                        refreshListView();
                    }
                }
            });
        }

        if (updateFirstBtn != null) {
            updateFirstBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DiaEntryDatabase db = DiaEntryDatabase.getInstance(MainActivity.this);
                    DiaEntry first = db.getFirstDiaEntry();
                    if (first != null) {
                        Random r = new Random();
                        first.setName(String.valueOf(r.nextInt()));
                        db.updateDiaEntry(first);

                        refreshListView();
                    }
                }
            });
        }
    }

    private void initListView() {
        this.diaListView = findViewById(R.id.diaListView);
        this.adapter = new DiaEntryOverviewListAdapter(this, DiaEntryDatabase.getInstance(this).getAllDiaEntriesAsCursor());
        diaListView.setAdapter(adapter);

    }


    public void refreshListView() {
        adapter.changeCursor(DiaEntryDatabase.getInstance(this).getAllDiaEntriesAsCursor());
    }
}