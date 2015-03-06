package net.chizography.droid.whosfirst;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.support.v4.app.*;
import android.view.*;

public class AboutActivity extends DialogFragment {

    public AboutActivity () {
        // Empty constructor required for DialogFragment
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        getDialog().setContentView(R.layout.about);

        return getView();
    }
    
    @Override public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.20f;
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(windowParams);
    }
}
