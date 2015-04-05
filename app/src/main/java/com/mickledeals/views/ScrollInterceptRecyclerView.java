package com.mickledeals.views;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Nicky on 12/26/2014.
 *
 * This class is for ViewPager to swipe to left when this scroll view
 * reaches far left
 */
public class ScrollInterceptRecyclerView extends RecyclerView {

    private float mInitialX;
    private boolean mCanScrollLeft = true;

    public ScrollInterceptRecyclerView(Context context) {
        super(context);
    }

    public ScrollInterceptRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollInterceptRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        final int action = e.getAction();
        float x = e.getX();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mInitialX = x;
                mCanScrollLeft = ViewCompat.canScrollHorizontally(this, -1);
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mCanScrollLeft && x > mInitialX) return false;
                break;
        }

        return super.onInterceptTouchEvent(e);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent e) {
//        final int action = e.getAction();
//        float x = e.getX();
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                mInitialX = x;
//                mCanScrollLeft = ViewCompat.canScrollHorizontally(this, -1);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                if (!mCanScrollLeft && x > mInitialX) return false;
//                break;
//        }
//
//        return super.onTouchEvent(e);
//    }


}
