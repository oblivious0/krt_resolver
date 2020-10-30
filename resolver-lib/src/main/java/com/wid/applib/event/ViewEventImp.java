package com.wid.applib.event;

import android.view.View;

import com.wid.applib.bean.EventBean;

import java.util.List;


/**
 * @author hyj
 * @time 2020/7/17 10:46
 * @class describe
 */
public interface ViewEventImp {

    /**
     * 普通控件点击事件
     *
     * @param view      被点击的控件
     * @param eventBeans Event实体
     */
    void onViewClick(View view, List<EventBean> eventBeans);

    /**
     * 列表项点击事件
     *
     * @param view      被点击的控件所在的 RecyclerView 【方便getData()获取数据】
     * @param eventBeans Event实体
     * @param position  被点击目标在列表中的索引
     */
    void onDataListClick(View view, List<EventBean> eventBeans, int position);




}
