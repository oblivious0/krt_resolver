package com.wid.applib.tool;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.blankj.utilcode.util.LogUtils;
import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.util.JsonValue;
import com.wid.applib.util.ViewValue;
import com.wid.applib.widget.base.InputView;
import com.wid.applib.widget.base.ScorllLabelView;
import com.wid.applib.widget.combination.WeatherView;
import com.wid.applib.widget.list.BannerView;
import com.wid.applib.widget.BaseView;
import com.wid.applib.widget.base.ButtonView;
import com.wid.applib.widget.combination.CountdownView;
import com.wid.applib.widget.base.DefaultView;
import com.wid.applib.widget.base.LabelView;
import com.wid.applib.widget.layout.LayoutView;
import com.wid.applib.widget.base.LineProgressBarView;
import com.wid.applib.widget.list.ListDataView;
import com.wid.applib.widget.list.ListMenuView;
import com.wid.applib.widget.combination.NavbarView;
import com.wid.applib.widget.base.PicView;
import com.wid.applib.widget.layout.TabTitleView;
import com.wid.applib.widget.list.OptionListView;

import java.util.Collections;
import java.util.List;

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

        for (BaseLayoutBean bean : list) {
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
                case ViewValue.COUNTDOWN:
                    baseView = new CountdownView(contextImp, bean, isChild);
                    break;
                case ViewValue.LINEPROGRESS:
                    baseView = new LineProgressBarView(contextImp, bean, isChild);
                    break;
                case ViewValue.INPUT:
                    baseView = new InputView(contextImp, bean, isChild);
                    break;
                case ViewValue.SELECTLIST:
                    baseView = new OptionListView(contextImp, bean, isChild);
                    break;
                case ViewValue.SCROLLLABEL:
                    baseView = new ScorllLabelView(contextImp, bean, isChild);
                    break;
                case ViewValue.WEATHER:
                    baseView = new WeatherView(contextImp, bean, isChild);
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
            if (baseView.view == null) {
                LogUtils.e(baseView.cid);
            } else {
                vg.addView(baseView.view);
            }
            views.add(baseView);
        }

    }
}
