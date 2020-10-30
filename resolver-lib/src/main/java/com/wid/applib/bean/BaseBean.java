package com.wid.applib.bean;

import java.util.List;

/**
 * author:Marcus
 * create on:2019/6/11 16:35
 * description
 */
public class BaseBean {
    private String bgColor;

    private String name;

    private List<Object> page;

    private int pageHeight;

    private String cid;

    private List<Object> bindData;

    private Object navbar;

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Object> getPage() {
        return page;
    }

    public void setPage(List<Object> page) {
        this.page = page;
    }

    public int getPageHeight() {
        return pageHeight;
    }

    public void setPageHeight(int pageHeight) {
        this.pageHeight = pageHeight;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public List<Object> getBindData() {
        return bindData;
    }

    public void setBindData(List<Object> bindData) {
        this.bindData = bindData;
    }

    public Object getNavbar() {
        return navbar;
    }

    public void setNavbar(Object navbar) {
        this.navbar = navbar;
    }
}
