package net.chizography.droid.whogoesfirst;
import android.view.*;
import android.widget.*;

public class CircleCountdown {
	
	private TextView tvTimer;
	
	CircleCountdown() {
		//todo
	}
	
	CircleCountdown(View v) {
		tvTimer = (TextView) v.findViewById(R.id.txtTimer);

		if (null==tvTimer) {
			Toast.makeText(v.getContext(), "dammit", Toast.LENGTH_SHORT).show();
		}
		else {
			Toast.makeText(v.getContext(), "cool", Toast.LENGTH_SHORT).show();
		}
	}
}
