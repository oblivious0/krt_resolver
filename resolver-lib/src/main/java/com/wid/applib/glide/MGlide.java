package com.wid.applib.glide;//package com.krt.applib.glide;
//
//import android.content.Context;
//import android.widget.ImageView;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.Priority;
//
//import jp.wasabeef.glide.transformations.CropCircleTransformation;
//import jp.wasabeef.glide.transformations.CropSquareTransformation;
//import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
//
//
///**
// * @author Marcus
// * @package krt.wid.tour_gz.manager
// * @description
// * @time 2017/11/2
// */
//
//public class MGlide {
//    /**
//     * 基础加载无任何配置
//     */
//    public static void baseLoad(Context context, Object url, ImageView imageView) {
//        baseLoad(context, url, imageView, Priority.NORMAL);
//    }
//
//
//    /**
//     * 基础加载无任何配置
//     */
//    public static void baseLoad(Context context, Object url, ImageView imageView, Priority priority) {
//        Glide.with(context)
//                .load(url)
//                .asBitmap()
//                .priority(priority)
//                .into(imageView);
//    }
//
//
//    /**
//     * 带占位符的加载
//     */
//    public static void loadWithPlaceHolder(Context context, Object url, int ph, ImageView imageView) {
//        Glide.with(context)
//                .load(url)
//                .asBitmap()
//                .placeholder(ph)
//                .into(imageView);
//    }
//
//    /**
//     * 带占位符的加载
//     */
//    public static void loadWithPlaceHolder(Context context, Object url, int ph, int error, ImageView imageView) {
//        Glide.with(context)
//                .load(url)
//                .asBitmap()
//                .placeholder(ph)
//                .error(error)
//                .into(imageView);
//    }
//
//    /**
//     * 加载圆形图片(使用普通ImageView) Glide会进行裁剪
//     */
//    public static void loadWithCircle(Context context, Object url, ImageView imageView) {
//        Glide.with(context)
//                .load(url)
//                .asBitmap()
//                .centerCrop()
//                .into(imageView);
//    }
//
//    /**
//     * 加载圆形图片(使用普通ImageView) Glide会进行裁剪
//     */
//    public static void loadWithCircle(Context context, Object url, int ph, ImageView imageView) {
//        Glide.with(context)
//                .load(url)
//                .placeholder(ph)
//                .bitmapTransform(new CropCircleTransformation(context))
//                .into(imageView);
//    }
//
//    /**
//     * 加载圆形图片(使用普通ImageView) Glide会进行裁剪
//     */
//    public static void loadWithCircle(Context context, Object url, int ph, int error, ImageView imageView) {
//        Glide.with(context)
//                .load(url)
//                .placeholder(ph)
//                .error(error)
//                .bitmapTransform(new CropCircleTransformation(context))
//                .into(imageView);
//    }
//
//    /**
//     * 加载圆角矩形图片
//     */
//    public static void loadWithRoundCorner(Context context, Object url, ImageView imageView, int corners) {
//        Glide.with(context)
//                .load(url)
//                .bitmapTransform(new RoundedCornersTransformation(context, corners, 0))
//                .into(imageView);
//    }
//
//    /**
//     * 加载圆角矩形图片
//     */
//    public static void loadWithRoundCorner(Context context, Object url, ImageView imageView, int ph, int corners) {
//        Glide.with(context)
//                .load(url)
//                .placeholder(ph)
//                .bitmapTransform(new RoundedCornersTransformation(context, corners, 0))
//                .into(imageView);
//    }
//
//    /**
//     * 加载圆角矩形图片
//     */
//    public static void loadWithRoundCorner(Context context, Object url, ImageView imageView, int ph, int error, int corners) {
//        Glide.with(context)
//                .load(url)
//                .error(error)
//                .placeholder(ph)
//                .bitmapTransform(new RoundedCornersTransformation(context, corners, 0))
//                .into(imageView);
//    }
//
//
//}
