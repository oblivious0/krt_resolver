package com.wid.applib.util;

import android.content.Intent;

import com.wid.applib.bean.ParamBean;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.manager.AppLibManager;

import java.util.List;

/**
 * @author hyj
 * @time 2020/7/28 9:29
 * @class describe
 */
public class IntentUtil {

    public static void startActivity(ContextImp imp, Class<?> cls, List<ParamBean> params) {
        Intent intent = new Intent(imp.getContext(), cls);
        for (ParamBean bean : params) {
            intent.putExtra(bean.getKey(), bean.getVal());
        }

        for (ParamBean bodyParam : params) {
            if ("props".equals(bodyParam.getSource()) || "variable".equals(bodyParam.getSource())) {

                Object val = imp.getContainer("element")
                        .get(bodyParam.getVal());
                intent.putExtra(bodyParam.getKey(), val == null ? bodyParam.getVal() : val.toString());
            } else if ("storage".equals(bodyParam.getSource())) {
                Object val = AppLibManager.getStorageVal(bodyParam.getVal(), imp.getContext());
                intent.putExtra(bodyParam.getKey(), val == null ? bodyParam.getVal() : val.toString());
            } else {
                //static
                intent.putExtra(bodyParam.getKey(), bodyParam.getVal());
            }

            if (bodyParam.isFromBroad()) {
                Object val = imp.getContainer("element")
                        .get(bodyParam.getBroadKey());
                intent.putExtra(bodyParam.getKey(), val == null ? bodyParam.getVal() : val.toString());
            }
        }
        imp.getContext().startActivity(intent);
    }

    public static String getWebUrl(String url, List<ParamBean> params) {
        StringBuilder stringBuilder = new StringBuilder(url + "?empty=0");
        for (int i = 0; i < params.size(); i++) {
            stringBuilder.append("&" + params.get(i).getKey() + "=" + params.get(i).getVal());
        }
        return stringBuilder.toString();
    }
}
