package net.chizography.droid.whosfirst;
import android.app.*;
import android.os.*;
import android.view.*;
import android.content.pm.*;
import android.widget.*;
import android.util.*;
import android.content.res.*;

public class FingerCircleActivity extends Activity {
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
			appendDpiDensity(tv);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private final void appendDpiDensity(TextView tv) {/*
		DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
		String dpi;
		
		switch (dm.densityDpi) {
			case DisplayMetrics.DENSITY_LOW:
				dpi = "LOW";
				break;
			case DisplayMetrics.DENSITY_MEDIUM:
				dpi = "MEDIUM";
				break;
			case DisplayMetrics.DENSITY_HIGH:
				dpi = "HIGH";
				break;
			case DisplayMetrics.DENSITY_XHIGH:
				dpi = "XHIGH";
				break;
			case DisplayMetrics.DENSITY_XXHIGH:
				dpi = "XXHIGH";
				break;
			case DisplayMetrics.DENSITY_XXXHIGH:
				dpi = "XXXHIGH";
				break;
			default:
				dpi = "????";
		}
		tv.setText(
            String.format("%s (%s)[%s]", tv.getText(), dpi, Float.toString(dm.densityDpi))
		);
	*/}
}
