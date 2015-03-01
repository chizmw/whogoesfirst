package net.chizography.droid.whosfirst;

import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.Paint;

public class AppPaint extends Paint {

    static enum paintType {
        DEFAULT,
        ERASE,
        WINNER_CIRCLE_FILL,
        WINNER_CIRCLE_BORDER,
        START_POSITION_CIRCLE,
        START_POSITION_TEXT,
        PLAYER_ORDER_CIRCLE_TEXT,
        FIDGET_PLAYER_CIRCLE_FILL,
        DEBUGGING
    }

    AppPaint(paintType bt) {
        super();
        this.setBrushType(paintType.DEFAULT);
        this.setBrushType(bt);
    }
    
    private void applyBlurMask() {
        this.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));
    }
    
    private void removeBlurMask() {
        this.setMaskFilter(null); 
    }

    public void setBrushType (paintType bt) {
        switch(bt) {
            case DEFAULT:
                this.setColor(Color.parseColor("#ffebe4bf"));
                this.setAlpha(100);
                int circleBrushSize = 35;
                this.setStrokeWidth(circleBrushSize);
                this.setStyle(Paint.Style.FILL);
                this.applyBlurMask();
                break;

            case WINNER_CIRCLE_FILL:
                this.setColor(Color.parseColor("#ff392b2f"));
                this.setAlpha(255);
                this.applyBlurMask();
                break;

            case WINNER_CIRCLE_BORDER:
                this.setBrushType(paintType.DEFAULT);
                this.setStyle(Paint.Style.STROKE);
                this.setAlpha(255);
                int borderBrushSize = 25;
                this.setStrokeWidth(borderBrushSize);
                this.applyBlurMask();
                break;

            case START_POSITION_CIRCLE:
                this.setColor(Color.RED);
                this.setAntiAlias(true);
                this.setAlpha(255);
                this.applyBlurMask();
                break;

            case START_POSITION_TEXT:
                this.setColor(Color.WHITE);
                this.setTextSize(60f);
                this.setAntiAlias(true);
                this.setTextAlign(Paint.Align.CENTER);
                this.setAlpha(255);
                this.setFakeBoldText(true);
                this.removeBlurMask();
                break;

            case PLAYER_ORDER_CIRCLE_TEXT:
                this.setBrushType(paintType.DEFAULT);
                this.setAlpha(255);
                this.setTextAlign(Paint.Align.CENTER);
                this.setFakeBoldText(true);
                this.setMaskFilter(new BlurMaskFilter(2, BlurMaskFilter.Blur.NORMAL));
                break;
                
            case FIDGET_PLAYER_CIRCLE_FILL:
                this.setColor(Color.RED);
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
