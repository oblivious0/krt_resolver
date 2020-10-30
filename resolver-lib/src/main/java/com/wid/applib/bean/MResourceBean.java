package com.wid.applib.bean;

/**
 * @author hyj
 * @time 2020/9/15 16:26
 * @class describe
 */
public class MResourceBean {
    /**
     * "file_name": "rAAyCF8zqr6ARJUnAAAl7Mfd5fY831.jpg",
     * "type": "1",
     * "image_name": "QQ截图20190603192230",
     * "image_url": "https://www.krtservice.com/krt-modules/group1/M00/00/24/rAAyCF8zqr6ARJUnAAAl7Mfd5fY831.jpg",
     * "remark": null
     */

    private String file_name;
    private String type;
    private String image_name;
    private String image_url;
    private String remark;

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
