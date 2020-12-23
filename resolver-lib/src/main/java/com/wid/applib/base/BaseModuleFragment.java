package com.wid.applib.base;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.blankj.utilcode.util.LogUtils;
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
import com.wid.applib.view.widget.BaseView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import krt.wid.http.MCallBack;
import krt.wid.http.Result;
import krt.wid.util.ParseJsonUtil;

/**
 * @author hyj
 * @time 2020/8/19 15:25
 * @class describe
 */
public abstract class BaseModuleFragment extends Fragment implements ContextImp {

    /**
     * 模块化组件容器，KEY：组件CID，VALUE：组件实例
     */
    public HashMap<String, BaseView> viewContainer = new HashMap<>();

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

    protected Context mContext;
    protected boolean isPrepared = false;

    protected FrameLayout parent;
    protected FrameLayout navbar;
    protected SwipeRefreshLayout swipeRefreshLayout;

    private boolean isInitView = false;

    private String jsonFile;
    private String pageType = "";
    private AppLibManager manager;

    public BaseModuleFragment setJsonFile(String name) {
        this.jsonFile = name;
        return this;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        isPrepared = true;
        return inflater.inflate(R.layout.fragment_module, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
        init();
        AppLibManager.registerContext(getPageId(), this);
        parent = view.findViewById(R.id.parent);
        navbar = view.findViewById(R.id.navbar);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setEnabled(false);
        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        excute();
    }

    protected abstract void init();

    public void excute() {
        if (!isInitView) return;
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

    /**
     * 懒加载数据
     */
    private void lazyLoad() {
        boolean visable = getUserVisibleHint();
        if (visable && isPrepared) {
            loadData();
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            lazyLoad();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AppLibManager.unRegisterContext(getPageId());
        EventBus.getDefault().unregister(this);
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
                                            getActivity().getIntent().getStringExtra("param"), ParamBean.class);

                                    if (paramBeanList != null) {
                                        for (ParamBean bean : paramBeanList) {
                                            commonElementContainer.put(bean.getKey(), bean.getVal());
                                        }
                                    }
                                }

                                @Override
                                public void onFinish() {
                                    isInitView = true;
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

    @Override
    public String getPageId() {
        return jsonFile;
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
        return mContext;
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

    @Override
    public String getPageType() {
        return pageType;
    }

    @Override
    public void setPageType(String pageType) {
        this.pageType = pageType==null?"":pageType;
    }
}
