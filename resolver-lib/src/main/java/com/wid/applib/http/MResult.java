package com.wid.applib.http;

import krt.wid.config.BaseModule;

/**
 * author: MaGua
 * create on:2020/12/12 9:21
 * description
 */
public class MResult<T> {

    public int count = -1;

    public int totalPage = -1;

    public String msg;

    public int code;

    public T data;

    public boolean isSuccess() {
        return code == BaseModule.getBaseModuleConfig().getServerSuccessCode();
    }
}
