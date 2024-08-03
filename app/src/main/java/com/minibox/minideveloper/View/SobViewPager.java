package com.minibox.minideveloper.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class SobViewPager extends ViewPager {
    private final Handler mHandler = new Handler();

    public SobViewPager(@NonNull Context context) {
        this(context,null);
    }

    @SuppressLint("ClickableViewAccessibility")
    public SobViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener((v, event) -> {
            int action = event.getAction();
            switch (action){
                case MotionEvent.ACTION_DOWN:
                   stopLooper();
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    startLooper();
                    break;
            }
            return false;
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        stopLooper();
    }

    private void startLooper() {
        mHandler.postDelayed(mTask,2000);
    }
    private void stopLooper(){
        mHandler.removeCallbacks(mTask);
        removeCallbacks(mTask);
    }

    private final Runnable mTask = new Runnable() {
        @Override
        public void run() {
            int count = getCurrentItem();
            count++;
            setCurrentItem(count);
            postDelayed(mTask,2000);
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopLooper();
    }

    @Override
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
        stopLooper();
    }
}
