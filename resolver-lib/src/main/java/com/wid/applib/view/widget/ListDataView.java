package com.wid.applib.view.widget;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
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
import com.wid.applib.util.AjaxUtil;
import com.wid.applib.util.BindDataUtil;
import com.wid.applib.util.FrameParamsBuilder;
import com.wid.applib.util.Util;
import com.wid.applib.view.MRecyclerView;
import com.wid.applib.view.module.ListWidget;

import java.util.ArrayList;
import java.util.List;

import krt.wid.http.MCallBack;
import krt.wid.http.Result;
import krt.wid.util.ParseJsonUtil;

/**
 * author: MaGua
 * create on:2020/11/3 14:30
 * description
 */
public class ListDataView extends BaseView<MRecyclerView> {

    private List<Object> list = new ArrayList<>();

    public ListDataView(ContextImp imp, BaseLayoutBean obj) {
        super(imp, obj);
    }

    public ListDataView(ContextImp imp, BaseLayoutBean obj, boolean isListChild) {
        super(imp, obj, isListChild);
    }

    @Override
    protected void initView() {
        type = "list";
        list = BindDataUtil.getDatas(bean);
        view = new MRecyclerView(contextImp.getContext());
        FrameLayout.LayoutParams lp = FrameParamsBuilder.builder()
                .setWidth(FrameLayout.LayoutParams.MATCH_PARENT)
                .setHeight(FrameLayout.LayoutParams.WRAP_CONTENT)
                .setMarginLeft(bean.getCommon().getX())
                .setMarginTop(bean.getCommon().getY())
                .build();
        view.setLayoutParams(lp);

        //判断是横向还是纵向滑动
        RecyclerView.LayoutManager lm = null;

        if (bean.getCommon().getDirection().equals("horizontal")) {
            if (bean.getCommon().isWrap()) {
                lm = new GridLayoutManager(contextImp.getContext(), bean.getCommon().getHorizontalNum());
            } else {
                lm = new LinearLayoutManager(contextImp.getContext(), RecyclerView.HORIZONTAL, false);
            }
        } else {
            lm = new LinearLayoutManager(contextImp.getContext());
        }

        view.setClipChildren(false);
        view.setLayoutManager(lm);
        view.setTag(R.id.cid, bean.getCid());
        ListAdapter adapter = new ListAdapter();
        view.setAdapter(adapter);

        try {
            if (list == null) {

                MCallBack callBack = new MCallBack<Result>((Activity) contextImp.getContext(), false) {
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
                    contextImp.getContainer("callback").put(bean.getAjax().get(0).getCid(), callBack);
                    contextImp.getContainer("ajax").put(bean.getAjax().get(0).getCid(), bean.getAjax().get(0));
                }

                Request request = AjaxUtil.assembleRequest(bean.getAjax().get(0), contextImp);
                if (!TextUtils.isEmpty(bean.getAjax().get(0).getSizeField())) {
                    SwipeRefreshLayout swipeRefreshLayout = null;
                    if (contextImp.getContext() instanceof ContextImp) {
                        swipeRefreshLayout = ((ContextImp) contextImp.getContext()).getSwipeRefreshLayout();
                    }
                    view.setSwipeRefreshLayout(swipeRefreshLayout);
                    view.setPageTurning(true, getVal(bean.getAjax().get(0).getSizeField(),
                            bean.getAjax().get(0).getData()));
                    view.setPageAjax(request,
                            bean.getAjax().get(0).getPageField(),
                            bean.getAjax().get(0).getSizeField());
                    view.setInitPage(getVal(bean.getAjax().get(0).getPageField(), bean.getAjax().get(0).getData()));
                    view.start();
                } else {
                    request.execute(callBack);
                }
            }
        } catch (Exception e) {
        }
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
            BindDataUtil.bindListDatas(bean, contextImp, frameLayout, item);
        }
    }
}
