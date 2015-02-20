package net.chizography.droid.whosfirst;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.util.SparseArray;
import android.view.MotionEvent;
import java.util.HashSet;
import java.util.Random;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class FingerCircles {
    
    private Canvas canvas;
    
    /** Paint to draw circles */
    private AppPaint mCirclePaint, mErasePaint, mWinnerPaint, paintWinnerCircleBorder;

    /** All available circles */
    private HashSet<CircleArea> mCircles = new HashSet<CircleArea>();
    private SparseArray<CircleArea> mCirclePointer = new SparseArray<CircleArea>();

    public void setOrderDisplayStyle(OrderStyle orderDisplayStyle) {
        this.orderDisplayStyle = orderDisplayStyle;
    }

    public OrderStyle getOrderDisplayStyle() {
        return orderDisplayStyle;
    }
    
    // inspired by http://im-dexter.blogspot.com/2014/12/enum-datatype-one-best-way-to-handle.html
    public static enum OrderStyle {
        VALUE_IN_CIRCLE  ( 1 ),
        BUTTON_BUBBLE    ( 2 );
        public final int value;
        OrderStyle(int i) { value = i; }
    };
    
    private OrderStyle orderDisplayStyle = OrderStyle.VALUE_IN_CIRCLE;
    
    public FingerCircles(final Canvas c) {
        init(c);
    }
    
    private void init(final Canvas c) {
        this.canvas = c;
        
        mCircles = new HashSet<CircleArea>();
		mCirclePointer = new SparseArray<CircleArea>();
        
        mCirclePaint = new AppPaint(AppPaint.paintType.DEFAULT);
        mErasePaint = new AppPaint(AppPaint.paintType.ERASE);
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
        AppLog.d(
            "c#" + 
            String.valueOf(mCircles.size()) +
            " p#" +
            String.valueOf(mCirclePointer.size())
        );
        
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
                if (circle.hasStartPosition()) {
                    switch(this.orderDisplayStyle) {
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
    
    public void drawPlayerOrderBadge(CircleArea circle, int startPosition) {
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
    
    public void pickPlayerOrder(final boolean pickedWinner) {
        if (pickedWinner)
            return;

        // I'm sure there is a more elegant way to do this
        Random rand = new Random();
        int ri = rand.nextInt((mCirclePointer.size()));
        CircleArea ca = mCirclePointer.get(ri);
        if(null==ca) {
            AppLog.w("Picked a pointer with no circle");
        }
        ca.setFirstPlayer(true);
        ca.setStartPosition(1);

        // assign remaining startPosition values
        int remainingPositions = mCirclePointer.size();
        // we have already assigned #1
        while (remainingPositions > 1) {
            CircleArea position_circle;
            // find a circle with no assigned start position
            do {
                int randomIndex = rand.nextInt(mCirclePointer.size());
                position_circle = mCirclePointer.get(randomIndex);
            }
            while (position_circle.hasStartPosition());

            // assign a position
            position_circle.setStartPosition(remainingPositions);
            // reduce the number of available positions
            remainingPositions--;
        }
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

    public CircleArea getTouchedCircle(final int xTouch, final int yTouch) {
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
            AppLog.w("removePointer() called for a null deatination");
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
