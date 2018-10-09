package net.chizography.droid.whosfirst.widget.prefs;

// based heavily on:
// http://stackoverflow.com/questions/531427/how-do-i-display-the-current-value-of-an-android-preference-in-the-preference-su

import android.content.Context;
import android.util.AttributeSet;

import net.chizography.droid.whosfirst.BuildConfig;

class ReadonlyPreference extends android.preference.EditTextPreference {

    ReadonlyPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public CharSequence getSummary() {
        return Integer.toString(BuildConfig.VERSION_CODE);
    }

    @Override
    public CharSequence getTitle() {
        return "Current Build";
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
