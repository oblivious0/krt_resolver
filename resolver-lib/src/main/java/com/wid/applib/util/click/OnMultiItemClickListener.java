package com.wid.applib.util.click;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;

/**
 * @author hyj
 * @time 2020/7/24 9:58
 * @class describe
 */
public abstract class OnMultiItemClickListener implements BaseQuickAdapter.OnItemClickListener {

    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    public abstract void onMultiClick(BaseQuickAdapter adapter, View view, int position);

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            // 超过点击间隔后再将lastClickTime重置为当前点击时间
            lastClickTime = curClickTime;
            onMultiClick(adapter, view, position);
        }
    }
}
