package net.chizography.droid.whogoesfirst;
import android.view.*;
import android.widget.*;
import android.content.*;
import android.os.*;

public class CircleCountdown {
	
	private View inflatedView;
	private int countdownSeconds = 5;
	
	CircleCountdown(final CirclesDrawingView cdv) {
		// don't bother doing anything until we have
		// more than one finger/circle
		if (cdv.getTouchedCircleCount() < 2) {
			return;
		}
		
		LayoutInflater inflater = (LayoutInflater) cdv.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflatedView = inflater.inflate(R.layout.activity_finger_chooser, null, false);
		TextView tv = (TextView) inflatedView.findViewById(R.id.txtTimer);
		
		if (tv==null) {
			cdv.simpleToast("dammit");
			return;
		}
		else {
			cdv.setTimerText(countdownSeconds);
			cdv.setTimerTextVisible(true);
			if (tv.getVisibility() != View.VISIBLE) {
				cdv.setTimerTextVisible(true);
			}
			
			// granularity needs to be less than a second
			// otherwise it "jumps"
			new CountDownTimer(countdownSeconds * 1000, 250) {
				public void onTick(long millisUntilFinished) {
					int i = (int) Math.ceil( (millisUntilFinished+500) / 1000 );
					cdv.setTimerText(i);
				}

				public void onFinish() {
					cdv.setTimerText(0);
				}
			}.start();
		}
	}
}
