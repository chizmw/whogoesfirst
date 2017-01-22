package net.chizography.droid.whosfirst;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import com.crashlytics.android.answers.Answers;
import io.fabric.sdk.android.Fabric;
import com.crashlytics.android.Crashlytics;

public class MainActivity extends Activity {
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
        
		final Intent intent = new Intent(this, FingerCircleActivity.class);
		startActivity(intent);
		this.finish();
	}
}
