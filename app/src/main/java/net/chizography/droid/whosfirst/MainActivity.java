package net.chizography.droid.whosfirst;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
							 WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		final Intent intent = new Intent(this, FingerCircleActivity.class);
		startActivity(intent);
        try {
            final Intent intent1 = new Intent(this, UserPreferences.class);
            startActivity(intent1);
        }
        catch (Exception e) {
            Log.e("FP0", e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
		this.finish();
	}
}
