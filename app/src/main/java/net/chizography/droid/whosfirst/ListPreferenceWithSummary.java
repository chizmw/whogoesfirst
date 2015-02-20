package net.chizography.droid.whosfirst;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

// see: http://stackoverflow.com/a/16661022
public class ListPreferenceWithSummary extends ListPreference {

    public ListPreferenceWithSummary(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListPreferenceWithSummary(Context context) {
        super(context);
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
        setSummary(value);
    }

    @Override
    public void setSummary(CharSequence summary) {
        super.setSummary(getEntry());
    }
}
