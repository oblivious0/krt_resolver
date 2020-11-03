package com.wid.applib.view.widget;

import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import androidx.cardview.widget.CardView;

import com.wid.applib.R;
import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.tool.ModuleViewFactory;
import com.wid.applib.util.FrameParamsBuilder;
import com.wid.applib.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * author: MaGua
 * create on:2020/11/3 11:29
 * description
 */
public class LayoutView extends BaseView<FrameLayout> {

    public LayoutView(ContextImp imp, BaseLayoutBean obj) {
        super(imp, obj);
    }

    public LayoutView(ContextImp imp, BaseLayoutBean obj, boolean isListChild) {
        super(imp, obj, isListChild);
    }

    @Override
    protected boolean bindInNewThread() {
        if (bean.getCommon() != null && Util.getRealColor(bean.getCommon().getShadowColor()) != 0
                && Util.getRealColor(bean.getCommon().getShadowColor()) != -1) {
            view = new CardView(contextImp.getContext());
            ((CardView) view).setCardBackgroundColor(Util.getRealColor(bean.getStyle().getBgColor()));
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
        view.setClipChildren(false);
        view.setVisibility(bean.getCommon().isIsHidden() ? View.GONE : View.VISIBLE);
        return true;
    }

    @Override
    protected void bindInMainThread() {
        if (bean.getChildren() != null && !bean.getChildren().isEmpty()) {
            List<BaseView> views = new ArrayList<>();
            ModuleViewFactory.createViews(bean.getChildren(), contextImp, view, views, isListChild);
        }
    }

    @Override
    public void bindData(String key, String val) {

    }
}
