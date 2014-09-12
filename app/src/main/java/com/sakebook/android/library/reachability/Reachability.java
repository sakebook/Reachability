package com.sakebook.android.library.reachability;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
    private FrameLayout mFloatRayout;
    private boolean mIsNear = false;

    private float startY;
    private float endY;

    private final static String STATUS_BAR = "statusbar";
    private final static String STATUS_BAR_NAME = "android.app.StatusBarManager";
    private final static String STATUS_BAR_OPEN = "expandNotificationsPanel";

    public Reachability() {
    }

    public Reachability(Context context) {
        this.mContext = context;
        mRootView = ((ViewGroup)((Activity)mContext).getWindow().getDecorView());
        mContentView = mRootView.findViewById(android.R.id.content);
        mMoveView = mRootView.getChildAt(0);
        mFloatRayout = new FrameLayout(mContext);
    }

    public void makeFloatNavibar(Position position) {

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
        frameParams.setMargins(48, 48, 48, 48);
        mFloatRayout.setLayoutParams(frameParams);

        FrameLayout.LayoutParams wrapParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        wrapParams.gravity = Gravity.CENTER;

        mFloatRayout.addView(circleView(), wrapParams);
        mFloatRayout.setBackgroundColor(Color.argb(0, 255, 255, 255));
//        mFloatRayout.setBackgroundResource(R.drawable.button_selector);
        mRootView.addView(mFloatRayout);
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

    public void switching() {
        if (mIsNear) {
            near();
        } else {
            far();
        }
    }

    public void near() {
        Toast.makeText(mContext, "near", Toast.LENGTH_SHORT).show();
        fadeOut();
    }

    public void far() {
        Toast.makeText(mContext, "far", Toast.LENGTH_SHORT).show();
        fadeIn();
    }

    private void fadeIn() {
        ValueAnimator animator = ObjectAnimator.ofFloat(mMoveView, "translationY", 0f, 500f);
        animator.setDuration(400);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
        mIsNear = true;
    }

    private void fadeOut() {
        ValueAnimator animator = ObjectAnimator.ofFloat(mMoveView, "translationY", 500f, 0f);
        animator.setDuration(400);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
        mIsNear = false;
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

    private View circleView() {
        ImageView view = new ImageView(mContext);
        view.setImageResource(R.drawable.ic_launcher);
        view.setBackgroundResource(R.drawable.button_selector);
        view.setPadding(8, 8, 8, 8);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switching();
            }
        });
        return view;
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
