package com.wid.applib.util;

import android.widget.FrameLayout;

/**
 * author:Marcus
 * create on:2019/11/15 9:50
 * description
 */
public class FrameParamsBuilder {
    private int width = 0;

    private int height = 0;

    private int marginTop = 0;

    private int marginBottom = 0;

    private int marginLeft = 0;

    private int marginRight = 0;

    private int gravity = FrameLayout.LayoutParams.UNSPECIFIED_GRAVITY;


    private FrameParamsBuilder() {
    }

    public static FrameParamsBuilder builder() {
        return new FrameParamsBuilder();
    }


    public FrameParamsBuilder setWidth(int width) {
        if (width == 750) {
            this.width = FrameLayout.LayoutParams.MATCH_PARENT;
        }
        this.width = width;
        return this;
    }

    public FrameParamsBuilder setHeight(int height) {
        this.height = height;
        return this;
    }

    public FrameParamsBuilder setMarginTop(int marginTop) {
        this.marginTop = marginTop;
        return this;
    }

    public FrameParamsBuilder setMarginBottom(int marginBottom) {
        this.marginBottom = marginBottom;
        return this;
    }

    public FrameParamsBuilder setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
        return this;
    }

    public FrameParamsBuilder setMarginRight(int marginRight) {
        this.marginRight = marginRight;
        return this;
    }

    public FrameParamsBuilder setGravity(int gravity) {
        this.gravity = gravity;
        return this;
    }

    public FrameLayout.LayoutParams build() {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                width == FrameLayout.LayoutParams.MATCH_PARENT || width == FrameLayout.LayoutParams.WRAP_CONTENT
                        ? width : Util.getRealValue(width), height == FrameLayout.LayoutParams.MATCH_PARENT || height == FrameLayout.LayoutParams.WRAP_CONTENT
                ? height : Util.getRealValue(height));
        lp.gravity = gravity;
        lp.setMargins(Util.getRealValue(marginLeft), Util.getRealValue(marginTop), Util.getRealValue(marginRight), Util.getRealValue(marginBottom));
        return lp;
    }


}
