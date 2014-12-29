package net.chizography.droid.whosfirst;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.content.pm.*;
import android.util.*;
import android.content.res.*;
import android.content.*;
import com.crashlytics.android.Crashlytics;

public class MainActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Crashlytics.start(this);
		
		// remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
							 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		final Intent intent = new Intent(this, FingerCircleActivity.class);
		startActivity(intent);
		this.finish();
	}
}
