package com.sakebook.android.library.reachability;

import android.animation.Animator;

public class BackAnimationListener implements Animator.AnimatorListener{

    private boolean mBackLock;
    private boolean mIsNear;

    public BackAnimationListener(boolean backLock) {
        this.mBackLock = backLock;
    }
    @Override
    public void onAnimationStart(Animator animation) {
        this.mBackLock = true;
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        this.mBackLock = false;
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        this.mBackLock = false;
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
    }

    public boolean isLocked() {
        return this.mBackLock;
    }

    public void setDistanceStatus(boolean isNear) {
        this.mIsNear = isNear;
    }

    public boolean isNear() {
        return this.mIsNear;
    }

}
