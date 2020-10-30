package com.wid.applib.bean;

import java.util.List;

/**
 * @author hyj
 * @time 2020/8/19 17:02
 * @class describe
 */
public class EventBean {
    /**
     * {
     * "type": "navigator",
     * "ifOuterChain": false,
     * "ifModulePage": false,
     * "url": "",
     * "pageId": "",
     * "urlforH5": "/views/index/searchPage",
     * "params": [],
     * "naviType": "navigateTo"
     * }
     */

    /**
     * 跳转类型  navigator：界面跳转
     */
    private String type;
    /**
     * 是否跳转链接地址
     */
    private boolean ifOuterChain;
    /**
     * 是否跳转模块化  （如果ifOuterChain与ifModulePage均为false，且type为navigator时，跳转至原生界面）
     */
    private boolean ifModulePage;
    /**
     * 如果为原生界面时，url为跳转标识，否则为跳转的链接地址
     */
    private String url;
    /**
     * 如果为模块化时，pageId为json文件名
     */
    private String pageId;

    /**
     * 小程序用，app暂未用到
     */
    private String urlforH5;

    /**
     * 跳转至原生界面时对应的传参
     */
    private List<ParamBean> params;

    private String naviType;
    /**
     * 小程序用
     */
    private boolean urlFromApi;
    private boolean ifAppsetNav;
    private int backDelta;

    private boolean needLogin;
    private String cid;
    private List<String> ajaxIds;

    private String target;
    private String name;
    private String number;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAjaxIds() {
        return ajaxIds;
    }

    public void setAjaxIds(List<String> ajaxIds) {
        this.ajaxIds = ajaxIds;
    }

    private List<ActionBean> list;

    public List<ActionBean> getList() {
        return list;
    }

    public void setList(List<ActionBean> list) {
        this.list = list;
    }

    public boolean isNeedLogin() {
        return needLogin;
    }

    public void setNeedLogin(boolean needLogin) {
        this.needLogin = needLogin;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public boolean isUrlFromApi() {
        return urlFromApi;
    }

    public void setUrlFromApi(boolean urlFromApi) {
        this.urlFromApi = urlFromApi;
    }

    public boolean isIfAppsetNav() {
        return ifAppsetNav;
    }

    public void setIfAppsetNav(boolean ifAppsetNav) {
        this.ifAppsetNav = ifAppsetNav;
    }

    public int getBackDelta() {
        return backDelta;
    }

    public void setBackDelta(int backDelta) {
        this.backDelta = backDelta;
    }

    public List<ParamBean> getParams() {
        return params;
    }

    public void setParams(List<ParamBean> params) {
        this.params = params;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isIfOuterChain() {
        return ifOuterChain;
    }

    public void setIfOuterChain(boolean ifOuterChain) {
        this.ifOuterChain = ifOuterChain;
    }

    public boolean isIfModulePage() {
        return ifModulePage;
    }

    public void setIfModulePage(boolean ifModulePage) {
        this.ifModulePage = ifModulePage;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getUrlforH5() {
        return urlforH5;
    }

    public void setUrlforH5(String urlforH5) {
        this.urlforH5 = urlforH5;
    }

    public String getNaviType() {
        return naviType;
    }

    public void setNaviType(String naviType) {
        this.naviType = naviType;
    }



}
