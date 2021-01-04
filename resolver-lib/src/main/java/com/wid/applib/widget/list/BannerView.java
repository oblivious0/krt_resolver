package com.wid.applib.widget.list;

import android.app.Activity;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.lzy.okgo.model.Response;
import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.http.MJsonConvert;
import com.wid.applib.http.MResult;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.http.AjaxUtil;
import com.wid.applib.util.BindDataUtil;
import com.wid.applib.util.FrameParamsBuilder;
import com.wid.applib.util.Util;
import com.wid.applib.widget.BaseView;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerAdapter;
import com.youth.banner.config.IndicatorConfig;
import com.youth.banner.indicator.CircleIndicator;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import krt.wid.bean.event.MEventBean;
import krt.wid.http.MCallBack;
import krt.wid.util.MConstants;
import krt.wid.util.ParseJsonUtil;

/**
 * author: MaGua
 * create on:2020/11/3 14:48
 * description
 */
public class BannerView extends BaseView<Banner> {

    private List<Object> list;
    private ImageAdapter adapter;
    private String data = "";

    public BannerView(ContextImp imp, BaseLayoutBean obj) {
        super(imp, obj);
    }

    public BannerView(ContextImp imp, BaseLayoutBean obj, boolean isListChild) {
        super(imp, obj, isListChild);
    }

    @Override
    protected void initView() {
        type = "banner";
        list = BindDataUtil.getDatas(bean);
        view = new Banner(contextImp.getContext());
        FrameLayout.LayoutParams lp = FrameParamsBuilder.builder()
                .setWidth(bean.getCommon().getWidth())
                .setHeight(bean.getCommon().getHeight())
                .setMarginLeft(bean.getCommon().getX())
                .setMarginTop(bean.getCommon().getY())
                .build();
        view.setLayoutParams(lp);
        adapter = new ImageAdapter();
        CircleIndicator circleIndicator = new CircleIndicator(contextImp.getContext());
        view.setAdapter(adapter)
                .setDelayTime(bean.getStyle().getSlidingInterval() * 1000)
                .setIndicator(circleIndicator, bean.getStyle().isIndicatorShow())
                .setIndicatorGravity(getIndicatorGravity(bean.getStyle().getIndicatorsAlignment()))
                .setIndicatorNormalColor(Util.getRealColor(bean.getStyle().getFillColor()))
                .setIndicatorSelectedColor(Util.getRealColor(bean.getStyle().getSelectFillColor()))
                .setIndicatorWidth(bean.getStyle().getIndicatorWidth() * 2,
                        bean.getStyle().getIndicatorWidth() * 2)
                .start();

        if (list == null) {
            MCallBack callBack = new MCallBack<MResult>((Activity) contextImp.getContext(), false) {
                @Override
                public void onSuccess(Response<MResult> response) {
                    if (response.body().code == 200) {


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
                            adapter.setDatas(list);
                            adapter.notifyDataSetChanged();
                        }
                        list = ParseJsonUtil.getBeanList(
                                ParseJsonUtil.toJson(response.body().data), Object.class);
                        adapter.setDatas(list);
                        adapter.notifyDataSetChanged();
                    }
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
                }            };

            if (!TextUtils.isEmpty(bean.getAjax().get(0).getCid())) {
                contextImp.getContainer("callback").put(bean.getAjax().get(0).getCid(), callBack);
                contextImp.getContainer("ajax").put(bean.getAjax().get(0).getCid(), bean.getAjax().get(0));
            }

            try {
                AjaxUtil.assembleRequest(bean.getAjax().get(0), contextImp)
                        .execute(callBack);
            } catch (Exception e) {

            }
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

    private int getIndicatorGravity(@NonNull String gravity) {
        switch (gravity) {
            case "left":
                return IndicatorConfig.Direction.LEFT;
            case "right":
                return IndicatorConfig.Direction.RIGHT;
            default:
                //默认居中
                return IndicatorConfig.Direction.CENTER;
        }
    }


    public class ImageAdapter extends BannerAdapter<Object, ImageAdapter.BannerViewHolder> {

        public ImageAdapter() {
            super(list);
        }

        @Override
        public BannerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
            FrameLayout frameLayout = new FrameLayout(parent.getContext());
            /**
             * 宽度和 ！高度 必须改成 match_parent
             * 否则会抛出异常 IllegalStateException: Pages must fill the whole ViewPager2 (use match_parent)
             */
            frameLayout.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            return new BannerViewHolder(frameLayout);
        }

        @Override
        public void onBindView(BannerViewHolder holder, Object data, int position, int size) {
            FrameLayout frameLayout = (FrameLayout) holder.itemView;
            BindDataUtil.bindListDatas(bean, contextImp, frameLayout, data);

        }

        class BannerViewHolder extends RecyclerView.ViewHolder {
            public BannerViewHolder(@NonNull FrameLayout view) {
                super(view);
            }
        }
    }

}
