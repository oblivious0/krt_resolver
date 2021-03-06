package com.wid.applib.widget.base;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.wid.applib.R;
import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.util.FrameParamsBuilder;
import com.wid.applib.util.Util;
import com.wid.applib.widget.BaseView;

import java.util.zip.Inflater;

/**
 * author: MaGua
 * create on:2020/11/3 10:28
 * description
 */
public class ScorllLabelView extends BaseView<TextView> {

    public ScorllLabelView(ContextImp imp, BaseLayoutBean obj) {
        super(imp, obj);
    }

    public ScorllLabelView(ContextImp imp, BaseLayoutBean obj, boolean isListChild) {
        super(imp, obj, isListChild);
    }

    @Override
    protected void initView() {
        type = "scrollLabel";
        view = (TextView) LayoutInflater.from(contextImp.getContext())
                .inflate(R.layout.view_scroll_label, null, false);
//        view = new TextView(contextImp.getContext());
        FrameLayout.LayoutParams lp = FrameParamsBuilder.builder()
                .setWidth(bean.getCommon().getWidth())
                .setHeight(bean.getCommon().getHeight())
                .setMarginLeft(bean.getCommon().getX())
                .setMarginTop(bean.getCommon().getY())
                .build();

        view.setLayoutParams(lp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            view.setLineHeight(Util.getRealValue(
                    bean.getStyle().getLineHeight()));
        }
        if (bean.getStyle().getLineBreakLines() != 0) {
            view.setMaxLines(bean.getStyle().getLineBreakLines());
        }
        if (bean.getStyle().isLineBreakMode()) {
            view.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        }

        view.setText(bean.getCommon().getText().trim());
        if (bean.getStyle().getColor().contains("#")) {
            view.setTextColor(Color.parseColor(bean.getStyle().getColor()));
        } else {
            view.setTextColor(Util.getRealColor(bean.getStyle().getColor()));
        }

        view.setSingleLine(false);
        view.setIncludeFontPadding(false);
        view.setPadding(bean.getStyle().getTextIndent(), 0, 0, 0);
        view.setLineSpacing(0, bean.getStyle().getLineHeight() / bean.getStyle().getFontSize());
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, Util.getRealValue(bean.getStyle().getFontSize()));
        view.setMovementMethod(ScrollingMovementMethod.getInstance());
        switch (bean.getStyle().getTextAlign()) {
            case "left":
                view.setGravity(Gravity.START);
                break;
            case "right":
                view.setGravity(Gravity.END);
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
        return true;
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
