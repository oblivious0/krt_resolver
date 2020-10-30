package com.krt.wid.resolver.module;

import com.krt.wid.resolver.tool.BaseEventInstance;
import com.wid.applib.base.BaseModuleFragment;
import com.wid.applib.event.ViewEventImp;
import com.wid.applib.imp.ConvertImp;

/**
 * author: MaGua
 * create on:2020/10/27 9:04
 * description
 */
public class BaseFragment extends BaseModuleFragment {

    BaseEventInstance eventInstance;

    @Override
    protected void init() {

    }

    @Override
    public ConvertImp getConvertTool() {
        return null;
    }

    @Override
    public ViewEventImp getOnClickTool() {

        if (eventInstance==null)
            eventInstance = new BaseEventInstance(this);

        return eventInstance;
    }

    @Override
    public boolean isLogin() {
        return false;
    }

    @Override
    public String getAuthorization() {
        return null;
    }
}
