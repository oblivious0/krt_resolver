package com.wid.applib.view.module;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.util.AdapterUtil;
import com.wid.applib.util.FrameParamsBuilder;

import krt.wid.util.ParseJsonUtil;

/**
 * author:Marcus
 * create on:2020/5/28 8:54
 * description
 */
public class ListMenus extends BaseWidget {

    public ListMenus(ContextImp context, Object object) {
        super(context, object);
    }

    public ListMenus(Context context, Object object) {
        super(context, object);
    }

    @Override
    public View generate() {
        BaseLayoutBean bean = ParseJsonUtil.getBean(ParseJsonUtil.toJson(object), BaseLayoutBean.class);
        RecyclerView recyclerView = new RecyclerView(getContext());
        GridLayoutManager manager = new GridLayoutManager(getContext(), bean.getCommon().getNum());
        recyclerView.setLayoutManager(manager);
        FrameLayout.LayoutParams lp = FrameParamsBuilder.builder()
                .setWidth(bean.getCommon().getWidth())
                .setHeight(bean.getCommon().getHeight())
                .setMarginLeft(bean.getCommon().getX())
                .setMarginTop(bean.getCommon().getY())
                .build();
        recyclerView.setTag(bean.getCid());
        recyclerView.setLayoutParams(lp);
        recyclerView.setAdapter(new AdapterUtil.ListMenusAdapter(bean.getCommon().getList()));
        recyclerView.setVisibility(bean.getCommon().isIsHidden() ? View.GONE : View.VISIBLE);
        return recyclerView;
    }
}
