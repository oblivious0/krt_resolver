package com.wid.applib.view;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.PostRequest;
import com.lzy.okgo.request.base.Request;
import com.wid.applib.http.MJsonConvert;
import com.wid.applib.http.MResult;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import krt.wid.bean.event.MEventBean;
import krt.wid.http.MCallBack;
import krt.wid.util.MConstants;
import krt.wid.util.ParseJsonUtil;

import static com.wid.applib.http.AjaxUtil.requestBodys;


/**
 * @author MaGua
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
    public TCallBack callBack;
    private String ajaxCid;
    private String[] bindKeys;

    public MRecyclerView(@NonNull Context context, String[] bind) {
        super(context);
        mContext = (Activity) context;
        this.bindKeys = bind;
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
            swipeRefreshLayout.setOnRefreshListener(() -> {
                swipeRefreshLayout.setEnabled(false);
                page = initPage;
                start();
            });
        }
    }

    public void setPageAjax(Request request, String pageField, String sizeField, String ajaxCid) {
        this.request = request;
        this.pageField = pageField;
        this.sizeField = sizeField;
        this.ajaxCid = ajaxCid;
        swipeRefreshLayout.setEnabled(true);
    }

    public void start() {
        if (request != null) {
            callBack = new TCallBack(mContext, isPageTurning);
        }

        if (request.getHeaders().get("Content-Type").equals("application/json;charset=utf-8")) {
            JSONObject jsonObject = requestBodys.get(ajaxCid);
            jsonObject.remove(pageField);
            jsonObject.put(pageField, page);
            ((PostRequest) request).upJson(jsonObject.toJSONString());
        } else {
            request.params(pageField, page);
        }

        request.execute(callBack);
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

    public class TCallBack extends MCallBack<MResult> {

        public TCallBack(Activity activity) {
            super(activity);
        }

        public TCallBack(Activity activity, boolean showDialog) {
            super(activity, showDialog);
        }

        @Override
        public MResult convertResponse(okhttp3.Response response) throws Throwable {
            MJsonConvert<MResult> convert = new MJsonConvert<>(MResult.class);
            MResult result = convert.convertResponse(response);
            if (result != null) {
                if (!result.isSuccess()) {
                    EventBus.getDefault().post(new MEventBean(MConstants.ACTION_RESULT_CODE, result.code));
                }
            }

            return result;
        }

        @Override
        public void onSuccess(Response<MResult> response) {
            if (response.body().isSuccess()) {
                List<Object> list = new ArrayList<>();

                try {
                    String data = ParseJsonUtil.toJson(response.body().data);
                    String currentData = data;
                    for (int i = 1; i < bindKeys.length; i++) {
                        switch (bindKeys[i]) {
                            case "data":
                                currentData = ParseJsonUtil.getStringByKey(currentData, "data");
                                break;
                            case "Array":
                            case "array":
                                String res = JSON.toJSON(currentData).toString();
                                list = JSONArray.parseArray(res, Object.class);
                                break;
                            default:
                        }
                    }
                } catch (Exception e) {
                    //data%krt_data%krt_Array%krt_familySum

                }


                if (page == initPage) {
                    mAdapter.setNewData(list);
                } else {
                    mAdapter.addData(list);
                    mAdapter.loadMoreComplete();
                }

                if (response.body().totalPage == -1) {
                    if (list != null && list.size() < size) {
                        mAdapter.loadMoreEnd();
                    } else {
                        mAdapter.setEnableLoadMore(true);
                    }
                } else {
                    if (page == response.body().totalPage) {
                        mAdapter.loadMoreEnd();
                    } else {
                        mAdapter.setEnableLoadMore(true);
                    }
                }

                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }

            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setEnabled(isPageTurning);
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

}
