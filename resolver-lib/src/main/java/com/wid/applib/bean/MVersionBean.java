package com.wid.applib.bean;

import java.util.List;

/**
 * @author hyj
 * @time 2020/9/14 15:26
 * @class describe
 */
public class MVersionBean {

    VersionInfoBean last_version;
    VersionInfoBean interpreter_last_version;
    List<VersionInfoBean> enable_version;
    List<VersionInfoBean> interpreter_enable_version;

    public VersionInfoBean getLast_version() {
        return last_version;
    }

    public void setLast_version(VersionInfoBean last_version) {
        this.last_version = last_version;
    }

    public List<VersionInfoBean> getEnable_version() {
        return enable_version;
    }

    public void setEnable_version(List<VersionInfoBean> enable_version) {
        this.enable_version = enable_version;
    }

    public VersionInfoBean getInterpreter_last_version() {
        return interpreter_last_version;
    }

    public void setInterpreter_last_version(VersionInfoBean interpreter_last_version) {
        this.interpreter_last_version = interpreter_last_version;
    }

    public List<VersionInfoBean> getInterpreter_enable_version() {
        return interpreter_enable_version;
    }

    public void setInterpreter_enable_version(List<VersionInfoBean> interpreter_enable_version) {
        this.interpreter_enable_version = interpreter_enable_version;
    }

    public static class VersionInfoBean{
        private String version;
        private String interpreter_code;
        private String publish_time;
        private String base_skin;
        private String custom_skin;

        /* -- 2版本解析器追加 -- */
        //发布状态：0测试版，1体验版，2发布版，3历史发布版
        private String is_publish;

        private String app_info = "";
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

        public String getIs_publish() {
            return is_publish;
        }

        public void setIs_publish(String is_publish) {
            this.is_publish = is_publish;
        }

        public String getApp_info() {
            return app_info;
        }

        public void setApp_info(String app_info) {
            this.app_info = app_info;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getInterpreter_code() {
            return interpreter_code;
        }

        public void setInterpreter_code(String interpreter_code) {
            this.interpreter_code = interpreter_code;
        }

        public String getPublish_time() {
            return publish_time;
        }

        public void setPublish_time(String publish_time) {
            this.publish_time = publish_time;
        }

        public String getBase_skin() {
            return base_skin;
        }

        public void setBase_skin(String base_skin) {
            this.base_skin = base_skin;
        }

        public String getCustom_skin() {
            return custom_skin;
        }

        public void setCustom_skin(String custom_skin) {
            this.custom_skin = custom_skin;
        }
    }
}
