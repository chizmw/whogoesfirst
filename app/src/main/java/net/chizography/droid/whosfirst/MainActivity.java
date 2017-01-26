package net.chizography.droid.whosfirst;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import com.crashlytics.android.answers.Answers;
import io.fabric.sdk.android.Fabric;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.CustomEvent;

import net.chizography.droid.whosfirst.base.FabricSingleton;

public class MainActivity extends FabricSingleton {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        Fabric.with(this, new Answers());
		Fabric.with(this, new Crashlytics());

		// remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
							 WindowManager.LayoutParams.FLAG_FULLSCREEN);

		Answers.getInstance().logCustom(new CustomEvent("Opened App"));

		final Intent intent = new Intent(this, FingerCircleActivity.class);
		startActivity(intent);
		this.finish();
	}
}
