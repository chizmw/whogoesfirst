package net.chizography.droid.whosfirst.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;

import io.fabric.sdk.android.Fabric;

/**
 * File sprang into existence thanks to chisel on 26/01/2017.
 */

@SuppressLint("Registered")
public class FabricSingleton extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Fabric.isInitialized()) {
            Fabric.with(this, new Answers());
            Fabric.with(this, new Crashlytics());
        }
    }

}