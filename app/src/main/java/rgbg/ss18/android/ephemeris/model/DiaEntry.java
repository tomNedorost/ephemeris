package rgbg.ss18.android.ephemeris.model;

import java.io.Serializable;
import java.util.Calendar;

// Aufbau eines DiaEntries. Dieser besteht aus:
// Id, Namen, Datum, Mood, Beschreibung
public class DiaEntry implements Serializable {
    // ToDo: Eigenschaft hinzufügen (Variable, Konstruktor, get, set)
    private long id;
    private String name;
    private Calendar date;
    private int mood;
    private String description;

    // Konstruktor, der alle Eigenschaften eines DiaEntries beschreibt
    public DiaEntry(String name, Calendar date, int mood, String description) {
        this.name = name;
        this.date = date;
        this.mood = mood;
        this.description = description;
    }

    public DiaEntry(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Konstruktor nur mit einem Namen
    public DiaEntry(String name) {
        this(name, null, 0, "Kein Inhalt gespeichert");
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public int getMood() {
        return mood;
    }

    public void setMood(int mood) {
        this.mood = mood;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
