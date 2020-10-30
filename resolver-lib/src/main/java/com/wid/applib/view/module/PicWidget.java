package com.wid.applib.view.module;

import android.content.Context;
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
import io.reactivex.functions.Consumer;
import krt.wid.util.MGlideUtil;
import krt.wid.util.ParseJsonUtil;

/**
 * author:Marcus
 * create on:2020/5/28 8:49
 * description
 */
public class PicWidget extends BaseWidget {

    public PicWidget(ContextImp context, Object object) {
        super(context, object);
    }

    public PicWidget(Context context, Object object) {
        super(context, object);
    }

    @Override
    public View generate() {
        BaseLayoutBean bean = ParseJsonUtil.getBean(ParseJsonUtil.toJson(object), BaseLayoutBean.class);

        final SelectableRoundedImageView img = new SelectableRoundedImageView(getContext());
        FrameLayout.LayoutParams lp = FrameParamsBuilder.builder()
                .setWidth(bean.getCommon().getWidth())
                .setHeight(bean.getCommon().getHeight())
                .setMarginLeft(bean.getCommon().getX())
                .setMarginTop(bean.getCommon().getY())
                .build();
        img.setScaleType(ImageView.ScaleType.FIT_XY);
        img.setLayoutParams(lp);
        int radius = Util.getRealValue(bean.getStyle().getBorderRadius()) / 2;
        img.setCornerRadiiDP(radius, radius, radius, radius);
        if (bean.getStyle().isIcon()) {
            if (!TextUtils.isEmpty(bean.getStyle().getIconFileName())) {
                String[] urls = bean.getStyle().getIconFileName().split("/");

                String picName = urls[urls.length - 1];
                File file = new File(Constants.basePath + "/" + picName);
                if (file.exists()) {
                    CropUtil.getInstance().cropImg(getContext(), picName,
                            bean.getStyle().getIconFileParam(), bitmap -> img.setImageBitmap(bitmap));
                } else {
                    //如果不存在需先下载
                    DownManager.downResOnBack(bean.getStyle().getIconFileName(),
                            picName, filePath -> {
                                LogUtils.e(filePath);
                                CropUtil.getInstance().cropImg(getContext(), filePath,
                                        bean.getStyle().getIconFileParam(), bitmap -> img.setImageBitmap(bitmap));
                            });
                }

            }
        } else {
            MGlideUtil.load(getContext(), bean.getCommon().getSrc(), img);
        }
        img.setTag(R.id.cid, bean.getCid());
        if(!bean.getCommon().isHidden()){
            if(bean.getAnimation()!=null){
                Observable.timer(Math.round(bean.getAnimation().getDelay()) * 500, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                FlubberAnimate.animate(bean.getAnimation(), img);
                                img.setVisibility(View.VISIBLE);
                            }
                        });
            }
        }else{
            img.setVisibility(View.GONE);
        }
        return img;
    }
}
