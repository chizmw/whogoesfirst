package net.chizography.droid.whosfirst.about;

import android.os.Bundle;
import android.webkit.WebView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import net.chizography.droid.whosfirst.R;
import net.chizography.droid.whosfirst.base.FabricSingleton;

public class AboutAppActivity extends FabricSingleton {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Answers.getInstance().logCustom(new CustomEvent("Opened AboutApp"));

        setContentView(R.layout.activity_about_app);

        WebView webView = findViewById(R.id.webView_about);
        webView.getSettings().setJavaScriptEnabled(false);
        webView.loadUrl("file:///android_asset/about/about.html");
    }
}
