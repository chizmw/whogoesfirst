package net.chizography.droid.whosfirst;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import hotchemi.android.rate.AppRate;

import android.util.Log;

public class FingerCircleActivity extends Activity {
    private String versionString;

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
		}
		catch (Exception e) {
			e.printStackTrace();
		}

        upgradeCheck();
        rateAppReminder();
	}
    
	@Override
    public void onResume() {
        super.onResume();
        // refresh/reload content view (force reload of prefs)
        setContentView(R.layout.activity_finger_chooser);
        TextView tv = (TextView) findViewById(R.id.appVersion);
        tv.setText(versionString);
    }
    
    private void rateAppReminder(){
        // prompt users to rate
        AppRate.with(this)
            .setInstallDays(28) // default 10, 0 means install day.
            .setLaunchTimes(50) // default 10
            .setRemindInterval(10) // default 1
            .setShowLaterButton(true)
            .setShowNeverButton(true)
            .setDebug(false) // default false
            .setOnClickButtonListener(which -> Log.d(MainActivity.class.getName(), Integer.toString(which)))
            .monitor();

        // Show a dialog if meets conditions
        AppRate.showRateDialogIfMeetsConditions(this);
    }
    
    private void upgradeCheck(){
        try {
            // the last version we ran with
            final SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(this);
            final int lastVersion = prefs.getInt("last_known_version",0);
                
            // our current version
            PackageInfo packageInfo =
                this.getPackageManager()
                    .getPackageInfo(this.getPackageName(), 0);
            final int versionCode = packageInfo.versionCode;
            
            // if they are different, update the shared pref
            if (lastVersion != versionCode) {
                final SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("last_known_version", versionCode);
                editor.apply();
                
                // so we can see that we triggered it
                AppLog.d(
                    "Version changed from " +
                        String.valueOf(lastVersion) +
                    " to " +
                        String.valueOf(versionCode)       
                );
                
                // try to encourage updates to ratings
                // (don't do this for the initial installation)
                if (lastVersion > 0) {
                    AppRate.with(this).clearAgreeShowDialog();
                }
            }
        }
        catch (Exception e) {
            AppLog.e(e.getMessage());
        }
    }
}
