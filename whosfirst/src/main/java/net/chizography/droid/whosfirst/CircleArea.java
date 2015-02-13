package net.chizography.droid.whosfirst;
import android.util.*;
import android.content.res.*;

public class CircleArea {
    private int radius;
    private int centerX;
    private int centerY;
	
	private boolean needsWiping = false;
	private boolean firstPlayer = false;
    
    private int startPosition = 0;

    CircleArea(int centerX, int centerY, int radius) {
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

    public void setFirstPlayer(boolean firstPlayer) {
        this.firstPlayer = firstPlayer;
    }

    public boolean isFirstPlayer() {
        return firstPlayer;
    }

    public void setNeedsWiping(boolean needsWiping) {
        this.needsWiping = needsWiping;
    }

    public boolean isNeedsWiping() {
        return needsWiping;
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
        return "Circle[" + centerX + ", " + centerY + ", " + radius + "]";
    }
	
	private final int scaleForDpiDensity(int size) {
		DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();

        // if we aren't DENSITY_XHIGH scale up/down based on DPI
        // e.g. http://developer.android.com/guide/practices/screens_support.html#DesigningResources -> Alternative Drawables
        // we (wrongly) used XHIGH instead of MEDIUM as our baseline when developing, simply because that's the device we had
        if (dm.densityDpi == DisplayMetrics.DENSITY_XHIGH) {
            return size;
        }
        else {
            Log.d("1P", String.format("dm.densityDpi:    %d", dm.densityDpi));
            Log.d("1P", String.format("DM.DENSITY_XHIGH: %d", DisplayMetrics.DENSITY_XHIGH));

            Double multiplier = (1.0 * dm.densityDpi) / (1.0 * DisplayMetrics.DENSITY_XHIGH);
            Log.d("1P", String.format("multiplier:       %f", multiplier));

            Log.d("1P", String.format("size before:      %d", size));
            size *= multiplier;
            Log.d("1P", String.format("size after:       %d", size));

            return (Integer)size;
        }
	}
}
