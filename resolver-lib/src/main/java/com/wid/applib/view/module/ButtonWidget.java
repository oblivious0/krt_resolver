package com.wid.applib.view.module;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wid.applib.R;
import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.util.ColorUtil;
import com.wid.applib.util.FrameParamsBuilder;
import com.wid.applib.util.Util;

import krt.wid.util.ParseJsonUtil;

/**
 * author:Marcus
 * create on:2020/7/2 10:37
 * description
 */
public class ButtonWidget extends BaseWidget {


    public ButtonWidget(ContextImp context, Object object) {
        super(context, object);
    }

    public ButtonWidget(Context context, Object object) {
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
        frameLayout.setLayoutParams(lp);
        GradientDrawable drawable = Util.getBgDrawable(ColorUtil.rgba2HexString(bean.getStyle().getBgColor()), GradientDrawable.RECTANGLE
                , bean.getStyle().getBorderRadius(), bean.getStyle().getBorderWidth(), ColorUtil.rgba2HexString(bean.getStyle().getBorderColor()));
        frameLayout.setBackgroundDrawable(drawable);

        frameLayout.setVisibility(bean.getCommon().isIsHidden() ? View.GONE : View.VISIBLE);

        TextView tv = new TextView(getContext());
        tv.setText(bean.getCommon().getText());
        if (bean.getStyle().getColor().contains("#")) {
            tv.setTextColor(Color.parseColor(bean.getStyle().getColor()));
        } else {
            tv.setTextColor(Util.getRealColor(bean.getStyle().getColor()));
        }
//
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, bean.getStyle().getFontSize());
        tv.setTag(bean.getCid());
        FrameLayout.LayoutParams tvlp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        tvlp.gravity = Gravity.CENTER;
        tv.setLayoutParams(tvlp);
        frameLayout.addView(tv);

        frameLayout.setTag(R.id.cid, bean.getCid());
//        frameLayout.setTag(R.id.pageId, pageCid);
        return frameLayout;
    }
}
