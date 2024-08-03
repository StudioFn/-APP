/*
 * 作者：远赤Copyright (c) 2023
 * 未经允许，禁止转载！！
 * 联系方式：QQ
 * 2308762185
 */

package com.minibox.minideveloper.View;

import android.content.Context;

public class GridLayoutManager extends androidx.recyclerview.widget.GridLayoutManager {
    private boolean isScrollEnable = false;

    public GridLayoutManager(Context mContext, int i) {
        super(mContext, i);
    }

    @Override
    public boolean canScrollVertically() {
        return isScrollEnable && super.canScrollVertically();
    }

    public void setScrollEnable(boolean scrollEnable) {
        this.isScrollEnable = scrollEnable;
    }
}
