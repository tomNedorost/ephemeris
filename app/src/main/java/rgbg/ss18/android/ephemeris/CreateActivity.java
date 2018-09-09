package rgbg.ss18.android.ephemeris;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import rgbg.ss18.android.ephemeris.adapter.MoodArrayAdapter;
import rgbg.ss18.android.ephemeris.database.DiaEntryDatabase;
import rgbg.ss18.android.ephemeris.model.DiaEntry;

import static rgbg.ss18.android.ephemeris.DetailActivity.TODO_KEY;


public class CreateActivity extends AppCompatActivity {

    // ToDo: Abfangen, wenn Standord nicht aktiv ist (aktuell App absturz)
    private static final int REQUEST_CODE_GALLERY = 999;
    private static final int REQUEST_CODE_LOCATION = 1000;

    private DiaEntry diaEntry, dbDiaEntry;
    private EditText title, description, city;
    private ImageButton selectImage, findLocation, selectMood;
    private ImageView imageView;
    private int selectedMood = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        init();
        initBtn();
        setUpAppBar();
        // wenn der Eintrag bearbeitet werden soll
        if (getIntent().getSerializableExtra(TODO_KEY) != null) {
            initWithEntry();
        }

    }

    // inits layout mit DiaEntry
    private void initWithEntry() {
        diaEntry = (DiaEntry) getIntent().getSerializableExtra(TODO_KEY);
        dbDiaEntry = DiaEntryDatabase.getInstance(this).getDiaEntry(diaEntry.getId());

        DiaEntryDatabase.getInstance(this).deleteDiaEntry(diaEntry);

        // checks every field, if filled and fills it
        // title
        if (dbDiaEntry.getName() != null) {
            title.setText(dbDiaEntry.getName());
        }

        // description
        if (dbDiaEntry.getDescription() != null) {
            description.setText(dbDiaEntry.getDescription());
        }

        // image
        if (dbDiaEntry.getImage() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(dbDiaEntry.getImage(), 0, dbDiaEntry.getImage().length);
            imageView.setImageBitmap(bitmap);
        }

        // city
        if (dbDiaEntry.getCity() != null) {
            city.setText(dbDiaEntry.getCity());
        }

        switch (dbDiaEntry.getMood()) {

            // very disappointed
            case 1:
                selectMood.setImageResource(R.drawable.ic_sentiment_1_very_dissatisfied_black_24dp);
                selectedMood = 1;
                break;

            // disappointed
            case 2:
                selectMood.setImageResource(R.drawable.ic_sentiment_2_dissatisfied_black_24dp);
                selectedMood = 2;
                break;

            // neutral
            case 3:
                selectMood.setImageResource(R.drawable.ic_sentiment_3_neutral_black_24dp);
                selectedMood = 3;
                break;

            // satisfied
            case 4:
                selectMood.setImageResource(R.drawable.ic_sentiment_4_satisfied_black_24dp);
                selectedMood = 4;
                break;

            // very satisfied
            case 5:
                selectMood.setImageResource(R.drawable.ic_sentiment_5_very_satisfied_black_24dp);
                selectedMood = 5;
                break;
        }
    }

    private void init() {
        title = findViewById(R.id.create_editText_title);
        description = findViewById(R.id.create_editText_entryDesc);
        imageView = findViewById(R.id.create_imageView_mainImage);
        city = findViewById(R.id.location_input);
    }

    // Sobald der Eintrag erstellen Button aufgerufen wird
    private void initBtn() {
        // Buttons instanziieren
        selectImage = findViewById(R.id.create_imageButton_selectImage);
        selectMood = findViewById(R.id.imageButton_moodSelect);
        findLocation = findViewById(R.id.location_find_btn);


        // ToDo: App stürzt ab wenn man kein Bild auswählt
        // onClickListener für Image auswählen setzen
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(5, 2)
                        .setRequestedSize(imageView.getWidth(), imageView.getHeight(), CropImageView.RequestSizeOptions.RESIZE_EXACT)
                        .start(CreateActivity.this);
            }
        });

        // onClickListener für Mood auswählen
        selectMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoodSelectDialog(v);
            }
        });

        findLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(CreateActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
            }
        });
    }


    // Create Entry Method for Toolbar
    public void createEntry(){
        // ToDo: wird das noch benötigit? Wenn ja wie für toolbar items umsetzten?
        // verhindert, dass man durch schnelle drücken des Buttons mehrere Einträge erstellt.
        //R.id.create_menu_done.setClickable(false);

        // neuen DiaEntry erstellen, den brauchen wir, da die createEntry Funktion ein DiaEntry Object verlangt.
        final DiaEntry newDiaEntry = new DiaEntry(title.getText().toString(), description.getText().toString());

        newDiaEntry.setDate(Calendar.getInstance());
        newDiaEntry.setMood(selectedMood);
        // fügt das bild zum DiaEntry als byte[] hinzu.
        if (imageView.getDrawable() != null) {
            try {
                byte[] image = imageViewToByte(imageView);
                newDiaEntry.setImage(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (city.getText().toString() != null) {
            newDiaEntry.setCity(city.getText().toString());
        }


        // Verbindung zur DB aufbauen
        DiaEntryDatabase db = DiaEntryDatabase.getInstance(CreateActivity.this);

        // diaEntry hinzufügen zur db
        db.createEntry(newDiaEntry);

        // verbindung zur DB schließen WICHTIG muss immer gemacht werden, wenn wir die db verwenden, da es sonst zu komplikationen kommt
        db.close();

        // Activity schließen,  Toast für erfolgreiche speicherung
        Toast.makeText(this, R.string.create_entrySaved, Toast.LENGTH_SHORT);
        finish();
    }


    // ToDo: Verändert nicht den imageButton nach Auswahl.
    public void showMoodSelectDialog(View view){
        final String [] moodNames = new String[] {"Sehr Unzufrieden", "Unzufrieden", "Neutral", "Zufrieden", "Sehr Zufrieden" };
        final Integer [] moodDrawables =  new Integer[] {R.drawable.ic_sentiment_1_very_dissatisfied_black_24dp, R.drawable.ic_sentiment_2_dissatisfied_black_24dp, R.drawable.ic_sentiment_3_neutral_black_24dp, R.drawable.ic_sentiment_4_satisfied_black_24dp, R.drawable.ic_sentiment_5_very_satisfied_black_24dp};
        ListAdapter moodAdapter = new MoodArrayAdapter(this, moodNames, moodDrawables );

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle(R.string.create_textView_moodSelect);
        mBuilder.setAdapter(moodAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int moodNr) {
               Toast.makeText(CreateActivity.this, "Stimmung ausgewählt: " + moodNames[moodNr], Toast.LENGTH_SHORT).show();
                switch (moodNr) {

                        // very disappointed
                    case 0:
                        selectMood.setImageResource(R.drawable.ic_sentiment_1_very_dissatisfied_black_24dp);
                        selectedMood = 1;
                        break;

                        // disappointed
                    case 1:
                        selectMood.setImageResource(R.drawable.ic_sentiment_2_dissatisfied_black_24dp);
                        selectedMood = 2;
                        break;

                        // neutral
                    case 2:
                        selectMood.setImageResource(R.drawable.ic_sentiment_3_neutral_black_24dp);
                        selectedMood = 3;
                        break;

                        // satisfied
                    case 3:
                        selectMood.setImageResource(R.drawable.ic_sentiment_4_satisfied_black_24dp);
                        selectedMood = 4;
                        break;

                        // very satisfied
                    case 4:
                        selectMood.setImageResource(R.drawable.ic_sentiment_5_very_satisfied_black_24dp);
                        selectedMood = 5;
                        break;
                }
            }
        });
        AlertDialog moodDialog = mBuilder.create();

        moodDialog.show();
    }


    // verwandelt das Bild in eine byte[], welche in einem DiaEntry und anschließend in der DB gespeichert werden kann
    private byte[] imageViewToByte(ImageView imageView) throws IOException {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        stream.close();
        return byteArray;
    }

    // erfragt ob die Erlaubnis erteilt worde ist, auf die Gallery zuzugreifen.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            } else {
                Toast.makeText(getApplicationContext(), "No permission to Gallery granted.", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        if (requestCode == REQUEST_CODE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(CreateActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CreateActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CreateActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    return;
                }else{
                    Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    String cityName = getCity(location.getLatitude(), location.getLongitude());
                    city.setText(cityName);
                }
            }
            return;
        }

    }

    // verwandelt das Bild in eine bitmap, die wir in unsere DB speichern können
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // überprüft, ob ein Bild ausgewählt wurde
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                imageView.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // verkleinert das bild auf die maxSize
    private Bitmap getResizedBitmap(Bitmap bitmap, int maxSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float bitmapRatio = (float) width / (float) height;

        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    // holt sich den Stadtnamen anhand der long und latitude
    private String getCity(double lat, double lon) {
        String city = new String();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> adresses;
        try{
            adresses = geocoder.getFromLocation(lat, lon, 2);
            for (Address address : adresses) {
                Log.e("Address", address.getLocality());
            }
            city = adresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
            city = "FAILURE";
        }

        return city;
    }

    // Setzt die AppBar auf mitsamt Toolbar.
    public void setUpAppBar (){
        // setzt die Toolbar auf
        Toolbar createToolbar = findViewById(R.id.create_toolbar);
        setSupportActionBar(createToolbar);

        ActionBar createActionBar = getSupportActionBar();

        if(createActionBar != null){
            createActionBar.setDisplayHomeAsUpEnabled(true);
           createActionBar.setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
        }
    }



    // Inflates toolbar menu.
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.create_menu, menu);

        return true;
    }

    // Handles toolbar selection.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Save Entry into Database
            case R.id.create_menu_done:

                createEntry();

                return true;

            // Switch to SettingsActivity
            case R.id.create_menu_settings:

                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);

                return true;

            default:

                return super.onOptionsItemSelected(item);

        }
    }
}
