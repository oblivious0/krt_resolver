package com.wid.applib.util;

import android.annotation.SuppressLint;
import android.widget.FrameLayout;

import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.bean.BindDataBean;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.tool.ModuleViewFactory;
import com.wid.applib.tool.PropertyBindTool;
import com.wid.applib.widget.BaseView;

import java.util.ArrayList;
import java.util.List;


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
    public static List<BaseView> bindListDatas(BaseLayoutBean bean, ContextImp imp, FrameLayout frameLayout, Object item) {
        List<BindDataBean> bindDatas = new ArrayList<>();
        List<BaseView> views = new ArrayList<>();
        frameLayout.removeAllViews();
        ModuleViewFactory.createViews(bean.getChildren(), imp, frameLayout, views, true);

        if (bean.getAjax() != null && bean.getAjax().size() != 0) {
            bindDatas.addAll(bean.getAjax().get(0).getBindData());
        } else if (bean.getStaticData() != null && bean.getStaticData().getBindData() != null) {
            bindDatas.addAll(bean.getStaticData().getBindData());
        }

        if (bindDatas.size() == 0) return views;

        for (BaseView baseV : views) {
            for (int j = 0; j < bindDatas.size(); j++) {
                BindDataUtil util = new BindDataUtil(bindDatas.get(j).getOriginKey());
                String val = PropertyBindTool.getProperty(bindDatas.get(j).getBindKeys(), item);
                baseV.bindData(util.getCid(), util.getViewProperty(), val);
            }
        }

        return views;
    }
}
