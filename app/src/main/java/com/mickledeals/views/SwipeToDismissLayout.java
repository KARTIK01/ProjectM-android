package com.mickledeals.views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by Nicky on 12/26/2014.
 *
 * This class is for ViewPager to swipe to left when this scroll view
 * reaches far left
 */
public class SwipeToDismissLayout extends RelativeLayout {

    private int previousFingerPosition = 0;
    private int thisPosition = 0;
    private int defaultViewHeight;

    private boolean isClosing = false;
    private boolean isScrollingUp = false;
    private boolean isScrollingDown = false;

    public SwipeToDismissLayout(Context context) {
        super(context);
    }

    public SwipeToDismissLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeToDismissLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        Log.e("ZZZ", "onInterceptTouchEvent");
//        return super.onInterceptTouchEvent(ev);
//
//    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        Log.e("ZZZ", "swipe layout onInterceptTouchEvent" + event.getAction());
        if (!ViewCompat.canScrollVertically(getChildAt(getChildCount() - 1), -1) ) {
            Log.e("ZZZ", "return false isScrollingUp=  " + isScrollingUp);
            return true;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Log.e("ZZZ", "return false isScrollingUp=  " + isScrollingUp + " scroll down" + isScrollingDown);
        // Fail fast if the child can scroll up

        // Get finger position on screen
        final int Y = (int) event.getRawY();

        // Switch on motion event type
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                // save default base layout height
                defaultViewHeight = this.getHeight();

                // Init finger and view position
                previousFingerPosition = Y;
                thisPosition = (int) this.getY();
                break;

            case MotionEvent.ACTION_UP:
                Log.e("ZZZ", "up");
                // If user was doing a scroll up
                if(isScrollingUp){
                    // Reset this position
                    this.setY(0);
                    // We are not in scrolling up mode anymore
                    isScrollingUp = false;
                }

                // If user was doing a scroll down
                if(isScrollingDown){
                    // Reset this position
                    this.setY(0);
                    // Reset base layout size
                    this.getLayoutParams().height = defaultViewHeight;
                    this.requestLayout();
                    // We are not in scrolling down mode anymore
                    isScrollingDown = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(!isClosing){
                    int currentYPosition = (int) this.getY();

                    // If we scroll up
                    if(previousFingerPosition >Y){
                        // First time android rise an event for "up" move
                        if(!isScrollingUp){
                            Log.e("ZZZ", "scroll up!!!!!!!!!");
                            isScrollingUp = true;
                        }

                        // Has user scroll down before -> view is smaller than it's default size -> resize it instead of change it position
                        if(this.getHeight()<defaultViewHeight){
                            this.getLayoutParams().height = this.getHeight() - (Y - previousFingerPosition);
                            this.requestLayout();
                        }
                        else {
                            // Has user scroll enough to "auto close" popup ?
                            if ((thisPosition - currentYPosition) > defaultViewHeight / 4) {
                                closeUpAndDismissDialog(currentYPosition);
                                return true;
                            }

                            //
                        }
                        this.setY(this.getY() + (Y - previousFingerPosition));

                    }
                    // If we scroll down
                    else{

                        // First time android rise an event for "down" move
                        if(!isScrollingDown){
                            Log.e("ZZZ", "scroll down!!!!!!!!!");
                            isScrollingDown = true;
                        }

                        // Has user scroll enough to "auto close" popup ?
                        if (Math.abs(thisPosition - currentYPosition) > defaultViewHeight / 2)
                        {
                            closeDownAndDismissDialog(currentYPosition);
                            return true;
                        }

                        // Change base layout size and position (must change position because view anchor is top left corner)
                        this.setY(this.getY() + (Y - previousFingerPosition));
                        this.getLayoutParams().height = this.getHeight() - (Y - previousFingerPosition);
                        this.requestLayout();
                    }

                    // Update position
                    previousFingerPosition = Y;
                }
                break;
        }
        return true;
    }

    public void closeUpAndDismissDialog(int currentPosition){
        Log.e("ZZZ", "close up");
        isClosing = true;
        ObjectAnimator positionAnimator = ObjectAnimator.ofFloat(this, "y", currentPosition, -this.getHeight());
        positionAnimator.setDuration(300);
        positionAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationCancel(Animator animation) {
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
            }
            @Override
            public void onAnimationStart(Animator animation) {
            }
            @Override
            public void onAnimationEnd(Animator animator)
            {
                ((Activity)getContext()).finish();
            }
        });
        positionAnimator.start();
    }

    public void closeDownAndDismissDialog(int currentPosition){

        Log.e("ZZZ", "close down");
        isClosing = true;
        Display display = ((Activity)getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenHeight = size.y;
        ObjectAnimator positionAnimator = ObjectAnimator.ofFloat(this, "y", currentPosition, screenHeight+this.getHeight());
        positionAnimator.setDuration(300);
        positionAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationCancel(Animator animation) {
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
            }
            @Override
            public void onAnimationStart(Animator animation) {
            }
            @Override
            public void onAnimationEnd(Animator animator)
            {
                ((Activity)getContext()).finish();
            }
        });
        positionAnimator.start();
    }
}
