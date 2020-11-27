package com.wid.applib.view.widget;

import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.util.FrameParamsBuilder;

/**
 * author: MaGua
 * create on:2020/11/27 15:21
 * description
 */
public class CountdownView extends BaseView<LinearLayout> {

    public CountdownView(ContextImp imp, BaseLayoutBean obj) {
        super(imp, obj);
    }

    public CountdownView(ContextImp imp, BaseLayoutBean obj, boolean isListChild) {
        super(imp, obj, isListChild);
    }

    @Override
    protected void initView() {
        type = "countDown";
        LinearLayout layout = new LinearLayout(contextImp.getContext());
        FrameLayout.LayoutParams lp = FrameParamsBuilder.builder()
                .setWidth(bean.getCommon().getWidth())
                .setHeight(bean.getCommon().getHeight())
                .setMarginLeft(bean.getCommon().getX())
                .setMarginTop(bean.getCommon().getY())
                .build();
        layout.setLayoutParams(lp);
        layout.setOrientation(LinearLayout.HORIZONTAL);

    }

    @Override
    protected boolean bindInNewThread() {
        return false;
    }

    @Override
    protected void bindInMainThread() {

    }

    @Override
    public void bindData(String cid, String key, String val) {

    }
}
