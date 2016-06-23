package net.chizography.droid.whosfirst;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class AboutActivity extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove title
        /*
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
                             */

        setContentView(R.layout.about);

        WebView webView = (WebView) findViewById(R.id.webView_about);
        webView.getSettings().setJavaScriptEnabled(false);
        webView.loadUrl("file:///android_asset/about/about.html");
        
    }
}
