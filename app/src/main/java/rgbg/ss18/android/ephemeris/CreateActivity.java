package rgbg.ss18.android.ephemeris;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
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
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import rgbg.ss18.android.ephemeris.database.DiaEntryDatabase;
import rgbg.ss18.android.ephemeris.model.DiaEntry;


public class CreateActivity extends AppCompatActivity {

    // ToDo: Abfangen, wenn kein Bild ausgewählt wird, nach Btn drücken
    // ToDo: Abfangen, wenn Standord nicht aktiv ist (aktuell App absturz)
    private static final int REQUEST_CODE_GALLERY = 999;
    private static final int REQUEST_CODE_LOCATION = 1000;

    private EditText title, description, city;
    private Button save, selectImage, findLocation;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dia_entry_create);

        init();
        initBtn();
    }

    private void init() {
        title = findViewById(R.id.createDiaEntryTitleEditText);
        description = findViewById(R.id.createDiaEntryDescEditText);
        imageView = findViewById(R.id.imageView);
        city = findViewById(R.id.location_input);
    }

    // Sobald der Eintrag erstellen Button aufgerufen wird
    private void initBtn() {
        // Buttons instanziieren
        save = findViewById(R.id.createDiaEntryBtn);
        selectImage = findViewById(R.id.selectImageBtn);
        findLocation = findViewById(R.id.location_find_btn);

        // onClickListener für save setzen
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // neuen DiaEntry erstellen, den brauchen wir, da die createEntry Funktion ein DiaEntry Object verlangt. Wenn du noch weitere Konstruktoren brauchst, gib Bescheid
                final DiaEntry newDiaEntry = new DiaEntry(title.getText().toString(), description.getText().toString());

                // fügt das bild zum DiaEntry als byte[] hinzu.
                try {
                    byte[] image = imageViewToByte(imageView);
                    newDiaEntry.setImage(image);
                } catch (IOException e) {
                    e.printStackTrace();
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

                // Activity schließen, eventueller Toast für erfolgreiche speicherung
                finish();
            }
        });

        // ToDo: App stürzt ab wenn man kein Bild auswählt
        // onClickListener für Image auswählen setzen
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(CreateActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GALLERY);
            }
        });

        findLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(CreateActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
            }
        });
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
        Uri uri = data.getData();
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            // wichtig! verkleinert das bild, sodass der Eintrag nicht die größe des Windows überschreitet. maxSize müssen wir hier noch austeste wie groß wir gehen können.
            bitmap = getResizedBitmap(bitmap, 1000);
            imageView.setImageBitmap(bitmap);
            inputStream.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
}