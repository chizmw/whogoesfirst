package net.chizography.droid.whogoesfirst;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.*;

public class CircleBrush extends Paint {
	public enum brushType {
		DEFAULT,
		ERASE,
		DEBUGGING
	}
	private void init() {
		// a default brush
		this.setColor(Color.argb(222,82,4,79));
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

			case DEBUGGING:
				this.setColor(Color.GRAY);
				break;
		}
	}
}
