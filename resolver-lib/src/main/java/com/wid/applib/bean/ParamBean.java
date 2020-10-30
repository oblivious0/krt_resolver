package com.wid.applib.bean;

/**
 * @author hyj
 * @time 2020/9/5 9:51
 * @class describe
 */
public class ParamBean {
    /**
     * 传递的字段名称
     */
    private String key;
    private String keyName;

    /**
     * 传递的字段值
     */
    private String val;
    private boolean fromProps;
//    private boolean ifChange;

    private String source;
    private boolean fromBroad;
    private String broadId;
    private String broadKey;

    public ParamBean() {
    }

    public ParamBean(String key, String val) {
        this.key = key;
        this.val = val;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isFromBroad() {
        return fromBroad;
    }

    public void setFromBroad(boolean fromBroad) {
        this.fromBroad = fromBroad;
    }

    public String getBroadId() {
        return broadId;
    }

    public void setBroadId(String broadId) {
        this.broadId = broadId;
    }

    public String getBroadKey() {
        return broadKey;
    }

    public void setBroadKey(String broadKey) {
        this.broadKey = broadKey;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public boolean isFromProps() {
        return fromProps;
    }

    public void setFromProps(boolean fromProps) {
        this.fromProps = fromProps;
    }
}