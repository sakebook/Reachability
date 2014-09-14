package com.sakebook.android.sample.reachabilitysample;

import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;

import com.sakebook.android.library.reachability.Reachability;


public class CustomAnimationActivity extends Activity {

    private Reachability mReachability;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_animation);

        ImageView view = new ImageView(this);
        view.setBackgroundResource(R.drawable.custom_button_selector);
        view.setScaleType(ImageView.ScaleType.CENTER);

        mReachability = new Reachability(this);
        mReachability.canTouchableBackView(true);
        mReachability.setBackImageResource(R.drawable.tiles);
        // Should call before makeHoverView!
        mReachability.setHoverView(view, android.R.drawable.ic_partial_secure, android.R.drawable.ic_secure);
        mReachability.makeHoverView(Reachability.Position.CENTER);
        mReachability.setCustomSlideInAnimation(1000, new AnticipateOvershootInterpolator(), fromLeftAnimation());
        mReachability.setCustomSlideOutAnimation(1000, new AnticipateOvershootInterpolator(), toRightAnimation());

        findViewById(R.id.switch_hover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReachability.switchHover();
            }
        });
    }

    private PropertyValuesHolder[] fromLeftAnimation() {

        PropertyValuesHolder holderX = PropertyValuesHolder.ofFloat( "translationX", -getWidth(), 0f );
        PropertyValuesHolder holderR = PropertyValuesHolder.ofFloat("rotation", 0f, 360f);
        PropertyValuesHolder[] holders = {holderX, holderR};
        return holders;
    }

    private PropertyValuesHolder[] toRightAnimation() {

        PropertyValuesHolder holderX = PropertyValuesHolder.ofFloat( "translationX", 0f, getWidth() );
        PropertyValuesHolder holderR = PropertyValuesHolder.ofFloat("rotation", 0f, 360f);
        PropertyValuesHolder[] holders = {holderX, holderR};
        return holders;
    }

    private float getWidth() {
        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return ((size.x/5)*3);
    }
}
