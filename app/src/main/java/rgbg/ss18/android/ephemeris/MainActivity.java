package rgbg.ss18.android.ephemeris;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Calendar;
import java.util.List;


import rgbg.ss18.android.ephemeris.adapter.EntryOverviewListAdapter;
import rgbg.ss18.android.ephemeris.database.DiaEntryDatabase;
import rgbg.ss18.android.ephemeris.model.DiaEntry;

public class MainActivity extends AppCompatActivity {

    public static final String CHANNEL_ID = "ephNotification";
    private FloatingActionButton createBtn;
    private ListView diaListView;
    private List<DiaEntry> dataSource;
    private EntryOverviewListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();
        initListView();
        initButtons();
        initAppBar();
    }

    // see: https://developer.android.com/guide/topics/ui/notifiers/notifications
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Sets up the toolbar
    private void initAppBar(){
        Toolbar mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
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

            case R.id.clearAllBtn:

                DiaEntryDatabase db = DiaEntryDatabase.getInstance(MainActivity.this);
                db.deleteAllDiaEntries();
                db.close();

                refreshListView();
                return true;

            default:

                return super.onOptionsItemSelected(item);

        }
    }

    // refreshes Listview on resume
    @Override
    protected void onResume() {
        super.onResume();
        refreshListView();
    }

    // inits create Button
    private void initButtons() {
        createBtn = findViewById(R.id.createBtn);

        setOnClickListener();
    }

    // sets onClickListener for create Button
    private void setOnClickListener() {
        if (createBtn != null) {
            createBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, CreateActivity.class);
                    startActivity(intent);

                    refreshListView();
                }
            });
        }
    }

    // inits Listview and connects it to the adapter
    private void initListView() {
        this.diaListView = findViewById(R.id.diaListView);
        this.dataSource = DiaEntryDatabase.getInstance(this).getAllDiaEntries();
        this.adapter = new EntryOverviewListAdapter(this, dataSource);
        diaListView.setAdapter(adapter);

        this.diaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object element = parent.getAdapter().getItem(position);

                if (element instanceof DiaEntry) {
                    DiaEntry diaEntry = (DiaEntry) element;

                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    intent.putExtra(DetailActivity.TODO_KEY, diaEntry);

                    startActivity(intent);
                }
            }
        });
    }

    // refreshes ListView by clearing it and get all DiaEntries from DB
    public void refreshListView() {
        dataSource.clear();
        dataSource.addAll(DiaEntryDatabase.getInstance(this).getAllDiaEntries());
        adapter.notifyDataSetChanged();
    }
}