package com.wid.applib.config;

import android.text.TextUtils;
import android.util.Log;

import com.wid.applib.base.BaseModuleFragment;

/**
 * @author: MaGua
 * @create on:2020/10/26 9:35
 * @description 模块化项目配置实体类：再App初始化时完成实例
 */
public class MProConfig {

    private MProConfig() {
    }

    private static MProConfig mProConfig;

    public static MProConfig getInstance() {
        if (mProConfig == null) {
            Log.e("MLib", "未在Application实例化Module配置！");
        }
        return mProConfig;
    }

    public static Builder build() {
        return new Builder();
    }

    private Class<? extends BaseModuleFragment> fragmentClz ;
    public static String skin_name;
    public static String btx_json_name;
    private String krt_pro_code;
    private String is_publish;
    private String current_development;

    public static class Builder {
        private String krtCode = "";
        private String isPublish = "";
        private Class<? extends BaseModuleFragment> clazz;

        public Builder setKrtCode(String val) {
            krtCode = val;
            return this;
        }

        public Builder setIsPublish(String val) {
            if (val==null){
                isPublish = "-1";
            }else {
                isPublish = val;
            }
            return this;
        }

        public Builder setFragmentClz(Class<? extends BaseModuleFragment> val) {
            clazz = val;
            return this;
        }

        public void generate() {

            if (mProConfig == null)
                mProConfig = new MProConfig();

            if (!TextUtils.isEmpty(krtCode))
                mProConfig.krt_pro_code = krtCode;

            if (!TextUtils.isEmpty(isPublish))
                mProConfig.is_publish = isPublish;

            if (clazz != null)
                mProConfig.fragmentClz = clazz;
        }

        public void reset() {
            mProConfig = null;
            generate();
        }

    }

    public Class<? extends BaseModuleFragment> getFragmentClz() {
        return fragmentClz;
    }

    public String getKrt_pro_code() {
        return krt_pro_code;
    }

    public String getIs_publish() {
        return is_publish;
    }

    public String getCurrent_development() {
        return current_development;
    }

    public void setCurrent_development(String current_development) {
        this.current_development = current_development;
    }
}
