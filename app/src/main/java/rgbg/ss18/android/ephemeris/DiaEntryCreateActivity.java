package rgbg.ss18.android.ephemeris;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.net.URI;

import rgbg.ss18.android.ephemeris.database.DiaEntryDatabase;
import rgbg.ss18.android.ephemeris.model.DiaEntry;

public class DiaEntryCreateActivity extends AppCompatActivity {

    private static final int SELECT_IMAGE = 100;

    private EditText title, description;
    private Button save, selectImage;
    private ImageView imageView;
    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dia_entry_create);

        initUi();
        initBtn();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SELECT_IMAGE) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void initUi() {
        title = findViewById(R.id.createDiaEntryTitleEditText);
        description = findViewById(R.id.createDiaEntryDescEditText);
        imageView = findViewById(R.id.imageView);
    }

    // Sobald der Eintrag erstellen Button aufgerufen wird
    private void initBtn() {
        // Button instanziieren
        save = findViewById(R.id.createDiaEntryBtn);
        selectImage = findViewById(R.id.selectImageBtn);

        // onClickListener für save setzen
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // neuen DiaEntry erstellen, den brauchen wir, da die createEntry Funktion ein DiaEntry Object verlangt. Wenn du noch weitere Konstruktoren brauchst, gib Bescheid
                DiaEntry newDiaEntry =  new DiaEntry(title.getText().toString(), description.getText().toString(), imageUri.toString());

                // verbindung zur DB aufbauen
                DiaEntryDatabase db = DiaEntryDatabase.getInstance(DiaEntryCreateActivity.this);

                // diaEntry hinzufügen zur db
                db.createEntry(newDiaEntry);

                // verbindung zur DB schließen WICHTIG muss immer gemacht werden, wenn wir die db verwenden, da es sonst zu komplikationen kommt
                db.close();

                // Activity schließen, eventueller Toast für erfolgreiche speicherung
                finish();
            }
        });

        // onClickListener für Image auswählen setzen
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_IMAGE);
            }
        });
    }
}
