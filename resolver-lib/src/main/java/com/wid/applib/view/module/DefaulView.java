package com.wid.applib.view.module;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.util.FrameParamsBuilder;
import com.wid.applib.util.Util;

import krt.wid.util.ParseJsonUtil;

/**
 * author:Marcus
 * create on:2020/7/2 10:53
 * description
 */
public class DefaulView extends BaseWidget {
    public DefaulView(ContextImp context, Object object) {
        super(context, object);
    }

    public DefaulView(Context context, Object object) {
        super(context, object);
    }

    @Override
    public View generate() {
        BaseLayoutBean bean = ParseJsonUtil.getBean(ParseJsonUtil.toJson(object), BaseLayoutBean.class);
        FrameLayout frameLayout = new FrameLayout(getContext());
        FrameLayout.LayoutParams lp = FrameParamsBuilder.builder()
                .setWidth(bean.getCommon().getWidth())
                .setHeight(bean.getCommon().getHeight())
                .setMarginLeft(bean.getCommon().getX())
                .setMarginTop(bean.getCommon().getY())
                .build();
        GradientDrawable drawable = Util.getBgDrawable("#ffffff", GradientDrawable.RECTANGLE, 0, 4, "#000000");
        frameLayout.setBackgroundDrawable(drawable);
        frameLayout.setLayoutParams(lp);
        TextView textView = new TextView(getContext());
        textView.setText(bean.getType());
        textView.setTextColor(Color.BLACK);
        FrameLayout.LayoutParams tv = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tv.gravity = Gravity.CENTER;
        textView.setLayoutParams(tv);
        frameLayout.addView(textView);
        return frameLayout;
    }
}
