package com.wid.applib.skin;

import com.wid.applib.bean.SkinIconBean;

import java.util.List;

import krt.wid.util.ParseJsonUtil;

/**
 * author: MaGua
 * create on:2021/1/20 11:29
 * description
 */
public class SkinManager {

    private SkinManager(String skinList) {
        iconBeans = ParseJsonUtil.getBeanList(skinList, SkinIconBean.class);
    }

    private static SkinManager instance;
    private List<SkinIconBean> iconBeans;

    public static SkinManager getInstance() {
        return instance;
    }

    public static boolean isLoadSkin() {
        return instance != null;
    }

    public SkinIconBean getSkinByCode(String code) {
        for (SkinIconBean icon : iconBeans) {
            if (code.equals(icon.getCode())) {
                return icon;
            }
        }
        return null;
    }


    public static class Builder {
        private String skinCode;
        private String skinList;

        public Builder setSkinCode(String skinCode) {
            this.skinCode = skinCode;
            return this;
        }

        public Builder setSkinList(String skinList) {
            this.skinList = skinList;
            return this;
        }

        public void generate() {
            if (instance == null) instance = new SkinManager(skinList);
        }
    }
}

