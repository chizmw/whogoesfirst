package net.chizography.droid.whosfirst;

import android.annotation.*;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import java.util.*;
import net.chizography.droid.whosfirst.*;
import android.os.*;

public class CirclesDrawingView extends View implements OnTouchListener {
    private static final String TAG = "CirclesDrawingView";

    private Rect mMeasuredRect;
	
	// chisel's debugging
	private boolean debugEnabled = false;

    /** Paint to draw circles */
    private CircleBrush mCirclePaint, mErasePaint, mDebugPaint, mWinnerPaint;
    private static final int CIRCLES_LIMIT = 28;

    /** All available circles */
    private HashSet<CircleArea> mCircles = new HashSet<CircleArea>(CIRCLES_LIMIT);
    private SparseArray<CircleArea> mCirclePointer = new SparseArray<CircleArea>(CIRCLES_LIMIT);
    
	private Context ctx;
	private CircleCountdown countdownTimer;
	private Canvas canvas;
	private boolean preventNewCircles;
	private boolean pickedWinner;

    public CirclesDrawingView(final Context ct) {
        super(ct);
        init(ct);
		simpleToast("c1");
    }

    public CirclesDrawingView(final Context ct, final AttributeSet attrs) {
        super(ct, attrs);
        init(ct);
    }
	
	public void simpleToast(String s) {
		Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
	}
	
	public boolean onTouch(View arg0, MotionEvent evt) {
		if (countdownTimer == null && getTouchedCircleCount() > 1) {
			countdownTimer = new CircleCountdown(this, 3);
		}
		return false;
	}
	
	private TextView getTextView(int id) {
		TextView tv = (TextView) ((Activity)getContext()).findViewById(id);
		if (null == tv) {
			return null;
		}

		return tv;
	}
	
	private TextView getStartHintView() {
		return getTextView(R.id.txtToStart);
	}
	
	private TextView getTimerView() {
		TextView tvTimer = (TextView) ((Activity)getContext()).findViewById(R.id.txtTimer);
		if (null == tvTimer) {
			simpleToast("set is null");
			return null;
		}
		
		return tvTimer;
	}
	
	private void drawCircleBorder(CircleArea ca, CircleBrush cb) {
		float borderRadius =
			ca.getRadius()
			+ cb.getStrokeWidth()
			- 10;
		canvas.drawCircle(ca.getCenterX(), ca.getCenterY(), borderRadius, cb);
	}
	
	public void setStartHintVisible(boolean visible) {
		TextView tvStartHint = getStartHintView();

		tvStartHint.setVisibility(
			visible ? VISIBLE : INVISIBLE
		);

		invalidate();
	}
	
	public void setTimerTextVisible(boolean visible) {
		TextView tvTimer = getTimerView();
				
		visibilityToast(tvTimer.getVisibility());
		tvTimer.setVisibility(
			visible ? VISIBLE : INVISIBLE
		);
		
		visibilityToast(tvTimer.getVisibility());
		
		invalidate();
	}
	
	public void setTimerText(int value) {
		TextView tv = getTimerView();
		
		// negative values are just stupid, and less than zero
		if (value < 0) {
			value = 9;
		}
		// don't allow double digit values
		else if (value > 9) {
			value = 9;
		}
		
		tv.setText(Integer.toString(value));
	}
	
	public int getTouchedCircleCount() {
		return mCirclePointer.size();
	}
	
    private void init(final Context ct) {
		// make life easier by storing the incoming context
		ctx = ct;
		
		preventNewCircles = false;
		pickedWinner = false;

		// make sure these are empty when we initialise
		// (hacking round my inability to remove them as we go)
		mCircles = new HashSet<CircleArea>(CIRCLES_LIMIT);
		mCirclePointer = new SparseArray<CircleArea>(CIRCLES_LIMIT);
		
		// visible paint
        mCirclePaint = new CircleBrush(CircleBrush.brushType.DEFAULT);
        
		// normal transparent paint
		mErasePaint = new CircleBrush(CircleBrush.brushType.ERASE);
		
		mWinnerPaint = new CircleBrush(CircleBrush.brushType.WINNER);
		
		// debugging paint
		mDebugPaint = new CircleBrush(CircleBrush.brushType.DEBUGGING);
		
		setOnTouchListener(this);
    }

    @Override
    public void onDraw(final Canvas canv) {
		canvas = canv;
		
		// this is another thing that needs refactoring
		if (countdownTimer==null && !pickedWinner && mCircles.size()==0) {
			setStartHintVisible(true);
		}
		else {
			setStartHintVisible(false);
		}
		
		// show/hide timer message area
		TextView tvTimer = getTextView(R.id.txtTimer);
		if (null == countdownTimer) {
			tvTimer.setVisibility(INVISIBLE);
		}
		
		// automatically process timer text changes
		TextWatcher twl = new TextWatcher(){
			public void onTextChanged(CharSequence s, int a, int b, int c) {}
			public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
			public void afterTextChanged(Editable e) {
				int value = Integer.parseInt(e.toString());
				if (value == 0) {
					// hide the timervalue, abort the countdown
					setOnTouchListener(null);
					abortCountdown();
					pickPlayerOrder();
				}
			}
		};
		tvTimer.addTextChangedListener(twl);

        for (CircleArea circle : mCircles) {
			CircleBrush p;
			if (circle.isFirstPlayer()) {
				p = mWinnerPaint;
				CircleBrush cb = new CircleBrush(CircleBrush.brushType.BORDER_WINNER);
				drawCircleBorder(circle, cb);
			}
			else if (circle.isNeedsWiping()) {
				p = debugEnabled ? mDebugPaint : mErasePaint;
			}
			else {
				p = mCirclePaint;
				if (pickedWinner) {
					p.setAlpha(30);
				}
			}
     	    canvas.drawCircle(circle.getCenterX(), circle.getCenterY(), circle.getRadius(), p);
            
            if (pickedWinner) {
                if (circle.hasStartPosition()) {
                    drawPlayerOrderNumber(circle, circle.getStartPosition());
                }
            }
        }
    }
    
    private void drawPlayerOrderNumber(CircleArea circle, int startPosition) {
        Paint paint;
        Paint circlePaint;
        String text = Integer.toString(startPosition);

        paint = new CircleBrush(CircleBrush.brushType.START_POSITION_TEXT);
        circlePaint = new CircleBrush(CircleBrush.brushType.START_POSITION_CIRCLE);

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
	
	private void pickPlayerOrder() {
		if (pickedWinner)
			return;
			
		preventNewCircles = true;
		
		// I'm sure there is a more elegant way to do this
		Random rand = new Random();
		int ri = rand.nextInt((mCirclePointer.size()));
		CircleArea ca = mCirclePointer.get(ri);
		ca.setFirstPlayer(true);
        ca.setStartPosition(1);
		pickedWinner = true;
        
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
		
		// after a short delay reset to go agaim
		new CountDownTimer(5000, 5000) {
			public void onTick(long millisUntilFinished) {
				// nothing on ticks
			}

			public void onFinish() {
				init(ctx);
			}
		}.start();
		
		
		invalidate();
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

	
    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        boolean handled = false;
		
		// sometimes we don't want any new circles
		if (preventNewCircles) {
			return super.onTouchEvent(event);
		}

        CircleArea touchedCircle;
        int xTouch;
        int yTouch;
        int pointerId;
        int actionIndex = event.getActionIndex();

        // get touch event coordinates and make transparent circle from it
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // it's the first pointer, so clear all existing pointers data
                clearCirclePointers();

                xTouch = (int) event.getX(0);
                yTouch = (int) event.getY(0);

                // check if we've touched inside some circle
                touchedCircle = obtainTouchedCircle(xTouch, yTouch);
                touchedCircle.setCenterX(xTouch);
                touchedCircle.setCenterY(yTouch);
                mCirclePointer.put(event.getPointerId(0), touchedCircle);
				
				// if a previously touched and released circle is retouched
				touchedCircle.setNeedsWiping(false);

                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                // It secondary pointers, so obtain their ids and check circles
                pointerId = event.getPointerId(actionIndex);

                xTouch = (int) event.getX(actionIndex);
                yTouch = (int) event.getY(actionIndex);

                // check if we've touched inside some circle
                touchedCircle = obtainTouchedCircle(xTouch, yTouch);

                mCirclePointer.put(pointerId, touchedCircle);
                touchedCircle.setCenterX(xTouch);
                touchedCircle.setCenterY(xTouch);
				// if a previously touched and released circle is retouched
				touchedCircle.setNeedsWiping(false);
				// new pointer, argh, new countdown needed
				abortCountdown();
				
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_MOVE:
				touchedCircle = scanForTouchedCircle(event);
				if (touchedCircle != null) {
					touchedCircle.setNeedsWiping(false);
				}
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_UP:
				// the finger that started the "gesture"
				touchedCircle = scanForTouchedCircle(event);
				touchedCircle.setNeedsWiping(true);
				if(mCirclePointer.size()==1){
					clearCirclePointers();
					abortCountdown();
				}
				else{
					Toast.makeText(this.getContext(),"non final Up",Toast.LENGTH_SHORT).show();
				}
                //clearCirclePointers();
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_POINTER_UP:
				// one of the "other" fingers
                pointerId = event.getPointerId(actionIndex);
				CircleArea c = mCirclePointer.get(pointerId);
				abortCountdown();
				if (null != c) {
					mCirclePointer.get(pointerId).setNeedsWiping(true);
                	mCirclePointer.remove(pointerId);
				}
				else {
					Toast.makeText(this.getContext(),"APU c null",Toast.LENGTH_SHORT).show();
				}
				invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_CANCEL:
                handled = true;
                break;

            default:
                // do nothing
                break;
        }

        return super.onTouchEvent(event) || handled;
    }
	
	private void abortCountdown() {
		setTimerTextVisible(false);
		if(countdownTimer!=null){
			countdownTimer.abortCountdown();
			countdownTimer = null;
		}
	}
	
	private void visibilityToast(int visibility) {
		if (!debugEnabled) { return; }
		switch (visibility) {
			case View.INVISIBLE:
				simpleToast("=INVISIBLE");
				break;

			case View.GONE:
				simpleToast("=GONE");
				break;

			case View.VISIBLE:
				simpleToast("=VISIBLE");
				break;

			default:
				simpleToast("=WTAF!");
		}
	}

    private void clearCirclePointers() {
        mCirclePointer.clear();
		mCircles.clear();
    }

    private CircleArea obtainTouchedCircle(final int xTouch, final int yTouch) {
        CircleArea touchedCircle = getTouchedCircle(xTouch, yTouch);

        if (null == touchedCircle) {
            touchedCircle = new CircleArea(xTouch, yTouch, 120);

            if (mCircles.size() == CIRCLES_LIMIT) {
				if (this.debugEnabled)
                	Log.d(TAG, "Clear all circles, size is " + mCircles.size());

                // remove first circle
				// I'm sure this removes all circles...
                mCircles.clear();
            }

			if (this.debugEnabled)
            	Log.w(TAG, "Added circle " + touchedCircle);
            mCircles.add(touchedCircle);
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

    @SuppressLint("DrawAllocation")
	@Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMeasuredRect = new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }
}
