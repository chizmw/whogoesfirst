package net.chizography.droid.whosfirst;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Logger;

public class FingerCircles {
    
    private Canvas canvas;
    
    /** Paint to draw circles */
    private AppPaint mCirclePaint;
    private AppPaint mWinnerPaint;
    private AppPaint paintWinnerCircleBorder;

    /** All available circles */
    private HashSet<CircleArea> mCircles = new HashSet<>();
    private SparseArray<CircleArea> mCirclePointer = new SparseArray<>();

    public void setOrderDisplayStyle(OrderStyle orderDisplayStyle) {
        this.orderDisplayStyle = orderDisplayStyle;
    }

    // inspired by http://im-dexter.blogspot.com/2014/12/enum-datatype-one-best-way-to-handle.html
    public enum OrderStyle {
        VALUE_IN_CIRCLE  ( 1 ),
        BUTTON_BUBBLE    ( 2 );

        OrderStyle(int i) {
        }
    }

    private OrderStyle orderDisplayStyle = OrderStyle.VALUE_IN_CIRCLE;
    
    public FingerCircles(final Canvas c) {
        init(c);
    }
    
    private void init(final Canvas c) {
        this.canvas = c;
        
        mCircles = new HashSet<>();
		mCirclePointer = new SparseArray<>();
        
        mCirclePaint = new AppPaint(AppPaint.paintType.DEFAULT);
        mWinnerPaint = new AppPaint(AppPaint.paintType.WINNER_CIRCLE_FILL);
        paintWinnerCircleBorder = new AppPaint(AppPaint.paintType.WINNER_CIRCLE_BORDER);
    }
    
    private void drawCircleBorder(CircleArea ca, AppPaint cb) {
        float borderRadius =
            ca.getRadius()
            + cb.getStrokeWidth()
            - 10;
        canvas.drawCircle(ca.getCenterX(), ca.getCenterY(), borderRadius, cb);
    }
    
    public int getTouchedCircleCount() {
        return mCirclePointer.size();
    }
    
    public void renderCircles(final boolean pickedWinner, final boolean showPlayerOrder) {
        // sometimes circles merge; weird things happen when we have more pointers
        // than circles
        for (CircleArea circle : mCircles) {
            AppPaint p;
            if (circle.isFirstPlayer()) {
                p = mWinnerPaint;
                AppPaint cb = paintWinnerCircleBorder;
                drawCircleBorder(circle, cb);
            }
            else {
                p = mCirclePaint;
                if (pickedWinner) {
                    p.setAlpha(30);
                }
            }
            canvas.drawCircle(circle.getCenterX(), circle.getCenterY(), circle.getRadius(), p);

            if (pickedWinner && showPlayerOrder) {
                if (circle == null) {
                    AppLog.e("null circle while looping through circles in renderCircles()");
                }
                else {
                    if (circle.hasStartPosition()) {
                        switch (this.orderDisplayStyle) {
                            case BUTTON_BUBBLE:
                                drawPlayerOrderBadge(circle, circle.getStartPosition());
                                break;
                            case VALUE_IN_CIRCLE:
                            default:
                                drawPlayerOrderNumber(circle, circle.getStartPosition());
                        }
                    }
                }
            }
        }
    }
    
    private void drawPlayerOrderNumber(CircleArea circle, int startPosition) {
        Paint textPaint = new AppPaint(AppPaint.paintType.PLAYER_ORDER_CIRCLE_TEXT);
        textPaint.setTextSize(circle.getRadius());
        String text = Integer.toString(startPosition);
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        canvas.drawText(
            text,
            circle.getCenterX(),
            circle.getCenterY() + (bounds.height()/2),
            textPaint
        );
    }
    
    private void drawPlayerOrderBadge(CircleArea circle, int startPosition) {
        Paint paint;
        Paint circlePaint;
        String text = Integer.toString(startPosition);

        paint = new AppPaint(AppPaint.paintType.START_POSITION_TEXT);
        circlePaint = new AppPaint(AppPaint.paintType.START_POSITION_CIRCLE);

        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        
        final int baseY = circle.getCenterY() - circle.getRadius() - (bounds.height()/2);

        canvas.drawCircle(
            circle.getCenterX(),
            baseY,
            bounds.height(),
            circlePaint
        );
        canvas.drawText(
            text,
            circle.getCenterX(),
            baseY + (paint.getTextSize()/3),
            paint
        );
    }

    private void dumpCirclePointers() {
        if (mCirclePointer == null) {
            AppLog.d("CIRCLEDUMP| null");
            return;
        }
        AppLog.d(
            String.format(
                Locale.getDefault(),
                "CIRCLEDUMP| %d circle pointers",
                mCirclePointer.size()
            )
        );

        for (CircleArea circle : mCircles) {
            if (circle == null) {
                AppLog.w(
                    "CIRCLEDUMP: NULL circle snuck in to the pot"
                );
                continue;
            }
            AppLog.d(
                String.format(
                    Locale.getDefault(),
                    "CIRCLEDUMP| CirclePointer (%4d, %4d); pointers=%d",
                    circle.getCenterX(),
                    circle.getCenterY(),
                    circle.getPointerCount()
                )
            );
        }
    }
    
    public void pickPlayerOrder(final boolean pickedWinner) {
        if (pickedWinner) {
            //AppLog.v("pickPlayerOrder(): pickedWinner is true; nothing to do");
            return;
        }
        AppLog.d("Time to pickPlayerOrder()");
        dumpCirclePointers();

        // check that we actually have circle pointers
        if (mCirclePointer == null) {
            AppLog.e("pickPlayerOrder(): mCirclePointer == null");
            return;
        }
        // and extra check that we have non-zero circle pointers
        if (mCirclePointer.size() < 1) {
            AppLog.e("pickPlayerOrder(): size < 1");
            return;
        }

        /*
         * null circles can appear when we remove circles from the hashset or pointer list
         * rather than re-arrange them all and complicate things even further, just ignore them at this stage.
         */


        /* I'm sure there is a more elegant way to do this;
         * basically pick a random number, and keep going until we pick a non-null circle from the list of pointers
         */
        CircleArea ca = null;
        Random rand = new Random();

        while (ca == null) {
            int ri = rand.nextInt((mCirclePointer.size()));
            ca = mCirclePointer.get(ri);

            AppLog.d("Winning position " + ri);
            if(null==ca) {
                AppLog.w("pickPlayerOrder(): Picked a pointer with no circle");
            }
            if (ca != null) {
                ca.setFirstPlayer(true);
                ca.setStartPosition(1);
            }
        }

        // assign remaining startPosition values
        /* we start with the number of remaining positions set to the total number of pointers;
         * keep picking circles and when we find one that's not null and doesn't (yet) have a start position,
         * set the start position for that circle, and decrement the number of remaining positions to assign
         */
        int remainingPositions = mCirclePointer.size();
        // we have already assigned #1
        while (remainingPositions > 1) {
            CircleArea position_circle;
            // find a circle with no assigned start position
            do {
                int randomIndex = rand.nextInt(mCirclePointer.size());
                AppLog.d("randomIndex=" + randomIndex);
                position_circle = mCirclePointer.get(randomIndex);
                if (position_circle == null) {
                    /* this can happen when "circles merge" ... it's a feature of HashSets */
                    AppLog.d("pickPlayerOrder(): remainingPositions: dammit! a null circle!");
                    /* we don't need to assign a start position for this bugger; decrement so we
                     * know we've dealt with this non-circle
                     */
                    remainingPositions--;
                }
            }
            while (position_circle == null || position_circle.hasStartPosition());

            // assign a position
            position_circle.setStartPosition(remainingPositions);
            // reduce the number of available positions
            remainingPositions--;
        }

        // when we fall out here we've assigned all start positions
        Answers.getInstance().logCustom(
            new CustomEvent("Selected First Player")
                .putCustomAttribute("Finger Count", mCirclePointer.size())
        );
        AppLog.i("Finger Count: " + mCirclePointer.size());
	}
    
    public CircleArea scanForTouchedCircle(final MotionEvent event){
        int actionIndex;
        CircleArea touchedCircle=null;
        int xTouch,yTouch,pointerId;
        final int pointerCount = event.getPointerCount();

        for (actionIndex = 0; actionIndex < pointerCount; actionIndex++) {
            // Some pointer has moved, search it by pointer id
            pointerId = event.getPointerId(actionIndex);

            xTouch = (int) event.getX(actionIndex);
            yTouch = (int) event.getY(actionIndex);

            touchedCircle = mCirclePointer.get(pointerId);

            if (null != touchedCircle) {
                touchedCircle.setCenterX(xTouch);
                touchedCircle.setCenterY(yTouch);
            }
        }
        return touchedCircle;
	}
    
    public void clearCirclePointers() {
        mCirclePointer.clear();
        mCircles.clear();
    }

    public CircleArea obtainTouchedCircle(final int xTouch, final int yTouch) {
        CircleArea touchedCircle = getTouchedCircle(xTouch, yTouch);

        if (null == touchedCircle) {
            touchedCircle = new CircleArea(xTouch, yTouch, 120);
            mCircles.add(touchedCircle);
            AppLog.d("Added: " + touchedCircle.toString());
        }

        return touchedCircle;
    }

    private CircleArea getTouchedCircle(final int xTouch, final int yTouch) {
        CircleArea touched = null;

        for (CircleArea circle : mCircles) {
            if ((circle.getCenterX() - xTouch) * (circle.getCenterX() - xTouch) + (circle.getCenterY() - yTouch) * (circle.getCenterY() - yTouch) <= circle.getRadius() * circle.getRadius()) {
                touched = circle;
                break;
            }
        }

        return touched;
    }
    
    public void putPointer (final int id, final CircleArea circle) {
        mCirclePointer.put(id, circle);
        circle.increasePointerCount();
    }
    
    public CircleArea getPointer (final int id) {
        return mCirclePointer.get(id);
    }
    
    public void removePointer (final int id) {
        // remove the referenced circle
        CircleArea ca = mCirclePointer.get(id);
        // if it's null we probably merged circles and removed one earlier
        if(null==ca){
            AppLog.w("removePointer() called for a null destination");
        }
        else if (ca.getPointerCount()==1){
            AppLog.d("Removing last by pointer: " + ca.toString());
            mCircles.remove(ca);
        }
        else {
            AppLog.d("The circle has " + ca.getPointerCount());
        }
        // always remove the reference
        mCirclePointer.remove(id);
    }
}
