package com.wid.applib.bean;

/**
 * @author hyj
 * @time 2020/9/5 9:59
 * @class describe
 */
public class LinksBean {
    /**
     * {
     * "text": "推荐",
     * "pageId": "9ykvztr680"
     * }
     */
    private String text;
    private String pageId;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }
}

