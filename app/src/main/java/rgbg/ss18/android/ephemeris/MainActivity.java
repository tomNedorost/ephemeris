package rgbg.ss18.android.ephemeris;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;


import rgbg.ss18.android.ephemeris.adapter.DiaEntryOverviewListAdapter;
import rgbg.ss18.android.ephemeris.database.DiaEntryDatabase;
import rgbg.ss18.android.ephemeris.model.DiaEntry;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton createBtn;
    private ListView diaListView;
    private List<DiaEntry> dataSource;
    private DiaEntryOverviewListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // needed for API 26+. Should be called first
       // ReminderNotification.createNotificationChannel();
        initListView();
        initButtons();
        initAppBar();
        initSettings();
    }

    // Sets up the toolbar
    private void initAppBar(){
        Toolbar mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
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

    // Sets the default preferences for Settings
  /* private void setDefaultPreferences (){
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }
*/
    // init Settings
    private void initSettings () {
       // setDefaultPreferences();
        SharedPreferences reminderSharedPref =  PreferenceManager.getDefaultSharedPreferences(this);

        // Sets Notification according to Settings
        if(reminderSharedPref.getBoolean(SettingsActivity.KEY_PREF_REMINDER, true))
            ReminderNotification.setUpReminder(MainActivity.this, NotificationReceiver.class, reminderSharedPref.getString(SettingsActivity.KEY_PREF_REMINDER_TIME,"12:00"));

    }

    // refresht die ListView immer wenn diese Activity angezeigt wird
    @Override
    protected void onResume() {
        super.onResume();
        refreshListView();
    }

    private void initButtons() {
        createBtn = findViewById(R.id.createBtn);

        setOnClickListener();
    }

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

    private void initListView() {
        this.diaListView = findViewById(R.id.diaListView);
        this.dataSource = DiaEntryDatabase.getInstance(this).getAllDiaEntries();
        this.adapter = new DiaEntryOverviewListAdapter(this, dataSource);
        diaListView.setAdapter(adapter);

        // ToDo: zur edit activity weiterleiten, dies funtkioniert ähnlich wie die createEntry Methode, nur mit der updateEntry Methode
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


    public void refreshListView() {
        dataSource.clear();
        dataSource.addAll(DiaEntryDatabase.getInstance(this).getAllDiaEntries());
        adapter.notifyDataSetChanged();
    }
}