package com.wid.applib.view.widget;

import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.wid.applib.MLib;
import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.bean.EventBean;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.util.EventBindUtil;
import com.wid.applib.util.FrameParamsBuilder;
import com.wid.applib.util.Util;
import com.wid.applib.view.NavBar;

import java.util.ArrayList;
import java.util.List;

import krt.wid.util.MTitle;

/**
 * author: MaGua
 * create on:2020/11/3 14:43
 * description
 */
public class NavbarView extends BaseView<LinearLayout> {

    private MTitle mTitle;
    private List<EventBean> rightEvent = new ArrayList<>();

    public NavbarView(ContextImp imp, BaseLayoutBean obj) {
        super(imp, obj);
    }

    public NavbarView(ContextImp imp, BaseLayoutBean obj, boolean isListChild) {
        super(imp, obj, isListChild);
    }

    @Override
    protected void initView() {
        type = "nav";

        view = new LinearLayout(contextImp.getContext());

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        view.setOrientation(LinearLayout.VERTICAL);
        view.setBackgroundColor(Util.getRealColor(bean.getStyle().getBgColor()));
        NavBar.Builder build = new NavBar.Builder(contextImp.getContext(), bean.getCommon().getHeight());
        build.setBackColor(bean.getStyle().getBgColor());
        build.setCenterText(bean.getCommon().getTitle(),
                Util.getRealColor(bean.getStyle().getTitleColor()) == -1 ?
                        -2 : Util.getRealColor(bean.getStyle().getTitleColor()),
                Util.getRealValue(bean.getStyle().getTitleFontSize()));

        mTitle = build.build();

        //左边图标按钮设置
        if (bean.getStyle().isBackIcon()) {
            ImageView imageView = new ImageView(contextImp.getContext());
            if (!TextUtils.isEmpty(bean.getStyle().getBackImgUrl())) {
                Glide.with(contextImp.getContext())
                        .load(bean.getStyle().getBackImgUrl())
                        .override(50, 50)
                        .into(imageView);
            } else if (!TextUtils.isEmpty(bean.getStyle().getBackIconFileName())) {

            }
            mTitle.setCustomLeftView(imageView);
        } else {
            mTitle.setLeftText(bean.getCommon().getBackText(),
                    Util.getRealColor(bean.getStyle().getBackTextColor()),
                    Util.getRealValue(bean.getStyle().getLeftFontSize()));
        }

        //右边图标按钮设置
        if (bean.getStyle().isRightIcon()) {
            ImageView imageView = new ImageView(contextImp.getContext());
            if (!TextUtils.isEmpty(bean.getStyle().getRightImgUrl())) {
                Glide.with(contextImp.getContext())
                        .load(bean.getStyle().getRightImgUrl())
                        .override(50, 50)
                        .into(imageView);
            } else if (!TextUtils.isEmpty(bean.getStyle().getRightIconFileName())) {

            }
            mTitle.setCustomRightView(imageView);
        } else {
            mTitle.setRightText(bean.getCommon().getRightText(),
                    Util.getRealColor(bean.getStyle().getRightTextColor()),
                    Util.getRealValue(bean.getStyle().getRightFontSize()));
        }

        FrameLayout.LayoutParams lp1 = FrameParamsBuilder.builder()
                .setWidth(FrameLayout.LayoutParams.MATCH_PARENT)
                .setHeight(bean.getCommon().getHeight())
                .setMarginTop(60)
                .build();
        mTitle.setLayoutParams(lp1);
        view.addView(mTitle);
    }

    @Override
    protected boolean bindInNewThread() {
        return true;
    }

    @Override
    protected void bindInMainThread() {

    }

    @Override
    public void bindData(String cid, String key, String val) {

    }

    /***
     * 导航栏左边按钮默认是返回按钮，一般仅设置右边按钮事件
     */
    @Override
    public void bindEvent() {
        if (bean.getEvent() != null) {
            // 筛选android端匹配的事件
            if (bean.getEvent().size() != 0) {
                for (int z = 0; z < bean.getEvent().size(); z++) {
                    if (bean.getCommon().getRightEvent().contains(bean.getEvent().get(z).getCid())) {
                        if (bean.getEvent().get(z).getTerminal() == null ||
                                bean.getEvent().get(z).getTerminal().size() == 0) {
                            rightEvent.add(bean.getEvent().get(z));
                            continue;
                        }
                        if (bean.getEvent().get(z).getTerminal().contains(MLib.TERMINAL)) {
                            rightEvent.add(bean.getEvent().get(z));
                        }
                    }
                }
            }

            //独立执行绑定流程
            mTitle.setRightClickListener(() -> {
                        if (contextImp.getOnClickTool() == null) {
                            return;
                        }
                        contextImp.getOnClickTool().onViewClick(mTitle, rightEvent);
                    }
            );

        }
    }
}
