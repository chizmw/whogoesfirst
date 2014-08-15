package net.chizography.droid.whogoesfirst;

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
import net.chizography.droid.whogoesfirst.*;
import android.os.*;

public class CirclesDrawingView extends View implements OnTouchListener {
    private static final String TAG = "CirclesDrawingView";

    /** Main bitmap */
    private Bitmap mBitmap = null;

    private Rect mMeasuredRect;
	
	// chisel's debugging
	private boolean debugEnabled = false;

    /** Paint to draw circles */
    private CircleBrush mCirclePaint, mErasePaint, mDebugPaint, mWinnerPaint;
    private static final int CIRCLES_LIMIT = 8;

    /** All available circles */
    private HashSet<CircleArea> mCircles = new HashSet<CircleArea>(CIRCLES_LIMIT);
    private SparseArray<CircleArea> mCirclePointer = new SparseArray<CircleArea>(CIRCLES_LIMIT);
    
	private Context ctx;
	private CircleCountdown countdownTimer;
	private Canvas canvas;
	private boolean preventNewCircles;
	private boolean pickedWinner;

    /**
     * Default constructor
     *
     * @param ct {@link android.content.Context}
     */
    public CirclesDrawingView(final Context ct) {
        super(ct);
        init(ct);
		simpleToast("c1");
    }

    public CirclesDrawingView(final Context ct, final AttributeSet attrs) {
        super(ct, attrs);
        init(ct);
    }
/*
    public CirclesDrawingView(final Context ct, final AttributeSet attrs, final int defStyle) {
        super(ct, attrs, defStyle);
        init(ct);
    }*/
	
	public void simpleToast(String s) {
		Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
	}
	
	public boolean onTouch(View arg0, MotionEvent evt) {
		if (countdownTimer == null && getTouchedCircleCount() > 1) {
			//setStartHintVisible(false);
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
		
        // Generate bitmap used for background
        //mBitmap = BitmapFactory.decodeResource(ct.getResources(), R.drawable.felt_01);

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
        // background bitmap to cover all area
		canvas = canv;
        //canvas.drawBitmap(mBitmap, null, mMeasuredRect, null);
		
		// this is another thing that needs refactoring
		if (countdownTimer==null && !pickedWinner && mCircles.size()==0) {
			setStartHintVisible(true);
		}
		else {
			setStartHintVisible(false);
		}
		
		// show/hide debug message area
		TextView tvDebug = getTextView(R.id.txtDebugMsg);
		//(TextView) ((Activity)getContext()).findViewById(R.id.txtDebugMsg);
		tvDebug.setVisibility(
			debugEnabled ? VISIBLE : INVISIBLE
		);
		
		// show/hide timer message area
		TextView tvTimer = getTextView(R.id.txtTimer);
		//(TextView) ((Activity)getContext()).findViewById(R.id.txtTimer);
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
					displayPlayerOrder();
				}
			}
		};
		tvTimer.addTextChangedListener(twl);

        for (CircleArea circle : mCircles) {
			CircleBrush p;
			if (circle.first_player) {
				p = mWinnerPaint;
			}
			else if (circle.needs_wiping) {
				p = debugEnabled ? mDebugPaint : mErasePaint;
			}
			else {
				p = mCirclePaint;
			}
     	    canvas.drawCircle(circle.centerX, circle.centerY, circle.radius, p);
			
			// this isn't working well; regardless of the order
			// I don't seem to be able to delete the pointer and the circle
			// without causing the app to crash (null pointer?)
			// until I can resolve this the circles stay (invisibly)
			// and we abuse needs_wiping to choose the paint to use
			/*
			if (circle.needs_wiping){
				// make sure we don't try to delete ourself multiple times
				// if there is a backlog of events
				if(mCircles.contains(circle)) {
					int idx = mCirclePointer.indexOfValue(circle);
					//mCircles.remove(circle);
					if (idx < 0) {
						//Toast.makeText(this.getContext(),"no idx",Toast.LENGTH_SHORT).show();
					}
					else {
						//mCirclePointer.delete(idx);
					}
				}
			}*/
        }
    }
	
	private void pickPlayerOrder() {
		if (pickedWinner)
			return;
			
		preventNewCircles = true;
		
		// I'm sure there is a more elegant way to do this
		Random rand = new Random();
		int ri = rand.nextInt((mCirclePointer.size()));
		CircleArea ca = mCirclePointer.get(ri);
		ca.first_player = true;
		pickedWinner = true;
		
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
	
	private void displayPlayerOrder() {
		
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
				touchedCircle.centerX = xTouch;
				touchedCircle.centerY = yTouch;
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
                touchedCircle.centerX = xTouch;
                touchedCircle.centerY = yTouch;
                mCirclePointer.put(event.getPointerId(0), touchedCircle);
				
				// if a previously touched and released circle is retouched
				touchedCircle.needs_wiping = false;

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
                touchedCircle.centerX = xTouch;
                touchedCircle.centerY = yTouch;
				// if a previously touched and released circle is retouched
				touchedCircle.needs_wiping = false;
				// new pointer, argh, new countdown needed
				abortCountdown();
				
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_MOVE:
				touchedCircle = scanForTouchedCircle(event);
				if (touchedCircle != null) {
					touchedCircle.needs_wiping = false;
				}
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_UP:
				// the finger that started the "gesture"
				touchedCircle = scanForTouchedCircle(event);
				touchedCircle.needs_wiping=true;
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
					mCirclePointer.get(pointerId).needs_wiping=true;
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

    /**
     * Clears all CircleArea - pointer id relations
     */
    private void clearCirclePointers() {
        mCirclePointer.clear();
		mCircles.clear();
    }

    /**
     * Search and creates new (if needed) circle based on touch area
     *
     * @param xTouch int x of touch
     * @param yTouch int y of touch
     *
     * @return obtained {@link CircleArea}
     */
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

    /**
     * Determines touched circle
     *
     * @param xTouch int x touch coordinate
     * @param yTouch int y touch coordinate
     *
     * @return {@link CircleArea} touched circle or null if no circle has been touched
     */
    private CircleArea getTouchedCircle(final int xTouch, final int yTouch) {
        CircleArea touched = null;

        for (CircleArea circle : mCircles) {
            if ((circle.centerX - xTouch) * (circle.centerX - xTouch) + (circle.centerY - yTouch) * (circle.centerY - yTouch) <= circle.radius * circle.radius) {
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
