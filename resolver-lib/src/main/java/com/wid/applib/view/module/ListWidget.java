package com.wid.applib.view.module;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.wid.applib.R;
import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.bean.ParamBean;
import com.wid.applib.http.MJsonConvert;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.http.AjaxUtil;
import com.wid.applib.util.BindDataUtil;
import com.wid.applib.util.FrameParamsBuilder;
import com.wid.applib.util.Util;
import com.wid.applib.view.MRecyclerView;

import java.util.ArrayList;
import java.util.List;

import krt.wid.http.MCallBack;
import krt.wid.http.Result;
import krt.wid.util.ParseJsonUtil;

/**
 * author:Marcus
 * create on:2020/6/1 14:52
 * description
 */
public class ListWidget extends BaseWidget {

    private BaseLayoutBean bean;
    private List<Object> list = new ArrayList<>();

    public ListWidget(ContextImp context, Object object) {
        super(context, object);
    }

    public ListWidget(Context context, Object object) {
        super(context, object);
    }

    @Override
    public View generate() {
        bean = ParseJsonUtil.getBean(ParseJsonUtil.toJson(object), BaseLayoutBean.class);
        list = BindDataUtil.getDatas(bean);

        MRecyclerView recyclerView = new MRecyclerView(getContext());
        FrameLayout.LayoutParams lp = FrameParamsBuilder.builder()
//                .setWidth(bean.getCommon().getWidth())
                .setWidth(FrameLayout.LayoutParams.MATCH_PARENT)
//                .setHeight(bean.getCommon().getHeight())
                .setHeight(FrameLayout.LayoutParams.WRAP_CONTENT)
                .setMarginLeft(bean.getCommon().getX())
                .setMarginTop(bean.getCommon().getY())
                .build();
        recyclerView.setLayoutParams(lp);
        //判断是横向还是纵向滑动
        RecyclerView.LayoutManager lm = null;

        if (bean.getCommon().getDirection().equals("horizontal")) {
            if (bean.getCommon().isWrap()) {
                lm = new GridLayoutManager(getContext(), bean.getCommon().getHorizontalNum());
            } else {
                lm = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
            }
        } else {
            lm = new LinearLayoutManager(getContext());
        }

        recyclerView.setClipChildren(false);
        recyclerView.setLayoutManager(lm);
        recyclerView.setTag(R.id.cid, bean.getCid());
        final ListAdapter adapter = new ListAdapter();
        recyclerView.setAdapter(adapter);

        try {
            if (list == null) {

                MCallBack callBack = new MCallBack<Result>((Activity) getContext(), false) {
                    @Override
                    public void onSuccess(Response<Result> response) {
                        if (response.body().isSuccess()) {

                            list = ParseJsonUtil.getBeanList(
                                    ParseJsonUtil.toJson(response.body().data), Object.class);
                            adapter.setNewData(list);
                        }
                    }

                    @Override
                    public Result convertResponse(okhttp3.Response response) throws Throwable {
                        MJsonConvert<Result> convert = new MJsonConvert<>(Result.class);
                        return convert.convertResponse(response);
                    }
                };

                if (!TextUtils.isEmpty(bean.getAjax().get(0).getCid())) {
                    imp.getContainer("callback").put(bean.getAjax().get(0).getCid(), callBack);
                    imp.getContainer("ajax").put(bean.getAjax().get(0).getCid(), bean.getAjax().get(0));
                }

                Request request = AjaxUtil.assembleRequest(bean.getAjax().get(0), imp);
                if (!TextUtils.isEmpty(bean.getAjax().get(0).getSizeField())) {
                    SwipeRefreshLayout swipeRefreshLayout = null;
                    if (getContext() instanceof ContextImp) {
                        swipeRefreshLayout = ((ContextImp) getContext()).getSwipeRefreshLayout();
                    }
                    recyclerView.setSwipeRefreshLayout(swipeRefreshLayout);
                    recyclerView.setPageTurning(true, getVal(bean.getAjax().get(0).getSizeField(),
                            bean.getAjax().get(0).getData()));
                    recyclerView.setPageAjax(request,
                            bean.getAjax().get(0).getPageField(),
                            bean.getAjax().get(0).getSizeField());
                    recyclerView.setInitPage(getVal(bean.getAjax().get(0).getPageField(), bean.getAjax().get(0).getData()));
                    recyclerView.start();
                } else {
                    request.execute(callBack);
                }
            }
        } catch (Exception e) {
        }

        return recyclerView;
    }

    private int getVal(String field, List<ParamBean> paramBeans) {
        for (ParamBean bean : paramBeans) {
            if (bean.getKey().equals(field)) {
                return Integer.parseInt(bean.getVal());
            }
        }
        return 10;
    }

    private class ListAdapter extends BaseQuickAdapter<Object, BaseViewHolder> {

        public ListAdapter() {
            super(R.layout.item_frame, list);
        }

        @Override
        protected void convert(BaseViewHolder helper, Object item) {
            FrameLayout frameLayout = (FrameLayout) helper.itemView;
            FrameLayout.LayoutParams lp = FrameParamsBuilder.builder()
                    .setWidth(bean.getCommon().getItemW())
                    .setHeight(bean.getCommon().getItemH())
                    .setMarginRight(bean.getCommon().getxPadding())
                    .setMarginBottom(bean.getCommon().getyPadding())
                    .build();
            frameLayout.setLayoutParams(lp);
            GradientDrawable drawable = Util.getBgDrawable(bean.getStyle().getBgColor(),
                    GradientDrawable.RECTANGLE, bean.getStyle().getBorderRadius(), bean.getStyle().getBorderWidth(),
                    bean.getStyle().getBorderColor());
            frameLayout.setBackgroundDrawable(drawable);
            if (bean.getCid().equals("1090m4mk5xl")) {
//                LogUtils.e(list);
            }
            BindDataUtil.bindListDatas(bean, imp, frameLayout, item);
        }
    }
}
