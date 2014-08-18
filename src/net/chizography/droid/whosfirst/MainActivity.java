package net.chizography.droid.whosfirst;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.content.pm.*;
import android.util.*;
import android.content.res.*;

public class MainActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
							 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_finger_chooser);
		
		try {
			PackageInfo pinfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
			TextView tv = (TextView) findViewById(R.id.appVersion);
			tv.setText(pinfo.versionName);
		}
		catch (Exception e) {
			//
		}
		
		DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
		TextView tv = (TextView) findViewById(R.id.appVersion);
		String dpi;
		switch (dm.densityDpi) {
			case dm.DENSITY_LOW:
				dpi = "LOW";
				break;
			case dm.DENSITY_MEDIUM:
				dpi = "MEDIUM";
				break;
			case dm.DENSITY_HIGH:
				dpi = "HIGH";
				break;
			case dm.DENSITY_XHIGH:
				dpi = "XHIGH";
				break;
			case dm.DENSITY_XXHIGH:
				dpi = "XXHIGH";
				break;
			case dm.DENSITY_XXXHIGH:
				dpi = "XXXHIGH";
				break;
			default:
				dpi="????";
		}
		tv.setText(
			tv.getText() + " (" + dpi + ")"
		);
	}
}
