package com.wid.applib.view.module;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.FrameLayout;

import androidx.cardview.widget.CardView;

import com.wid.applib.R;
import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.tool.ModuleViewFactory;
import com.wid.applib.util.FrameParamsBuilder;
import com.wid.applib.util.Util;

import java.util.List;

import krt.wid.util.ParseJsonUtil;

/**
 * author:Marcus
 * create on:2020/5/28 8:55
 * description
 */
public class LayoutWidget extends BaseWidget {

    public LayoutWidget(ContextImp context, Object object) {
        super(context, object);
    }

    public LayoutWidget(Context context, Object object) {
        super(context, object);
    }

    @Override
    public View generate() {
        BaseLayoutBean bean = ParseJsonUtil.getBean(ParseJsonUtil.toJson(object), BaseLayoutBean.class);
        FrameLayout frameLayout;
        if (bean.getCommon() != null && Util.getRealColor(bean.getCommon().getShadowColor()) != 0
                && Util.getRealColor(bean.getCommon().getShadowColor()) != -1) {
            frameLayout = new CardView(getContext());
            ((CardView) frameLayout).setCardBackgroundColor(Util.getRealColor(bean.getStyle().getBgColor()));
            ((CardView) frameLayout).setRadius(bean.getCommon().getRadius());
            ((CardView) frameLayout).setCardElevation(10);
        } else {
            frameLayout = new FrameLayout(getContext());
            GradientDrawable drawable = Util.getBgDrawable(Util.getRealColor(bean.getStyle().getBgColor()) + "",
                    GradientDrawable.RECTANGLE,
                    bean.getStyle().getBorderRadius() == 0 ? bean.getCommon().getRadius() : bean.getStyle().getBorderRadius(),
                    bean.getStyle().getBorderWidth() == 0 ? 1 : bean.getStyle().getBorderWidth(),
                    bean.getStyle().getBorderColor());
            frameLayout.setBackgroundDrawable(drawable);
        }

        FrameLayout.LayoutParams lp = FrameParamsBuilder.builder()
                .setWidth(bean.getCommon().getWidth())
                .setHeight(bean.getCommon().getHeight())
                .setMarginLeft(bean.getCommon().getX())
                .setMarginTop(bean.getCommon().getY())
                .build();
        frameLayout.setLayoutParams(lp);
        frameLayout.setClipChildren(false);

//        if (bean.getChildren() != null && !bean.getChildren().isEmpty()) {
//            List<View> list = ModuleViewFactory.generate(bean.getChildren(), imp);
//            if (!list.isEmpty()) {
//                for (View view : list) {
//                    frameLayout.addView(view);
//                }
//            }
//        }

        frameLayout.setTag(R.id.cid, bean.getCid());
        frameLayout.setVisibility(bean.getCommon().isIsHidden() ? View.GONE : View.VISIBLE);
        return frameLayout;
    }
}

