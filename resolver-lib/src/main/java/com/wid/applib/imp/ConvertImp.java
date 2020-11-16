package com.wid.applib.imp;

import android.view.View;

import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.view.widget.BaseView;

/**
 * author:Marcus
 * create on:2020/5/28 9:29
 * description
 */
public interface ConvertImp {
    BaseView convert(String type, BaseLayoutBean object, boolean isChild);
}
