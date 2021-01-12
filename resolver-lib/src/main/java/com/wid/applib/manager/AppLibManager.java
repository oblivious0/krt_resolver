package com.wid.applib.manager;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.FrameLayout;

import com.blankj.utilcode.util.LogUtils;
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
import com.wid.applib.util.FrameParamsBuilder;
import com.wid.applib.util.JsonValue;
import com.wid.applib.util.SpUtil;
import com.wid.applib.util.ViewValue;
import com.wid.applib.view.MRecyclerView;
import com.wid.applib.widget.BaseView;
import com.wid.applib.widget.list.ListDataView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import krt.wid.util.ParseJsonUtil;

/**
 * @author hyj
 * @time 2020/8/19 9:17
 * @class describe 核心类 主要用于解析Json，主导模块化组件生成流程并且打包界面
 */
public class AppLibManager {

    //***********************************静态成员区******************************************

    /**
     * 默认api地址索引
     */
    public static int defaultPath = 0;
    /**
     * 正式地址和测试地址
     */
    private static List<String> basePaths = new ArrayList<>();
    private static List<String> baseBetaPaths = new ArrayList<>();

    /**
     * 存放所有contextImp,便于跨界面获取其内容
     */
    private static HashMap<String, ContextImp> moduleStack = new HashMap<>();

    /**
     * 获取接口
     *
     * @param index
     * @return 因json内接口使用默认api时为空，因此在解析时将默认值调到99，当传99时当空值一样选择默认API路径
     */
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

    public static String getBetaPath(int index) {
        try {
            if (index == 99)
                return baseBetaPaths.get(defaultPath);

            return baseBetaPaths.get(index);
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean putBetaPath(String url) {
        if (baseBetaPaths.contains(url)) return false;

        baseBetaPaths.add(url);
        return true;
    }

    /**
     * 在界面生命周期创建注册进AppLibManager
     *
     * @param pageId
     * @param contextImp
     */
    public static void registerContext(String pageId, ContextImp contextImp) {
        moduleStack.put(pageId, contextImp);
    }

    /**
     * 从sp中放入数据
     *
     * @param k
     * @param v
     * @param context
     */
    public static void putStorageVal(String k, String v, Context context) {
        SpUtil.putStorageVal(k, v, context);
    }

    /**
     * @param k
     * @param context
     * @return
     */
    public static String getStorageVal(String k, Context context) {
        return SpUtil.getStorageVal(k, context);
    }

    /**
     * 在界面生命周期注销时反注册
     *
     * @param pageId
     */
    public static void unRegisterContext(String pageId) {
        moduleStack.remove(pageId);
    }

    /**
     * 根据cid获取contextImp
     *
     * @param cid
     * @return
     */
    public static ContextImp getPageContext(String cid) {
        return moduleStack.get(cid);
    }

    //**************************************************************************************


    private static final String TAG = "ModuleManager";

    private ContextImp moduleUI;

    private AppLibManager(ContextImp moduleUi) {
        moduleUI = moduleUi;
    }

    private AppLibManagerListener listener;

    /**
     * 逐步解析JSON，包装界面
     *
     * @param json
     */
    private void parsingJson(String json) throws ModuleJsonParseException {
        moduleUI.setPageType(ParseJsonUtil.getStringByKey(json, "pageType"));

//        if ("dbe3a48859f7408".equals(ParseJsonUtil.getStringByKey(json, "cid"))){
//            LogUtils.e("调试专用代码");
//        }

        final FrameLayout headerView = moduleUI.getHeaderView();
        headerView.removeAllViews();

        //先解析页面变量
        String variableJson = ParseJsonUtil.getStringByKey(json, JsonValue.VARIABLE);
        if (!TextUtils.isEmpty(variableJson)) {
            List<ParamBean> variables = ParseJsonUtil.getBeanList(variableJson, ParamBean.class);
            if (variables != null) {
                for (ParamBean param : variables) {
                    moduleUI.getContainer("element").put(param.getKeyName(), param.getVal());
                }
            }
        }

        //解析页面变量之后通过回调把intent携带的页面传入参数覆/保存进页面变量
        if (listener != null) {
            listener.initElement();
        }

        //开始解析界面组件
        String pageJson = ParseJsonUtil.getStringByKey(json, JsonValue.PAGE);
        if (TextUtils.isEmpty(pageJson)) {
            throw new ModuleJsonParseException("The unresolved page[node] json !");
        }
        //先解析标题栏
        List<BaseLayoutBean> nav = getContentData(pageJson, JsonValue.NAVBAR);
        if (nav == null || nav.isEmpty()) {
            Log.i(TAG, "The unresolved header[node] json !");
        } else {
            List<BaseView> views = new ArrayList<>();
            ModuleViewFactory.createViews(nav, moduleUI, headerView, views, false);
        }

        //之后解析内容
        FrameLayout contentView = moduleUI.getContentView();
        List<BaseLayoutBean> list = getContentData(pageJson, JsonValue.CONTENT);
        if (list == null || list.isEmpty()) {
            Log.i(TAG, "The unresolved body[node] json !");
            return;
        }

        //根据页面类型来解析内容
        switch (moduleUI.getPageType()) {
            case "list": {
                //列表页
                //列表页仅有两个子节点，列表头和列表，筛选出列表，其余组件作为列表头
                List<BaseLayoutBean> listDataView = new ArrayList<>();
                int list_index = -1;

                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getType().equals(ViewValue.LIST)) {
                        listDataView.add(list.get(i));
                        list_index = i;
                        break;
                    }
                }

                List<BaseView> views = new ArrayList<>();
                if (list_index == -1) {
                    //列表页不放列表？那就当普通页处理吧
                    ModuleViewFactory.createViews(list, moduleUI, contentView, views, false);
                } else {
                    //list将作为列表头组件的集合，剔除list
                    list.remove(list_index);

                    //创建列表
                    ModuleViewFactory.createViews(listDataView, moduleUI, contentView, views, false);
                    if (views.size() == 0) {
                        //创建列表出现了问题
                        Log.i(TAG, "The unresolved list type !");
                        return;
                    }

                    ListDataView recyclerViewBaseView = (ListDataView) views.get(0);

                    //创建列表头
                    FrameLayout listHeaderView = new FrameLayout(moduleUI.getContext());
                    FrameLayout.LayoutParams lp = FrameParamsBuilder.builder()
                            .setWidth(FrameLayout.LayoutParams.MATCH_PARENT)
                            .setHeight(FrameLayout.LayoutParams.WRAP_CONTENT)
                            .build();
                    listHeaderView.setLayoutParams(lp);
                    List<BaseView> headerViews = new ArrayList<>();
                    ModuleViewFactory.createViews(list, moduleUI, listHeaderView, headerViews, false);
                    recyclerViewBaseView.getAdapter().addHeaderView(listHeaderView);
                }
                break;
            }
            default: {
                //默认普通页
                List<BaseView> views = new ArrayList<>();
                ModuleViewFactory.createViews(list, moduleUI, contentView, views, false);
                break;
            }
        }


        //解析广播
        String broadcastJson = ParseJsonUtil.getStringByKey(json, JsonValue.BROADCAST);
        if (!TextUtils.isEmpty(broadcastJson)) {
            List<BroadCastBean> broadCastBeanList = ParseJsonUtil.getBeanList(broadcastJson, BroadCastBean.class);
            for (BroadCastBean bean : broadCastBeanList) {
                moduleUI.getContainer("broadCast").put(bean.getCid(), bean);
            }
        }

        //解析页面层级ajax
        String ajaxJson = ParseJsonUtil.getStringByKey(json, JsonValue.AJAX);
        if (!TextUtils.isEmpty(ajaxJson)) {
            List<AjaxBean> ajaxBeanList = ParseJsonUtil.getBeanList(ajaxJson, AjaxBean.class);
            for (AjaxBean ajaxBean : ajaxBeanList) {
                moduleUI.getContainer("ajax").put(ajaxBean.getCid(), ajaxBean);
            }
        }

        //解析事件
        String eventJson = ParseJsonUtil.getStringByKey(json, JsonValue.EVENT);
        if (!TextUtils.isEmpty(eventJson)) {
            List<EventBean> eventBeanList = ParseJsonUtil.getBeanList(eventJson, EventBean.class);
            for (EventBean eventBean : eventBeanList) {
                moduleUI.getContainer("eventBus")
                        .put(eventBean.getCid(), new EventBusListener(eventBean));
            }
        }

        //解析生命周期
        String stateJson = ParseJsonUtil.getStringByKey(json, JsonValue.STATE);
        if (!TextUtils.isEmpty(stateJson)) {
            List<StateBean> stateBeanList = ParseJsonUtil.getBeanList(stateJson, StateBean.class);
            for (StateBean stateBean : stateBeanList) {
                moduleUI.getContainer("state").put(stateBean.getCid(), stateBean);
            }
        }

        //告知界面解析完毕，可以执行部分生命周期调用的方法
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
