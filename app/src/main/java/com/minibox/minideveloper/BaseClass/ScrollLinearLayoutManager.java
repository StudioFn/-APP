package com.minibox.minideveloper.BaseClass;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.LinearLayoutManager;

public class ScrollLinearLayoutManager extends LinearLayoutManager {

    private boolean isScrollEnable = false;

    public ScrollLinearLayoutManager(Context context) {
        super(context);
    }


    @Override
    public boolean canScrollVertically() {
        return isScrollEnable && super.canScrollVertically();
    }

    public void setScrollEnable(boolean scrollEnable) {
        this.isScrollEnable = scrollEnable;
    }
}
