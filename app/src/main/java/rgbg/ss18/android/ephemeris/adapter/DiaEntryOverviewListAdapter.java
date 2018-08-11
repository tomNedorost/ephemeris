package rgbg.ss18.android.ephemeris.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.Calendar;

import rgbg.ss18.android.ephemeris.R;
import rgbg.ss18.android.ephemeris.database.DiaEntryDatabase;

public class DiaEntryOverviewListAdapter extends CursorAdapter{
    public DiaEntryOverviewListAdapter(final Context context, final Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.dia_entry_overview_list, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ((TextView) view.findViewById(R.id.name)).setText(cursor.getString(cursor.getColumnIndex(DiaEntryDatabase.NAME_COL)));

        TextView date = (TextView) view.findViewById(R.id.date);

        if (cursor.isNull(cursor.getColumnIndex(DiaEntryDatabase.DATE_COL))) {
            date.setVisibility(View.GONE);
        } else {
            date.setVisibility(View.VISIBLE);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(cursor.getInt(cursor.getColumnIndex(DiaEntryDatabase.DATE_COL)) * 1000);
            date.setText(String.valueOf(calendar.get(Calendar.YEAR)));
        }
    }
}
