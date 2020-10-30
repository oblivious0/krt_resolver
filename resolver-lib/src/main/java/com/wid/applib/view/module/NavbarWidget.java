package com.wid.applib.view.module;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.util.FrameParamsBuilder;
import com.wid.applib.util.Util;
import com.wid.applib.view.NavBar;

import krt.wid.util.MTitle;
import krt.wid.util.ParseJsonUtil;

/**
 * @author hyj
 * @time 2020/7/21 11:29
 * @class describe
 */
public class NavbarWidget extends BaseWidget {

    public NavbarWidget(ContextImp context, Object object) {
        super(context, object);
    }

    public NavbarWidget(Context context, Object object) {
        super(context, object);
    }

    @Override
    public View generate() {
        BaseLayoutBean bean = ParseJsonUtil.getBean(ParseJsonUtil.toJson(object), BaseLayoutBean.class);
        MTitle mTitle = new NavBar.Builder(getContext(), bean.getCommon().getHeight())
                .setBackColor(bean.getStyle().getBgColor())
                .setCenterText(bean.getCommon().getTitle(), Util.getRealColor(bean.getStyle().getTitleColor()), Util.getRealValue(bean.getStyle().getTitleFontSize()))
                .setLeftText(bean.getCommon().getBackText(), Util.getRealColor(bean.getStyle().getBackTextColor()), Util.getRealValue(bean.getStyle().getLeftFontSize()))
                .setLeftImage( bean.getStyle().getBackImgUrl())
                .build();

        FrameLayout.LayoutParams lp = FrameParamsBuilder.builder()
                .setWidth(FrameLayout.LayoutParams.MATCH_PARENT)
                .setHeight(100)
                .build();
        mTitle.setLayoutParams(lp);

        return mTitle;
    }
}
