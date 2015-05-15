package com.mickledeals.views;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by Nicky on 1/4/2015.
 */
public class NotifyingScrollView extends ScrollView {
    private float mInitialY;

    private boolean mCanScrollTop = true;
    private boolean mCanScrollBottom = true;

    public interface OnScrollChangedListener {
        void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt);
    }

    private OnScrollChangedListener mScrollChangedListener;

    public NotifyingScrollView(Context context) {
        super(context);
    }

    public NotifyingScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NotifyingScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mScrollChangedListener != null) {
            mScrollChangedListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }

    public void setOnScrollChangedListener(OnScrollChangedListener listener) {
        mScrollChangedListener = listener;
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        final int action = e.getAction();
        float y = e.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mInitialY = y;
                mCanScrollTop = ViewCompat.canScrollVertically(this, -1);
                mCanScrollBottom = ViewCompat.canScrollVertically(this, 1);
                break;
            case MotionEvent.ACTION_MOVE:
                if ((!mCanScrollTop && y > mInitialY) || (!mCanScrollBottom && y < mInitialY)) {
                    return false;
                }
                break;
        }
        return super.onTouchEvent(e);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        final int action = e.getAction();
        float y = e.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mInitialY = y;
                mCanScrollTop = ViewCompat.canScrollVertically(this, -1);
                mCanScrollBottom = ViewCompat.canScrollVertically(this, 1);
                break;
            case MotionEvent.ACTION_MOVE:
                if ((!mCanScrollTop && y > mInitialY) || (!mCanScrollBottom && y < mInitialY)) {
                    return false;
                }
                break;
        }
        return super.onInterceptTouchEvent(e);
    }

}
