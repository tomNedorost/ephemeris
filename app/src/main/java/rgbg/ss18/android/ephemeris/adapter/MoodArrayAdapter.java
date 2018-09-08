package rgbg.ss18.android.ephemeris.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rgbg.ss18.android.ephemeris.R;

public class MoodArrayAdapter extends ArrayAdapter<String> {


    private List<Integer> moodDrawables;
    private int selectedMood = 5;

    public MoodArrayAdapter(Context context, List<String> moodNames, List<Integer> moodDrawables) {
        super(context, android.R.layout.select_dialog_item, moodNames);
        this.moodDrawables = moodDrawables;
    }

    public MoodArrayAdapter(Context context, String [] moodNames, Integer[] moodDrawables) {
        super(context, android.R.layout.select_dialog_item, moodNames);
        this.moodDrawables = Arrays.asList(moodDrawables);
    }

    public MoodArrayAdapter(Context context, String[] moodNames, int moodDrawables) {
        super(context, android.R.layout.select_dialog_item, moodNames);

        final TypedArray mood = context.getResources().obtainTypedArray(moodDrawables);
        this.moodDrawables = new ArrayList<Integer>() {{ for (int i = 0; i < mood.length(); i++) {add(mood.getResourceId(i, -1));} }};

        // recycle the array
        mood.recycle();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView textView = (TextView) view.findViewById(android.R.id.text1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(moodDrawables.get(position), 0, 0, 0);
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(moodDrawables.get(position), 0, 0, 0);
        }
        textView.setCompoundDrawablePadding(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getContext().getResources().getDisplayMetrics()));
        return view;
    }
}