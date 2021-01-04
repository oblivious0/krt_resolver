package com.wid.applib.widget.combination;

import android.widget.FrameLayout;

import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.util.FrameParamsBuilder;
import com.wid.applib.util.Util;
import com.wid.applib.view.CountDown;
import com.wid.applib.widget.BaseView;


/**
 * author: MaGua
 * create on:2020/11/27 15:21
 * description 倒计时组件
 */
public class CountdownView extends BaseView<CountDown> {

    public CountdownView(ContextImp imp, BaseLayoutBean obj) {
        super(imp, obj);
    }

    public CountdownView(ContextImp imp, BaseLayoutBean obj, boolean isListChild) {
        super(imp, obj, isListChild);
    }

    @Override
    protected void initView() {
        type = "countDown";
        FrameLayout.LayoutParams lp = FrameParamsBuilder.builder()
                .setWidth(bean.getCommon().getWidth())
                .setHeight(bean.getCommon().getHeight())
                .setMarginLeft(bean.getCommon().getX())
                .setMarginTop(bean.getCommon().getY())
                .build();
        CountDown countDown = new CountDown(contextImp.getContext());
        countDown.setLayoutParams(lp);
        countDown.setIsShowBorder(bean.getCommon().isShowBorder(), bean.getCommon().getBorderColor(),
                bean.getCommon().getBgColor());
        countDown.setTextStyle(bean.getCommon().getFontSize(),
                Util.getRealColor(bean.getCommon().getColor()));
        countDown.setSeparator(bean.getCommon().getSeparator(),
                bean.getCommon().getSeparatorSize(),
                Util.getRealColor(bean.getCommon().getSeparatorColor()));
        countDown.setHidZeroDay(bean.getCommon().isHideZeroDay());
        countDown.isShow(bean.getCommon().isShowDays(),
                bean.getCommon().isShowHours(),
                bean.getCommon().isShowMinutes(),
                bean.getCommon().isShowSeconds());
        countDown.setTimeStamp(bean.getCommon().getTimestamp());
        view = countDown;
    }

    @Override
    protected boolean bindInNewThread() {
        return true;
    }

    @Override
    protected void bindInMainThread() {
        view.start();
    }

    @Override
    public void bindData(String cid, String key, String val) {

    }


}
