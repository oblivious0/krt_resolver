package com.wid.applib.bean;

import java.util.List;

/**
 * author:MaGua
 * create on:2020/10/15 10:45
 * description
 */
public class AppInfoBean {
    /*
     *{
     *      "startType":"tabbar", 启动页类型
     *      "startPageId":"10n71tyrc2w", 启动页json
     *      "basePath":
     *      [
     *          {
     *              "prod":"https://ly3618.com/jx-test/api/",
     *              "dev":"https://ly3618.com/jx-test/api/"
     *          }
     *      ],
     *      "defaultPath":0
     * }"
     */

    private String startType;
    private String startPageId;
    private List<BasePathBean> basePath;
    private int defaultPath;

    public String getStartType() {
        return startType;
    }

    public void setStartType(String startType) {
        this.startType = startType;
    }

    public String getStartPageId() {
        return startPageId;
    }

    public void setStartPageId(String startPageId) {
        this.startPageId = startPageId;
    }

    public List<BasePathBean> getBasePath() {
        return basePath;
    }

    public void setBasePath(List<BasePathBean> basePath) {
        this.basePath = basePath;
    }

    public int getDefaultPath() {
        return defaultPath;
    }

    public void setDefaultPath(int defaultPath) {
        this.defaultPath = defaultPath;
    }

    public static class BasePathBean{
        private String prod;
        private String dev;

        public String getProd() {
            return prod;
        }

        public void setProd(String prod) {
            this.prod = prod;
        }

        public String getDev() {
            return dev;
        }

        public void setDev(String dev) {
            this.dev = dev;
        }
    }

}
