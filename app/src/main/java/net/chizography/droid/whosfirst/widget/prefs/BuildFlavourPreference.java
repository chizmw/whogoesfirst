package net.chizography.droid.whosfirst.widget.prefs;

// based heavily on:
// http://stackoverflow.com/questions/531427/how-do-i-display-the-current-value-of-an-android-preference-in-the-preference-su

import android.content.Context;
import android.util.AttributeSet;

import net.chizography.droid.whosfirst.BuildConfig;

import java.util.Locale;

public class BuildFlavourPreference extends ReadonlyPreference {

    public BuildFlavourPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public CharSequence getTitle() {
        return "Flavour";
    }

    @Override
    public CharSequence getSummary() {
        String summary;
        if (BuildConfig.FLAVOR.length() > 0) {
            summary = String.format(
                    Locale.getDefault(),
                    "%s-%s",
                    BuildConfig.BUILD_TYPE,
                    BuildConfig.FLAVOR
            );
        }
        else {
            summary = BuildConfig.BUILD_TYPE;
        }
        return summary;
    }
}
