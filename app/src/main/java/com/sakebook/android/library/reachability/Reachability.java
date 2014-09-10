package com.sakebook.android.library.reachability;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Created by sakemotoshinya on 2014/09/10.
 */
public class Reachability {

    private Context mContext;
    private ViewGroup mRootView;
    private View mMoveView;
    private View mContentView;
    private RelativeLayout mFloatRayout;
    private boolean mIsNear = false;

    public Reachability() {
    }

    public Reachability(Context context) {
        this.mContext = context;
        mRootView = ((ViewGroup)((Activity)mContext).getWindow().getDecorView());
        mContentView = mRootView.findViewById(android.R.id.content);
        mMoveView = mRootView.getChildAt(0);
        mFloatRayout = new RelativeLayout(mContext);
//        addView();
//        makeFloatNavibar();
    }

    public void makeFloatNavibar() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        mFloatRayout.setLayoutParams(params);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.bottomMargin = 100;
        mFloatRayout.addView(circleView(), params);
        mRootView.addView(mFloatRayout);
    }

    private View circleView() {
        ImageView view = new ImageView(mContext);
        view.setFadingEdgeLength(50);
        view.setImageResource(R.drawable.ic_launcher);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switching();
            }
        });
        return view;
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
