package com.wid.applib.event;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.CloneUtils;
import com.blankj.utilcode.util.LogUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wid.applib.bean.ActionBean;
import com.wid.applib.bean.AjaxBean;
import com.wid.applib.bean.EventBean;
import com.wid.applib.bean.ParamBean;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.manager.AppLibManager;
import com.wid.applib.tool.PropertyBindTool;
import com.wid.applib.http.AjaxUtil;
import com.wid.applib.view.MRecyclerView;
import com.wid.applib.view.widget.BaseView;
import com.youth.banner.Banner;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import krt.wid.util.MToast;
import krt.wid.util.ParseJsonUtil;

import static com.wid.applib.tool.PropertyBindTool.getProperty;


/**
 * @author hyj
 * @time 2020/7/17 10:55
 * @class describe
 */
public abstract class MBaseEventListener implements ViewEventImp {

    protected ContextImp contextImp;

    public MBaseEventListener(ContextImp context) {
        contextImp = context;
    }

    @Override
    public void onViewClick(View view, List<EventBean> eventBeans) {
        for (EventBean eventBean : eventBeans) {

            List<ParamBean> paramBeans = new ArrayList<>();
            //判断参数集是否为空，不为空将遍历参数集，把传出值替换为实际数据；
            if (eventBean.getParams() != null) {
                for (ParamBean paramBean : eventBean.getParams()) {
                    ParamBean bean = new ParamBean();
                    bean.setKey(paramBean.getKey());


                    switch (paramBean.getSource()) {
                        case "props":
                        case "variable":
                            Object val = contextImp.getContainer("element")
                                    .get(paramBean.getVal());
                            bean.setVal(val.toString());
                            break;

                        case "storage":
                            Object val1 = AppLibManager.getStorageVal(paramBean.getVal(), contextImp.getContext());
                            bean.setVal(val1.toString());
                            break;

                        default:
                            bean.setVal(paramBean.getVal());
                            break;
                    }
                    paramBeans.add(bean);
                }
            }

            action(view, eventBean, eventBean.getParams());
        }
    }

    @Override
    public void onDataListClick(View view, List<EventBean> eventBeans, int position) {
        Object json = new Object();

        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            try {
                BaseQuickAdapter<Object, BaseViewHolder> adapter =
                        (BaseQuickAdapter<Object, BaseViewHolder>) recyclerView.getAdapter();
                json = adapter.getItem(position);
            } catch (Exception e) {
                return;
            }
        } else if (view instanceof Banner) {
            Banner banner = (Banner) view;
            try {
                json = banner.getAdapter().getData(position);
            } catch (Exception e) {
                return;
            }
        }

        for (EventBean eventBean : eventBeans) {
            if ("c67a44a5b9d6037".equals(eventBean.getCid())){
                LogUtils.e("");
            }
            EventBean eventBean1 = CloneUtils.deepClone(eventBean, EventBean.class);
            if (eventBean1.isUrlFromApi()) {
                // data%krt_Array%krt_linkUrl
                String[] stringKey = eventBean1.getUrl().split("%krt_");
                String url = getProperty(stringKey, json);
                eventBean1.setUrl(url);
            }

            List<ParamBean> paramBeans = new ArrayList<>();
            //判断参数集是否为空，不为空将遍历参数集，把传出值替换为实际数据；
            if (eventBean.getParams() != null) {
                for (ParamBean paramBean : eventBean.getParams()) {
                    ParamBean bean = new ParamBean();
                    bean.setKey(paramBean.getKey());
                    switch (paramBean.getSource()) {
                        case "props":
                        case "variable":
                            Object val = contextImp.getContainer("element")
                                    .get(paramBean.getVal());
                            bean.setVal(val.toString());
                            break;

                        case "storage":
                            Object val1 = AppLibManager.getStorageVal(paramBean.getVal(), contextImp.getContext());
                            bean.setVal(val1.toString());
                            break;

                        case "transKey":
                            if (paramBean.getVal().contains("%krt_")) {
                                String val2 = getProperty(paramBean.getVal().split("%krt_"), json);
                                bean.setVal(val2);
                            }
                            break;

                        default:
                            bean.setVal(paramBean.getVal());
                            break;
                    }
                    paramBeans.add(bean);
                }
            }
            action(view, eventBean1, paramBeans);
        }
    }

    private void action(View view, EventBean eventBean, List<ParamBean> params) {

        if (eventBean.isNeedLogin() && !contextImp.isLogin()) {
            //处于需要登录的情况下切未登录的状态返回
            gotoLogin(view.getContext());
            return;
        }

        if ("tobedeveloped".equals(eventBean.getUrl())) {
            MToast.showToast(view.getContext(), "敬请期待！");
            return;
        }

        switch (eventBean.getType()) {
            case EventMessageWarp.NAVIGATOR:

                if (!TextUtils.isEmpty(eventBean.getNaviType()) && eventBean.getNaviType().equals("navigateBack")) {
                    ((Activity) view.getContext()).finish();
                    return;
                }

                if (eventBean.isIfOuterChain()) {
                    String url = disposeUrl(eventBean.getUrl(), eventBean.getDevelopment());
                    onStartWebActivity(view, url, params);
                } else if (eventBean.isIfModulePage() && !eventBean.isIfOuterChain()) {
                    onStartModuleActivity(view, eventBean.getPageId(), params);
                } else {
                    onClickListener(view, eventBean.getUrl(), params);
                }

                break;
            case EventMessageWarp.SEND_AJAX:
                for (String ids : eventBean.getAjaxIds()) {
                    String[] cell = ids.split("%krt_");
                    for (String cellStr : cell) {
                        ContextImp ctx = null;
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
                                    AjaxUtil.execute(ajaxBean, contextImp);
                                }
                            }
                        }
                    }
                }
                break;
            case EventMessageWarp.SEND_BROAD_CAST:
                if (params != null) {
                    HashMap<String, String> param = new HashMap<>(params.size());
                    for (ParamBean bean : params) {
                        param.put(bean.getKey(), bean.getVal());
                    }
                    BroadCastMessageWarp warp = new BroadCastMessageWarp(eventBean.getName(), param);
                    EventBus.getDefault().postSticky(warp);
                }
                break;
            case EventMessageWarp.SHARE_PAGE:
                sharePage();
                break;
            case EventMessageWarp.STATE_CHANGE:
                for (ActionBean actionBean : eventBean.getList()) {
                    String[] cell = actionBean.getTarget().split("%krt_");
                    String[] val = cell[cell.length - 1].split("%krt%");

                    switch (actionBean.getType()) {
                        case "attr":
                            for (ActionBean.Attr attr : actionBean.getAttrList()) {
                                BaseView baseView = ((BaseView) contextImp.getContainer("view").get(val[1]));
                                if (baseView!=null)
                                    baseView.bindData(val[1], attr.getAttr().split("_")[1], attr.getTarget());
                            }
                            break;
                        case "hid":
                            ((BaseView) contextImp.getContainer("view").get(val[1])).view.setVisibility(View.GONE);
                            break;
                        case "clear":
                            BaseView baseView = ((BaseView) contextImp.getContainer("view").get(val[1]));
                            if (baseView!=null) {
                                View view1 = baseView.view;
                                if (view1 instanceof MRecyclerView) {
                                    ((BaseQuickAdapter) ((MRecyclerView) view1).getAdapter()).getData().clear();
                                } else if (view1 instanceof Banner) {
                                    ((Banner) view1).getAdapter().setDatas(new ArrayList());
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
                break;
            case EventMessageWarp.PHONE_CALL:
                onCallPhone(eventBean.getNumber());
                break;
//                case EventMessageWarp.
            default:
                break;
        }
    }

    /**
     * 实现分享事件
     */
    protected abstract void sharePage();

    protected abstract void gotoLogin(Context context);

    /**
     * 不做跳转时的相应，继承后重写实现，默认不做任何操作
     * 可选重写
     *
     * @param view
     * @param objects
     */
    protected abstract void onClickListener(View view, String type, List<ParamBean> objects);

    protected abstract void onStartWebActivity(View view, String url, List<ParamBean> objects);

    protected abstract void onStartModuleActivity(View view, String jsonName, List<ParamBean> objects);

    protected abstract void onCallPhone(String number);

    protected abstract String disposeUrl(String url, List<String> development);
}
