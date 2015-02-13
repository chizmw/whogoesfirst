package net.chizography.droid.whosfirst;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.*;
import android.util.*;
import android.content.res.*;

public class CircleBrush extends Paint {
	
	int circleBrushSize = 35;
	int borderBrushSize = 25;
	
	public enum brushType {
		DEFAULT,
		ERASE,
		WINNER,
		BORDER_WINNER,
        START_POSITION_CIRCLE,
        START_POSITION_TEXT,
		DEBUGGING
	}
	
	CircleBrush() {
		super();
		this.set(new AppPaint(AppPaint.paintType.DEFAULT));
	}
	
	CircleBrush(brushType bt) {
        AppPaint.paintType replacementPaint;
        switch(bt) {
            case DEBUGGING:
                replacementPaint = AppPaint.paintType.DEBUGGING;
                break;
            case ERASE:
                replacementPaint = AppPaint.paintType.ERASE;
                break;
            case WINNER:
                replacementPaint = AppPaint.paintType.WINNER_CIRCLE_FILL;
                break;
            case BORDER_WINNER:
                replacementPaint = AppPaint.paintType.WINNER_CIRCLE_BORDER;
                break;
            case START_POSITION_CIRCLE:
                replacementPaint = AppPaint.paintType.START_POSITION_CIRCLE;
                break;
            case START_POSITION_TEXT:
                replacementPaint = AppPaint.paintType.START_POSITION_TEXT;
                break;
            default:
                replacementPaint = AppPaint.paintType.DEFAULT;
        }
        this.set(new AppPaint(replacementPaint));
	}
}
