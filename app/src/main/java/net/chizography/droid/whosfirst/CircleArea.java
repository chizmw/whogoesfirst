package net.chizography.droid.whosfirst;
import android.content.res.*;
import android.util.DisplayMetrics;

import java.util.Locale;

public class CircleArea {
    private final int radius;
    private int centerX;
    private int centerY;
	
	//private boolean needsWiping = false;
	private boolean firstPlayer = false;
    
    private int startPosition = 0;
    
    // a bit hacky
    private int pointerCount = 0;
    
    public void increasePointerCount(){
        pointerCount++;
    }

    public int getPointerCount() {
        return pointerCount;
    }

    CircleArea(int centerX, int centerY, @SuppressWarnings("SameParameterValue") int radius) {
        this.radius = scaleForDpiDensity(radius);
        this.centerX = centerX;
        this.centerY = centerY;
    }

    public void setCenterX(int centerX) {
        this.centerX = centerX;
    }

    public int getCenterX() {
        return centerX;
    }

    public void setCenterY(int centerY) {
        this.centerY = centerY;
    }

    public int getCenterY() {
        return centerY;
    }

    public int getRadius() {
        return radius;
    }

    public void setFirstPlayer(@SuppressWarnings("SameParameterValue") boolean firstPlayer) {
        this.firstPlayer = firstPlayer;
    }

    public boolean isFirstPlayer() {
        return firstPlayer;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    public int getStartPosition() {
        return startPosition;
    }
    
    public boolean hasStartPosition() {
        return (startPosition > 0);
    }

    

    @Override
    public String toString() {
        return "Circle[x:" + centerX + ", y:" + centerY + ", r:" + radius + ", p:" + pointerCount + "]";
    }
	
	private int scaleForDpiDensity(int size) {
		DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();

        // if we aren't DENSITY_XHIGH scale up/down based on DPI
        // e.g. http://developer.android.com/guide/practices/screens_support.html#DesigningResources -> Alternative Drawables
        // we (wrongly) used XHIGH instead of MEDIUM as our baseline when developing, simply because that's the device we had
        if (dm.densityDpi == DisplayMetrics.DENSITY_XHIGH) {
            return size;
        }
        else {
            AppLog.d(String.format(Locale.getDefault(), "dm.densityDpi:    %d", dm.densityDpi));
            AppLog.d(String.format(Locale.getDefault(), "DM.DENSITY_XHIGH: %d", DisplayMetrics.DENSITY_XHIGH));

            Double multiplier = (1.0 * dm.densityDpi) / (1.0 * DisplayMetrics.DENSITY_XHIGH);
            AppLog.d(String.format(Locale.getDefault(), "multiplier:       %f", multiplier));

            AppLog.d(String.format(Locale.getDefault(), "size before:      %d", size));
            size *= multiplier;
            AppLog.d(String.format(Locale.getDefault(), "size after:       %d", size));

            return size;
        }
	}
}
