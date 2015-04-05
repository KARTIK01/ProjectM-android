
package com.mickledeals.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.mickledeals.R;
import com.mickledeals.utils.Utils;

public class PagerIndicator extends View {
    private static int SPACING;
    private static int RADIUS;
    private int mSize = 0;
    private int mPosition = 0;
    private static final Paint mOnPaint = new Paint(Paint.ANTI_ALIAS_FLAG);;
    private static final Paint mOffPaint = new Paint(Paint.ANTI_ALIAS_FLAG);;

    public PagerIndicator(Context context) {
        super(context);
        init();
    }

    public PagerIndicator(Context c, int size) {
        this(c);
        mSize = size;
    }

    public PagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    private void init() {
        mOnPaint.setColor(getResources().getColor(R.color.colorPrimary2));
        mOffPaint.setColor(0xFF999999);
        SPACING = Utils.getPixelsFromDip(10f, getResources());
        RADIUS = Utils.getPixelsFromDip(3f, getResources());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < mSize; ++i) {
            if (i == mPosition) {
                canvas.drawCircle(i * (2 * RADIUS + SPACING) + RADIUS, RADIUS, RADIUS, mOnPaint);
            } else {
                canvas.drawCircle(i * (2 * RADIUS + SPACING) + RADIUS, RADIUS, RADIUS, mOffPaint);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mSize * (2 * RADIUS + SPACING) - SPACING, 2 * RADIUS);
    }

    public int getPosition() {
    	return mPosition;
    }

	public void setPosition(int id) {
        mPosition = id;
    }

    public void setSize(int size) {
        mSize = size;
    }

}
