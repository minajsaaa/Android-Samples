package com.blueinno.android.smartlamp.component;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.io.PrintWriter;
import java.io.StringWriter;

public class NonViewPager extends ViewPager {

    private boolean enabled;

    public NonViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            if( this.enabled ) {
                return super.onTouchEvent(event);
            }
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onInterceptTouchEvent(event);
        }
        return false;
    }
}
