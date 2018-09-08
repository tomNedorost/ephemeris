package rgbg.ss18.android.ephemeris.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import rgbg.ss18.android.ephemeris.R;
import rgbg.ss18.android.ephemeris.database.DiaEntryDatabase;
import rgbg.ss18.android.ephemeris.model.DiaEntry;

public class DiaEntryOverviewListAdapter extends ArrayAdapter<DiaEntry>{
    public DiaEntryOverviewListAdapter(Context context, List<DiaEntry> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        DiaEntry currentDiaEntry = getItem(position);

        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.dia_entry_overview_list, parent, false);
        }
        ((TextView) view.findViewById(R.id.name)).setText(currentDiaEntry.getName());

        TextView date = view.findViewById(R.id.date);

        if (currentDiaEntry.getDate() == null) {
            date.setVisibility(View.GONE);
        } else {
            date.setVisibility(View.VISIBLE);
            date.setText(String.valueOf(new SimpleDateFormat("d.MMM.yyyy").format(currentDiaEntry.getDate().getTime())));
        }

        return view;
    }
}
