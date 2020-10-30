package com.wid.applib.base;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jaeger.library.StatusBarUtil;
import com.wid.applib.R;
import com.wid.applib.bean.AjaxBean;
import com.wid.applib.bean.BroadCastBean;
import com.wid.applib.bean.ParamBean;
import com.wid.applib.bean.StateBean;
import com.wid.applib.event.BroadCastMessageWarp;
import com.wid.applib.event.EventBusListener;
import com.wid.applib.exception.ModuleJsonParseException;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.manager.AppLibManager;
import com.wid.applib.util.Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import krt.wid.http.MCallBack;
import krt.wid.http.Result;
import krt.wid.util.ParseJsonUtil;

/**
 * @author hyj
 * @time 2020/8/19 9:07
 * @class describe
 */
public abstract class BaseModuleActivity extends AppCompatActivity implements ContextImp {

    /**
     * 模块化组件容器，KEY：组件CID，VALUE：组件实例
     */
    public HashMap<String, View> viewContainer = new HashMap<>();

    /**
     * 模块化网络请求反射bean容器，KEY:AJAX的CID，VALUE：实体类
     */
    public HashMap<String, AjaxBean> ajaxsContainer = new HashMap<>();

    /**
     * 网络请求回调处理，和ajaxs配套使用
     */
    public HashMap<String, MCallBack<Result>> ajaxCallbackContainer = new HashMap<>();

    public HashMap<String, EventBusListener> eventBusContainer = new HashMap<>();

    public HashMap<String, BroadCastBean> broadCastContainer = new HashMap<>();

    public HashMap<String, StateBean> stateContainer = new HashMap<>();
    /**
     * 公共变量集
     */
    public HashMap<String, String> commonElementContainer = new HashMap<>();


    private String pageId;

    private FrameLayout parent;
    private FrameLayout navbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AppLibManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_module);
        AppLibManager.registerContext(getPageId(), this);
        EventBus.getDefault().register(this);
        StatusBarUtil.setTranslucentForImageView(this, 0, null);
        init();
        pageId = getIntent().getStringExtra("name");
        parent = findViewById(R.id.parent);
        navbar = findViewById(R.id.navbar);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setEnabled(false);
        loadData();
    }

    protected abstract void init();

    public void loadData() {
        if (parent.getChildCount() == 0) {
            File file = new File(Constants.path + getPageId() + ".json");
            if (file.exists()) {
                String json = Util.getJson(file);
                try {
                    manager = new AppLibManager.Builder(this)
                            .setJson(json)
                            .setManagerListenner(new AppLibManager.AppLibManagerListener() {
                                @Override
                                public void initElement() {
                                    List<ParamBean> paramBeanList = ParseJsonUtil.getBeanList(
                                            getIntent().getStringExtra("param"), ParamBean.class);
                                    for (ParamBean bean : paramBeanList) {
                                        commonElementContainer.put(bean.getKey(), bean.getVal());
                                    }
                                }

                                @Override
                                public void onFinish() {
                                    excute();
                                }
                            })
                            .convert();
                } catch (ModuleJsonParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void excute() {
        for (StateBean bean : stateContainer.values()) {
            switch (bean.getType()) {
                case "created":
                    for (String cid : bean.getEvents()) {
                        eventBusContainer.get(cid).execute(this);
                    }
                    break;
                case "logout":
                    if (!isLogin())
                        for (String cid : bean.getEvents()) {
                            eventBusContainer.get(cid).execute(this);
                        }
                    break;
                case "login":
                    if (isLogin())
                        for (String cid : bean.getEvents()) {
                            eventBusContainer.get(cid).execute(this);
                        }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (manager != null) {
            excute();
        }
    }

    /**
     * 默认不使用部分手机自带的手机字号大小
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        //非默认值
        if (res.getConfiguration().fontScale != 1) {
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppLibManager.unRegisterContext(getPageId());
        EventBus.getDefault().unregister(this);
    }

    @Override
    public String getPageId() {
        return pageId;
    }

    @Override
    public FrameLayout getHeaderView() {
        return navbar;
    }

    @Override
    public FrameLayout getContentView() {
        return parent;
    }

    @Override
    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public HashMap getContainer(String type) {
        switch (type) {
            case "view":
                return viewContainer;
            case "ajax":
                return ajaxsContainer;
            case "callback":
                return ajaxCallbackContainer;
            case "eventBus":
                return eventBusContainer;
            case "broadCast":
                return broadCastContainer;
            case "state":
                return stateContainer;
            case "element":
                return commonElementContainer;
            default:
                return null;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void receiveBroadCast(BroadCastMessageWarp bean) {
        for (BroadCastBean broadCastBean : broadCastContainer.values()) {
            if (broadCastBean.getName().equals(bean.getBroadCastName())) {
                for (String cid : broadCastBean.getEvents()) {
                    if (bean.getParams() != null) {
                        commonElementContainer.putAll(bean.getParams());
                    }
                    eventBusContainer.get(cid).execute(this);
                }
            }
        }
    }
}
