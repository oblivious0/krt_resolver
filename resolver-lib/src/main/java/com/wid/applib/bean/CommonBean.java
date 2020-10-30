package com.wid.applib.bean;

import java.util.List;

/**
 * author:Marcus
 * create on:2019/6/4 15:36
 * description
 */
public class CommonBean {

    /**
     * height : 181
     * isHidden : false
     * list : [{"icon":"https://www.ly3618.com/yijiyou/static/images/index/scene.png","text":"景区","url":"/views/index/scenicSpot"},{"icon":"https://www.ly3618.com/yijiyou/static/images/index/hotel.png","text":"酒店","url":"/views/index/grogshop"},{"icon":"https://www.ly3618.com/yijiyou/static/images/index/food.png","text":"餐饮","url":"/views/index/catering"},{"icon":"https://www.ly3618.com/yijiyou/static/images/index/village.png","text":"农家乐","url":"/views/index/agritainment"}]
     * lock : false
     * name :
     * num : 4
     * width : 750
     * x : 0
     * y : 411
     * zIndex : 11
     */

    private int height;
    private boolean isHidden;
    private boolean lock;
    private String name;
    private int num;
    private int width;
    private int x;
    private int y;
    private int zIndex;
    private String src;
    private String targetUrl;
    private String text;
    private List<ListBean> list;
    private int xPadding;
    private int yPadding;
    private String direction;
    private boolean wrap;
    private int itemW;
    private int itemH;
    private int horizontalNum;
    private int radius;
    private String shadowColor;
    private List<LinksBean> links;
    private String title;
    private String backText;
    private String rightText;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackText() {
        return backText;
    }

    public void setBackText(String backText) {
        this.backText = backText;
    }

    public String getRightText() {
        return rightText;
    }

    public void setRightText(String rightText) {
        this.rightText = rightText;
    }

    public List<LinksBean> getLinks() {
        return links;
    }

    public void setLinks(List<LinksBean> links) {
        this.links = links;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isIsHidden() {
        return isHidden;
    }

    public void setIsHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZIndex() {
        return zIndex;
    }

    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public int getzIndex() {
        return zIndex;
    }

    public void setzIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    public int getxPadding() {
        return xPadding;
    }

    public void setxPadding(int xPadding) {
        this.xPadding = xPadding;
    }

    public int getyPadding() {
        return yPadding;
    }

    public void setyPadding(int yPadding) {
        this.yPadding = yPadding;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public boolean isWrap() {
        return wrap;
    }

    public void setWrap(boolean wrap) {
        this.wrap = wrap;
    }

    public int getItemW() {
        return itemW;
    }

    public void setItemW(int itemW) {
        this.itemW = itemW;
    }

    public int getItemH() {
        return itemH;
    }

    public void setItemH(int itemH) {
        this.itemH = itemH;
    }

    public int getHorizontalNum() {
        return horizontalNum;
    }

    public void setHorizontalNum(int horizontalNum) {
        this.horizontalNum = horizontalNum;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String getShadowColor() {
        return shadowColor;
    }

    public void setShadowColor(String shadowColor) {
        this.shadowColor = shadowColor;
    }

}