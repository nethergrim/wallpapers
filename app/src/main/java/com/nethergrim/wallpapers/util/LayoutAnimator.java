package com.nethergrim.wallpapers.util;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * @author Andrew Drobyazko (c2q9450@gmail.com) on 15.09.15.
 */
public class LayoutAnimator implements ValueAnimator.AnimatorUpdateListener,
        Animator.AnimatorListener {

    private View v1;
    private View v2;
    private float mDelta;
    private float mState;
    private boolean mRunning;

    public LayoutAnimator(View v1, View v2, float delta) {
        this.v1 = v1;
        this.v2 = v2;
        mDelta = delta;
    }

    public void toggle() {
        if (mRunning) {
            return;
        }
        boolean firstOrder = mState < 0.0001f;
        ValueAnimator valueAnimator;
        if (firstOrder) {
            valueAnimator = ValueAnimator.ofFloat(0f, 2f);
            v2.setTranslationY(mDelta);
        } else {
            valueAnimator = ValueAnimator.ofFloat(2f, 0f);
        }

        valueAnimator.setDuration(350);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(this);
        valueAnimator.addListener(this);
        valueAnimator.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        float value = (float) animation.getAnimatedValue();
        setState(value);
    }

    public void setState(float state) {
        mState = state;
        if (state <= 1.0f) {
            v1.setTranslationY(state * mDelta);
        } else {
            state = state - 1.0f;
            v2.setTranslationY((state * mDelta * -1) + mDelta);
        }
    }

    @Override
    public void onAnimationStart(Animator animation) {
        mRunning = true;
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        mRunning = false;
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
