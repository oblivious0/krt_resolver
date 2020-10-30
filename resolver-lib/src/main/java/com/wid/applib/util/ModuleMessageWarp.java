package com.wid.applib.util;

/**
 * @author hyj
 * @time 2020/8/3 15:10
 * @class describe
 */
public class ModuleMessageWarp {

    private String msg;
    private Object obj;

    public ModuleMessageWarp(String msg) {
        this.msg = msg;
    }

    public ModuleMessageWarp(String msg, Object obj) {
        this.msg = msg;
        this.obj = obj;
    }

    public String getMsg() {
        return msg;
    }

    public Object getObj() {
        return obj;
    }
}
