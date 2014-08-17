package net.chizography.droid.whosfirst;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.content.pm.*;
import android.graphics.*;
import java.io.*;
import android.util.*;


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
		
		//getScreen();
	}
	
	private void getScreen() {
		View content = findViewById(R.id.activityfingerchooserCirclesDrawingView);
		content = getWindow().getDecorView().getRootView();
		content.setDrawingCacheEnabled(true);
		Bitmap bitmap = content.getDrawingCache();
		content.setDrawingCacheEnabled(false);
		
		File file = new File( Environment.getExternalStorageDirectory() + "/data/test.png");
		Log.d("MainActivity", file.toString());
		try {
			file.createNewFile();
			FileOutputStream ostream = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, ostream);
			ostream.close();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
