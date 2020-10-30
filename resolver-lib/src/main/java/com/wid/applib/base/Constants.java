package com.wid.applib.base;

import android.os.Environment;

import com.blankj.utilcode.util.AppUtils;

import java.io.File;

/**
 * author:Marcus
 * create on:2019/5/30 11:23
 * description
 */
public class Constants {

    private static String Base_Module = "https://www.krtservice.com/krt-module/apis/module/";

    public static String getUrl(String val) {
        return Base_Module + val;
    }

    public static final String imgPath = Environment.getExternalStorageDirectory().getPath() + File.separator
            + AppUtils.getAppName() + File.separator + "image" + File.separator;

    public static final String path = Environment.getExternalStorageDirectory().getPath() + File.separator + AppUtils.getAppName() + File.separator;

    public static final String basePath = Environment.getExternalStorageDirectory().getPath() + File.separator + AppUtils.getAppName();
}
