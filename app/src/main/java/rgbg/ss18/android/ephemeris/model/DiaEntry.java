package rgbg.ss18.android.ephemeris.model;

import java.util.Calendar;

public class DiaEntry {
    private long id;
    private String name;
    private Calendar date;

    public DiaEntry(String name, Calendar date) {
        this.name = name;
        this.date = date;
    }

    public DiaEntry(String name) {
        this(name, null);
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

}
