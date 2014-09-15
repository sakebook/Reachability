package com.sakebook.android.library.reachability;

import android.animation.Animator;

/**
 * Created by sakemotoshinya on 2014/09/15.
 */
public class HoverAnimationListener implements Animator.AnimatorListener{

    private boolean mHoverLock;
    private boolean mIsShown;

    public HoverAnimationListener(boolean hoverLock) {
        this.mHoverLock = hoverLock;
    }

    @Override
    public void onAnimationStart(Animator animation) {
        this.mHoverLock = true;
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        this.mHoverLock = false;
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        this.mHoverLock = false;
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
    }

    public boolean isLocked() {
        return this.mHoverLock;
    }

    public void setShowStatus(boolean isShown) {
        this.mIsShown = isShown;
    }

    public boolean isShown() {
        return this.mIsShown;
    }
}
