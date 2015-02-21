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
import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;
import android.util.Log;

public class FingerCircleActivity extends Activity {
    private String versionString;
    private TextView tvVersion;
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
            versionString = pinfo.versionName;
			tvVersion = (TextView) findViewById(R.id.appVersion);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

        // prompt users to rate
        AppRate.with(this)
            .setInstallDays(10) // default 10, 0 means install day.
            .setLaunchTimes(10) // default 10
            .setRemindInterval(5) // default 1
            .setShowNeutralButton(true) // default true
            .setDebug(false) // default false
            .setOnClickButtonListener(new OnClickButtonListener() { // callback listener.
                @Override
                public void onClickButton(int which) {
                    Log.d(MainActivity.class.getName(), Integer.toString(which));
                }
            })
            .monitor();

        // Show a dialog if meets conditions
        AppRate.showRateDialogIfMeetsConditions(this);
	}
	@Override
    public void onResume() {
        super.onResume();
        // refresh/reload content view (force reload of prefs)
        setContentView(R.layout.activity_finger_chooser);
        TextView tv = (TextView) findViewById(R.id.appVersion);
        tv.setText(versionString);
    }
}
