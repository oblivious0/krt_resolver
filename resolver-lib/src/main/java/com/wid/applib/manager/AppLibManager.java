package com.wid.applib.manager;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.wid.applib.bean.AjaxBean;
import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.bean.BroadCastBean;
import com.wid.applib.bean.EventBean;
import com.wid.applib.bean.ParamBean;
import com.wid.applib.bean.StateBean;
import com.wid.applib.event.EventBusListener;
import com.wid.applib.exception.ModuleJsonParseException;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.tool.ModuleViewFactory;
import com.wid.applib.util.JsonValue;
import com.wid.applib.util.SpUtil;
import com.wid.applib.view.widget.BaseView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import krt.wid.util.ParseJsonUtil;

/**
 * @author hyj
 * @time 2020/8/19 9:17
 * @class describe 主要用于解析Json，主导模块化组件生成流程
 */
public class AppLibManager {

    public static int defaultPath = 0;

    private static List<String> basePaths = new ArrayList<>();

    private static HashMap<String, ContextImp> moduleStack = new HashMap<>();

    private static HashMap<String, String> STORAGE_VAL = new HashMap<>();

    private static final String TAG = "ModuleManager";

    private ContextImp moduleUI;

    private AppLibManager(ContextImp moduleUi) {
        moduleUI = moduleUi;
    }

    public static String getBasePath(int index) {
        try {
            if (index == 99)
                return basePaths.get(defaultPath);

            return basePaths.get(index);
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean putBasePath(String url) {
        if (basePaths.contains(url)) return false;

        basePaths.add(url);
        return true;
    }

    public static void registerContext(String pageId, ContextImp contextImp) {
        moduleStack.put(pageId, contextImp);
    }

    public static void putStorageVal(String k, String v, Context context) {
        SpUtil.putStorageVal(k, v, context);
    }

    public static String getStorageVal(String k, Context context) {
        return SpUtil.getStorageVal(k, context);
    }

    public static boolean contains(String k) {
        return STORAGE_VAL.containsKey(k);
    }

    public static void unRegisterContext(String pageId) {
        moduleStack.remove(pageId);
    }

    public static ContextImp getPageContext(String cid) {
        return moduleStack.get(cid);
    }

    private AppLibManagerListener listener;

    /**
     * 逐步解析JSON，生成界面组件
     *
     * @param json
     */
    private void parsingJson(String json) throws ModuleJsonParseException {
        final FrameLayout headerView = moduleUI.getHeaderView();
        headerView.removeAllViews();
        String variableJson = ParseJsonUtil.getStringByKey(json, JsonValue.VARIABLE);
        if (!TextUtils.isEmpty(variableJson)) {
            List<ParamBean> variables = ParseJsonUtil.getBeanList(variableJson, ParamBean.class);
            if (variables != null) {
                for (ParamBean param : variables) {
                    moduleUI.getContainer("element").put(param.getKeyName(), param.getVal());
                }
            }
        }

        if (listener != null) {
            listener.initElement();
        }

        String pageJson = ParseJsonUtil.getStringByKey(json, JsonValue.PAGE);
        if (TextUtils.isEmpty(pageJson)) {
            throw new ModuleJsonParseException("The unresolved page[node] json !");
        }

        List<BaseLayoutBean> nav = getContentData(pageJson, JsonValue.NAVBAR);
        if (nav == null || nav.isEmpty()) {
            Log.i(TAG, "The unresolved header[node] json !");
        } else {
            List<BaseView> views = new ArrayList<>();
            ModuleViewFactory.createViews(nav, moduleUI, headerView, views, false);
        }

        FrameLayout contentView = moduleUI.getContentView();
        List<BaseLayoutBean> list = getContentData(pageJson, JsonValue.CONTENT);
        if (list == null || list.isEmpty()) {
            Log.i(TAG, "The unresolved body[node] json !");
            return;
        }

        List<BaseView> views = new ArrayList<>();
        ModuleViewFactory.createViews(list, moduleUI, contentView, views, false);

        String broadcastJson = ParseJsonUtil.getStringByKey(json, JsonValue.BROADCAST);
        if (!TextUtils.isEmpty(broadcastJson)) {
            List<BroadCastBean> broadCastBeanList = ParseJsonUtil.getBeanList(broadcastJson, BroadCastBean.class);
            for (BroadCastBean bean : broadCastBeanList) {
                moduleUI.getContainer("broadCast").put(bean.getCid(), bean);
            }
        }

        String ajaxJson = ParseJsonUtil.getStringByKey(json, JsonValue.AJAX);
        if (!TextUtils.isEmpty(ajaxJson)) {
            List<AjaxBean> ajaxBeanList = ParseJsonUtil.getBeanList(ajaxJson, AjaxBean.class);
            for (AjaxBean ajaxBean : ajaxBeanList) {
                moduleUI.getContainer("ajax").put(ajaxBean.getCid(), ajaxBean);
            }
        }

        String eventJson = ParseJsonUtil.getStringByKey(json, JsonValue.EVENT);
        if (!TextUtils.isEmpty(eventJson)) {
            List<EventBean> eventBeanList = ParseJsonUtil.getBeanList(eventJson, EventBean.class);
            for (EventBean eventBean : eventBeanList) {
                moduleUI.getContainer("eventBus")
                        .put(eventBean.getCid(), new EventBusListener(eventBean));
            }
        }

        String stateJson = ParseJsonUtil.getStringByKey(json, JsonValue.STATE);
        if (!TextUtils.isEmpty(stateJson)) {
            List<StateBean> stateBeanList = ParseJsonUtil.getBeanList(stateJson, StateBean.class);
            for (StateBean stateBean : stateBeanList) {
                moduleUI.getContainer("state").put(stateBean.getCid(), stateBean);
            }
        }

        if (listener != null) {
            listener.onFinish();
        }
    }

    public interface AppLibManagerListener {

        void initElement();

        void onFinish();
    }

    public void setListener(AppLibManagerListener listener) {
        this.listener = listener;
    }

    /**
     * 解析PAGE内组件部分
     *
     * @param json
     * @param key
     * @return
     */
    private List<BaseLayoutBean> getContentData(String json, String key) {
        String content = ParseJsonUtil.getStringByKey(json, key);
        if (TextUtils.isEmpty(content)) {
            return null;
        } else {
            return ParseJsonUtil.getBeanList(content, BaseLayoutBean.class);
        }
    }

    public static class Builder {

        private AppLibManager manager;
        private String jsonStr;
        private AppLibManagerListener listenner;

        public Builder(ContextImp moduleUi) {
            manager = new AppLibManager(moduleUi);
        }

        public AppLibManager convert() throws ModuleJsonParseException {
            manager.setListener(listenner);
            manager.parsingJson(jsonStr);
            return manager;
        }

        public Builder setManagerListenner(AppLibManagerListener listenner) {
            this.listenner = listenner;
            return this;
        }

        public Builder setJson(String json) {
            jsonStr = json;
            return this;
        }

    }

}
