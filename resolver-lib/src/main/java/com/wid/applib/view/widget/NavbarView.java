package com.wid.applib.view.widget;

import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.util.FrameParamsBuilder;
import com.wid.applib.util.Util;
import com.wid.applib.view.NavBar;

import krt.wid.util.MTitle;

/**
 * author: MaGua
 * create on:2020/11/3 14:43
 * description
 */
public class NavbarView extends BaseView<MTitle> {

    public NavbarView(ContextImp imp, BaseLayoutBean obj) {
        super(imp, obj);
    }

    public NavbarView(ContextImp imp, BaseLayoutBean obj, boolean isListChild) {
        super(imp, obj, isListChild);
    }

    @Override
    protected void initView() {
        type = "nav";
        view = new NavBar.Builder(contextImp.getContext(), bean.getCommon().getHeight())
                .setBackColor(bean.getStyle().getBgColor())
                .setCenterText(bean.getCommon().getTitle(), Util.getRealColor(bean.getStyle().getTitleColor()), Util.getRealValue(bean.getStyle().getTitleFontSize()))
                .setLeftText(bean.getCommon().getBackText(), Util.getRealColor(bean.getStyle().getBackTextColor()), Util.getRealValue(bean.getStyle().getLeftFontSize()))
                .build();

        FrameLayout.LayoutParams lp = FrameParamsBuilder.builder()
                .setWidth(FrameLayout.LayoutParams.MATCH_PARENT)
                .setHeight(100)
                .build();
        view.setLayoutParams(lp);
    }

    @Override
    protected boolean bindInNewThread() {

        return true;
    }

    @Override
    protected void bindInMainThread() {
        ImageView imageView = new ImageView(contextImp.getContext());
        Glide.with(contextImp.getContext())
                .load(bean.getStyle().getBackImgUrl())
                .override(50,50)
                .into(imageView);
        view.setCustomLeftView(imageView);
    }

    @Override
    public void bindData(String cid, String key, String val) {

    }
}
