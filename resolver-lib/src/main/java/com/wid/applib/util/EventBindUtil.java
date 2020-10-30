package com.wid.applib.util;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.wid.applib.bean.EventBean;
import com.wid.applib.event.ViewEventImp;
import com.wid.applib.util.click.OnMultiClickListener;
import com.wid.applib.util.click.OnMultiItemClickListener;
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
                adapter.setOnItemClickListener(new OnMultiItemClickListener() {
                    @Override
                    public void onMultiClick(BaseQuickAdapter adapter, View view, int position) {
                        eventListener.onDataListClick(recyclerView, eventBeans, position);
                    }
                });
            }
        } else if (view instanceof Banner) {
            final Banner banner = (Banner) view;
            BannerAdapter adapter = banner.getAdapter();
            if (adapter != null) {
                adapter.setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(Object data, int position) {
                        eventListener.onDataListClick(banner, eventBeans, position);
                    }
                });
            }
        }else{
            view.setOnClickListener(new OnMultiClickListener() {
                @Override
                public void onMultiClick(View v) {
                    eventListener.onViewClick(v, eventBeans);
                }
            });
        }
    }

}
