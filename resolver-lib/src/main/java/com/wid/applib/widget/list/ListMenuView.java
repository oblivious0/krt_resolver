package com.wid.applib.widget.list;

import android.view.View;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.util.AdapterUtil;
import com.wid.applib.util.FrameParamsBuilder;
import com.wid.applib.widget.BaseView;

/**
 * author: MaGua
 * create on:2020/11/3 14:26
 * description
 */
public class ListMenuView extends BaseView<RecyclerView> {

    public ListMenuView(ContextImp imp, BaseLayoutBean obj) {
        super(imp, obj);
    }

    public ListMenuView(ContextImp imp, BaseLayoutBean obj, boolean isListChild) {
        super(imp, obj, isListChild);
    }

    @Override
    protected void initView() {
        type = "listMenu";
        view = new RecyclerView(contextImp.getContext());
        GridLayoutManager manager = new GridLayoutManager(contextImp.getContext(), bean.getCommon().getNum());
        view.setLayoutManager(manager);
        FrameLayout.LayoutParams lp = FrameParamsBuilder.builder()
                .setWidth(bean.getCommon().getWidth())
                .setHeight(bean.getCommon().getHeight())
                .setMarginLeft(bean.getCommon().getX())
                .setMarginTop(bean.getCommon().getY())
                .build();
        view.setLayoutParams(lp);
        view.setAdapter(new AdapterUtil.ListMenusAdapter(bean.getCommon().getList()));
        view.setVisibility(bean.getCommon().isIsHidden() ? View.GONE : View.VISIBLE);
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
}
