package com.wid.applib.tool;

import android.text.TextUtils;
import android.view.View;

import com.wid.applib.MLib;
import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.bean.EventBean;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.util.EventBindUtil;
import com.wid.applib.util.JsonValue;
import com.wid.applib.util.ViewValue;
import com.wid.applib.view.module.BannerWidget;
import com.wid.applib.view.module.ButtonWidget;
import com.wid.applib.view.module.DefaulView;
import com.wid.applib.view.module.LabelWidget;
import com.wid.applib.view.module.LayoutWidget;
import com.wid.applib.view.module.ListMenus;
import com.wid.applib.view.module.ListWidget;
import com.wid.applib.view.module.NavbarWidget;
import com.wid.applib.view.module.PicWidget;
import com.wid.applib.view.module.TabTitle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import krt.wid.util.ParseJsonUtil;


/**
 * @author hyj
 * @time 2020/8/20 9:16
 * @class describe 模块化组件工厂
 */
public class ModuleViewFactory {

    public static List<View> generate(List<BaseLayoutBean> list, ContextImp contextImp) {
        List<View> views = new ArrayList<>();

        Collections.sort(list, (o1, o2) -> o1.getCommon().getzIndex() - o2.getCommon().getzIndex());

        for (int i = 0; i < list.size(); i++) {
            Object object = list.get(i);
            String type = ParseJsonUtil.getStringByKey(ParseJsonUtil.toJson(object), JsonValue.TYPE);

            String cids = ParseJsonUtil.getStringByKey(ParseJsonUtil.toJson(object), "cid");
//            if (cids.equals("120tjt3i8wx")){
//                LogUtils.e("120tjt3i8wx");
//            }

            if (TextUtils.isEmpty(type)) {
                continue;
            }

            View view = null;

            switch (type) {
                case ViewValue.LISTMENUS:
                    view = new ListMenus(contextImp, object).generate();
                    break;
                case ViewValue.LAYOUT:
                    view = new LayoutWidget(contextImp, object).generate();
                    break;
                case ViewValue.PIC:
                    view = new PicWidget(contextImp, object).generate();
                    break;
                case ViewValue.LABEL:
                    view = new LabelWidget(contextImp, object).generate();
                    break;
                case ViewValue.LIST:
                    view = new ListWidget(contextImp, object).generate();
                    break;
                case ViewValue.BUTTON:
                    view = new ButtonWidget(contextImp, object).generate();
                    break;
                case ViewValue.NAVBAR:
                    view = new NavbarWidget(contextImp, object).generate();
                    break;
                case ViewValue.BANNER:
                    view = new BannerWidget(contextImp, object).generate();
                    break;
                case ViewValue.TABTITLE:
                    view = new TabTitle(contextImp, object).generate();
                    break;
                default:
                    if (contextImp.getConvertTool() != null) {
                        view = contextImp.getConvertTool().convert(type, object);
                    }
                    if (view == null) {
                        view = new DefaulView(contextImp, object).generate();
                    }
                    break;
            }
            if (view != null) {
                if (contextImp.getOnClickTool() != null) {
                    List<EventBean> obj = ParseJsonUtil.getBeanList(
                            ParseJsonUtil.getStringByKey(ParseJsonUtil.toJson(object), "event"),
                            EventBean.class);
                    if (obj != null) {
                        view.setClickable(false);
                        if (obj.size() != 0) {
                            List<EventBean> events = new ArrayList<>();
                            for (int z = 0; z < obj.size(); z++) {
                                if (obj.get(z).getTerminal()==null){
                                    events.add(obj.get(z));
                                    continue;
                                }
                                if (obj.get(z).getTerminal().contains(MLib.TERMINAL)) {
                                    events.add(obj.get(z));
                                }
                            }
                            EventBindUtil.bindClick(view, contextImp.getOnClickTool(), events);
                        } else {
                            view.setClickable(false);
                        }
                    }
                }
            }

            String cid = ParseJsonUtil.getStringByKey(ParseJsonUtil.toJson(object), "cid");
            contextImp.getContainer("view").put(cid, view);
            views.add(view);
        }

        return views;
    }
}
