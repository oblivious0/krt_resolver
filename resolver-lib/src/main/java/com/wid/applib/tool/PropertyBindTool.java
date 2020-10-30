package com.wid.applib.tool;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.joooonho.SelectableRoundedImageView;
import com.wid.applib.util.ViewValue;

import krt.wid.util.MGlideUtil;
import krt.wid.util.ParseJsonUtil;

/**
 * @author hyj
 * @time 2020/8/20 10:56
 * @class describe
 */
public class PropertyBindTool {

    public static void bindData(Context context, View view, String type, String value, String property) {
        try {
            switch (type) {
                case ViewValue.LABEL: {
                    switch (property) {
                        case "text":
                            ((TextView) view).setText(Html.fromHtml(value));
                            break;
                        case "fontSize":
                            ((TextView) view).setTextSize(Float.parseFloat(value));
                            break;
                        default:
                            break;
                    }
                }
                break;
                case ViewValue.PIC: {
                    switch (property) {
                        case "src":
                        case "iconFileName":
                            MGlideUtil.load(context, value, (SelectableRoundedImageView) view);
                            break;
                        default:
                            break;
                    }
                }
                break;
                default:
                    break;
            }
        }catch (Exception e){}
    }

    public static String getProperty(String[] bindKeys, Object data) {
        //Array%krt_title
        //data%krt_Array%krt_goodsPrice
        //data%krt_Array%krt_gzcardViewspot%krt_num

        String json = data.toString();
        String jstring = "";
        String val = "";

        if (bindKeys.length == 1 && bindKeys[0].equals("data")) {
            return json;
        }

        for (int i = 0; i < bindKeys.length; i++) {
            if (!bindKeys[i].equals("data") && !bindKeys[i].equals("Array")) {
                if (i != bindKeys.length - 1) {
                    if (TextUtils.isEmpty(jstring)) {
                        jstring = ParseJsonUtil.getStringByKey(json, bindKeys[i]);
                    } else {
                        jstring = ParseJsonUtil.getStringByKey(jstring, bindKeys[i]);
                    }
                } else {
                    if (TextUtils.isEmpty(jstring)) {
                        jstring = ParseJsonUtil.toJson(data);
                    }
                    val = ParseJsonUtil.getStringByKey(jstring, bindKeys[i]);
                }

            }
        }
        return val;

    }

}
