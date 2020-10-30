package com.krt.wid.resolver.tool;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.krt.wid.resolver.TestActivity;
import com.krt.wid.resolver.module.BaseFragment;
import com.krt.wid.resolver.web.WebActivity;
import com.wid.applib.bean.ParamBean;
import com.wid.applib.event.MBaseEventListener;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.manager.AppLibManager;

import java.util.ArrayList;
import java.util.List;

import krt.wid.util.MToast;
import krt.wid.util.ParseJsonUtil;

/**
 * author: MaGua
 * create on:2020/10/27 9:29
 * description
 */
public class BaseEventInstance extends MBaseEventListener {

    public BaseEventInstance(ContextImp context) {
        super(context);
    }

    @Override
    protected void sharePage() {

    }

    @Override
    protected void gotoLogin(Context context) {
        MToast.showToast(context, "此处跳转原生登录页");
    }

    @Override
    protected void onClickListener(View view, String type, List<ParamBean> objects) {
        Intent intent = new Intent(contextImp.getContext(), TestActivity.class);
        contextImp.getContext().startActivity(intent);
    }

    @Override
    protected void onStartWebActivity(View view, String url, List<ParamBean> objects) {
        Intent intent = new Intent(contextImp.getContext(), WebActivity.class)
                .putExtra("url", url);
        contextImp.getContext().startActivity(intent);
    }

    @Override
    protected void onStartModuleActivity(View view, String jsonName, List<ParamBean> objects) {
        List<ParamBean> paramBeans = new ArrayList<>();
        for (ParamBean bodyParam : objects) {
            if ("props".equals(bodyParam.getSource()) || "variable".equals(bodyParam.getSource())) {

                Object val = contextImp.getContainer("element")
                        .get(bodyParam.getVal());
                paramBeans.add(new ParamBean(bodyParam.getKey(), val == null ? bodyParam.getVal() : val.toString()));
            } else if ("storage".equals(bodyParam.getSource())) {
                Object val = AppLibManager.getStorageVal(bodyParam.getVal(), contextImp.getContext());
                paramBeans.add(new ParamBean(bodyParam.getKey(), val == null ? bodyParam.getVal() : val.toString()));
            } else {
                //static
                paramBeans.add(new ParamBean(bodyParam.getKey(), bodyParam.getVal()));
            }

            if (bodyParam.isFromBroad()) {
                Object val = contextImp.getContainer("element")
                        .get(bodyParam.getBroadKey());
                paramBeans.add(new ParamBean(bodyParam.getKey(), val == null ? bodyParam.getVal() : val.toString()));
            }
        }

        Intent intent = new Intent(contextImp.getContext(), BaseFragment.class)
                .putExtra("name", jsonName)
                .putExtra("param", ParseJsonUtil.toJson(paramBeans));
        contextImp.getContext().startActivity(intent);
    }

    @Override
    protected void onCallPhone(String number) {
        MToast.showToast(contextImp.getContext(), "拨打电话：" + number);
    }
}
