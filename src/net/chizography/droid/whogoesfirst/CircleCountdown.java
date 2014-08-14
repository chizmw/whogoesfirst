package net.chizography.droid.whogoesfirst;
import android.view.*;
import android.widget.*;
import android.content.*;

public class CircleCountdown {
	
	private View inflatedView;
	
	CircleCountdown(CirclesDrawingView cdv) {
		LayoutInflater inflater = (LayoutInflater) cdv.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflatedView = inflater.inflate(R.layout.activity_finger_chooser, null, false);
		TextView tv = (TextView) inflatedView.findViewById(R.id.txtTimer);
		
		if (tv==null) {
			cdv.simpleToast("dammit");
			return;
		}
		else {
			cdv.setTimerText(3);
			cdv.setTimerTextVisible(true);
			if (tv.getVisibility() != View.VISIBLE) {
				cdv.setTimerTextVisible(true);
			}
		}
	}
}
