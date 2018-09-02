package rgbg.ss18.android.ephemeris;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //setting up toolbar
        Toolbar searchToolbar = findViewById(R.id.search_toolbar);
        setSupportActionBar(searchToolbar);
        setUpReturnArrow();
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
