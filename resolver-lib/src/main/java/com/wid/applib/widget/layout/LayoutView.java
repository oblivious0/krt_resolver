package com.wid.applib.widget.layout;

import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.FrameLayout;

import androidx.cardview.widget.CardView;

import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.tool.ModuleViewFactory;
import com.wid.applib.util.FrameParamsBuilder;
import com.wid.applib.util.Util;
import com.wid.applib.widget.BaseView;

import java.util.ArrayList;
import java.util.List;

/**
 * author: MaGua
 * create on:2020/11/3 11:29
 * description
 */
public class LayoutView extends BaseView<FrameLayout> {

    public List<BaseView> childViews;

    public LayoutView(ContextImp imp, BaseLayoutBean obj) {
        super(imp, obj);
    }

    public LayoutView(ContextImp imp, BaseLayoutBean obj, boolean isListChild) {
        super(imp, obj, isListChild);
    }

    @Override
    protected void initView() {
        type = "layout";
        childViews = new ArrayList<>();
        if (bean.getCommon() != null && Util.getRealColor(bean.getCommon().getShadowColor()) != 0
                && Util.getRealColor(bean.getCommon().getShadowColor()) != -1) {
            view = new CardView(contextImp.getContext());

            int color = Util.getRealColor(bean.getStyle().getBgColor());
            if (color == 0) color = -1;
            ((CardView) view).setCardBackgroundColor(color);
            ((CardView) view).setRadius(bean.getCommon().getRadius());
            ((CardView) view).setCardElevation(10);
        } else {
            view = new FrameLayout(contextImp.getContext());
            GradientDrawable drawable = Util.getBgDrawable(Util.getRealColor(bean.getStyle().getBgColor()) + "",
                    GradientDrawable.RECTANGLE,
                    bean.getStyle().getBorderRadius() == 0 ? bean.getCommon().getRadius() : bean.getStyle().getBorderRadius(),
                    bean.getStyle().getBorderWidth() == 0 ? 1 : bean.getStyle().getBorderWidth(),
                    bean.getStyle().getBorderColor());
            view.setBackgroundDrawable(drawable);
        }

        FrameLayout.LayoutParams lp = FrameParamsBuilder.builder()
                .setWidth(bean.getCommon().getWidth())
                .setHeight(bean.getCommon().getHeight())
                .setMarginLeft(bean.getCommon().getX())
                .setMarginTop(bean.getCommon().getY())
                .build();
        view.setLayoutParams(lp);
        view.setVisibility(bean.getCommon().isIsHidden() ? View.GONE : View.VISIBLE);
        if (bean.getChildren() != null && !bean.getChildren().isEmpty()) {
            ModuleViewFactory.createViews(bean.getChildren(), contextImp, view, childViews, isListChild);
        }

    }

    @Override
    protected boolean bindInNewThread() {
        view.setClipChildren(false);
        return true;
    }

    @Override
    protected void bindInMainThread() {

    }

    @Override
    public void bindData(String cid, String key, String val) {
//        if (cid.equals(this.cid)) {
//
//        }

        if (childViews.size() != 0) {
            for (BaseView baseV : childViews) {
                baseV.bindData(cid, key, val);
            }
        }

    }
}
