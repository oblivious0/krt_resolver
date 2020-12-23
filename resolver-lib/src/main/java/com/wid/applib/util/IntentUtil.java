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

            switch (bodyParam.getSource()) {
                case "props":
                case "variable":
                    Object val = imp.getContainer("element")
                            .get(bodyParam.getVal());
                    intent.putExtra(bodyParam.getKey(), val == null ? "" : val.toString());
                    break;

                case "storage":
                    Object val1 = AppLibManager.getStorageVal(bodyParam.getVal(), imp.getContext());
                    intent.putExtra(bodyParam.getKey(), val1 == null ? "" : val1.toString());
                    break;

                default:
                    intent.putExtra(bodyParam.getKey(), bodyParam.getVal());
                    break;
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
