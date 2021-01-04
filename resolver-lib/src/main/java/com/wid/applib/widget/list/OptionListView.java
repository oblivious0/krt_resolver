package com.wid.applib.widget.list;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.wid.applib.MLib;
import com.wid.applib.R;
import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.bean.EventBean;
import com.wid.applib.http.AjaxUtil;
import com.wid.applib.http.MJsonConvert;
import com.wid.applib.http.MResult;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.util.BindDataUtil;
import com.wid.applib.util.FrameParamsBuilder;
import com.wid.applib.util.Util;
import com.wid.applib.util.ViewValue;
import com.wid.applib.widget.BaseView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import krt.wid.http.MCallBack;
import krt.wid.util.ParseJsonUtil;

/**
 * author: MaGua
 * create on:2020/12/23 14:48
 * description
 */
public class OptionListView extends BaseView<RecyclerView> {

    private List<Object> list;
    private OptionAdapter mAdapter;
    private String data;

    private BaseLayoutBean defaultLayout;
    private BaseLayoutBean highlightLayout;

    public OptionListView(ContextImp imp, BaseLayoutBean obj) {
        super(imp, obj);
    }

    public OptionListView(ContextImp imp, BaseLayoutBean obj, boolean isListChild) {
        super(imp, obj, isListChild);
    }

    @Override
    protected void initView() {
        type = "selectList";
        list = BindDataUtil.getDatas(bean);

        for (BaseLayoutBean baseLayoutBean : bean.getChildren()) {
            if (baseLayoutBean.getType().equals(ViewValue.LAYOUT)) {

                //重置所有layout的定位
                baseLayoutBean.getCommon().setX(0);
                baseLayoutBean.getCommon().setY(0);

                if (bean.getCommon().getHighLightId().equals(baseLayoutBean.getCid())) {
                    highlightLayout = baseLayoutBean;
                } else {
                    if (defaultLayout == null) {
                        defaultLayout = baseLayoutBean;
                    }
                }
            }
        }

        view = new RecyclerView(contextImp.getContext());
        FrameLayout.LayoutParams lp = FrameParamsBuilder.builder()
                .setWidth(bean.getCommon().getWidth())
                .setHeight(bean.getCommon().getHeight())
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
        mAdapter = new OptionAdapter();
        view.setAdapter(mAdapter);

        if (list == null) {
            Request request = AjaxUtil.assembleRequest(bean.getAjax().get(0), contextImp);
            MCallBack callBack = new MCallBack<MResult>((Activity) contextImp.getContext(), false) {
                @Override
                public void onSuccess(Response<MResult> response) {
                    try {
                        data = ParseJsonUtil.toJson(response.body().data);
                        String[] str = bean.getAjax().get(0).getBindData().get(0).getBindKeys();
                        String currentData = data;
                        for (int i = 1; i < str.length; i++) {
                            switch (str[i]) {
                                case "data":
                                    currentData = ParseJsonUtil.getStringByKey(currentData, "data");
                                    break;
                                case "Array":
                                case "array":
                                    String res = JSON.toJSON(currentData).toString();
                                    list = JSONArray.parseArray(res, Object.class);
                                    return;
                                default:
                            }
                        }
                    } catch (Exception e) {
                        //data%krt_data%krt_Array%krt_familySum

                    } finally {
                        mAdapter.setNewData(list);
                    }
                }

                @Override
                public MResult convertResponse(okhttp3.Response response) throws Throwable {
                    MJsonConvert<MResult> convert = new MJsonConvert<>(MResult.class);
                    return convert.convertResponse(response);
                }
            };

            if (!TextUtils.isEmpty(bean.getAjax().get(0).getCid())) {
                contextImp.getContainer("callback").put(bean.getAjax().get(0).getCid(), callBack);
                contextImp.getContainer("ajax").put(bean.getAjax().get(0).getCid(), bean.getAjax().get(0));
            }

            request.execute(callBack);
        } else {
            mAdapter.setNewData(list);
        }

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

    @Override
    public void bindEvent() {

        List<EventBean> events = new ArrayList<>();
        if (bean.getEvent() != null) {
            if (bean.getEvent().size() != 0) {
                for (int z = 0; z < bean.getEvent().size(); z++) {
                    if (bean.getEvent().get(z).getTerminal() == null ||
                            bean.getEvent().get(z).getTerminal().size() == 0) {
                        events.add(bean.getEvent().get(z));
                        continue;
                    }
                    if (bean.getEvent().get(z).getTerminal().contains(MLib.TERMINAL)) {
                        events.add(bean.getEvent().get(z));
                    }
                }
            }
        }

        Flowable.intervalRange(0, 2, 0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(aLong -> {
                })
                .doOnComplete(() -> {
                    contextImp.getOnClickTool().onDataListClick(view, events, mAdapter.getCurrentSelectedIndex());
                })
                .subscribe();

        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            //添加配置的点击事件
            if (contextImp.getOnClickTool() != null) {
                contextImp.getOnClickTool().onDataListClick(this.view, events, position);
            }

            //除了配置的点击事件，还有组件本身的事件要完成
            mAdapter.setCurrentSelectedIndex(position);
        });

        view.setAdapter(mAdapter);
    }

    private class OptionAdapter extends BaseQuickAdapter<Object, BaseViewHolder> {

        int currentSelectedIndex = 0;

        public int getCurrentSelectedIndex() {
            return currentSelectedIndex;
        }

        public void setCurrentSelectedIndex(int currentSelectedIndex) {
            int tempIndex = this.currentSelectedIndex;
            this.currentSelectedIndex = currentSelectedIndex;

            mAdapter.notifyItemChanged(tempIndex);
            mAdapter.notifyItemChanged(currentSelectedIndex);

        }

        public OptionAdapter() {
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
            List<BaseView> baseViewList = BindDataUtil.bindListDatas(bean, contextImp, frameLayout, item);
            for (BaseView base : baseViewList) {
                if (helper.getPosition() == currentSelectedIndex) {
                    if (base.cid.equals(highlightLayout.getCid())) {
                        base.view.setVisibility(View.VISIBLE);
                    } else {
                        base.view.setVisibility(View.GONE);
                    }
                } else {
                    if (base.cid.equals(defaultLayout.getCid())) {
                        base.view.setVisibility(View.VISIBLE);
                    } else {
                        base.view.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

}
