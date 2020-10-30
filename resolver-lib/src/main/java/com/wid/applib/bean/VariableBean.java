package com.wid.applib.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author hyj
 * @time 2020/9/17 9:30
 * @class describe
 */
public class VariableBean {
    /**
     * "keyName": "citycode",
     *       "type": "string",
     *       "default": "360702",
     *       "remark": "地市编码"
     */

    private String keyName;
    private String type;
    @JSONField(name = "default")
    private String def;
    private String remark;

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDef() {
        return def;
    }

    public void setDef(String def) {
        this.def = def;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
