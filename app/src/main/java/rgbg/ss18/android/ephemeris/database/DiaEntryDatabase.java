package rgbg.ss18.android.ephemeris.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rgbg.ss18.android.ephemeris.model.DiaEntry;

public class DiaEntryDatabase extends SQLiteOpenHelper{
    public static DiaEntryDatabase INSTANCE = null;

    // ToDo: Version ändern, wenn neue Column hinzugefügt wurde
    private static final String DB_NAME = "DIARYENTRIES";
    private static final int VERSION = 5;
    private static final String TABLE_NAME = "diaryentries";

    public static final String ID_COL = "ID";
    public static final String NAME_COL = "name";
    public static final String DATE_COL = "date";
    public static final String MOOD_COL = "mood";
    public static final String DESC_COL = "description";
    public static final String IMG_URI_COL = "uri";
    // ToDo: neue Column hier nennen

    // Todo: neue Col hier hinzufügen
    private String[] ALL_COLS = {ID_COL, NAME_COL, DATE_COL, MOOD_COL, DESC_COL, IMG_URI_COL};

    private DiaEntryDatabase(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    public static DiaEntryDatabase getInstance (final Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DiaEntryDatabase(context);
        }

        return INSTANCE;
    }

    // ToDo: neue Columns/Felder hier hinzufügen
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createQuery = "CREATE TABLE " + TABLE_NAME + " (" + ID_COL + " INTEGER PRIMARY KEY, " +  NAME_COL + " TEXT NOT NULL, " + DATE_COL + " INTEGER DEFAULT NULL, " + MOOD_COL + " INTEGER DEFAULT NULL, " + DESC_COL + " TEXT," + IMG_URI_COL + " Text)";

        db.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropQuery = "DROP TABLE IF EXISTS " + TABLE_NAME;

        db.execSQL(dropQuery);
        onCreate(db);
    }

    // ToDo: mit value.put den Wert der neuen Columns hier hinzufügen
    // Erstellt einen neuen DiaEntry in der DB mit Hilfe eines DiaEntries
    public DiaEntry createEntry(final DiaEntry diaEntry){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NAME_COL, diaEntry.getName());
        values.put(DATE_COL, diaEntry.getDate() == null ? null : diaEntry.getDate().getTimeInMillis() / 1000);
        values.put(MOOD_COL, diaEntry.getMood());
        values.put(DESC_COL, diaEntry.getDescription());
        values.put(IMG_URI_COL, diaEntry.getUriString());

        long newId = db.insert(TABLE_NAME, null, values);

        db.close();

        return readDiaEntry(newId);
    }

    // ToDo: diaEntry richtig mit allen Cols beschreiben
    // Liest einen DiaEntry aus der DB mit hilfe der ID
    public DiaEntry readDiaEntry(final long id) {
        SQLiteDatabase db =  this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, ALL_COLS, ID_COL + " = ?", new String[]{String.valueOf(id)}, null, null, null);

        DiaEntry diaEntry = null;

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            diaEntry = new DiaEntry(cursor.getString(cursor.getColumnIndex(NAME_COL)));
            diaEntry.setId(cursor.getLong(cursor.getColumnIndex(ID_COL)));

            Calendar calendar = null;

            if (!cursor.isNull(cursor.getColumnIndex(DATE_COL))) {
                calendar = Calendar.getInstance();
                calendar.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(DATE_COL)) * 1000);
            }

            diaEntry.setDate(calendar);
            diaEntry.setMood(cursor.getInt(cursor.getColumnIndex(MOOD_COL)));
            diaEntry.setDescription(cursor.getString(cursor.getColumnIndex(DESC_COL)));
            diaEntry.setUriString(cursor.getString(cursor.getColumnIndex(IMG_URI_COL)));
        }

        db.close();
        return diaEntry;
    }

    // Gibt eine Liste mit allen DiaEntries zurück.
    public List<DiaEntry> readAllDiaEntries() {
        List<DiaEntry> diaEntries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " +  TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do{
                DiaEntry diaEntry = readDiaEntry(cursor.getLong(cursor.getColumnIndex(ID_COL)));
                if (diaEntry != null) {
                    diaEntries.add(diaEntry);
                }
            }while(cursor.moveToNext());
        }

        db.close();

        return diaEntries;
    }

    // ToDo: alle Cols updaten
    // Updated einen bestehenden DiaEntry, welcher übergeben werden muss.
    public DiaEntry updateDiaEntry(final DiaEntry diaEntry) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(NAME_COL, diaEntry.getName());
        contentValues.put(DATE_COL, diaEntry.getDate() == null ? null : diaEntry.getDate().getTimeInMillis() / 1000);
        contentValues.put(MOOD_COL, diaEntry.getMood());
        contentValues.put(DESC_COL, diaEntry.getDescription());
        contentValues.put(IMG_URI_COL, diaEntry.getUriString());

        db.update(TABLE_NAME, contentValues, ID_COL + " = ?", new String[]{String.valueOf(diaEntry.getId())});

        db.close();

        return this.readDiaEntry(diaEntry.getId());
    }

    public void deleteDiaEntry(final DiaEntry diaEntry) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAME, ID_COL + " = ?", new String[]{String.valueOf(diaEntry.getId())});

        db.close();
    }

    public void deleteAllDiaEntries() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_NAME);

        db.close();
    }
}
