package rgbg.ss18.android.ephemeris;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import rgbg.ss18.android.ephemeris.database.DiaEntryDatabase;
import rgbg.ss18.android.ephemeris.model.DiaEntry;

public class DiaEntryCreateActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_GALLERY = 999;

    private EditText title, description;
    private Button save, selectImage;
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
    }

    // Sobald der Eintrag erstellen Button aufgerufen wird
    private void initBtn() {
        // Buttons instanziieren
        save = findViewById(R.id.createDiaEntryBtn);
        selectImage = findViewById(R.id.selectImageBtn);

        // onClickListener für save setzen
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // neuen DiaEntry erstellen, den brauchen wir, da die createEntry Funktion ein DiaEntry Object verlangt. Wenn du noch weitere Konstruktoren brauchst, gib Bescheid
                DiaEntry newDiaEntry = new DiaEntry(title.getText().toString(), description.getText().toString());

                try {
                    byte[] image = imageViewToByte(imageView);
                    newDiaEntry.setImage(image);
                } catch (IOException e) {
                    e.printStackTrace();
                }

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
                ActivityCompat.requestPermissions(DiaEntryCreateActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GALLERY);
            }
        });
    }

    // verwandelt das Bild in eine byte[], welche in einem DiaEntry und anschließend in der DB gespeichert werden kann
    private byte[] imageViewToByte(ImageView imageView) throws IOException {
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
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
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            } else {
                Toast.makeText(getApplicationContext(), "No permission granted.", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
}