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
 * description
 */
public abstract class BaseView<T extends View> {

    public String cid;
    public T view;
    public boolean isListChild = false;
    public ContextImp contextImp;

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
        Observable.just(bean)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .filter(baseLayoutBean -> {
                    cid = bean.getCid();
                    bindEvent();

                    return bindInNewThread();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(baseLayoutBean -> {
                    bindInMainThread();
                });

    }

    protected abstract boolean bindInNewThread();

    protected abstract void bindInMainThread();

    public abstract void bindData(String key, String val);

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
