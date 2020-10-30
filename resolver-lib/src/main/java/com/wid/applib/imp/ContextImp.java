package com.wid.applib.imp;

import android.content.Context;
import android.widget.FrameLayout;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.wid.applib.event.ViewEventImp;

import java.util.HashMap;

/**
 * @author hyj
 * @time 2020/8/19 10:03
 * @class describe
 */
public interface ContextImp {

    /**
     * 获取模块化界面CID
     *
     * @return
     */
    String getPageId();

    /**
     * 获取模块化界面头部标题栏视图
     *
     * @return
     */
    FrameLayout getHeaderView();

    /**
     * 获取模块化界面内容视图
     *
     * @return
     */
    FrameLayout getContentView();

    /**
     * 获取界面列表刷新组件
     *
     * @return
     */
    SwipeRefreshLayout getSwipeRefreshLayout();

    /**
     * 获取上下文类
     *
     * @return
     */
    Context getContext();

    ConvertImp getConvertTool();

    ViewEventImp getOnClickTool();

    HashMap getContainer(String type);

    boolean isLogin();

    String getAuthorization();

}
