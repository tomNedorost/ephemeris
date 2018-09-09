package rgbg.ss18.android.ephemeris;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.List;

import rgbg.ss18.android.ephemeris.adapter.EntryOverviewListAdapter;
import rgbg.ss18.android.ephemeris.database.DiaEntryDatabase;
import rgbg.ss18.android.ephemeris.model.DiaEntry;

public class SearchActivity extends AppCompatActivity {

    private EditText searchText;
    private ImageButton searchButton;
    private List<DiaEntry> diaEntries;
    private ListView searchListView;
    private EntryOverviewListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initAppBar();
        initUi();
        initBtn();
        initListView();

    }

    // same procedure in MainActivity
    private void initListView() {
        searchListView = findViewById(R.id.search_list_view);
        diaEntries = DiaEntryDatabase.getInstance(this).getAllDiaEntries();
        adapter = new EntryOverviewListAdapter(this, diaEntries);
        searchListView.setAdapter(adapter);

        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object element = parent.getAdapter().getItem(position);

                if (element instanceof DiaEntry) {
                    DiaEntry diaEntry = (DiaEntry) element;

                    Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
                    intent.putExtra(DetailActivity.TODO_KEY, diaEntry);

                    startActivity(intent);
                }
            }
        });
    }

    // sets onCicklistener for searchButton
    private void initBtn() {
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchString = searchText.getText().toString();
                DiaEntryDatabase db = DiaEntryDatabase.getInstance(SearchActivity.this);
                List<DiaEntry> dbDiaEntries = db.searchFor(searchString);
                diaEntries.clear();
                diaEntries.addAll(dbDiaEntries);

                adapter.notifyDataSetChanged();
                searchText.getText().clear();
                db.close();
            }
        });
    }

    private void initUi() {
        searchText = findViewById(R.id.search_input);
        searchButton = findViewById(R.id.search_btn);
        searchListView = findViewById(R.id.search_list_view);
    }


    // Sets up all members of the AppBar
    public void initAppBar(){
        //setting up toolbar
        Toolbar searchToolbar = findViewById(R.id.search_toolbar);
        setSupportActionBar(searchToolbar);

        setUpReturnArrow();
    }

    public void setUpReturnArrow (){
        ActionBar searchActionBar = getSupportActionBar();

        if(searchActionBar != null){
            searchActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    // Inflates toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.search_menu, menu);

        return true;
    }



    // Handles Toolbar Selection.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

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
