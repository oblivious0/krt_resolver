package com.wid.applib.widget.base;

import android.graphics.drawable.GradientDrawable;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.util.FrameParamsBuilder;
import com.wid.applib.util.Util;
import com.wid.applib.widget.BaseView;

/**
 * author: MaGua
 * create on:2020/12/25 9:56
 * description
 */
public class InputView extends BaseView<EditText> {

    public InputView(ContextImp imp, BaseLayoutBean obj) {
        super(imp, obj);
    }

    public InputView(ContextImp imp, BaseLayoutBean obj, boolean isListChild) {
        super(imp, obj, isListChild);
    }

    @Override
    protected void initView() {
        type = "input";
        view = new EditText(contextImp.getContext());
        FrameLayout.LayoutParams lp = FrameParamsBuilder.builder()
                .setWidth(bean.getCommon().getWidth())
                .setHeight(bean.getCommon().getHeight())
                .setMarginLeft(bean.getCommon().getX())
                .setMarginTop(bean.getCommon().getY())
                .build();
        view.setPadding(0, 0, 0, 0);

        view.setLayoutParams(lp);
        view.setBackground(null);

        view.setHint(bean.getStyle().getPlaceholder() == null ? "" : bean.getStyle().getPlaceholder());
        view.setTextSize(16);
        switch (bean.getStyle().getInputAlign()) {
            case "left":
                view.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
                break;
            case "right":
                view.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
                break;
            case "center":
                view.setGravity(Gravity.CENTER);
                break;
            default:
                view.setGravity(Gravity.CENTER_VERTICAL);
                break;
        }

        GradientDrawable drawable = Util.getBgDrawable(bean.getStyle().getBgColor(),
                GradientDrawable.RECTANGLE, bean.getStyle().getBorderRadius(), bean.getStyle().getBorderWidth(),
                bean.getStyle().getBorderColor());
        view.setBackgroundDrawable(drawable);
        view.setVisibility(bean.getCommon().isIsHidden() ? View.GONE : View.VISIBLE);
    }

    @Override
    protected boolean bindInNewThread() {
        return false;
    }

    @Override
    protected void bindInMainThread() {

    }

    @Override
    public void bindData(String cid, String key, String val) {
        try {
            if (cid.equals(this.cid)) {
                switch (key) {
                    case "text":
                        if (TextUtils.isEmpty(val)) {
                            view.setText("");
                        } else {
                            double num;
                            try {
                                num = Double.parseDouble(val);
                                if (num % 1 == 0) {
                                    view.setText(String.valueOf((int) num));
                                } else {
                                    view.setText(String.valueOf(num));
                                }
                            } catch (Exception e) {
                                view.setText(Html.fromHtml(val));
                            }
                        }
                        break;
                    case "fontSize":
                        view.setTextSize(Float.parseFloat(val));
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {

        }
    }
}
