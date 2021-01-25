package com.wid.applib.widget.base;

import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.tencent.smtt.sdk.TbsVideo;
import com.wid.applib.R;
import com.wid.applib.bean.BaseLayoutBean;
import com.wid.applib.imp.ContextImp;
import com.wid.applib.util.FrameParamsBuilder;
import com.wid.applib.util.Util;
import com.wid.applib.view.MRecyclerView;
import com.wid.applib.widget.BaseView;

import krt.wid.util.MGlideUtil;
import krt.wid.util.MToast;

/**
 * @author xzy
 * @package com.wid.applib.widget.base
 * @description
 * @time 2021/1/21
 */
public class VideoView extends BaseView<FrameLayout> {

    private ImageView thump, playImg;

    /**
     * 视频地址
     */
    private String videoUrl = "";


    public VideoView(ContextImp imp, BaseLayoutBean obj) {
        super(imp, obj);
    }

    public VideoView(ContextImp imp, BaseLayoutBean obj, boolean isListChild) {
        super(imp, obj, isListChild);
    }

    @Override
    protected void initView() {
        type = "video";
        view = new FrameLayout(contextImp.getContext());
        FrameLayout.LayoutParams lp = FrameParamsBuilder.builder()
                .setWidth(bean.getCommon().getWidth())
                .setHeight(bean.getCommon().getHeight())
                .setMarginLeft(bean.getCommon().getX())
                .setMarginTop(bean.getCommon().getY())
                .build();
        view.setLayoutParams(lp);
        initVideo();
    }

    private void initVideo() {
        thump = new ImageView(contextImp.getContext());
        FrameLayout.LayoutParams lp = FrameParamsBuilder.builder()
                .setWidth(bean.getCommon().getWidth())
                .setHeight(bean.getCommon().getHeight())
                .build();
        thump.setLayoutParams(lp);
        MGlideUtil.load(contextImp.getContext(), bean.getCommon().getPoster(), thump);
        playImg = new ImageView(contextImp.getContext());
        if (!bean.getCommon().getSrc().isEmpty()) {
            videoUrl = bean.getCommon().getSrc();
        }
        MGlideUtil.load(contextImp.getContext(), R.mipmap.play, playImg);
        FrameLayout.LayoutParams lp1 = FrameParamsBuilder.builder()
                .setWidth(60)
                .setHeight(60)
                .setGravity(Gravity.CENTER)
                .build();
        playImg.setLayoutParams(lp1);
        view.addView(thump);
        view.addView(playImg);

        playImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MToast.showToast(contextImp.getContext(), videoUrl);
                if (TbsVideo.canUseTbsPlayer(contextImp.getContext()) && !TextUtils.isEmpty(videoUrl)) {
                    TbsVideo.openVideo(contextImp.getContext(), videoUrl);
                }

            }
        });

    }

    @Override
    protected boolean bindInNewThread() {
        return false;
    }

    @Override
    protected void bindInMainThread() {

    }

    @Override
    public void bindEvent() {
//        super.bindEvent();
    }

    @Override
    public void bindData(String cid, String key, String val) {
        try {
            if (cid.equals(this.cid)) {
                switch (key) {
                    case "src":
                        if (!TextUtils.isEmpty(val)) {
                            videoUrl = val;
                        }
                        break;
                    case "poster":
                        if (!TextUtils.isEmpty(val)) {
                            MGlideUtil.load(contextImp.getContext(), bean.getCommon().getPoster(), thump);
                        }
                        break;

                    default:
                        break;
                }
            }
        } catch (Exception e) {

        }
    }
}
