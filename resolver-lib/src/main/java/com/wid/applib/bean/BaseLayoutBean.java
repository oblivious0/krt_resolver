package com.wid.applib.bean;

import java.util.List;

/**
 * author:Marcus
 * create on:2019/11/13 15:38
 * description
 */
public class BaseLayoutBean {

    /**
     * children : []
     * cid : 24wyxeiiba0
     * common : {"height":181,"isHidden":false,"list":[{"icon":"https://www.ly3618.com/yijiyou/static/images/index/scene.png","text":"景区","url":"/views/index/scenicSpot"},{"icon":"https://www.ly3618.com/yijiyou/static/images/index/hotel.png","text":"酒店","url":"/views/index/grogshop"},{"icon":"https://www.ly3618.com/yijiyou/static/images/index/food.png","text":"餐饮","url":"/views/index/catering"},{"icon":"https://www.ly3618.com/yijiyou/static/images/index/village.png","text":"农家乐","url":"/views/index/agritainment"}],"lock":false,"name":"","num":4,"width":750,"x":0,"y":411,"zIndex":11}
     * name : 菜单列表
     * nid : 0
     * style : {}
     * type : listMenus
     * waitToCopy : false
     */

    private String cid;
    private CommonBean common;
    private String name;
    private String nid;
    private StyleBean style;
    private String type;
    private boolean waitToCopy;
    private List<BaseLayoutBean> children;
    private List<AjaxBean> ajax;
    private StaticDataBean staticData;
    private List<EventBean> event;
    private AnimationBean animation;

    public AnimationBean getAnimation() {
        return animation;
    }

    public void setAnimation(AnimationBean animation) {
        this.animation = animation;
    }

    public List<EventBean> getEvent() {
        return event;
    }

    public void setEvent(List<EventBean> event) {
        this.event = event;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public CommonBean getCommon() {
        return common;
    }

    public void setCommon(CommonBean common) {
        this.common = common;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public StyleBean getStyle() {
        return style;
    }

    public void setStyle(StyleBean style) {
        this.style = style;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isWaitToCopy() {
        return waitToCopy;
    }

    public void setWaitToCopy(boolean waitToCopy) {
        this.waitToCopy = waitToCopy;
    }

    public List<BaseLayoutBean> getChildren() {
        return children;
    }

    public void setChildren(List<BaseLayoutBean> children) {
        this.children = children;
    }

    public List<AjaxBean> getAjax() {
        return ajax;
    }

    public void setAjax(List<AjaxBean> ajax) {
        this.ajax = ajax;
    }


    public StaticDataBean getStaticData() {
        return staticData;
    }

    public void setStaticData(StaticDataBean staticData) {
        this.staticData = staticData;
    }






}
