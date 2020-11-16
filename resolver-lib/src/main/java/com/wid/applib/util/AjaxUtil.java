package com.wid.applib.util;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.Callback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.wid.applib.bean.AjaxBean;
import com.wid.applib.bean.BindDataBean;
import com.wid.applib.bean.ParamBean;
import com.wid.applib.bean.TransferKeyBean;
import com.wid.applib.http.MJsonConvert;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.manager.AppLibManager;
import com.wid.applib.tool.PropertyBindTool;
import com.wid.applib.view.widget.BaseView;

import org.greenrobot.eventbus.EventBus;

import krt.wid.bean.event.MEventBean;
import krt.wid.http.MCallBack;
import krt.wid.http.Result;
import krt.wid.util.MConstants;
import krt.wid.util.ParseJsonUtil;

/**
 * @author hyj
 * @time 2020/7/20 15:31
 * @class describe
 */
public class AjaxUtil {

    public static Request assembleRequest(AjaxBean bean, ContextImp contextImp) {
        if (bean == null) return null;
        Request request;
        if (bean.getMethod().equals("POST")) {
            request = OkGo.<Result>post(AppLibManager.getBasePath(bean.getBasePathIdx()) + bean.getUrl());
        } else if (bean.getMethod().equals("GET")) {
            request = OkGo.<Result>get(AppLibManager.getBasePath(bean.getBasePathIdx()) + bean.getUrl());
        } else {
            LogUtils.e("Request type err!");
            return null;
        }
        HttpParams params = new HttpParams();
        for (ParamBean bodyParam : bean.getData()) {
            if ("props".equals(bodyParam.getSource()) || "variable".equals(bodyParam.getSource())) {

                Object val = contextImp.getContainer("element")
                        .get(bodyParam.getVal());
                params.put(bodyParam.getKey(), val == null ? "" : val.toString());
            } else if ("storage".equals(bodyParam.getSource())) {
                Object val = AppLibManager.getStorageVal(bodyParam.getVal(), contextImp.getContext());
                params.put(bodyParam.getKey(), val == null ? "" : val.toString());
            } else {
                //static
                params.put(bodyParam.getKey(), bodyParam.getVal());
            }

            if (bodyParam.isFromBroad()) {
                Object val = contextImp.getContainer("element")
                        .get(bodyParam.getBroadKey());
                params.put(bodyParam.getKey(), val == null ? bodyParam.getVal() : val.toString());
            }
        }
        for (ParamBean headerParam : bean.getData()) {
            request.headers(headerParam.getKey(), headerParam.getVal());
        }
        request.headers("Authorization", contextImp.getAuthorization());
        request.params(params);
        return request;
    }

    public static void execute(AjaxBean bean, ContextImp contextImp) {
        Request request = assembleRequest(bean, contextImp);
        if (request != null) {

            if (contextImp.getContainer("callback").containsKey(bean.getCid())) {
                request.execute((Callback) contextImp.getContainer("callback").get(bean.getCid()));
            } else {
                request.execute(new MCallBack<Result>((Activity) contextImp.getContext(), false) {
                    @Override
                    public void onSuccess(Response<Result> response) {
                        if (response.body().isSuccess()) {
                            String data = response.body().data.toString();
                            if (bean.getTransferKey() != null)
                                for (TransferKeyBean transferKeyBean : bean.getTransferKey()) {
                                    String val = PropertyBindTool.getProperty(transferKeyBean.getKey().split("%krt_"), data);

                                    if (!TextUtils.isEmpty(transferKeyBean.getVariableName())) {
                                        contextImp.getContainer("element").put(transferKeyBean.getVariableName(), val);
                                    }

                                    if (!TextUtils.isEmpty(transferKeyBean.getStorageName())) {
                                        AppLibManager.putStorageVal(transferKeyBean.getStorageName(), val, contextImp.getContext());
                                    }
                                }

                            for (BindDataBean bindDataBean : bean.getBindData()) {
                                //"originKey": "label%krt%2b74f446e8c95ba%krt_text",
                                //"layout%krt%koyc74hhx2 %krt_ label%krt%2bpkblxllsc %krt_ text"
                                String[] str = bindDataBean.getOriginKey().split("%krt_");
                                String pro = str[str.length - 1];
                                String[] str1 = str[str.length - 2].split("%krt%");
                                String type = str1[0];
                                String cid = str1[1];
                                String[] bindK = bindDataBean.getBindKey().split("%krt_");
                                if (bindK[0].equals("data")) {

                                    ((BaseView) contextImp.getContainer("view").get(cid))
                                            .bindData(cid, pro, ParseJsonUtil.getStringByKey(data, bindK[1]));

//                                    PropertyBindTool.bindData(contextImp.getContext(),
//                                            (View) contextImp.getContainer("view").get(cid),
//                                            type, ParseJsonUtil.getStringByKey(data, bindK[1]), pro);
                                }
                            }
                        }
                    }

                    @Override
                    public Result convertResponse(okhttp3.Response response) throws Throwable {
                        MJsonConvert<Result> convert = new MJsonConvert<>(Result.class);
                        Result result = convert.convertResponse(response);
                        if (result != null) {
                            if (!result.isSuccess()) {
                                EventBus.getDefault().post(new MEventBean(MConstants.ACTION_RESULT_CODE, result.code));
                            }
                        }
                        return result;
                    }
                });
            }
        }
    }
}
