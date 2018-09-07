package rgbg.ss18.android.ephemeris;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.List;

import rgbg.ss18.android.ephemeris.adapter.DiaEntryOverviewListAdapter;
import rgbg.ss18.android.ephemeris.database.DiaEntryDatabase;
import rgbg.ss18.android.ephemeris.model.DiaEntry;

public class SearchActivity extends AppCompatActivity {

    private EditText searchText;
    private ImageButton searchButton;
    private List<DiaEntry> diaEntries;
    private ListView searchListView;
    private DiaEntryOverviewListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //setting up toolbar
        Toolbar searchToolbar = findViewById(R.id.search_toolbar);
        setSupportActionBar(searchToolbar);
        setUpReturnArrow();

        // inits all needed things
        initUi();
        initBtn();
        initListView();
    }

    // siehe MainActivity
    private void initListView() {
        searchListView = findViewById(R.id.search_list_view);
        diaEntries = DiaEntryDatabase.getInstance(this).getAllDiaEntries();
        adapter = new DiaEntryOverviewListAdapter(this, diaEntries);
        searchListView.setAdapter(adapter);

        // ToDo: zur edit activity weiterleiten, dies funtkioniert ähnlich wie die createEntry Methode, nur mit der updateEntry Methode
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

    private void initBtn() {
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchString = searchText.getText().toString();
                DiaEntryDatabase db = DiaEntryDatabase.getInstance(SearchActivity.this);

                diaEntries.clear();
                diaEntries.addAll(db.searchFor(searchString));
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

    //inflates toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.search_menu, menu);

        return true;
    }


    public void setUpReturnArrow (){
        ActionBar searchActionBar = getSupportActionBar();

        if(searchActionBar != null){
            searchActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    // Handles Toolbar Selection.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Switch to SearchActivity
            case R.id.map:

                Intent mapIntent = new Intent(this, MapActivity.class);
                startActivity(mapIntent);

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
