package net.chizography.droid.whosfirst.about;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import net.chizography.droid.whosfirst.R;

public class AboutAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        WebView webView = (WebView) findViewById(R.id.webView_about);
        webView.getSettings().setJavaScriptEnabled(false);
        webView.loadUrl("file:///android_asset/about/about.html");
    }
}
