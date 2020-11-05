package com.wid.applib.tool;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.util.JsonValue;
import com.wid.applib.util.ViewValue;
import com.wid.applib.view.widget.BannerView;
import com.wid.applib.view.widget.BaseView;
import com.wid.applib.view.widget.ButtonView;
import com.wid.applib.view.widget.DefaultView;
import com.wid.applib.view.widget.LabelView;
import com.wid.applib.view.widget.LayoutView;
import com.wid.applib.view.widget.ListDataView;
import com.wid.applib.view.widget.ListMenuView;
import com.wid.applib.view.widget.NavbarView;
import com.wid.applib.view.widget.PicView;
import com.wid.applib.view.widget.TabTitleView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import krt.wid.util.ParseJsonUtil;


/**
 * @author hyj
 * @time 2020/8/20 9:16
 * @class describe 模块化组件工厂
 */
public class ModuleViewFactory {

    @SuppressLint("CheckResult")
    public static void createViews(List<BaseLayoutBean> list, ContextImp contextImp,
                                   ViewGroup vg, List<BaseView> views, boolean isChild) {
        Collections.sort(list, (o1, o2) -> o1.getCommon().getzIndex() - o2.getCommon().getzIndex());

        for (BaseLayoutBean bean: list) {
            String type = ParseJsonUtil.getStringByKey(ParseJsonUtil.toJson(bean), JsonValue.TYPE);
            if (TextUtils.isEmpty(type)) {
                return;
            }
            BaseView baseView = null;
            switch (type) {
                case ViewValue.LISTMENUS:
                    baseView = new ListMenuView(contextImp, bean, isChild);
                    break;
                case ViewValue.LAYOUT:
                    baseView = new LayoutView(contextImp, bean, isChild);
                    break;
                case ViewValue.PIC:
                    baseView = new PicView(contextImp, bean, isChild);
                    break;
                case ViewValue.LABEL:
                    baseView = new LabelView(contextImp, bean, isChild);
                    break;
                case ViewValue.LIST:
                    baseView = new ListDataView(contextImp, bean, isChild);
                    break;
                case ViewValue.BUTTON:
                    baseView = new ButtonView(contextImp, bean, isChild);
                    break;
                case ViewValue.NAVBAR:
                    baseView = new NavbarView(contextImp, bean, isChild);
                    break;
                case ViewValue.BANNER:
                    baseView = new BannerView(contextImp, bean, isChild);
                    break;
                case ViewValue.TABTITLE:
                    baseView = new TabTitleView(contextImp, bean, isChild);
                    break;
                default:
                    if (contextImp.getConvertTool() != null) {
                        baseView = contextImp.getConvertTool().convert(type, bean, isChild);
                    }
                    if (baseView == null) {
                        baseView = new DefaultView(contextImp, bean, isChild);
                    }
                    break;
            }
            if (!isChild) {
                contextImp.getContainer("view").put(baseView.cid, baseView);
            }
            vg.addView(baseView.view);
            views.add(baseView);
        }

    }

//    public static List<View> generate(List<BaseLayoutBean> list, ContextImp contextImp) {
//        List<View> views = new ArrayList<>();
//
//        Collections.sort(list, (o1, o2) -> o1.getCommon().getzIndex() - o2.getCommon().getzIndex());
//
//        for (int i = 0; i < list.size(); i++) {
//            Object object = list.get(i);
//            String type = ParseJsonUtil.getStringByKey(ParseJsonUtil.toJson(object), JsonValue.TYPE);
//            if (TextUtils.isEmpty(type)) {
//                continue;
//            }
//            View view = null;
//
//            switch (type) {
//                case ViewValue.LISTMENUS:
//                    view = new ListMenus(contextImp, object).generate();
//                    break;
//                case ViewValue.LAYOUT:
//                    view = new LayoutWidget(contextImp, object).generate();
//                    break;
//                case ViewValue.PIC:
//                    view = new PicWidget(contextImp, object).generate();
//                    break;
//                case ViewValue.LABEL:
//                    view = new LabelWidget(contextImp, object).generate();
//                    break;
//                case ViewValue.LIST:
//                    view = new ListWidget(contextImp, object).generate();
//                    break;
//                case ViewValue.BUTTON:
//                    view = new ButtonWidget(contextImp, object).generate();
//                    break;
//                case ViewValue.NAVBAR:
//                    view = new NavbarWidget(contextImp, object).generate();
//                    break;
//                case ViewValue.BANNER:
//                    view = new BannerWidget(contextImp, object).generate();
//                    break;
//                case ViewValue.TABTITLE:
//                    view = new TabTitle(contextImp, object).generate();
//                    break;
//                default:
//                    if (contextImp.getConvertTool() != null) {
//                        view = contextImp.getConvertTool().convert(type, object);
//                    }
//                    if (view == null) {
//                        view = new DefaulView(contextImp, object).generate();
//                    }
//                    break;
//            }
//            if (view != null) {
//                if (contextImp.getOnClickTool() != null) {
//                    List<EventBean> obj = ParseJsonUtil.getBeanList(
//                            ParseJsonUtil.getStringByKey(ParseJsonUtil.toJson(object), "event"),
//                            EventBean.class);
//                    if (obj != null) {
//                        view.setClickable(false);
//                        if (obj.size() != 0) {
//                            List<EventBean> events = new ArrayList<>();
//                            for (int z = 0; z < obj.size(); z++) {
//                                if (obj.get(z).getTerminal() == null) {
//                                    events.add(obj.get(z));
//                                    continue;
//                                }
//                                if (obj.get(z).getTerminal().contains(MLib.TERMINAL)) {
//                                    events.add(obj.get(z));
//                                }
//                            }
//                            EventBindUtil.bindClick(view, contextImp.getOnClickTool(), events);
//                        } else {
//                            view.setClickable(false);
//                        }
//                    }
//                }
//            }
//
//            String cid = ParseJsonUtil.getStringByKey(ParseJsonUtil.toJson(object), "cid");
//            contextImp.getContainer("view").put(cid, view);
//            views.add(view);
//        }
//
//        return views;
//    }
}
