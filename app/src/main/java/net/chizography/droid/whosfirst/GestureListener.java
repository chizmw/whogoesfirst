package net.chizography.droid.whosfirst;

import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * File sprang into existence thanks to Chisel on 15/02/2015.
 */

abstract class GestureListener extends GestureDetector.SimpleOnGestureListener {
    protected abstract void onSwipeLeft();
    protected abstract void onSwipeRight();
    protected abstract void onSwipeTop();
    protected abstract void onSwipeBottom();

    private static final int SWIPE_THRESHOLD = 250;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean result = false;
        try {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight();
                    } else {
                        onSwipeLeft();
                    }
                    result = true;
                }
            }
            else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    onSwipeBottom();
                } else {
                    onSwipeTop();
                }
                result = true;
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }
}
