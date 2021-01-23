package com.wid.applib.widget.list;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.wid.applib.R;
import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.bean.ParamBean;
import com.wid.applib.http.AjaxUtil;
import com.wid.applib.http.MJsonConvert;
import com.wid.applib.http.MResult;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.util.BindDataUtil;
import com.wid.applib.util.FrameParamsBuilder;
import com.wid.applib.util.Util;
import com.wid.applib.view.MRecyclerView;
import com.wid.applib.widget.BaseView;

import java.util.ArrayList;
import java.util.List;

import krt.wid.http.MCallBack;
import krt.wid.util.ParseJsonUtil;

/**
 * @author xzy
 * @package com.wid.applib.widget.list
 * @description 瀑布流组件
 * @time 2021/1/19
 */
public class WaterFallListView extends BaseView<MRecyclerView> {

    private List<Object> list = new ArrayList<>();
    private String data = "";

    public WaterFallListView(ContextImp imp,BaseLayoutBean obj){
        super(imp, obj);
    }

    public WaterFallListView(ContextImp imp, BaseLayoutBean obj, boolean isListChild) {
        super(imp, obj,isListChild);
    }

    @Override
    protected void initView() {
        type = "waterfall";
        list = BindDataUtil.getDatas(bean);
        String[] str;
        if (list == null) {
            str = bean.getAjax().get(0).getBindData().get(0).getBindKeys();
        } else {
            str = bean.getStaticData().getBindData().get(0).getBindKeys();
        }
        view = new MRecyclerView(contextImp.getContext(), str);

        //当为列表页时，长度拉满留余一小部分，且置顶
        FrameLayout.LayoutParams lp = FrameParamsBuilder.builder()
                .setWidth(FrameLayout.LayoutParams.MATCH_PARENT)
                .setHeight(contextImp.getPageType().equals("list") ?
                        ((Activity) contextImp.getContext()).getWindowManager().getDefaultDisplay().getHeight() - 60
                        : bean.getCommon().getHeight())
                .setMarginLeft(bean.getCommon().getX())
                .setMarginTop(bean.getCommon().getY())
                .build();
        view.setLayoutParams(lp);
        view.setClipChildren(false);
        view.setLayoutManager(new StaggeredGridLayoutManager(bean.getCommon().getHorizontalNum(), StaggeredGridLayoutManager.VERTICAL));
        view.setTag(R.id.cid, bean.getCid());
        ListAdapter adapter = new ListAdapter();
        view.setAdapter(adapter);
        try {
            if (list == null) {
                Request request = AjaxUtil.assembleRequest(bean.getAjax().get(0), contextImp);
                if (!TextUtils.isEmpty(bean.getAjax().get(0).getSizeField())) {
                    SwipeRefreshLayout swipeRefreshLayout = (contextImp).getSwipeRefreshLayout();
                    view.setSwipeRefreshLayout(swipeRefreshLayout);
                    view.setPageTurning(true, getVal(bean.getAjax().get(0).getSizeField(),
                            bean.getAjax().get(0).getData()));
                    view.setPageAjax(request,
                            bean.getAjax().get(0).getPageField(),
                            bean.getAjax().get(0).getSizeField(),
                            bean.getAjax().get(0).getCid());
                    view.setInitPage(getVal(bean.getAjax().get(0).getPageField(), bean.getAjax().get(0).getData()));
                    view.start();

                    if (!TextUtils.isEmpty(bean.getAjax().get(0).getCid())) {
                        contextImp.getContainer("callback").put(bean.getAjax().get(0).getCid(), view.callBack);
                        contextImp.getContainer("ajax").put(bean.getAjax().get(0).getCid(), bean.getAjax().get(0));
                    }

                } else {

                    MCallBack callBack = new MCallBack<MResult>((Activity) contextImp.getContext(), false) {
                        @Override
                        public void onSuccess(Response<MResult> response) {
                            if (response.body().isSuccess()) {

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
                                    adapter.setNewData(list);
                                }

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
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private int getVal(String field, List<ParamBean> paramBeans) {
        for (ParamBean bean : paramBeans) {
            if (bean.getKey().equals(field)) {
                return Integer.parseInt(bean.getVal());
            }
        }
        return 10;
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


    private class ListAdapter extends BaseQuickAdapter<Object, BaseViewHolder> {

         public ListAdapter() {
             super(R.layout.layout_waterfull, list);
        }

        @Override
        public int getItemViewType(int position) {
            return position % bean.getChildren().size();
        }

        @Override
        protected void convert(BaseViewHolder helper, Object item) {


            CardView cardView = (CardView) helper.itemView;
            FrameLayout frameLayout = (FrameLayout) helper.getView(R.id.frame);
            if (bean.getChildren().get(helper.getItemViewType()).getCommon() != null && Util.getRealColor(bean.getChildren().get(helper.getItemViewType()).getCommon().getShadowColor()) != 0
                    && Util.getRealColor(bean.getChildren().get(helper.getItemViewType()).getCommon().getShadowColor()) != -1) {

                int color = Util.getRealColor(bean.getChildren().get(helper.getItemViewType()).getStyle().getBgColor());
                if (color == 0) color = -1;cardView.setCardBackgroundColor(color);
                cardView.setRadius(bean.getChildren().get(helper.getItemViewType()).getCommon().getRadius());
                cardView.setCardElevation(10);
            } else {
                GradientDrawable drawable = Util.getBgDrawable(Util.getRealColor(bean.getChildren().get(helper.getItemViewType()).getStyle().getBgColor()) + "",
                        GradientDrawable.RECTANGLE,
                        bean.getStyle().getBorderRadius() == 0 ? bean.getChildren().get(helper.getItemViewType()).getCommon().getRadius() : bean.getChildren().get(helper.getItemViewType()).getStyle().getBorderRadius(),
                        bean.getStyle().getBorderWidth() == 0 ? 1 : bean.getChildren().get(helper.getItemViewType()).getStyle().getBorderWidth(),
                        bean.getStyle().getBorderColor());
                frameLayout.setBackgroundDrawable(drawable);
            }

            FrameLayout.LayoutParams lp = FrameParamsBuilder.builder()
                    .setWidth(bean.getChildren().get(helper.getItemViewType()).getCommon().getWidth())
                    .setHeight(bean.getChildren().get(helper.getItemViewType()).getCommon().getHeight())
                    .setMarginLeft(bean.getCommon().getxPadding())
                    .setMarginBottom(bean.getCommon().getyPadding())
                    .build();
            cardView.setLayoutParams(lp);
            BindDataUtil.bindListDataPosition(bean, contextImp, frameLayout, item,helper.getItemViewType());

        }

    }
}
