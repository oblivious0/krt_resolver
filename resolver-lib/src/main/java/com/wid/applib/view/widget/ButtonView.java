package com.wid.applib.view.widget;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.util.ColorUtil;
import com.wid.applib.util.FrameParamsBuilder;
import com.wid.applib.util.Util;

/**
 * author: MaGua
 * create on:2020/11/3 11:03
 * description
 */
public class ButtonView extends BaseView<FrameLayout> {

    public ButtonView(ContextImp imp, BaseLayoutBean obj) {
        super(imp, obj);
    }

    public ButtonView(ContextImp imp, BaseLayoutBean obj, boolean isListChild) {
        super(imp, obj, isListChild);
    }

    @Override
    protected void initView() {
        type = "button";
        view = new FrameLayout(contextImp.getContext());
        FrameLayout.LayoutParams lp = FrameParamsBuilder.builder()
                .setWidth(bean.getCommon().getWidth())
                .setHeight(bean.getCommon().getHeight())
                .setMarginLeft(bean.getCommon().getX())
                .setMarginTop(bean.getCommon().getY())
                .build();
        view.setLayoutParams(lp);
    }

    @Override
    protected boolean bindInNewThread() {


        GradientDrawable drawable = Util.getBgDrawable(ColorUtil.rgba2HexString(bean.getStyle().getBgColor()), GradientDrawable.RECTANGLE
                , bean.getStyle().getBorderRadius(), bean.getStyle().getBorderWidth(), ColorUtil.rgba2HexString(bean.getStyle().getBorderColor()));
        view.setBackgroundDrawable(drawable);
        view.setVisibility(bean.getCommon().isIsHidden() ? View.GONE : View.VISIBLE);
        TextView tv = new TextView(contextImp.getContext());
        tv.setText(bean.getCommon().getText());
        if (bean.getStyle().getColor().contains("#")) {
            tv.setTextColor(Color.parseColor(bean.getStyle().getColor()));
        } else {
            tv.setTextColor(Util.getRealColor(bean.getStyle().getColor()));
        }
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, bean.getStyle().getFontSize());
        tv.setTag(bean.getCid());
        FrameLayout.LayoutParams tvlp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        tvlp.gravity = Gravity.CENTER;
        tv.setLayoutParams(tvlp);
        view.addView(tv);
        return true;
    }

    @Override
    protected void bindInMainThread() {

    }

    @Override
    public void bindData(String cid, String key, String val) {

    }
}
