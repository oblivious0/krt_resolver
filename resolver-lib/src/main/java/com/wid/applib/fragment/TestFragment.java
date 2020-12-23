package com.wid.applib.fragment;

import com.wid.applib.base.BaseModuleFragment;
import com.wid.applib.event.ViewEventImp;
import com.wid.applib.imp.ConvertImp;

/**
 * author:Marcus
 * create on:2019/3/26 20:35
 * description
 */
public class TestFragment extends BaseModuleFragment {

    @Override
    protected void init() {

    }

    @Override
    public ConvertImp getConvertTool() {
        return null;
    }

    @Override
    public ViewEventImp getOnClickTool() {
        return null;
    }

    @Override
    public boolean isLogin() {
        return false;
    }

    @Override
    public String[] getAuthorization() {
        return null;
    }
}
