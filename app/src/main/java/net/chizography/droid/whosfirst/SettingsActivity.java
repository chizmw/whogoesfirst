package net.chizography.droid.whosfirst;

import android.app.Activity;
import android.os.Bundle;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import net.chizography.droid.whosfirst.base.FabricSingleton;

public class SettingsActivity extends FabricSingleton {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Answers.getInstance().logCustom(new CustomEvent("Opened Settings"));


        // Display the preferences fragment as the content of the activity
        getFragmentManager().beginTransaction()
            .replace(android.R.id.content, new SettingsFragment()).commit();
    }
}
