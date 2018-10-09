package net.chizography.droid.whosfirst.widget.prefs;

// based heavily on:
// http://stackoverflow.com/questions/531427/how-do-i-display-the-current-value-of-an-android-preference-in-the-preference-su

import android.content.Context;
import android.util.AttributeSet;

import net.chizography.droid.whosfirst.BuildConfig;

public class BuildNumberPreference extends ReadonlyPreference {

    public BuildNumberPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public CharSequence getTitle() {
        return "Build";
    }

    @Override
    public CharSequence getSummary() {
        return Integer.toString(BuildConfig.VERSION_CODE);
    }
}
