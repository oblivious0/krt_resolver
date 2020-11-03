package com.wid.applib.util;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.FrameLayout;

import com.wid.applib.R;
import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.bean.BindDataBean;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.tool.ModuleViewFactory;
import com.wid.applib.tool.PropertyBindTool;
import com.wid.applib.view.widget.BaseView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;


/**
 * author:Marcus
 * create on:2020/6/4 11:31
 * description 用来切割 binddata key 中的各个属性
 */
public class BindDataUtil {

    private String viewProperty = "";

    private String viewName = "";

    private String cid = "";


    public BindDataUtil(String originKey) {
        deal(originKey);
    }

    private void deal(String originKey) {
        String[] first = originKey.split("%krt_");
        //最后一个为视图属性值
        viewProperty = first[first.length - 1];
        String[] second = first[first.length - 2].split("%krt%");
        cid = second[second.length - 1];
        viewName = second[second.length - 2];
    }

    public String getViewProperty() {
        return viewProperty;
    }

    public String getViewName() {
        return viewName;
    }

    public String getCid() {
        return cid;
    }

    public static List<Object> getDatas(BaseLayoutBean bean) {

        List<Object> list = null;
        if (bean.getStaticData() != null) {
            if (bean.getStaticData().getData() != null && !bean.getStaticData().getData().isEmpty()) {
                list = bean.getStaticData().getData();
            }
        }

        return list;

    }

    @SuppressLint("CheckResult")
    public static void bindListDatas(BaseLayoutBean bean, ContextImp imp, FrameLayout frameLayout, Object item) {
        final List<BindDataBean> bindDatas = new ArrayList<>();
        List<BaseView> views = new ArrayList<>();
        ModuleViewFactory.createViews(bean.getChildren(), imp, frameLayout, views, true);

        if (bean.getAjax() != null && bean.getAjax().size() != 0) {
            bindDatas.addAll(bean.getAjax().get(0).getBindData());
        } else if (bean.getStaticData() != null && bean.getStaticData().getBindData() != null) {
            bindDatas.addAll(bean.getStaticData().getBindData());
        }

        if (bindDatas != null) {

            View view = null;
            String tag = "";
            String childTag = "";

            for (int i = 0; i < views.size(); i++) {
                view = views.get(i).view;
                tag = views.get(i).cid;
                //如果是容器布局需要循环子view进行绑定
                if (view instanceof FrameLayout) {
                    FrameLayout layout = (FrameLayout) view;
                    for (int j = 0; j < layout.getChildCount(); j++) {
                        if (layout.getChildAt(j).getTag() == null) {
                            childTag = layout.getChildAt(j).getTag(R.id.cid).toString();
                        }

                        for (int z = 0; z < bindDatas.size(); z++) {
                            BindDataUtil util1 = new BindDataUtil(bindDatas.get(z).getOriginKey());
                            if (util1.getCid().equals(childTag)) {
//                                String type = bindDatas.get(z).getBindKeys()[bindDatas.get(z).getBindKeys().length - 1];
                                String val = PropertyBindTool.getProperty(bindDatas.get(z).getBindKeys(), item);
                                PropertyBindTool.bindData(imp.getContext(),
                                        layout.getChildAt(j), util1.getViewName(), val, util1.getViewProperty());
                            }
                        }
                    }
                    frameLayout.addView(layout);
                } else {
                    for (int j = 0; j < bindDatas.size(); j++) {
                        BindDataUtil util = new BindDataUtil(bindDatas.get(j).getOriginKey());
                        if (util.getCid().equals(tag)) {
//                            String type = bindDatas.get(j).getBindKeys()[bindDatas.get(j).getBindKeys().length - 1];
                            String val = PropertyBindTool.getProperty(bindDatas.get(j).getBindKeys(), item);
                            PropertyBindTool.bindData(imp.getContext(),
                                    view, util.getViewName(), val, util.getViewProperty());
                        }
                    }
                    frameLayout.addView(view);
                }
            }
        }
    }
}
