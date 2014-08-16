package net.chizography.droid.whosfirst;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.*;
import android.util.*;

public class CircleBrush extends Paint {
	public enum brushType {
		DEFAULT,
		ERASE,
		WINNER,
		BORDER_WINNER,
		DEBUGGING
	}
	private void init() {
		// a default brush
		this.setColor(Color.parseColor("#ffebe4bf"));
		this.setAlpha(100);
        this.setStrokeWidth(35);
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
				break;
				
			case BORDER_WINNER:
				this.setBrushType(brushType.DEFAULT);
				this.setStyle(Paint.Style.STROKE);
				this.setAlpha(255);
				this.setStrokeWidth(25);
				break;

			case DEBUGGING:
				this.setColor(Color.GRAY);
				break;
		}
	}
}
