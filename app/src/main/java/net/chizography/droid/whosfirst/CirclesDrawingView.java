package net.chizography.droid.whosfirst;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.widget.Toast;
import android.content.res.*;

import java.util.Locale;

public class CirclesDrawingView extends View implements OnTouchListener {
    private FingerCircles fingerCircles;
    
    private SharedPreferences prefs;
    private GestureDetector gestureDetector;
	private boolean debugEnabled = false;
    private boolean showPlayerOrder = false;
    private String playerOrderStyle;
    // keep track of when we initially load the pref
    // so we can skip overwriting it on later runs
    private boolean loadedOrderPref = false;
    private boolean showSwipeHint = true;

	private Context _context;
	private CircleCountdown countdownTimer;
	private Canvas canvas;
	private boolean preventNewCircles;
	private boolean pickedWinner;

    private final TextWatcher textWatcher = new TextWatcher(){
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

    public CirclesDrawingView(final Context ct, final AttributeSet attrs) {
        super(ct, attrs);
        init(ct);
    }
    
    /* 
     *  Previous changes (in AndroidManifest.xml) preserved
     *  the chooser mode on orientation change.
     *
     *  This extra piece of code resets circles and timer.
     *
     *  This stops things lookkng rather strange :)
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (fingerCircles != null) {
            fingerCircles.clearCirclePointers();
        }
        
        this.abortCountdown();
    }
	
	private void simpleToast(String s) {
		Toast.makeText(_context, s, Toast.LENGTH_SHORT).show();
	}
	
	public boolean onTouch(View arg0, MotionEvent evt) {
		if (countdownTimer == null && fingerCircles.getTouchedCircleCount() > 1) {
            int timerStartAt;
            try {
                timerStartAt = Integer.parseInt(
                    prefs.getString(
                       _context.getString(R.string.prefs_StartCountdownAt_key),
                       "3"
                    )
                );
                countdownTimer = new CircleCountdown(
                    this,
                    timerStartAt,
                    fingerCircles.getTouchedCircleCount()
                );
            }
            catch (Exception e) {
                simpleToast(e.getMessage());
            }
            
		}
        return gestureDetector.onTouchEvent(evt);
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
	
	
	private void setStartHintVisible(boolean visible) {
		TextView tvStartHint = getStartHintView();

		tvStartHint.setVisibility(
			visible ? VISIBLE : INVISIBLE
		);

		invalidate();
	}
	
	public void setTimerTextVisible(boolean visible) {
		TextView tvTimer = getTimerView();

        if (tvTimer != null) {
            tvTimer.setVisibility(
                visible ? VISIBLE : INVISIBLE
            );
        }

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

        if (tv != null) {
            tv.setText(String.format(Locale.ENGLISH, "%d", value));
        }
    }
	
	
    private boolean isStartHintState() {
        return (countdownTimer==null && !pickedWinner && fingerCircles.getTouchedCircleCount()<=1);
    }
	
    private void init(final Context ct) {
		// make life easier by storing the incoming context
		_context = ct;
        
        prefs = PreferenceManager.getDefaultSharedPreferences(_context);
        
        debugEnabled = prefs.getBoolean(_context.getString(R.string.prefs_ShowDebugOutput_key), false);
        showSwipeHint = prefs.getBoolean(_context.getString(R.string.prefs_ShowSwipeHint_key), true);
        playerOrderStyle = prefs.getString(_context.getString(R.string.prefs_ShowPlayerOrderAs_key), "1");

		// only reload this value from prefs if it's unset; preserves current choice
        if (!loadedOrderPref){
            showPlayerOrder = prefs.getBoolean(_context.getString(R.string.prefs_ShowPlayerOrder_key), false);
            loadedOrderPref = true;
        }
		preventNewCircles = false;
		pickedWinner = false;
        
        // make sure we cleanup any lingering circles
        if (fingerCircles != null) {
            fingerCircles.clearCirclePointers();
        }
        
        // keep an ear out for touchimg
		setOnTouchListener(this);
        // also check for (flick) gestures
        GestureListener gl = getGestureListener();
        gestureDetector = new GestureDetector(_context, gl);
    }

    private GestureListener getGestureListener() {
        return new GestureListener(){
                @Override
                public void onSwipeLeft() {
                    if (debugEnabled)
                        simpleToast("onSwipeLeft");
                    showPlayerOrder = !showPlayerOrder;
                    fingerCircles.clearCirclePointers();
                    setSwipeHintEnabled(false);
                }

                @Override
                public void onSwipeRight() {
                    if (debugEnabled)
                        simpleToast("onSwipeRight");
                    showPlayerOrder = !showPlayerOrder;
                    fingerCircles.clearCirclePointers();
                    setSwipeHintEnabled(false);
                }

                @Override
                public void onSwipeTop() {
                    fingerCircles.clearCirclePointers();
                    // show preferences
                    final Intent intent = new Intent(_context, SettingsActivity.class);
                    _context.startActivity(intent);
                }

                @Override
                public void onSwipeBottom() {
                    // just tidy up
                    fingerCircles.clearCirclePointers();
                }
            };
    }

    private void drawChooserModeImage() {
        // make sure we don't try to draw to a null canvas
        if (canvas==null) {
            return;
        }
        int length = 100;
        int left   = 50;
        int top    = 25;
        
        Drawable dl;
        if (showPlayerOrder) {
            //noinspection deprecation
            dl = getResources().getDrawable(R.drawable.meeple_order);
        }
        else {
            //noinspection deprecation
            dl = getResources().getDrawable(R.drawable.meeple);
        }

        if (dl != null) {
            dl.setBounds(
                left,
                top,
                left + length,
                top + length
            );
        }
        if (dl != null) {
            dl.setAlpha(60);
        }
        if (dl != null) {
            dl.draw(canvas);
        }
    }
    
    private void drawSwipeHint() {
        // make sure we don't try to draw to a null canvas
        if (canvas==null || !showSwipeHint) {
            return;
        }
        
        // the location of these is aligned by eye;
        // the fingertip should not appear to move, only the arrows
        int length = 150;
        int leftX = 170;
        int leftY = 0;
        int rightX = 230;
        int rightY = 0;
        int alpha = 60;
        
        try {
            Drawable dl,dr;
           
            if (showPlayerOrder && debugEnabled) {
                //noinspection deprecation
                dl = getResources().getDrawable(R.drawable.flick_left_512);
                if (dl != null) {
                    dl.setBounds(
                        leftX,
                        leftY,
                        leftX + length,
                        leftY + length
                    );
                }
                if (dl != null) {
                    dl.setAlpha(alpha);
                }
                if (dl != null) {
                    dl.draw(canvas);
                }
            }
            else {
                //noinspection deprecation
                dr = getResources().getDrawable(R.drawable.flick_right_512);
                if (dr != null) {
                    dr.setBounds(
                        rightX,
                        rightY,
                        rightX + length,
                        rightY + length
                    );
                }
                if (dr != null) {
                    dr.setAlpha(alpha);
                }
                if (dr != null) {
                    dr.draw(canvas);
                }
            }
        }
        catch(Exception e) {simpleToast(e.getMessage());}
    }
    
    private void setSwipeHintEnabled(@SuppressWarnings("SameParameterValue") boolean visible) {
       SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(_context).edit();
       editor.putBoolean(_context.getString(R.string.prefs_ShowSwipeHint_key), visible);
       editor.apply();
    }

    private void pickPlayerOrder() {
        preventNewCircles = true;
        fingerCircles.pickPlayerOrder(pickedWinner);
        pickedWinner = true;

        // after a short delay reset to go again
        new CountDownTimer(5000, 5000) {
            public void onTick(long millisUntilFinished) {
                // nothing on ticks
            }

            public void onFinish() {
                init(_context);
            }
        }.start();
        
        invalidate();
    }
    
    @Override
    public void onDraw(final Canvas canv) {
		canvas = canv;
        if (null == fingerCircles) {
            fingerCircles = new FingerCircles(canvas);
        }
        
        // somewhat hacky...
        switch (Integer.parseInt(playerOrderStyle)) {
            case 2:
                fingerCircles.setOrderDisplayStyle(FingerCircles.OrderStyle.BUTTON_BUBBLE);
                break;
            case 1:
            default:
                fingerCircles.setOrderDisplayStyle(FingerCircles.OrderStyle.VALUE_IN_CIRCLE);
        }
		
		// this is another thing that needs refactoring
        if (isStartHintState()) {
			setStartHintVisible(true);
		}
		else {
			setStartHintVisible(false);
		}
        
        drawChooserModeImage();
        // draw the swipe hint
		drawSwipeHint();
        
		// show/hide timer message area
		TextView tvTimer = getTextView(R.id.txtTimer);
		if (null == countdownTimer) {
            if (tvTimer != null) {
                tvTimer.setVisibility(INVISIBLE);
            }
        }
		
		// automatically process timer text changes
		TextWatcher twl = textWatcher;
        if (tvTimer != null) {
            tvTimer.addTextChangedListener(twl);
        }

        fingerCircles.renderCircles(pickedWinner,showPlayerOrder);
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
                fingerCircles.clearCirclePointers();

                xTouch = (int) event.getX(0);
                yTouch = (int) event.getY(0);

                // check if we've touched inside some circle
                touchedCircle = fingerCircles.obtainTouchedCircle(xTouch, yTouch);
                touchedCircle.setCenterX(xTouch);
                touchedCircle.setCenterY(yTouch);
                fingerCircles.putPointer(event.getPointerId(0), touchedCircle);

                //noinspection ConstantConditions
                if (touchedCircle==null) {
                    AppLog.e("ACTION_DOWN: null touchedCircle");
                }
                else {
                    AppLog.d("ACTION_DOWN: " + touchedCircle.toString());
                }

                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                // It secondary pointers, so obtain their ids and check circles
                pointerId = event.getPointerId(actionIndex);

                xTouch = (int) event.getX(actionIndex);
                yTouch = (int) event.getY(actionIndex);

                // check if we've touched inside some circle
                touchedCircle = fingerCircles.obtainTouchedCircle(xTouch, yTouch);
                if (touchedCircle==null) {
                    AppLog.e("ACTION_POINTER_DOWN: null touchedCircle");
                }
                else {
                    AppLog.d("ACTION_POINTER_DOWN: " + touchedCircle.toString());
                }

                //mCirclePointer.put(pointerId, touchedCircle);
                fingerCircles.putPointer(pointerId, touchedCircle);
                if (touchedCircle != null) {
                    touchedCircle.setCenterX(xTouch);
                }
                if (touchedCircle != null) {
                    touchedCircle.setCenterY(yTouch);
                }

                // new pointer, argh, new countdown needed
				abortCountdown();
				
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_MOVE:
				touchedCircle = fingerCircles.scanForTouchedCircle(event);
                if (touchedCircle==null) {
                    AppLog.e("ACTION_MOVE: null touchedCircle");
                }
                else {
                    AppLog.d("ACTION_MOVE: " + touchedCircle.toString());
                }
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_UP:
				// the finger that started the "gesture"
				touchedCircle = fingerCircles.scanForTouchedCircle(event);
                if (touchedCircle==null){
                    AppLog.e("ACTION_UP: null touchedCircle");
                }
                else {
                    AppLog.d("ACTION_UP: " + touchedCircle.toString());
                }
                
				if(fingerCircles.getTouchedCircleCount()==1){
					fingerCircles.clearCirclePointers();
					abortCountdown();
				}
				else{
                    AppLog.e("ACTION_UP: non-final up");
                    fingerCircles.clearCirclePointers();
                    abortCountdown();
				}
                
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_POINTER_UP:
				// one of the "other" fingers
                pointerId = event.getPointerId(actionIndex);
				CircleArea c = fingerCircles.getPointer(actionIndex);
                
				abortCountdown();
				if (null != c) {
                    AppLog.d("ACTION_POINTER_UP: " + c.toString());
                    fingerCircles.removePointer(pointerId);
				}
				else {
                    AppLog.e("ACTION_POINTER_UP: nothing matched");
                    fingerCircles.clearCirclePointers();
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

    
    @SuppressLint("DrawAllocation")
	@Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }
}
