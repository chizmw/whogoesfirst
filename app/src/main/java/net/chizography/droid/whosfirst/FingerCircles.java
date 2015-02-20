package net.chizography.droid.whosfirst;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.util.SparseArray;
import android.view.MotionEvent;
import java.util.HashSet;
import java.util.Random;
import android.util.Log;

public class FingerCircles {
    
    private Canvas canvas;
    
    /** Paint to draw circles */
    private AppPaint mCirclePaint, mErasePaint, mWinnerPaint, paintWinnerCircleBorder;
    private static final int CIRCLES_LIMIT = 28;

    /** All available circles */
    private HashSet<CircleArea> mCircles = new HashSet<CircleArea>(CIRCLES_LIMIT);
    private SparseArray<CircleArea> mCirclePointer = new SparseArray<CircleArea>(CIRCLES_LIMIT);
    
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
                    drawPlayerOrderNumber(circle, circle.getStartPosition());
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

        canvas.drawCircle(
            circle.getCenterX(),
            circle.getCenterY() - circle.getRadius(),
            bounds.height(),
            circlePaint
        );
        canvas.drawText(
            text,
            circle.getCenterX(),
            circle.getCenterY() - circle.getRadius() + (paint.getTextSize()/3),
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

            if (mCircles.size() == CIRCLES_LIMIT) {
                mCircles.clear();
            }

            mCircles.add(touchedCircle);
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
    }
    
    public CircleArea getPointer (final int id) {
        return mCirclePointer.get(id);
    }
    
    public void removePointer (final int id) {
        // remove the referenced circle
        CircleArea ca = mCirclePointer.get(id);
        mCircles.remove(ca);
        // remove the reference
        mCirclePointer.remove(id);
    }
}
