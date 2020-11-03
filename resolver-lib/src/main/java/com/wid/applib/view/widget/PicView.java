package com.wid.applib.view.widget;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.blankj.utilcode.util.LogUtils;
import com.joooonho.SelectableRoundedImageView;
import com.wid.applib.R;
import com.wid.applib.animate.FlubberAnimate;
import com.wid.applib.base.Constants;
import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.manager.DownManager;
import com.wid.applib.util.CropUtil;
import com.wid.applib.util.FrameParamsBuilder;
import com.wid.applib.util.Util;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import krt.wid.util.MGlideUtil;

/**
 * author: MaGua
 * create on:2020/11/3 10:55
 * description
 */
public class PicView extends BaseView<SelectableRoundedImageView> {

    public PicView(ContextImp imp, BaseLayoutBean obj) {
        super(imp, obj);
    }

    public PicView(ContextImp imp, BaseLayoutBean obj, boolean isListChild) {
        super(imp, obj, isListChild);
    }

    @Override
    protected boolean bindInNewThread() {
        view = new SelectableRoundedImageView(contextImp.getContext());
        FrameLayout.LayoutParams lp = FrameParamsBuilder.builder()
                .setWidth(bean.getCommon().getWidth())
                .setHeight(bean.getCommon().getHeight())
                .setMarginLeft(bean.getCommon().getX())
                .setMarginTop(bean.getCommon().getY())
                .build();

        switch (bean.getStyle().getMode()) {
            case "aspectFit":
                view.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                break;
            case "aspectFill":
                view.setScaleType(ImageView.ScaleType.CENTER_CROP);
                break;
            default:
                //scaleToFill
                view.setScaleType(ImageView.ScaleType.FIT_XY);
                break;
        }

        view.setLayoutParams(lp);
        int radius = Util.getRealValue(bean.getStyle().getBorderRadius()) / 2;
        view.setCornerRadiiDP(radius, radius, radius, radius);
        return true;
    }

    @SuppressLint("CheckResult")
    @Override
    protected void bindInMainThread() {
        if (bean.getStyle().isIcon()) {
            if (!TextUtils.isEmpty(bean.getStyle().getIconFileName())) {
                String[] urls = bean.getStyle().getIconFileName().split("/");

                String picName = urls[urls.length - 1];
                File file = new File(Constants.basePath + "/" + picName);
                if (file.exists()) {
                    CropUtil.getInstance().cropImg(contextImp.getContext(), picName,
                            bean.getStyle().getIconFileParam(), bitmap -> view.setImageBitmap(bitmap));
                } else {
                    //如果不存在需先下载
                    DownManager.downResOnBack(bean.getStyle().getIconFileName(),
                            picName, filePath -> {
                                LogUtils.e(filePath);
                                CropUtil.getInstance().cropImg(contextImp.getContext(), filePath,
                                        bean.getStyle().getIconFileParam(), bitmap -> view.setImageBitmap(bitmap));
                            });
                }

            }
        } else {
            MGlideUtil.load(contextImp.getContext(), bean.getCommon().getSrc(), view);
        }
        if (!bean.getCommon().isHidden()) {
            if (bean.getAnimation() != null) {
                Observable.timer(Math.round(bean.getAnimation().getDelay()) * 500, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aLong -> {
                            FlubberAnimate.animate(bean.getAnimation(), view);
                            view.setVisibility(View.VISIBLE);
                        });
            }
        } else {
            view.setVisibility(View.GONE);
        }
    }

    @Override
    public void bindData(String key, String val) {
        switch (key) {
            case "src":
            case "iconFileName":
                MGlideUtil.load(contextImp.getContext(), val, view);
                break;
            default:
                break;
        }
    }
}
