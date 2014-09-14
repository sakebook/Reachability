/**
 * Copyright (C) 2014 ShinyaSakemoto <sakebook@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sakebook.android.library.reachability;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.lang.reflect.Method;

public class Reachability {

    /**
     * position of HoverView
     * */
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
    private boolean mHasCustomView = false;
    private int mDrawablePull = 0;
    private int mDrawablePush = 0;

    private ObjectAnimator mInAnimator = null;
    private ObjectAnimator mOutAnimator = null;

    private float startY;
    private float endY;
    private float halfWindow;

    private final static String TAG = "Reachability";
    private final static String STATUS_BAR = "statusbar";
    private final static String STATUS_BAR_NAME = "android.app.StatusBarManager";
    private final static String STATUS_BAR_OPEN = "expandNotificationsPanel";
    private final static int DURATION_TIME = 400;
    private final static int MARGIN = 48;

    /**
     * Constructor
     * @param context context
     * */
    public Reachability(Context context) {
        this.mContext = context;
        mRootView = ((ViewGroup)((Activity)mContext).getWindow().getDecorView());
        mContentView = mRootView.findViewById(android.R.id.content);
        mMoveView = mRootView.getChildAt(0);
        mFloatLayout = new FrameLayout(mContext);
        halfWindow = getHalfWindow();
        initHoverView();
    }

    private float getHalfWindow() {
        WindowManager wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return (size.y/5)*2;
    }

    private void initHoverView() {
        if (mHoverView == null) {
            mHoverView = new ImageView(mContext);
            mHoverView.setImageResource(R.drawable.back_pull);
            mHoverView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            mHoverView.setBackgroundResource(R.drawable.button_selector);
            mHoverView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchBack();
                }
            });
        }
    }

    /**
     *<p> You can set custom HoverView only ImageView.<br>
     *     If this method use, should call before makeHoverView</p>
     * @param view ImageView
     * @param pullResource pull image resource id
     * @param pushResource push image resource id
     * */
    public void setHoverView(ImageView view, int pullResource, int pushResource) {
        mHasCustomView = true;
        mHoverView = view;
        mDrawablePull = pullResource;
        mDrawablePush = pushResource;
        mHoverView.setImageResource(mDrawablePull);
        mHoverView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchBack();
            }
        });
    }

    /**
     * HoverView add to FloatLayout
     * @param position LEFT, CENTER, RIGHT
     * */
    public void makeHoverView(Position position) {

        int gravity;
        if (Position.LEFT.equals(position)) {
            gravity = Gravity.BOTTOM|Gravity.LEFT;
        } else if (Position.RIGHT.equals(position)) {
            gravity = Gravity.BOTTOM|Gravity.RIGHT;
        } else {
            gravity = Gravity.BOTTOM|Gravity.CENTER;
        }

        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        frameParams.gravity = gravity;
        mFloatLayout.setLayoutParams(frameParams);

        FrameLayout.LayoutParams wrapParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        wrapParams.gravity = gravity;
        wrapParams.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);

        mFloatLayout.addView(getHoverView(), wrapParams);
        mFloatLayout.setBackgroundColor(Color.argb(0, 255, 255, 255));
        mRootView.addView(mFloatLayout);
        switchHover();
    }

    /**
     * Set image to RootView
     * @param resourceId image resource id
     * */
    public void setBackImageResource(int resourceId) {
        mRootView.setBackgroundResource(resourceId);
    }

    /**
     * Set color of RootView
     * @param color color id
     * */
    public void setBackColor(int color) {
        mRootView.setBackgroundColor(color);
    }

    /**
     * Can touch to RootView
     * @param bool true: can touch. false: can't touch.
     * */
    public void canTouchableBackView(boolean bool) {
        if (bool) {
            setListener();
        }
    }

    /**
     * <p>show status bar <br>
     * requirement AndroidManifest.xml <uses-permission android:name="android.permission.EXPAND_STATUS_BAR" /></p>
     * */
    public void showStatusBar() {
        try {
            Object service = mContext.getSystemService(STATUS_BAR);
            Class clazz = Class.forName(STATUS_BAR_NAME);
            Method method = clazz.getMethod(STATUS_BAR_OPEN);
            method.invoke(service);
        } catch (Exception e) {
            Log.w(TAG, "Not permission. You write to uses-permission STATUS_BAR in manifest");
            e.printStackTrace();
        }
    }

    /**
     * <p>Move MoveView.<br>
     *     Animation does not overlap.</p>
     * */
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

    /**
     * <p>Move HoverView.<br>
     *     Animation does not overlap.</p>
     * */
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

    private boolean isSetCustomAnimation() {
        if (mInAnimator != null && mOutAnimator != null) {
            return true;
        }
        Log.i(TAG, "Not set custom animation.");
        return false;
    }

    private void pull() {
        if (mHasCustomView && mDrawablePush != 0) {
            getHoverView().setImageResource(mDrawablePush);
        } else {
            getHoverView().setImageResource(R.drawable.back_push);
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(mMoveView, "translationY", 0f, halfWindow);
        animator.setDuration(DURATION_TIME);
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
        if (mHasCustomView && mDrawablePull != 0) {
            getHoverView().setImageResource(mDrawablePull);
        } else {
            getHoverView().setImageResource(R.drawable.back_pull);
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(mMoveView, "translationY", halfWindow, 0f);
        animator.setDuration(DURATION_TIME);
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
        animator.setDuration(DURATION_TIME);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
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
        animator.setDuration(DURATION_TIME);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
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


    /**
     * <p>You can set custom animation to HoverView slide in.<br>
     * It is not necessary to set the target of the animation  </p>
     * @param duration Animation duration
     * @param interpolator Animatoin interpolator
     * @param holders PropertyValueHolder...
     * */
    public void setCustomSlideInAnimation(int duration, TimeInterpolator interpolator, PropertyValuesHolder... holders) {
        if (mInAnimator == null) {
            mInAnimator = ObjectAnimator.ofPropertyValuesHolder(getHoverView(), holders);
            mInAnimator.setDuration(duration);
            mInAnimator.setInterpolator(interpolator);
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

    /**
     * <p>You can set custom animation to HoverView slide out.<br>
     * It is not necessary to set the target of the animation  </p>
     * @param duration Animation duration
     * @param interpolator Animatoin interpolator
     * @param holders PropertyValueHolder...
     * */
    public void setCustomSlideOutAnimation(int duration, TimeInterpolator interpolator, PropertyValuesHolder... holders) {
        if (mOutAnimator == null) {
            mOutAnimator = ObjectAnimator.ofPropertyValuesHolder(getHoverView(), holders);
            mOutAnimator.setDuration(duration);
            mOutAnimator.setInterpolator(interpolator);
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

    private void setListener() {
        mContentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;//RootView unreachable
            }
        });
        mRootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    startY = event.getY();
                } else if (MotionEvent.ACTION_UP == event.getAction()) {
                    endY = event.getY();

                    if ((endY - startY) > 50) {
                        showStatusBar();
                    }
                }
                return false;
            }
        });
    }

    /**
     * Getter of HoverView
     * */
    private ImageView getHoverView() {
        return this.mHoverView;
    }
}
