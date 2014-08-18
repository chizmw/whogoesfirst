package net.chizography.droid.whosfirst;
import android.util.*;
import android.content.res.*;

public class CircleArea {
    int radius;
    int centerX;
    int centerY;
	
	boolean needs_wiping = false;
	boolean first_player = false;

    CircleArea(int centerX, int centerY, int radius) {
        this.radius = scaleForDpiDensity(radius);
        this.centerX = centerX;
        this.centerY = centerY;
    }

    @Override
    public String toString() {
        return "Circle[" + centerX + ", " + centerY + ", " + radius + "]";
    }
	
	private final int scaleForDpiDensity(int size) {
		DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();

		switch (dm.densityDpi) {
			case dm.DENSITY_LOW:
				// probably wrong size but needs a device to test on
				break;
			case dm.DENSITY_MEDIUM:
				// probably wrong size but needs a device to test on
				break;
			case dm.DENSITY_HIGH:
				// probably wrong size but needs a device to test on
				break;
			case dm.DENSITY_XHIGH:
				// this is the dpi density of the n7 the app was developed om
				// so we don't need to alter thid
				break;
			case dm.DENSITY_XXHIGH:
				// e.g. Nexus 5
				size *= 1.2;
				break;
			case dm.DENSITY_XXXHIGH:
				// probably wrong size but needs a device to test on
				size *= 1.4;
				break;
			default:
				// nothing to do here
		}
		return size;
	}
}
