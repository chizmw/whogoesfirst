package net.chizography.droid.whosfirst;
import android.content.res.*;
import android.util.DisplayMetrics;
import android.view.*;

public class CircleArea {
    private int radius;
    private int centerX;
    private int centerY;
    private float startX, startY, distanceSum;
	
	//private boolean needsWiping = false;
	private boolean firstPlayer = false;
    
    private int startPosition = 0;
    
    // a bit hacky
    private int pointerCount = 0;

    private void setStartX(float startX) {
        this.startX = startX;
    }

    public float getStartX() {
        return startX;
    }

    private void setStartY(float startY) {
        this.startY = startY;
    }

    public float getStartY() {
        return startY;
    }

    private void setDistanceSum(float distanceSum) {
        this.distanceSum = distanceSum;
    }

    public float getDistanceSum() {
        return distanceSum;
    }
    
    public void increasePointerCount(){
        pointerCount++;
    }
    
    public void decreasePointerCount(){
        pointerCount--;
    }
    
    public boolean hasPointers(){
        return pointerCount > 0;
    }
    
    public int getPointerCount() {
        return pointerCount;
    }

    CircleArea(int centerX, int centerY, int radius) {
        this.radius = scaleForDpiDensity(radius);
        this.centerX = centerX;
        this.centerY = centerY;
        
        // our starting point for distance tracking
        setStartX(centerX);
        setStartY(centerY);
        setDistanceSum(0);
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
        return "Circle[x:" + centerX + ", y:" + centerY + ", r:" + radius + ", p:" + pointerCount + ", d:" + String.valueOf(getDistanceSum()) + "]";
    }
	
    //float getDistance(float startX, float startY, MotionEvent ev) {
    float getDistance(MotionEvent ev) {
        float distanceSum = 0;
              
        final int historySize = ev.getHistorySize();
        for (int h = 0; h < historySize; h++) {
            // historical point
            float hx = ev.getHistoricalX(0, h);
            float hy = ev.getHistoricalY(0, h);
            // distance between startX,startY and historical point
            float dx = (hx-getStartX());
            float dy = (hy-getStartY());
            distanceSum += Math.sqrt(dx*dx+dy*dy);
            // make historical point the start point for next loop iteration
            setStartX(hx);
            setStartY(hy);
        }
        // add distance from last historical point to event's point
        float dx = (ev.getX(0)-getStartX());
        float dy = (ev.getY(0)-getStartY());
        distanceSum += Math.sqrt(dx*dx+dy*dy);
        
        setDistanceSum(distanceSum);
        AppLog.d("distance: " + toString());
        return distanceSum;        
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
            AppLog.d(String.format("dm.densityDpi:    %d", dm.densityDpi));
            AppLog.d(String.format("DM.DENSITY_XHIGH: %d", DisplayMetrics.DENSITY_XHIGH));

            Double multiplier = (1.0 * dm.densityDpi) / (1.0 * DisplayMetrics.DENSITY_XHIGH);
            AppLog.d(String.format("multiplier:       %f", multiplier));

            AppLog.d(String.format("size before:      %d", size));
            size *= multiplier;
            AppLog.d(String.format("size after:       %d", size));

            return size;
        }
	}
}
