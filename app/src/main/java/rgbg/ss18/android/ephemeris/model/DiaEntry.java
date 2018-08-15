package rgbg.ss18.android.ephemeris.model;

import java.io.Serializable;
import java.util.Calendar;

public class DiaEntry implements Serializable {
    // ToDo: Eigenschaft hinzufügen (Variable, Konstruktor, get, set)
    private long id;
    private String name;
    private Calendar date;
    private int mood;


    public DiaEntry(String name, Calendar date, int mood) {
        this.name = name;
        this.date = date;
        this.mood = mood;
    }

    public DiaEntry(String name) {
        this(name, null, 0);
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

}
