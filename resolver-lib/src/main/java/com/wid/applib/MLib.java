package com.wid.applib;

/**
 * @author hyj
 * @time 2020/8/20 9:29
 * @class describe 模块化配置项
 */
public class MLib {

    public final static String TAG = "resolver";

    /**
     * 代表解析器为Android端
     */
    public final static String TERMINAL = "1";

    /**
     * 当前解析器版本
     */
    public final static String COMPILER_VERSION = "3";
    /**
     * 当前解析器端版本
     */
    public final static String TERMINAL_VERSION = "0";

    /**
     * 测试版
     */
    public final static String VER_TEST = "-1";
    /**
     * 开发版
     */
    public final static String VER_ALPHA = "0";
    /**
     * 体验版
     */
    public final static String VER_BETA = "1";
    /**
     * 发布版
     */
    public final static String VER_OFFICIAL = "2";
    /**
     * 历史发布版
     */
    public final static String VER_HISTORY = "3";

    /**
     * app启动运行环境（正式地址环境）
     */
    public final static String DEVELOPMENT_PROD = "prod";

    /**
     * app启动运行环境（测式地址环境）
     */
    public final static String DEVELOPMENT_DEV = "dev";
}
