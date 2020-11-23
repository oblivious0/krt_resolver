package com.wid.applib.view.module;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lzy.okgo.model.Response;
import com.wid.applib.R;
import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.http.MJsonConvert;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.http.AjaxUtil;
import com.wid.applib.util.BindDataUtil;
import com.wid.applib.util.FrameParamsBuilder;
import com.wid.applib.util.Util;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerAdapter;
import com.youth.banner.config.IndicatorConfig;
import com.youth.banner.indicator.CircleIndicator;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import krt.wid.bean.event.MEventBean;
import krt.wid.http.MCallBack;
import krt.wid.http.Result;
import krt.wid.util.MConstants;
import krt.wid.util.ParseJsonUtil;

/**
 * @author hyj
 * @time 2020/7/3 17:40
 * @class describe
 */
public class BannerWidget extends BaseWidget {

    private BaseLayoutBean bean;
    private List<Object> list = new ArrayList<>();

    public BannerWidget(ContextImp context, Object object) {
        super(context, object);
    }

    public BannerWidget(Context context, Object object) {
        super(context, object);
    }

    @Override
    public View generate() {

        bean = ParseJsonUtil.getBean(ParseJsonUtil.toJson(object), BaseLayoutBean.class);
        list = BindDataUtil.getDatas(bean);
        Banner banner = new Banner(getContext());
        FrameLayout.LayoutParams lp = FrameParamsBuilder.builder()
                .setWidth(bean.getCommon().getWidth())
                .setHeight(bean.getCommon().getHeight())
                .setMarginLeft(bean.getCommon().getX())
                .setMarginTop(bean.getCommon().getY())
                .build();
        banner.setLayoutParams(lp);

        final ImageAdapter adapter = new ImageAdapter();

        CircleIndicator circleIndicator = new CircleIndicator(getContext());
        banner.setAdapter(adapter)
                .setDelayTime(bean.getStyle().getSlidingInterval() * 1000)
                .setIndicator(circleIndicator, bean.getStyle().isIndicatorShow())
                .setIndicatorGravity(getIndicatorGravity(bean.getStyle().getIndicatorsAlignment()))
                .setIndicatorNormalColor(Util.getRealColor(bean.getStyle().getFillColor()))
                .setIndicatorSelectedColor(Util.getRealColor(bean.getStyle().getSelectFillColor()))
                .setIndicatorWidth(bean.getStyle().getIndicatorWidth() * 2,
                        bean.getStyle().getIndicatorWidth() * 2)
                .start();

        if (list == null) {
//            if (bean.getAjax().equals(""))

            MCallBack callBack = new MCallBack<Result>((Activity) getContext(), false) {
                @Override
                public void onSuccess(Response<Result> response) {
                    if (response.body().code == 200) {
                        list = ParseJsonUtil.getBeanList(
                                ParseJsonUtil.toJson(response.body().data), Object.class);
                        adapter.setDatas(list);
                        adapter.notifyDataSetChanged();
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
            };

            if (!TextUtils.isEmpty(bean.getAjax().get(0).getCid())) {
                imp.getContainer("callback").put(bean.getAjax().get(0).getCid(), callBack);
                imp.getContainer("ajax").put(bean.getAjax().get(0).getCid(), bean.getAjax().get(0));
            }

            try {
                AjaxUtil.assembleRequest(bean.getAjax().get(0), imp)
                        .execute(callBack);
            }catch (Exception e){

            }
        }

        banner.setTag(R.id.cid, bean.getCid());

        return banner;
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
            BindDataUtil.bindListDatas(bean, imp, frameLayout, data);

        }

        class BannerViewHolder extends RecyclerView.ViewHolder {
            public BannerViewHolder(@NonNull FrameLayout view) {
                super(view);
            }
        }
    }

}
