package com.wid.applib.util;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.wid.applib.bean.EventBean;
import com.wid.applib.event.ViewEventImp;
import com.wid.applib.event.click.OnMultiClickListener;
import com.wid.applib.event.click.OnMultiItemClickListener;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerAdapter;
import com.youth.banner.listener.OnBannerListener;

import java.util.List;

/**
 * @author hyj
 * @time 2020/7/17 14:34
 * @class describe
 */
public class EventBindUtil {

    public static void bindClick(View view, final ViewEventImp eventListener, final List<EventBean> eventBeans) {

        if (eventListener == null) {
            return;
        }

        if (view instanceof RecyclerView) {
            final RecyclerView recyclerView = (RecyclerView) view;
            BaseQuickAdapter adapter = (BaseQuickAdapter) recyclerView.getAdapter();
            if (adapter != null) {
                adapter.setOnItemClickListener((adapter1, view1, position) ->
                        eventListener.onDataListClick(recyclerView, eventBeans, position));
                recyclerView.setAdapter(adapter);
            }
        } else if (view instanceof Banner) {
            final Banner banner = (Banner) view;
            BannerAdapter adapter = banner.getAdapter();
            if (adapter != null) {
                adapter.setOnBannerListener((data, position) ->
                        eventListener.onDataListClick(banner, eventBeans, position));
            }
        } else {
            view.setOnClickListener(view12 ->
                    eventListener.onViewClick(view12, eventBeans));
        }
    }

}
