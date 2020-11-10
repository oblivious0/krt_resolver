package com.wid.applib.event;

import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.wid.applib.bean.ActionBean;
import com.wid.applib.bean.AjaxBean;
import com.wid.applib.bean.EventBean;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.manager.AppLibManager;
import com.wid.applib.tool.PropertyBindTool;
import com.wid.applib.util.AjaxUtil;
import com.wid.applib.view.MRecyclerView;
import com.wid.applib.view.widget.BaseView;
import com.youth.banner.Banner;

import java.util.ArrayList;

/**
 * @author hyj
 * @time 2020/8/3 15:57
 * @class describe
 */
public class EventBusListener {

    EventBean bean;

    public EventBusListener(EventBean bean) {
        this.bean = bean;
    }

    public void execute(ContextImp contextImp) {

//        LogUtils.e(ParseJsonUtil.toJson(bean));

        switch (bean.getType()) {
            case EventMessageWarp.SEND_AJAX:
                for (String ids : bean.getAjaxIds()) {
                    String[] cell = ids.split("%krt_");
                    ContextImp ctx = null;
                    for (String cellStr : cell) {
                        String[] splitStr = cellStr.split("%krt%");
                        if ("page".equals(splitStr[0])) {
                            if (splitStr[1].equals(contextImp.getPageId())) {
                                ctx = contextImp;
                            } else {
                                ctx = AppLibManager.getPageContext(splitStr[1]);
                            }
                        } else if ("ajax".equals(splitStr[0])) {
                            if (ctx != null) {
                                Object obj = ctx.getContainer("ajax").get(splitStr[1]);
                                if (obj != null) {
                                    AjaxBean ajaxBean = (AjaxBean) obj;
                                    //此处执行Ajax
                                    AjaxUtil.execute(ajaxBean, contextImp);
                                }
                            }
                        }
                    }
                }
                break;
            case EventMessageWarp.STATE_CHANGE:
                for (ActionBean actionBean : bean.getList()) {
                    String[] cell = actionBean.getTarget().split("%krt_");
                    String[] val = cell[cell.length - 1].split("%krt%");

                    switch (actionBean.getType()) {
                        case "attr":
                            for (ActionBean.Attr attr : actionBean.getAttrList()) {
                                ((BaseView) contextImp.getContainer("view").get(val[1]))
                                        .bindData(attr.getAttr().split("_")[1], attr.getTarget());
                            }
                            break;
                        case "hid":
                            ((BaseView) contextImp.getContainer("view").get(val[1])).view.setVisibility(View.GONE);
                            break;
                        case "clear":
                            View view = ((BaseView) contextImp.getContainer("view").get(val[1])).view;
                            if (view instanceof MRecyclerView) {
                                ((BaseQuickAdapter) ((MRecyclerView) view).getAdapter()).getData().clear();
                            } else if (view instanceof Banner) {
                                ((Banner) view).getAdapter().setDatas(new ArrayList());
                            }
                            break;
//                        case ""
                        default:
                            break;
                    }
                }

                break;
            default:
                break;
        }

//        MToast.showToast(contextImp.getContext(), "收到广播");
    }
}
