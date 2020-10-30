package com.wid.applib.util;

import android.graphics.Color;

import com.blankj.utilcode.util.ColorUtils;

/**
 * @author hyj
 * @time 2020/7/3 16:56
 * @class describe
 */
public class ColorUtil {

    /**
     * rgba 转 16进制
     * @param rgba 格式：rgbargba(142, 138, 138, 1)
     * @return
     */
    public static String rgba2HexString(String rgba){
//        LogUtils.eTag("rgba2HexString",rgba);

        String[] ints = rgba.split("\\(")[1].split("\\)")[0].split(",");
        int color = Color.argb((int) (Float.parseFloat(ints[3].trim()) * 255),
                Integer.parseInt(ints[0].trim()),
                Integer.parseInt(ints[1].trim()),
                Integer.parseInt(ints[2].trim()));
        return ColorUtils.int2ArgbString(color);
    }
}
