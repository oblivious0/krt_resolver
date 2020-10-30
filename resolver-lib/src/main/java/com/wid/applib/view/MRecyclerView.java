package com.wid.applib.view;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.wid.applib.http.MJsonConvert;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import krt.wid.bean.event.MEventBean;
import krt.wid.http.MCallBack;
import krt.wid.http.Result;
import krt.wid.util.MConstants;
import krt.wid.util.ParseJsonUtil;


/**
 * @author hyj
 * @time 2020/7/31 9:21
 * @class describe
 */
public class MRecyclerView extends RecyclerView {

    private int page = 1, size = 0, initPage = 1;
    private boolean isPageTurning = false;
    private Request request;
    private String pageField = "", sizeField = "";
    private SwipeRefreshLayout swipeRefreshLayout;
    private Activity mContext;
    private BaseQuickAdapter<Object, BaseViewHolder> mAdapter;

    public MRecyclerView(@NonNull Context context) {
        super(context);
        mContext = (Activity) context;
    }

    public void setPageTurning(boolean pageTurning, int size) {
        isPageTurning = pageTurning;
        if (isPageTurning) {
            this.size = size < 1 ? 1 : size;
        }
        mAdapter.setEnableLoadMore(pageTurning);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setEnabled(false);
                }
                page++;
                start();
            }
        }, this);
    }

    public void setInitPage(int initPage) {
        this.initPage = initPage;
        page = initPage;
    }

    public void setSwipeRefreshLayout(final SwipeRefreshLayout swipeRefreshLayout) {
        if (swipeRefreshLayout != null) {
            this.swipeRefreshLayout = swipeRefreshLayout;
            swipeRefreshLayout.setEnabled(true);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshLayout.setEnabled(false);
                    page = initPage;
                    start();
                }
            });
        }
    }

    public void setPageAjax(Request request, String pageField, String sizeField) {
        this.request = request;
        this.pageField = pageField;
        this.sizeField = sizeField;
        swipeRefreshLayout.setEnabled(true);
    }

    public void start() {
        if (request != null) {
            request.params(pageField, page)
                    .execute(new MCallBack<Result>(mContext, isPageTurning) {
                        @Override
                        public void onSuccess(Response<Result> response) {
                            if (response.body().isSuccess()) {
                                List<Object> list = ParseJsonUtil.getBeanList(
                                        ParseJsonUtil.toJson(response.body().data), Object.class);

                                if (page == initPage) {
                                    mAdapter.setNewData(list);
                                } else {
                                    mAdapter.addData(list);
                                    mAdapter.loadMoreComplete();
                                }

                                if (list.size() < size) {
                                    mAdapter.loadMoreEnd();
                                } else {
                                    mAdapter.setEnableLoadMore(true);

                                }

                                if (swipeRefreshLayout.isRefreshing()) {
                                    swipeRefreshLayout.setRefreshing(false);
                                }

                            }
                        }

                        @Override
                        public Result convertResponse(okhttp3.Response response) throws Throwable {
                            MJsonConvert<Result> convert = new MJsonConvert<>(Result.class);
                            Result result = convert.convertResponse(response);
                            if (result!=null){
                                if (!result.isSuccess()){
                                    EventBus.getDefault().post(new MEventBean(MConstants.ACTION_RESULT_CODE, result.code));
                                }
                            }

                            return result;
                        }

                        @Override
                        public void onFinish() {
                            super.onFinish();
                            if (swipeRefreshLayout != null) {
                                swipeRefreshLayout.setEnabled(isPageTurning);
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }
                    });
        }
    }

    private BaseQuickAdapter<Object, BaseViewHolder> getBaseAdapter() {
        if (mAdapter == null) {
            mAdapter = (BaseQuickAdapter<Object, BaseViewHolder>) getAdapter();
        }
        return mAdapter;
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        super.setAdapter(adapter);
        getBaseAdapter();
    }

}
