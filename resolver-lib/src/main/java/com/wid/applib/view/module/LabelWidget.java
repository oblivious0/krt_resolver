package com.wid.applib.view.module;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wid.applib.R;
import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.util.FrameParamsBuilder;
import com.wid.applib.util.Util;

import krt.wid.util.ParseJsonUtil;

/**
 * author:Marcus
 * create on:2020/5/28 8:52
 * description
 */
public class LabelWidget extends BaseWidget {

    public LabelWidget(ContextImp context, Object object) {
        super(context, object);
    }

    public LabelWidget(Context context, Object object) {
        super(context, object);
    }

    @Override
    public View generate() {
        BaseLayoutBean bean = ParseJsonUtil.getBean(ParseJsonUtil.toJson(object), BaseLayoutBean.class);
        TextView tv = new TextView(getContext());
        FrameLayout.LayoutParams lp = FrameParamsBuilder.builder()
                .setWidth(bean.getCommon().getWidth())
                .setHeight(bean.getCommon().getHeight())
                .setMarginLeft(bean.getCommon().getX())
                .setMarginTop(bean.getCommon().getY())
                .build();
        tv.setText(bean.getCommon().getText().trim());
        if (bean.getStyle().getColor().contains("#")) {
            tv.setTextColor(Color.parseColor(bean.getStyle().getColor()));
        } else {
            tv.setTextColor(Util.getRealColor(bean.getStyle().getColor()));
        }
        tv.setIncludeFontPadding(false);
        tv.setPadding(bean.getStyle().getTextIndent(), 0, 0, 0);
        tv.setLineSpacing(0, bean.getStyle().getLineHeight() / bean.getStyle().getFontSize());
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, Util.getRealValue(bean.getStyle().getFontSize()));

        switch (bean.getStyle().getTextAlign()) {
            case "left":
                tv.setGravity(Gravity.START);
                break;
            case "right":
                tv.setGravity(Gravity.END);
                break;
            case "center":
                tv.setGravity(Gravity.CENTER);
                break;
            default:
                tv.setGravity(Gravity.CENTER_VERTICAL);
                break;
        }

        GradientDrawable drawable = Util.getBgDrawable(bean.getStyle().getBgColor(),
                GradientDrawable.RECTANGLE, bean.getStyle().getBorderRadius(), bean.getStyle().getBorderWidth(),
                bean.getStyle().getBorderColor());
        tv.setBackgroundDrawable(drawable);
        tv.setLayoutParams(lp);
        tv.setTag(bean.getCid());
        tv.setVisibility(bean.getCommon().isIsHidden() ? View.GONE : View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            tv.setLineHeight(Util.getRealValue(
                    bean.getStyle().getLineHeight()));
        }
        if (bean.getStyle().getLineBreakLines() != 0) {
            tv.setMaxLines(bean.getStyle().getLineBreakLines());
        }
        if (bean.getStyle().isLineBreakMode()) {
            tv.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        }

        tv.setTag(R.id.cid, bean.getCid());
        return tv;
    }

}
