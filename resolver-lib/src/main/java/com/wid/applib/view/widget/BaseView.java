package com.wid.applib.view.widget;

import android.annotation.SuppressLint;
import android.view.View;

import com.wid.applib.MLib;
import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.bean.EventBean;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.util.EventBindUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import krt.wid.util.ParseJsonUtil;

/**
 * author: MaGua
 * create on:2020/11/2 17:07
 * description 组件基类，组件化组件需继承此类
 * 泛型类型为实例出的Android控件的类型；如果是组合控件，则为最外层的容器组件（ViewGroup子类)
 */
public abstract class BaseView<T extends View> {

    /**
     * 组件的CID
     */
    public String cid;
    /**
     * 组件的具体实例
     */
    public T view;
    /**
     * 此组件是否为某列表组件的子组件
     */
    public boolean isListChild = false;

    /**
     * 此组件所在的全局（Activity或者Fragment)
     */
    public ContextImp contextImp;

    /**
     * 组件类别
     */
    public String type;

    /**
     * 组件初始化配置实例
     */
    protected BaseLayoutBean bean;

    public BaseView(ContextImp imp, BaseLayoutBean obj) {
        this.contextImp = imp;
        this.bean = obj;
        generate();
    }

    public BaseView(ContextImp imp, BaseLayoutBean obj, boolean isListChild) {
        this.isListChild = isListChild;
        this.contextImp = imp;
        this.bean = obj;
        generate();
    }

    @SuppressLint("CheckResult")
    private void generate() {
        cid = bean.getCid();
        initView();

        view.post(() -> Observable.just(bean)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .filter(baseLayoutBean -> bindInNewThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(baseLayoutBean -> {
                    bindInMainThread();
                    bindEvent();
                }));
    }

    /**
     * 组件所有初始化操作必须在主线程内
     */
    protected abstract void initView();

    /**
     * 初始化完成后，如需执行除了访问接口以外的耗时操作的时间在此方法完成，比如：倒计时
     * 所有影响组件外观样式操作禁止放入此线程操作内，如果是初始化项请在initView()执行
     * 如果需要中止之后操作，返回false，则不会执行bindInMainThread
     *
     * @return
     */
    protected abstract boolean bindInNewThread();

    /**
     * 当组件完成初始化操作之后需要执行的事件
     * 注意：此方法在bindInNewThread()完成之后执行，并且可被bindInNewThread()方法中断
     */
    protected abstract void bindInMainThread();

    /**
     * 设置组件的属性方法
     * 注意：如果组件类型为容器类控件(不包括组合控件)组件，需要自行负责子组件的绑定，或则直接丢给子组件绑定
     * 可参照LayoutView绑定方式
     *
     * @param cid 需要设置组件的cid
     * @param key 需要设置组件的属性
     * @param val 设置的内容
     */
    public abstract void bindData(String cid, String key, String val);

    /**
     * 绑定组件事件：当terminal默认为所有端都需要执行的事件
     */
    private void bindEvent() {
        if (bean.getEvent() != null) {
            view.setClickable(false);
            if (bean.getEvent().size() != 0) {
                List<EventBean> events = new ArrayList<>();
                for (int z = 0; z < bean.getEvent().size(); z++) {
                    if (bean.getEvent().get(z).getTerminal() == null) {
                        events.add(bean.getEvent().get(z));
                        continue;
                    }
                    if (bean.getEvent().get(z).getTerminal().contains(MLib.TERMINAL)) {
                        events.add(bean.getEvent().get(z));
                    }
                }
                EventBindUtil.bindClick(view, contextImp.getOnClickTool(), events);
            } else {
                view.setClickable(false);
            }
        }
    }

}
