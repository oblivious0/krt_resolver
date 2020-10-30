package com.wid.applib.view.module;

import android.content.Context;

import com.wid.applib.imp.ContextImp;
import com.wid.applib.imp.WidgetImp;

/**
 * author:Marcus
 * create on:2020/5/28 10:35
 * description
 */
public abstract class BaseWidget implements WidgetImp {

    protected Object object;
    protected ContextImp imp;
    private Context mContext;

    public BaseWidget(ContextImp context, Object object) {
        this.object = object;
        this.imp = context;
    }

    public BaseWidget(Context context, Object object) {
        this.mContext = context;
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public Context getContext(){
        if (imp!=null){
            return imp.getContext();
        }else{
            return mContext;
        }
    }
}
