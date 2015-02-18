package net.chizography.droid.whosfirst;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

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
	@Override
    public void onResume() {
        super.onResume();
        // refresh/reload content view (force reload of prefs)
        setContentView(R.layout.activity_finger_chooser);
    }
    
	private final void appendDpiDensity(TextView tv) {}
}
