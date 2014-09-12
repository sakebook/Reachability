package com.sakebook.android.library.reachability;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.lang.reflect.Method;

/**
 * Created by sakemotoshinya on 2014/09/10.
 */
public class Reachability {

    public enum Position {
        LEFT, CENTER, RIGHT
    }

    private Context mContext;
    private ViewGroup mRootView;
    private View mMoveView;
    private View mContentView;
    private FrameLayout mFloatLayout;
    private ImageView mHoverView = null;
    private boolean mIsNear = false;
    private boolean mBackLock =false;
    private boolean mIsShown = false;
    private boolean mHoverLock =false;

    private ValueAnimator mInAnimator = null;
    private ValueAnimator mOutAnimator = null;

    private float startY;
    private float endY;
    private float halfWindow;

    private final static String TAG = "Reachability";
    private final static String STATUS_BAR = "statusbar";
    private final static String STATUS_BAR_NAME = "android.app.StatusBarManager";
    private final static String STATUS_BAR_OPEN = "expandNotificationsPanel";

    public Reachability(Context context) {
        this.mContext = context;
        mRootView = ((ViewGroup)((Activity)mContext).getWindow().getDecorView());
        mContentView = mRootView.findViewById(android.R.id.content);
        mMoveView = mRootView.getChildAt(0);
        mFloatLayout = new FrameLayout(mContext);
        halfWindow = getHalfWindow();
    }

    public void makeHoverView(Position position) {

        int gravity;
        if (Position.LEFT.equals(position)) {
            gravity = Gravity.BOTTOM|Gravity.LEFT;
        } else if (Position.RIGHT.equals(position)) {
            gravity = Gravity.BOTTOM|Gravity.RIGHT;
        } else {
            gravity = Gravity.BOTTOM|Gravity.CENTER;
        }

        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        frameParams.gravity = gravity;
        mFloatLayout.setLayoutParams(frameParams);

        FrameLayout.LayoutParams wrapParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        wrapParams.gravity = Gravity.CENTER;
        wrapParams.setMargins(48, 48, 48, 48);

        mFloatLayout.addView(getHoverView(), wrapParams);
        mFloatLayout.setBackgroundColor(Color.argb(0, 255, 255, 255));
        mRootView.addView(mFloatLayout);
        switchHover();
    }

    public void setBackImage(Drawable drawable) {
        if (Build.VERSION.SDK_INT > 15) {
            mRootView.setBackground(drawable);
        }else {
            mRootView.setBackgroundDrawable(drawable);
        }
    }

    public void setBackImageResource(int resourceId) {
        mRootView.setBackgroundResource(resourceId);
    }

    public void setBackColor(int color) {
        mRootView.setBackgroundColor(color);
    }

    public void canTouchableBackView(boolean bool) {
        if (bool) {
            setListner();
        } else {
        }
    }

    public void showStatusBar() {
        try {
            Object service = mContext.getSystemService(STATUS_BAR);
            Class clazz = Class.forName(STATUS_BAR_NAME);
            Method method = clazz.getMethod(STATUS_BAR_OPEN);
            method.invoke(service);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void switchBack() {
        if (mBackLock) {
            return;
        }
        if (mIsNear) {
            push();
        } else {
            pull();
        }
    }

    public void switchHover() {
        if (mHoverLock) {
            return;
        }
        if (isSetCustomAnimation()) {
            if (mIsShown) {
                customSlideOut();
            } else {
                customSlideIn();
            }
        } else {
            if (mIsShown) {
                slideOut();
            } else {
                slideIn();
            }
        }
    }

    private float getHalfWindow() {
        WindowManager wm = (WindowManager)mContext.getSystemService(mContext.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return (size.y/5)*2;
    }

    private void pull() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mMoveView, "translationY", 0f, halfWindow);
        animator.setDuration(400);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mBackLock = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mBackLock = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mBackLock = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
        mIsNear = true;
    }

    private void push() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mMoveView, "translationY", halfWindow, 0f);
        animator.setDuration(400);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mBackLock = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mBackLock = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mBackLock = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
        mIsNear = false;
    }

    private void slideIn() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(getHoverView(), "translationY", 300f, 0f);
        animator.setDuration(500);
        animator.setInterpolator(new AnticipateOvershootInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mHoverLock = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mHoverLock = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mHoverLock = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
        mIsShown = true;
    }

    private void slideOut() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(getHoverView(), "translationY", 0f, 300f);
        animator.setDuration(500);
        animator.setInterpolator(new AnticipateOvershootInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mHoverLock = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mHoverLock = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mHoverLock = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
        mIsShown = false;
    }

    private boolean isSetCustomAnimation() {
        if (mInAnimator != null && mOutAnimator != null) {
            return true;
        }
        Log.i(TAG, "Not set custom animation.");
        return false;
    }

    public void setCustomSlideInAnimation(ValueAnimator animator) {
        if (mInAnimator == null) {
            mInAnimator = animator;
            mInAnimator.setTarget(getHoverView());
            mInAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mHoverLock = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mHoverLock = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    mHoverLock = false;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
        }
    }

    public void setCustomSlideOutAnimation(ValueAnimator animator) {
        if (mOutAnimator == null) {
            mOutAnimator = animator;
            mOutAnimator.setTarget(getHoverView());
            mOutAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mHoverLock = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mHoverLock = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    mHoverLock = false;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
        }
    }

    private void customSlideIn() {
        if (mInAnimator == null) {
            Log.w(TAG, "Not set slide in animation");
            return;
        }
        mInAnimator.start();
        mIsShown = true;
    }

    private void customSlideOut() {
        if (mOutAnimator == null) {
            Log.w(TAG, "Not set slide out animation");
            return;
        }
        mOutAnimator.start();
        mIsShown = false;
    }

    private void setListner() {
        mContentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mRootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    Log.d("touch", "down: "+event.getY());
                    startY = event.getY();
                } else if (MotionEvent.ACTION_UP == event.getAction()) {
                    Log.d("touch", "up: "+event.getY());
                    endY = event.getY();

                    if ((endY - startY) > 50) {
                        showStatusBar();
                    }
                }
                return false;
            }
        });
    }

    private View getHoverView() {
        if (mHoverView == null) {
            mHoverView = new ImageView(mContext);
            mHoverView.setImageResource(R.drawable.ic_launcher);
            mHoverView.setBackgroundResource(R.drawable.button_selector);
            // padding is space in layout image
            mHoverView.setPadding(16, 16, 16, 16);
            mHoverView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchBack();
                }
            });
        }
        return mHoverView;
    }

    private void addView() {
        FrameLayout frame = new FrameLayout(mContext);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        frame.setLayoutParams(params);
        frame.addView(new Button(mContext));

        RelativeLayout relative = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams paramses = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        relative.setLayoutParams(paramses);
        relative.addView(new Button(mContext));


//        ((FrameLayout)mContentView).addView(frame);
        mRootView.addView(frame);
        mRootView.addView(relative);
    }
}
