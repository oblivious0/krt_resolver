package com.krt.wid.resolver.module;

import com.krt.wid.resolver.tool.BaseEventInstance;
import com.wid.applib.base.BaseModuleActivity;
import com.wid.applib.event.ViewEventImp;
import com.wid.applib.imp.ConvertImp;

/**
 * author: MaGua
 * create on:2020/10/27 9:05
 * description
 */
public class BaseActivity extends BaseModuleActivity {

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

        if (eventInstance == null)
            eventInstance = new BaseEventInstance(this);

        return null;
    }

    @Override
    public boolean isLogin() {
        return false;
    }

    @Override

    public String[] getAuthorization() {
        return new String[]{"", ""};
    }
}
