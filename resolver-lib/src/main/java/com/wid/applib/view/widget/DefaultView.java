package com.wid.applib.view.widget;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.util.FrameParamsBuilder;
import com.wid.applib.util.Util;

/**
 * author: MaGua
 * create on:2020/11/3 14:16
 * description
 */
public class DefaultView extends BaseView<FrameLayout> {

    public DefaultView(ContextImp imp, BaseLayoutBean obj) {
        super(imp, obj);
    }

    public DefaultView(ContextImp imp, BaseLayoutBean obj, boolean isListChild) {
        super(imp, obj, isListChild);
    }

    @Override
    protected void initView() {
        type = "nullView";
        view = new FrameLayout(contextImp.getContext());
        FrameLayout.LayoutParams lp = FrameParamsBuilder.builder()
                .setWidth(bean.getCommon().getWidth())
                .setHeight(bean.getCommon().getHeight())
                .setMarginLeft(bean.getCommon().getX())
                .setMarginTop(bean.getCommon().getY())
                .build();
        view.setLayoutParams(lp);

        GradientDrawable drawable = Util.getBgDrawable("#ffffff", GradientDrawable.RECTANGLE, 0, 4, "#000000");
        view.setBackgroundDrawable(drawable);
        TextView textView = new TextView(contextImp.getContext());
        textView.setText(bean.getType());
        textView.setTextColor(Color.BLACK);
        FrameLayout.LayoutParams tv = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tv.gravity = Gravity.CENTER;
        textView.setLayoutParams(tv);
        view.addView(textView);
    }

    @Override
    protected boolean bindInNewThread() {


        return true;
    }

    @Override
    protected void bindInMainThread() {

    }

    @Override
    public void bindData(String cid, String key, String val) {

    }
}
