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
	private void init() {
		// a default brush
		this.setColor(Color.parseColor("#ffebe4bf"));
		this.setAlpha(100);
        this.setStrokeWidth(circleBrushSize);
        this.setStyle(Paint.Style.FILL);
        this.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));  
	}
	
	CircleBrush() {
		super();
		init();
	}
	
	CircleBrush(brushType bt) {
		super();
		init();
		this.setBrushType(bt);
	}
	
	CircleBrush(int c) {
		super();
		init();
		this.setColor(c);
	}
	
	public void setBrushType (brushType bt) {
		switch(bt) {
			case DEFAULT:
				break;

			case ERASE:
				this.setColor(Color.TRANSPARENT);
				break;
				
			case WINNER:
				this.setColor(Color.parseColor("#ff392b2f"));
				this.setAlpha(255);
				this.setMaskFilter(new BlurMaskFilter(3, BlurMaskFilter.Blur.NORMAL));
				break;
				
			case BORDER_WINNER:
				this.setBrushType(brushType.DEFAULT);
				this.setStyle(Paint.Style.STROKE);
				this.setAlpha(255);
				this.setStrokeWidth(borderBrushSize);
				this.setMaskFilter(new BlurMaskFilter(3, BlurMaskFilter.Blur.NORMAL));
				break;
                
            case START_POSITION_CIRCLE:
                this.setColor(Color.RED);
                this.setAntiAlias(true);
                this.setAlpha(255);
                
                this.setMaskFilter(new BlurMaskFilter(3, BlurMaskFilter.Blur.NORMAL));
                break;
                
            case START_POSITION_TEXT:
                this.setColor(Color.WHITE);
                this.setTextSize(60f);
                this.setAntiAlias(true);
                this.setTextAlign(Paint.Align.CENTER);
                this.setMaskFilter(null);
                this.setAlpha(255);
                this.setFakeBoldText(true);
                break;

			case DEBUGGING:
				this.setColor(Color.GRAY);
				break;
		}
	}
}
