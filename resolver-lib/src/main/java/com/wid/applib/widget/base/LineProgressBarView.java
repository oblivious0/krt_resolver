package com.wid.applib.widget.base;

import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.wid.applib.R;
import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.util.FrameParamsBuilder;
import com.wid.applib.util.Util;
import com.wid.applib.widget.BaseView;

/**
 * @author xzy
 * @package com.wid.applib.widget
 * @description 横向进度条
 * @time 2020/11/27
 */
public class LineProgressBarView  extends BaseView<FrameLayout> {
    public LineProgressBarView(ContextImp imp, BaseLayoutBean obj) {
        super(imp, obj);
    }

    public LineProgressBarView(ContextImp imp, BaseLayoutBean obj, boolean isListChild) {
        super(imp, obj,isListChild);
    }

    @Override
    protected void initView() {
        type = "lineProgress";
        view = new FrameLayout(contextImp.getContext());
        FrameLayout.LayoutParams lp = FrameParamsBuilder.builder()
                .setWidth(bean.getCommon().getWidth())
                .setHeight(bean.getCommon().getHeight())
                .setMarginLeft(bean.getCommon().getX())
                .setMarginTop(bean.getCommon().getY())
                .build();
        view.setLayoutParams(lp);
        initProgressBar();
    }

    @Override
    protected boolean bindInNewThread() {

        return true;
    }


    /**
     * 设置progressbar样式
     */
    private void initProgressBar(){

        ProgressBar mProgressBar = new ProgressBar(contextImp.getContext(),null,
                android.R.attr.progressBarStyleHorizontal);

        //设置圆角半径
        int roundRadius = bean.getCommon().getHeight() / 2;

        //准备progressBar带圆角的背景Drawable
        GradientDrawable progressBg = new GradientDrawable();
        //设置圆角弧度,
        if(bean.getCommon().isRound()) {
            progressBg.setCornerRadius(roundRadius);
        }

        progressBg.setColor(Util.getRealColor(bean.getCommon().getInactiveColor()));

        //准备progressBar带圆角的进度条Drawable
        GradientDrawable progressContent = new GradientDrawable();
        //设置圆角弧度
        if(bean.getCommon().isRound()) {
            progressContent.setCornerRadius(roundRadius);
        }
        //设置绘制颜色，此处可以自己获取不同的颜色
        progressContent.setColor(Util.getRealColor(bean.getCommon().getActiveColor()));

        //ClipDrawable是对一个Drawable进行剪切操作，可以控制这个drawable的剪切区域，以及相相对于容器的对齐方式
        ClipDrawable progressClip = new ClipDrawable(progressContent, Gravity.LEFT, ClipDrawable.HORIZONTAL);
        //Setup LayerDrawable and assign to progressBar
        //待设置的Drawable数组
        Drawable[] progressDrawables = {progressBg, progressClip};
        LayerDrawable progressLayerDrawable = new LayerDrawable(progressDrawables);

        //根据ID设置progressBar对应内容的Drawable
        progressLayerDrawable.setId(0, android.R.id.background);
        progressLayerDrawable.setId(1, android.R.id.progress);
        //设置progressBarDrawable
        mProgressBar.setProgressDrawable(progressLayerDrawable);

        FrameLayout.LayoutParams tvlp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        tvlp.gravity = Gravity.CENTER;
        mProgressBar.setLayoutParams(tvlp);
        //最大数量
        int allNum = bean.getCommon().getActiveNum() + bean.getCommon().getInactiveNum();
        mProgressBar.setMax(100);
        mProgressBar.setProgress(bean.getCommon().getActiveNum() * 100 / allNum);
        view.addView(mProgressBar);
        //显示百分比
        if(bean.getCommon().isShowPercent()){
            initTextView(String.valueOf(bean.getCommon().getActiveNum() * 100 / allNum));
        }

    }

    /**
     * 设置TextView样式
     */
    private void initTextView(String text){
        TextView textView = new TextView(contextImp.getContext());
        textView.setTextColor(ContextCompat.getColor(contextImp.getContext(), R.color.search_icon_color));
        textView.setText(text + "%");
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, Util.getRealValue(20));
        FrameLayout.LayoutParams tvlp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        tvlp.gravity = Gravity.LEFT|Gravity.CENTER_VERTICAL;
        tvlp.setMarginStart(10);
        textView.setLayoutParams(tvlp);
        view.addView(textView);
    }

    @Override
    protected void bindInMainThread() {

    }



    @Override
    public void bindData(String cid, String key, String val) {

    }
}
